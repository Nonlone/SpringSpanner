package per.nonlone.spanner.aop;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class JavassistTestMain {

    public static void main(String[] args) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get(Target.class.getName());
    }
}
