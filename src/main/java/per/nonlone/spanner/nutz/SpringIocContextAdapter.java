package per.nonlone.spanner.nutz;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.nutz.ioc.IocContext;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.meta.IocObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Spring 上下文适配器，包装Spring的ApplicationContext来支持Nutz IocContext上下文接口
 * 
 * @author leishunyang
 * @email leishunyang@ppmoney.com
 *
 */
@Component
public class SpringIocContextAdapter implements IocContext, ApplicationContextAware {

	/**
	 * Nutz Bean 为ProxyObject包装类，ProxyObject以添加此前缀来区分
	 */
	private static final String NUTZ_BEAN_PREFIX = "nutz_";

	private ApplicationContext applicationContext;

	public boolean save(String scope, String name, ObjectProxy obj) {
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext) this.applicationContext)
		        .getBeanFactory();
		if (defaultListableBeanFactory != null) {
			if (obj instanceof WrappedObjectProxy) {
				WrappedObjectProxy springObjectProxy = (WrappedObjectProxy) obj;
				IocObject iocObject = springObjectProxy.getIocObject();
				// 注册 Nutz Bean的包装类ObjectProxy到Spring

				// 获取BeanDefinitation
				BeanDefinitionBuilder objectProxyBeanDefinitionBuilder = BeanDefinitionBuilder
				        .genericBeanDefinition(WrappedObjectProxy.class);
				BeanDefinition objectProxyBeanDefinition = objectProxyBeanDefinitionBuilder.getBeanDefinition();
				// 注册
				String objectProxtName = NUTZ_BEAN_PREFIX + name;
				if (springObjectProxy.getIocObject().isSingleton()) { // 单例
					defaultListableBeanFactory.registerBeanDefinition(objectProxtName, objectProxyBeanDefinition);
					defaultListableBeanFactory.registerSingleton(objectProxtName, obj);
				} else { // 非单例
					if (StringUtils.hasText(scope) && ("prototype".equalsIgnoreCase(scope)
					        || "request".equalsIgnoreCase(scope) || "sesssion".equalsIgnoreCase(scope))) {
						objectProxyBeanDefinition.setScope(scope.toLowerCase().trim());
					}
					defaultListableBeanFactory.registerBeanDefinition(objectProxtName, objectProxyBeanDefinition);
				}
				SpringNutIoc.registerIocObject(defaultListableBeanFactory, true, true, name, iocObject,
				        springObjectProxy.getObj());
				return true;
			}
		}
		return false;
	}

	/**
	 * 主要删除Nutz Bean的包装类，对应Nutz Bean本身的类不删除
	 */
	public boolean remove(String scope, String name) {
		String objectProxtName = NUTZ_BEAN_PREFIX + name;
		if (applicationContext.containsBean(objectProxtName)
		        || applicationContext.containsBeanDefinition(objectProxtName)) {
			((DefaultListableBeanFactory) ((ConfigurableApplicationContext) this.applicationContext).getBeanFactory())
			        .removeBeanDefinition(objectProxtName);
			return true;
		}
		return false;
	}

	public ObjectProxy fetch(String name) {
		String objectProxtName = NUTZ_BEAN_PREFIX + name;
		if (applicationContext.containsBean(objectProxtName)
		        || applicationContext.containsBeanDefinition(objectProxtName)) {
			// 存在已经注册的Nutz Bean 包装类
			return applicationContext.getBean(objectProxtName,ObjectProxy.class);
		} else if (applicationContext.containsBean(name) || applicationContext.containsBeanDefinition(name)) {
			// 存在已经注册的Bean 类
			Object object = applicationContext.getBean(name);
			return new ObjectProxy(object);
		}
		return null;
	}

	public void clear() {
		if (applicationContext != null) {
			applicationContext.publishEvent(new ContextRefreshedEvent(applicationContext));
		}
	}

	public void depose() {
		if (applicationContext != null) {
			applicationContext.publishEvent(new ContextClosedEvent(applicationContext));
			applicationContext = null;
		}
	}

	public Set<String> names() {
		return new HashSet<String>(Arrays.asList(applicationContext.getBeanDefinitionNames()));
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
