package per.nonlone.spanner.nutz;

import java.util.Map;
import java.util.Map.Entry;

import org.nutz.NutException;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocContext;
import org.nutz.ioc.meta.IocField;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.lang.Lang;
import org.nutz.mvc.IocProvider;
import org.nutz.mvc.NutConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 自实现静态工厂，自实现单例，注入到Spring容器管理， 向Nutz提供Spring容器管理，Nutz的bean都存放到Spring容器中
 *
 * @author leishunyang
 */
@Component
public class SpringIocProviderSingleton
        implements IocProvider, ApplicationContextAware, FactoryBean<SpringIocProviderSingleton> {

    private ApplicationContext applicationContext;

    // 屏蔽默认构造器
    private SpringIocProviderSingleton() {}

    // 单例模式，静态内部类
    private static class SpringIocProviderSingletonHolder {
        static SpringIocProviderSingleton springIocProviderSingleton = new SpringIocProviderSingleton();
    }

    /**
     * IocProvicder接口实现，生成Ioc并初始化全部注册Bean
     */
    public Ioc create(NutConfig config, String[] args) {
        IocContext iocContext = getInstance().applicationContext.getBean(SpringIocContextAdapter.class);
        try {
            CrackedComboIocLoader crackedComboIocLoader = new CrackedComboIocLoader(args);
            Ioc ioc = new SpringNutIoc(new RenovateObjectMaker(), crackedComboIocLoader, iocContext, "app");
            Map<String, IocObject> iocObjectMap = crackedComboIocLoader.obtainIocObjectMap();
            if (null != iocObjectMap && !iocObjectMap.isEmpty()) {
                for (Entry<String, IocObject> entry : iocObjectMap.entrySet()) {
                    String iocName = entry.getKey();
                    IocObject iocObject = entry.getValue();
                    //创建Nutz Bean
                    boolean result = doGet(iocName, iocObject, ioc, iocObjectMap);
                    if (!result)
                        throw new NutException(String.format("Error Create Nutz Bean! type<%s> , name<%s>", iocObject.getType(), iocName));
                }
            }
            return ioc;
        } catch (ClassNotFoundException e) {
            throw Lang.wrapThrow(e);
        } catch (NutException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 注册Nutz Bean（获取一下，让Bean自行注册），递归调用处理Field的依赖性问题
     *
     * @param iocName      注册名字
     * @param iocObject    注册Bean元数据
     * @param ioc          Ioc容器
     * @param iocObjectMap IocObject元数据集，判断对应Field是否为Nutz注册的Bean
     * @return
     * @throws NutException
     */
    private boolean doGet(String iocName, IocObject iocObject, Ioc ioc, Map<String, IocObject> iocObjectMap) throws NutException {
        boolean result = true;
        //已经创建，跳过
        if (ioc.has(iocName))
            return result;
        IocField[] iocFields = iocObject.getFields();
        for (IocField iocField : iocFields) {
            IocValue iocValue = iocField.getValue();
            String iocValueType = iocField.getValue().getType();
            if (IocValue.TYPE_REFER.equals(iocValueType)
                    && !ioc.has((String) iocValue.getValue())
                    && iocObjectMap.containsKey(iocValue.getValue())) {
                IocObject fieldIocObject = iocObjectMap.get(iocValue.getValue());
                // 递归，直到对应Field都存在，或者对应Bean不含有引用其他Bean为止
                result &= doGet((String) iocValue.getValue(), fieldIocObject, ioc, iocObjectMap);
            }
        }
        if (!result)
            throw new NutException(String.format("Error Create Nutz Bean! type<%s> , name<%s>", iocName, iocObject.getType()));
        ioc.get(iocObject.getType(), iocName);
        return result;
    }

    /**
     * 静态工厂，Nutz Mirror优先调用此方法生成
     *
     * @return
     */
    public static SpringIocProviderSingleton getInstance() {
        return SpringIocProviderSingletonHolder.springIocProviderSingleton;
    }

    public SpringIocProviderSingleton getObject() {
        return getInstance();
    }

    public Class<?> getObjectType() {
        return getObject().getClass();
    }

    public boolean isSingleton() {
        return true;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        getInstance().applicationContext = applicationContext;
    }

}
