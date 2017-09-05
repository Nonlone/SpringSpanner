package per.nonlone.spanner.aop;

public class Target {

    public void methodA(){
        System.out.println("This is a public method");
    }

    protected void methodB(){
        System.out.println("This is a protected method");
    }

    private void methodC(){
        System.out.println("This is a private method");
    }

    public static void methodD(){
        System.out.println("This is a public static  method");
    }

}
