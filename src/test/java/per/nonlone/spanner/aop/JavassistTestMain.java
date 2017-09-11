package per.nonlone.spanner.aop;

import javassist.*;

public class JavassistTestMain {

    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ccTarget = pool.get(Target.class.getName());
        CtClass ccProxy = pool.makeClass(Target.class.getName()+"$$JAVSSIT",ccTarget);

        CtMethod[] targetMethods = ccTarget.getDeclaredMethods();
        for (CtMethod targetMethod : targetMethods) {
            if(Modifier.isPrivate(targetMethod.getModifiers())){
                //私有方法
                targetMethod.setModifiers(Modifier.PROTECTED);
            }

            //公开方法，public和protected，但是标记static的无法切入
            System.out.println(targetMethod.getName() + "," + targetMethod.getModifiers());
            CtMethod proxyMethod = CtNewMethod.copy(targetMethod, ccProxy, null);
            proxyMethod.insertBefore(String.format(" System.out.println(\"Proxy Class %s call\"); ", targetMethod.getName()));
            ccProxy.addMethod(proxyMethod);
        }


        //装载类
//        ccTarget.toClass(Target.class.getClassLoader());

        Class<?> clazz = ccProxy.toClass();
        Target target = (Target)clazz.newInstance();

        target.methodA();
        target.methodB();
        target.methodD();
    }
}