package per.nonlone.spanner.spring.aop;

import java.lang.reflect.Modifier;

import javassist.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.util.Assert;

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
        return getProxy(null);
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        Class<?> clazz = getProxyClass(classLoader,this.advised.getTargetClass());
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CreateAopProxyException(String.format("New Instance for Class %s", clazz.getName()), e);
        }
    }
    
    /**
     * 构建AsmAop代理类
     * @param clazz
     * @return
     */
    private Class<?> getProxyClass(ClassLoader classLoader,Class<?> clazz){
        Class<?> rootClass = this.advised.getTargetClass();
        Assert.state(rootClass != null, String.format("Target Class %s must be available for creating a ASM proxy",clazz.getName()));
        //判断是否为代理类
        if (isAopProxyClass(clazz))
            return clazz;
        //判断类是否可以代理
        if (!checkClassAgent(clazz))
            return clazz;
        //尝试获取代理类
        String proxyClassName = clazz.getName() + CLASSNAME_SUFFIX;
        Class<?> proxyClass = tryLoadClass(classLoader,proxyClassName);
        if (proxyClass != null)
            return proxyClass;
        //代理类不存在，创建代理类
        return createJasistAopProxyClass(classLoader,clazz);
    }
    
    private Class<?> createJasistAopProxyClass(ClassLoader classLoader,Class<?> clazz){
        CtClass targetClass = null;
        try{
            if(clazz!=null)
                targetClass = CLASS_POOL.get(clazz.getName());
        }catch (NotFoundException e) {
            logger.error(String.format("%s can not found by Javassist", clazz.getName()));
            return clazz;
        }
        //确认入参和Javassist获取到被代理的类
        if(targetClass==null || clazz == null)
            throw new CreateAopProxyException("Create Class is Null");
        CtClass proxyClass = CLASS_POOL.makeClass(targetClass.getName()+CLASSNAME_SUFFIX,targetClass);
        CtField[] targetCtFields = targetClass.getFields();
        CtMethod[] targetCtMethods = targetClass.getMethods();

        try {
            return proxyClass.toClass();
        } catch (CannotCompileException e) {
            logger.error(String.format("ToClass Error %s",e.getMessage()),e);
        }
        return clazz;
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
    protected Class<?> tryLoadClass(ClassLoader classLoader,String className) {
        if(classLoader!=null){
            try {
                return (Class<?>) Class.forName(className, false, classLoader);
            } catch (ClassNotFoundException e) {
                try {
                    return (Class<?>) classLoader.loadClass(className);
                }
                catch (ClassNotFoundException e1) {}
            }
        }
        try {
            return (Class<?>) Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            classLoader = getClass().getClassLoader();
            try {
                return (Class<?>) Class.forName(className, false, classLoader);
            }
            catch (ClassNotFoundException e2) {
                try {
                    return (Class<?>) classLoader.loadClass(className);
                }
                catch (ClassNotFoundException e3) {}
            }
        }
        return null;
    }
    
    
}
