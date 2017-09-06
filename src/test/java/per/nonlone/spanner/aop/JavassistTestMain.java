package per.nonlone.spanner.aop;

import javassist.ClassPool;
import javassist.CtClass;

public class JavassistTestMain {

    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ccTarget = pool.get(Target.class.getName());
        CtClass ccProxy = pool.makeClass(Target.class.getName()+"$$JAVSSIT",ccTarget);


        Class<?> clazz = ccProxy.toClass();
        Target target = (Target)clazz.newInstance();

        target.methodA();
        target.methodB();
        Target.methodD();
    }
}