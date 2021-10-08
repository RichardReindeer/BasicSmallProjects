package bambi.demo;

import com.bambi.core.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 程序员的身份
 */
@RequestMapping("/demo")
public class LoginController {
    //返回一个登录页面
    @RequestMapping("/toLogin.do")
    public String toLogin(){
        System.out.println("方法跟踪");
        return "login";
    }

    @RequestMapping("/login.do")
    public String login(HttpServletRequest httpServletRequest){
        System.out.println("loginInController's login() is start");
            String username = httpServletRequest.getParameter("username");
            String password = httpServletRequest.getParameter("password");
            System.out.println("获取到的用户名："+username);
            System.out.println("获取到的密码："+password);
            if("tom".equals(username)&&"123".equals(password)){
                //登录成功
                //用redirect来发送重定向
                return "redirect:welcome.do";
            }
            else {
                httpServletRequest.setAttribute("login_fail","用户名或密码错误");
                return "login";
            }
    }
    @RequestMapping("/welcome.do")
    public String welcome(){
        return "welcome";
    }
}
