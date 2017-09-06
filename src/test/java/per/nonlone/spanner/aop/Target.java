package per.nonlone.spanner.aop;

public class Target {

    public Target(){
        methodC();
    }

    public void methodA(){
        System.out.println("This is a public method A");
        methodE();
        methodF();
    }

    protected void methodB(){
        System.out.println("this is protected method B");
        methodE();
        methodF();
    }

    private void methodC(){
        System.out.println("this is private method C");
    }

    public static void methodD(){
        System.out.println("this is static method D");
    }

    public void methodE(){
        System.out.println("This is a public method E call by inner method");
    }

    protected void methodF(){
        System.out.println("This is a protected method F call by inner method");
    }

}
