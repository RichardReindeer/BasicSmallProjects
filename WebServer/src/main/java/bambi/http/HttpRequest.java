package bambi.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象
 * 该类的每一个实例用于表示浏览器发送过来的一个HTTP请求
 * Http协议要求一个请求由三部分构成:
 * 请求头、消息头、消息正文
 */
public class HttpRequest {
    //请求行相关信息
    private String method; //请求方式
    private String uri;    //抽象路径
    private String protocol;//协议版本

    private String requestURI;//抽象路径中的请求部分，uri中"?"左侧的内容
    private String queryString; //抽象路路径中的参数部分,uri中"?"右侧的内容
    private Map<String,String> parameters = new HashMap<>();//保存每一组的参数
    //消息头相关信息
    private Map<String,String> headers = new HashMap<>();

    //消息正文的相关信息

    private Socket socket;
    public HttpRequest(Socket socket) throws EmptyRequestException {
        System.out.println("HttpRequest:开始解析请求");
        this.socket = socket;
        //1.解析请求行
        parseRequestLine();
        //2.解析消息头
        parseHeaders();
        //3.解析消息正文
        parseContent();
        System.out.println("HttpRequest:请求解析完毕");
    }

    private void parseRequestLine() throws EmptyRequestException {
        System.out.println("HttpRequest:开始解析请求行....");

        try {
            String line = readLine();
            if(line.isEmpty()){
                //如果是空字符串，说明是空请求!!!!
                throw new EmptyRequestException("发送的是一个空请求");
            }
            System.out.println("请求行: "+line);

            String[] data = line.split("\\s");//以空格为分隔符拿到三项
            method = data[0];
            uri = data[1];
            protocol = data[2];

            parseUri();//进一步解析Uri

            System.out.println("method:"+method);
            System.out.println("uri:"+uri);
            System.out.println("protocol:"+protocol);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("HttpRequest:请求行解析完毕");
    }

    private void parseUri(){


        System.out.println("HttpRequest:进一步解析uri。。。");

        /**
         * 对于不含有参数的Uri而言则不需要做过多的处理，只需要将uri的值直接赋给requestUri
         * 因为requestUri专门用来保存uri的请求部分，不含有参数而言uri就是请求部分
         *
         * 对于有参数的uri，我们要进一步拆分
         * 首先按照"?"将uri拆分为两部分:请求部分和参数部分
         * 然后将请求部分赋值给属性requestUri
         * 将参数部分赋值给属性queryString
         *
         * 只要再对queryString进一步擦hi分出每一组参数:
         * 首先按照"&"拆分出每个参数，然后每个参数再按照"="拆分为参数名和参数值
         * 将参数名作为key，参数值作为value保存到属性parameters这个Map中即可.
         *
         */

        //对uri解码，将%XX的16进制所表示的信息解码还原对应的文字
        try {
            uri = URLDecoder.decode(uri,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(uri.contains("?")) {//判断uri中是否含有参数,如果没有?就代表没有参数
            String[] arr = uri.split("\\?");
            requestURI = arr[0];
            if(arr.length>1){
                queryString = arr[1];
                //进一步拆分参数
                arr = queryString.split("&");
                for(String para : arr){//name = value
                    String[] paras = para.split("=");
                    if(paras.length>1){
                        parameters.put(paras[0],paras[1]);
                    }else {
                        parameters.put(paras[0],null);
                    }
                }
            }
        }  else {
            requestURI = uri;
        }
        System.out.println("requestURI:"+requestURI);
        System.out.println("queryString:"+queryString);
        System.out.println("parameters:"+parameters);
        System.out.println("进一步解析成功");
    }

    private void parseHeaders(){
        System.out.println("HttpRequest:开始解析消息头.....");

        try {
            while (true){
                String message = readLine();
                //字符串有一个方法是isEmpty()
    //                if("".equals(message)){
    //                    break;
    //                }
                if(message.isEmpty()){
                    break;
                }
                String[] messages = message.split(":\\s");//\s是空格，多加一个\是转义符
                headers.put(messages[0],messages[1]);
                System.out.println("消息头的名字:"+messages[0]);
                System.out.println("消息头的值:"+messages[1]);
            }

            System.out.println("Map:"+headers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("HttpRequest:消息头解析完毕");
    }

    private void parseContent(){
        System.out.println("HttpRequest:开始解析消息正文");
        System.out.println("HttpRequest:消息正文解析完毕");
    }


    private String readLine() throws IOException {
        /**
         * 只要socket相同，不管用getInputStream调取多少次输入流，调取到的都是同一个输入流对象
         */
        InputStream inputStream = socket.getInputStream();
        int id;
        StringBuilder stringBuilder = new StringBuilder();
        char cur = 'a';//本次读取到的字符
        char pre = 'a';//上次读取到的字符
        while ((id = inputStream.read())!=-1){
            cur = (char) id;
            if(pre ==13 && cur ==10){
                break;
            }
            stringBuilder.append(cur);
            pre = cur;
        }
        return stringBuilder.toString().trim();
    }

    /**
     * 因为这些属性的值都是从客户端获取到的，其他类调用的时候不需要进行set来改值
     * 保险起见只创建get方法
     * @return
     */
    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    /**
     * 根据key获取value
     * @param key
     * @return
     */
    public String getParameter(String key){
        return parameters.get(key);
    }
}
