package bambi.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 响应对象
 * 当前类的每一个实例表示给客户端发送一个HTTP响应对象
 * 一个响应应当包括三部分:状态行、响应头、响应正文
 */
public class HttpResponse {
    //状态行相关信息
    private int statusCode = 200;//状态代码，默认值是200
    private String statusReadson = "OK";//状态描述:默认值是OK

    //响应头相关信息
    private Map<String,String> headMap = new HashMap<>();
    //响应正文相关信息
    private File entity;//响应正文对应的实体文件
    private byte[] data;//正文的字节数据
    private Socket socket;

    public HttpResponse(Socket socket) {
        this.socket = socket;
    }

    /**
     * 将当前响应对象内容以标准的HTTP响应格式发送给客户端
     */
    public void flush(){
        /**
         * 将ClientHeader中重复的代码写到一个方法中，因为获取的状态行、响应头和响应正文之间又并不是完全一样，所以可以再单独定义三个私有细节方法
         * 来单独实现每个功能
         */
        //发送一个响应
        System.out.println("开始发送响应");
        //1发送状态行
        sendStatusLine();
        //2发送响应头
        sendHeaders();
        //3发送响应正文
        sendContent();
        System.out.println("响应发送完毕");
    }

    private void sendStatusLine(){
        try {
            System.out.println("开始发送状态行");
            String line = "HTTP/1.1"+" "+statusCode+" "+statusReadson;
            System.out.println("状态行:"+line);
            println(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendHeaders(){
        try {
            /*System.out.println("开始发送响应头");
            OutputStream outputStream = socket.getOutputStream();
            println("Content-Type: index.html");
            println("Content-length: "+entity.length());*/
            Set<Map.Entry<String,String>> entrySet = headMap.entrySet();
            for(Map.Entry<String,String> entry : entrySet){
                String key = entry.getKey();
                String value = entry.getValue();
                String line = key+": "+value;
                println(line);
            }
            //单独发送一个CRLF表示响应头发送完毕
            println("");//发送一个空字符串会变化0个字节的写入，等于什么都没写
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendContent(){
        System.out.println("开始发送响应正文");
        if(data != null){
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(entity!=null) {
            //判断是否有正文
            try (
                    /**
                     * 放在括号中，会自动关闭fileInputStream
                     */
                    FileInputStream fileInputStream = new FileInputStream(entity);
            ) {
                System.out.println("开始发送响应正文");
                OutputStream outputStream = socket.getOutputStream();
                int len;
                byte[] data = new byte[1024 * 10];
                while ((len = fileInputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void println(String line) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(line.getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(13);
        outputStream.write(10);
    }

    /**
     * 给文件属性添加GET SET方法，方便外界传入一个File文件
     * @return
     */
    public File getEntity() {

        return entity;
    }

    public void setEntity(File entity) {
        String fileName = entity.getName();
        String fileKind = fileName.substring(fileName.lastIndexOf(".")+1);//找到文件中最后一个.的位置，+1表示从这个字符之后开始读
        String type = HttpContext.getMimeType(fileKind);//根据key获取value

        setHeadMap("Content-Type",type);
        setHeadMap("Content-Length",entity.length()+"");
        this.entity = entity;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReadson() {
        return statusReadson;
    }

    public void setStatusReadson(String statusReadson) {
        this.statusReadson = statusReadson;
    }

    public Map<String, String> getHeadMap() {
        return headMap;
    }

    public void setHeadMap(String key , String value) {
        this.headMap.put(key,value);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
        setHeadMap("Content-Length",data.length+"");
    }
}
