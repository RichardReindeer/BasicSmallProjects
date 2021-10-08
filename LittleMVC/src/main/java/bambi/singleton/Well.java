package bambi.singleton;

/**
 * 这是懒汉式单例模型创建方法
 *      即当外界需要这个实例对象的时候 才会被创建
 */
public class Well {
    //保存实例  为了以后频繁的使用
    private static Well well;

    //1.构造器私有化
    /**
     * 构造器私有化 防止外界调用
     */
    private Well(){
        System.out.println("Well's constructor");
    }

    /**
     * 外界想要获取这个实例 必须通过这个方法来获取
     * 因为构造器已被私有化
     *
     * 懒汉式写法存在线程安全问题 需要在方法上加上synchronized关键字 变成同步方法 但是同步方法耗费的时间就会相对长一些
     * @return
     */
    public synchronized static Well getInstance(){
        //存在线程安全问题
        if(well==null){
            well = new Well();
        }
        return well;
    }

}
