package cn.chinaunicom.monitor.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import cn.chinaunicom.monitor.utils.Const;

/**
 * Created by yfyang on 2017/8/1.
 */

public class AbstractHttpClient {
    protected static final OkHttpClient Client = new OkHttpClient();
    protected String serverUrl;
    protected String sessionId;

    protected AbstractHttpClient(String serverUrl, String sessionId) {
        this.serverUrl = serverUrl;
        this.sessionId = sessionId;
        Client.setConnectTimeout(Const.HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    protected String syncOkHttpPost(String actionUrl, String postString) {
        String requestUrl = serverUrl + actionUrl;
        final MediaType reqType = MediaType.parse("application/json; charset=UTF-8");
        RequestBody body = RequestBody.create(reqType, postString);
        Request request = new Request.Builder().url(requestUrl).header("Cookie", "JSESSIONID=" + sessionId).post(body).build();
        String rtnValue = null;
        try {
            Response response = Client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                if (!response.isRedirect()) {
                    rtnValue = response.body().string();
                }
            }
        } catch (Exception e) {
            Log.e("HTTP-Exception", requestUrl + "(" + postString + ")");
        }
        return rtnValue;
    }

    protected <T> T syncOkHttpPost(String actionUrl, String postString, Type respType) {
        String requestUrl = serverUrl + actionUrl;
        final MediaType reqType = MediaType.parse("application/json; charset=UTF-8");
        Log.e("HTTP-JSON-STRING", postString);
        RequestBody body = RequestBody.create(reqType, postString);
        Request request = new Request.Builder().url(requestUrl).header("Cookie", "JSESSIONID=" + sessionId).post(body).build();
        T rtnValue = null;
        try {
            Response response = Client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                if (!response.isRedirect()) {
                    String respString = response.body().string();
                    if (respString != null && respType != null) {
                        Log.e("HTTP-JSON-STRING","RESP:" + respString);
                        rtnValue = new Gson().fromJson(respString, respType);
                    }
                }
            }
        } catch (JsonSyntaxException jsonException) {
            Log.e("HTTP-JSON-Execption", requestUrl + "(" + postString + ")");
        } catch (Exception e) {
            Log.e("HTTP-Exception", requestUrl + "(" + postString + ")");
            Log.e("Exception", e.toString());
        }
        return rtnValue;
    }

    private String buildPostString(String postString) {
        return "sessionId=" + sessionId + "content=" + postString;
    }
}
