package per.nonlone.spanner.spring.aop;

import org.apache.log4j.Logger;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class AsmAopProxy implements AopProxy{
    
    private final static Logger logger = Logger.getLogger(AsmAopProxy.class);

    //ASMAOP切片类后缀
    public final static String CLASSNAME_SUFFIX = "$$ASMAOP";
    
    protected final AdvisedSupport advised; 
    
    public AsmAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating ASM proxy: target source is " + this.advised.getTargetSource());
        }

        try {
            Class<?> rootClass = this.advised.getTargetClass();
            Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");

            Class<?> proxySuperClass = rootClass;
            if (ClassUtils.isCglibProxyClass(rootClass)) {
                proxySuperClass = rootClass.getSuperclass();
                Class<?>[] additionalInterfaces = rootClass.getInterfaces();
                for (Class<?> additionalInterface : additionalInterfaces) {
                    this.advised.addInterface(additionalInterface);
                }
            }
        }catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

}
