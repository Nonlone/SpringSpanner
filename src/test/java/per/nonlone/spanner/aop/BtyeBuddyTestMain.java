package per.nonlone.spanner.aop;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

public class BtyeBuddyTestMain {

    public static void main(String[] args) throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy().subclass(Target.class).make();
        Class<?> proxyClass = dynamicType.load(
                BtyeBuddyTestMain.class.getClassLoader(),
                ClassLoadingStrategy.Default.WRAPPER).getLoaded();
        System.out.println(proxyClass.getName());
        Object object = proxyClass.newInstance();
        if (Target.class.isInstance(object)) {
            Target target = (Target)object;
            target.methodA();
            target.methodB();
        }
    }
}
