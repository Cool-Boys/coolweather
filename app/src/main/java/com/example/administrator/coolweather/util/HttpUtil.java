package com.example.administrator.coolweather.util;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016-08-25.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address,
                                       final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int len = 0;
                    byte[] buffer = new byte[1024*1024];
                    len = in.read(buffer);
                    out.write(buffer, 0, len);
                    String result = out.toString();
                    in.close();
                    out.close();
//                    BufferedReader reader = new BufferedReader(new
//                            InputStreamReader(in));
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        response.append(line);
//                    }
                    String reStr = "";

                    if (address.indexOf("xml")>0) {
                        if (result != null) {
                            reStr = parseXMLWithPull(result.toString());

                        }
                    }
                    else
                    {
                        reStr= result.toString();
                    }

                    if (listener != null) {
// 回调onFinish()方法
                        listener.onFinish(reStr);
                    }
                } catch (Exception e) {
                    if (listener != null) {
// 回调onError()方法
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


    private static String parseXMLWithPull(String response) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xmlPullParser = factory.newPullParser();
        xmlPullParser.setInput(new StringReader(response));
        int eventType = xmlPullParser.getEventType();
        String city = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String nodeName = xmlPullParser.getName();
            switch (eventType) {
// 开始解析某个结点
                case XmlPullParser.START_TAG: {
                    if ("city".equals(nodeName)) {
                        city = xmlPullParser.nextText();
                    }
                    break;
                }
                case XmlPullParser.END_TAG: {
                    if ("app".equals(nodeName)) {
                        Log.d("httpUtil", "city is " + city);
                    }
                    break;
                }
                default:
                    break;
            }
            eventType = xmlPullParser.next();
        }

        return city;
    }


}

