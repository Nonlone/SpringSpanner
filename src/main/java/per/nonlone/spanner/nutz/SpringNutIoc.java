package per.nonlone.spanner.nutz;

import org.nutz.ioc.IocContext;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.ObjectMaker;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.StringUtils;

/**
 * 用Spring封装NutIoc入口，暴露对应的ObjectMaker入参
 * 提供IocObject到注册到BeanDefinitionRegistry的静态方法工具类
 * 
 * @author leishunyang
 *
 */
public class SpringNutIoc extends NutIoc {

	private static final char NUTZ_CLASS_METHOD_SPLITER = '#';
	
	private IocContext context;

	public SpringNutIoc(ObjectMaker maker, IocLoader loader, IocContext context, String defaultScope) {
		super(maker, loader, context, defaultScope);
		this.context = context;
	}
	
	@Override
	public boolean has(String name) {
		return context.fetch(name)!=null;
	}

	/**
	 * 工具方法，往Spring注册对应的IocObject
	 * @param registry BeanDefinition注册容器
	 * @param isIgnoreDupicateName 是否负略重复名检查
	 * @param isDefinedField 是否注册对应Field
	 * @param iocObjectName NutzBean 名字
	 * @param iocObject NutzBean 配置
	 * @param object NutzBean 实例（用于单例）
	 * @return 注册结果
	 */
	public static boolean registerIocObject(BeanDefinitionRegistry registry, boolean isIgnoreDupicateName,
	        boolean isDefinedField, String iocObjectName, IocObject iocObject, Object object) {
		boolean isSingletonRegistrable = false;
		if (registry instanceof DefaultListableBeanFactory)
			isSingletonRegistrable = true;
		BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(iocObject.getType())
		        .getBeanDefinition();
		if (null == beanDefinition) // 无法解析对应Nutz Bean
			return false;
		((AbstractBeanDefinition) beanDefinition).setAutowireMode(AbstractBeanDefinition.AUTOWIRE_AUTODETECT);
		if (isIgnoreDupicateName || !registry.containsBeanDefinition(iocObjectName)) { // 重命名判断
			// 设置构造函数
			if (null != iocObject.getArgs() && iocObject.getArgs().length > 0) {
				for (int i = 0; i < iocObject.getArgs().length; i++) {
					IocValue iocValue = iocObject.getArgs()[i];
					beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(i, iocValue.getValue(),
					        iocValue.getClass().getName());
				}
			}
			// 设置注入field
			if (isDefinedField && null != iocObject.getFields() && iocObject.getFields().length > 0) {
				for (int i = 0; i < iocObject.getFields().length; i++) {
					IocField iocField = iocObject.getFields()[i];
					IocValue iocValue = iocField.getValue();
					Object iocFieldValue = null;
					// 引用类型
					if ("refer".equalsIgnoreCase(iocValue.getType())) {
						RuntimeBeanReference ref = new RuntimeBeanReference((String) iocValue.getValue());
						iocFieldValue = ref;
					} else {
						iocFieldValue = iocValue.getValue();
					}
					PropertyValue pv = new PropertyValue(iocField.getName(), iocFieldValue);
					beanDefinition.getPropertyValues().addPropertyValue(pv);
				}
			}
			// 设置工厂方法
			if (StringUtils.hasText(iocObject.getFactory())) {
				String factoryBeanName = iocObject.getFactory().substring(0,
				        iocObject.getFactory().lastIndexOf(NUTZ_CLASS_METHOD_SPLITER));
				String factoryMethodName = iocObject.getFactory()
				        .substring(iocObject.getFactory().lastIndexOf(NUTZ_CLASS_METHOD_SPLITER));
				if (StringUtils.hasText(factoryBeanName) && StringUtils.hasText(factoryMethodName)) {
					beanDefinition.setFactoryBeanName(factoryBeanName);
					beanDefinition.setFactoryMethodName(factoryMethodName);
				}
			}
			String scope = iocObject.getScope();
			if (StringUtils.hasText(scope) && ("prototype".equalsIgnoreCase(scope) || "request".equalsIgnoreCase(scope)
			        || "sesssion".equalsIgnoreCase(scope))) {
				beanDefinition.setScope(scope.toLowerCase().trim());
			}
			registry.registerBeanDefinition(iocObjectName, beanDefinition);
			if (isSingletonRegistrable && iocObject.isSingleton()) {
				((DefaultListableBeanFactory) registry).registerSingleton(iocObjectName, object);
			}
		}
		return true;
	}

}
