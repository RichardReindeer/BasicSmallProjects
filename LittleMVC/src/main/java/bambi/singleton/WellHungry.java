package bambi.singleton;

/**
 * 饿汉式写法
 */
public class WellHungry {

    //在类加载的时候创建
    private static WellHungry wellHungry = new WellHungry();

    /**
     * 将构造器私有化
     */
    private WellHungry() {
        System.out.println("这是私有化的构造器哦");
    }

    public static WellHungry getInstance(){
        return wellHungry;
    }
}
