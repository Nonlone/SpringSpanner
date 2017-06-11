package per.nonlone.spanner.spring.aop;

import java.lang.reflect.Modifier;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.nutz.lang.Lang;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.util.Assert;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import per.nonlone.spanner.spring.exception.CreateAopProxyException;

public class JavassistAopProxy implements AopProxy{
    
    private final static Logger logger = Logger.getLogger(JavassistAopProxy.class);
    
    private final static ClassPool CLASS_POOL = ClassPool.getDefault();

    //AOP切片类后缀
    public final static String CLASSNAME_SUFFIX = "$$JASISTAOP";
    
    protected final AdvisedSupport advised; 
    
    public JavassistAopProxy(){
        advised = null;
    }
    
    public JavassistAopProxy(AdvisedSupport advised) {
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
            if (isAopProxyClass(rootClass)) {
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
    
    /**
     * 构建AsmAop代理类
     * @param clazz
     * @return
     */
    private <T> Class<T> buildAopProxyClass(Class<T> clazz){
        Class<?> rootClass = this.advised.getTargetClass();
        Assert.state(rootClass != null, "Target class must be available for creating a ASM proxy");
        //判断是否为代理类
        if (isAopProxyClass(clazz))
            return clazz;
        //判断类是否可以代理
        if (!checkClassAgent(clazz))
            return clazz;
        //尝试获取代理类
        String proxyClassName = clazz.getName() + CLASSNAME_SUFFIX;
        Class<T> proxyClass = tryLoadClass(proxyClassName);
        if (proxyClass != null)
            return proxyClass;
        //代理类不存在，创建代理类
        return createAsmAopProxyClass(clazz);
    }
    
    private <T> Class<T> createAsmAopProxyClass(Class<T> clazz){
        CtClass targetCtClass = null;
        try{
            if(clazz!=null)
                targetCtClass = CLASS_POOL.get(clazz.getName());
        }catch (NotFoundException e) {
            logger.error(String.format("%s can not found by Javassist", clazz.getName()));
            return clazz;
        }
        //确认入参和Javassist获取到被代理的类
        if(targetCtClass==null || clazz == null)
            throw new CreateAopProxyException("Create Class is Null");
        CtField[] targetCtFields = targetCtClass.getFields();
        CtMethod[] targetCtMethods = targetCtClass.getMethods();
        
        return null;
    }
    
    
    /**
     * 判断是否AsmAopProxy生成类型
     * @param clazz
     * @return
     */
    private static boolean isAopProxyClass(Class<?> clazz){
        return (clazz!=null && StringUtils.isNotBlank(clazz.getName()) && clazz.getName().endsWith(CLASSNAME_SUFFIX));
    }

    /**
     * 判断类是否可继承
     * @param clazz
     * @return
     */
    protected boolean checkClassAgent(Class<?> clazz) {
        if (clazz == null)
            return false;
        if (clazz.isInterface()
            || clazz.isArray()
            || clazz.isEnum()
            || clazz.isPrimitive()
            || clazz.isMemberClass()
            || clazz.isAnnotation()
            || clazz.isAnonymousClass())
            throw new CreateAopProxyException(String.format("%s is a Invisible Class", clazz.getName()));
        if (Modifier.isFinal(clazz.getModifiers()) || Modifier.isAbstract(clazz.getModifiers()))
            throw new CreateAopProxyException(String.format("%s is a Inheritable Class", clazz.getName()));
        return true;
    }
    
    @SuppressWarnings("unchecked")
    protected <T> Class<T> tryLoadClass(String className) {
        try {
            return (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            ClassLoader classLoader = getClass().getClassLoader();
            try {
                return (Class<T>) Class.forName(className, false, classLoader);
            }
            catch (ClassNotFoundException e2) {
                try {
                    return (Class<T>) Lang.loadClass(className);
                }
                catch (ClassNotFoundException e1) {
                    try {
                        return (Class<T>) classLoader.loadClass(className);
                    }
                    catch (ClassNotFoundException e3) {}
                }
            }
        }
        return null;
    }
    
    
}
