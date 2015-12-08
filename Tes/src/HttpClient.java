import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


/*
 * 项目名：Tes 文件名：HttpClient.java 版权：Copyright by www.symboltech.com 描述： 修改人：symbol 修改时间：2015年12月7日 修改内容：
 */

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author symbol
 * @version 2015年12月7日
 * @see HttpClient
 * @since
 */

public class HttpClient
{

    public static void main(String[] args)
    {
        CloseableHttpClient clinet = HttpClientBuilder.create().build();
        try
        {
            StringEntity entity = new StringEntity("this is a stringentity");
            HttpPost post = new HttpPost("");
            post.setEntity(entity);
            CloseableHttpResponse resp = clinet.execute(post);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
