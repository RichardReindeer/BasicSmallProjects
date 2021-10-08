package bambi.vo;

/**
 * vo:value object 值对象
 *
 * 当前类的每一个实例用于表示user.dat文件中的一个用户信息
 */
public class User {
    private String username;
    private String password;
    private String nickName;
    private String checkSex;
    private int age;

    public User(String username, String password, String nickName, String checkSex, int age) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.checkSex = checkSex;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCheckSex() {
        return checkSex;
    }

    public void setCheckSex(String checkSex) {
        this.checkSex = checkSex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickName='" + nickName + '\'' +
                ", checkSex='" + checkSex + '\'' +
                ", age=" + age +
                '}';
    }
}
