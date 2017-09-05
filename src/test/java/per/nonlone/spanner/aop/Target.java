package per.nonlone.spanner.aop;

public class Target {

    public void methodA(){
        System.out.println("This is a public method A");
    }

    protected void methodB(){
        System.out.println("this is protected method B");
    }

    private void methodC(){
        System.out.println("this is private method C");
    }

    public static void methodD(){
        System.out.println("this is static method D");
    }}
}
