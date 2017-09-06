package per.nonlone.spanner.aop;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class JavassistTestMain {

    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ccTarget = pool.get(Target.class.getName());
        CtClass ccProxy = pool.makeClass(Target.class.getName()+"$$JAVSSIT",ccTarget);

        CtMethod[] targetMethods = ccTarget.getDeclaredMethods();
        for(CtMethod targetMethod:targetMethods){
            CtMethod proxyMethod = CtNewMethod.copy(targetMethod,ccProxy,null);
            proxyMethod.insertBefore(String.format(" System.out.println(\"%s call\"); ",targetMethod.getName()));
            ccProxy.addMethod(proxyMethod);
        }


        Class<?> clazz = ccProxy.toClass();
        Target target = (Target)clazz.newInstance();

        target.methodA();
        target.methodB();
        target.methodD();
    }
}