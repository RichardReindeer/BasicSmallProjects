package bambi.singleton;

//测试懒汉式写法
public class Test {
    public static void main(String[] args) {
        Well well = Well.getInstance();
        Well well1 = Well.getInstance();
        System.out.println(well==well1);
    }
}
