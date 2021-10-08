package bambi.http;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP协议规定的内容都是定义在这里，以便将来重用
 *
 * 注意代码重用！！！！
 */
public class HttpContext {
    /**
     * Content-Type信息
     * key：资源的后缀名
     * value：对应的Content-Type的值
     */
    private static Map<String,String> mimeMapping = new HashMap<>();

    //静态的属性在静态块中初始化

    static {
        initMimeMapping();
    }

    private static void initMimeMapping(){
//        mimeMapping.put("html","text/html");
//        mimeMapping.put("css","text/css");
//        mimeMapping.put("js","application/javascript");
//        mimeMapping.put("png","image/png");
//        mimeMapping.put("jpg","image/jpeg");
//        mimeMapping.put("gif","image/gif");

        /**
         * 通过解析config/web.xml文件初始化mimeMaping
         * 将根下标签所有名为<mime-mapping>的子标签获取到
         * 并将其中的子标签:
         * <extension>中间的文本作为key
         * <mime-type>中间的文本作为value
         * 保存到mimeMapping这个Map中
         * 初始化完毕后,mimeMapping中应当有1011个元素
         */

        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read("./WebServer/config/web.xml");
            Element root = document.getRootElement();
            List<Element> mappingList = root.elements("mime-mapping");
            for(Element element : mappingList){
                String key = element.elementText("extension");
                String value = element.elementText("mime-type");
                mimeMapping.put(key,value);
            }
            System.out.println("------------------------------------------------"+mimeMapping.size());
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据资源后缀名获取对应的Content-Type的值
     * @param ext
     * @return
     */
    public static String getMimeType(String ext){
        return mimeMapping.get(ext);
    }
}
