package han.com.utils;

import android.util.Log;
import java.io.FileOutputStream;
import java.util.Scanner;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtil {

    private static final String className = HttpUtil.class.getName();

    public static void httpFileDownload(String uri, String fileName) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet get = new HttpGet(uri);
        HttpResponse response = httpclient.execute(get);
        response.getEntity().writeTo(new FileOutputStream(fileName));
        Log.d(className, "file saved at: " + fileName);
    }

    public static Object[] httpPostData(String uri, String data) throws Exception {
        Log.d(className, "requesting: " + uri);
        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpPost put = new HttpPost(uri);
        put.setEntity(new StringEntity(data));
        Log.d(className, "sent: " + data);

        HttpResponse response = httpclient.execute(put);
        Log.d(className, "http status: " + response.getStatusLine());

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            return new Object[]{response.getStatusLine().getStatusCode()};
        }


        StringBuilder sb = new StringBuilder(100);
        HttpEntity entity = response.getEntity();
        Scanner scan = new Scanner(entity.getContent());

        while (scan.hasNextLine()) {
            sb.append(scan.nextLine());
        }

        Log.d(className, "receive: " + sb.toString());

        scan.close();
        return new Object[]{HttpStatus.SC_OK, sb.toString()};
    }

    private HttpUtil() {
    }
}
