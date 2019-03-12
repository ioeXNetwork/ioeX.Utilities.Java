package org.ioexnetwork.ioex;


import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.ioexnetwork.common.ErrorCode;

import java.io.*;
import java.nio.charset.Charset;

/**
 * 
 */
public class HttpRequestUtil {
    public String doGet(String url){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try{
            //通過默認配置創建一個httpClient實例
            httpClient = HttpClients.createDefault();
            //創建httpGet遠程連接實例
            HttpGet httpGet = new HttpGet(url);
            //httpGet.addHeader("Connection", "keep-alive");
            //設置請求頭信息
            httpGet.addHeader("Accept", "application/json");
            //配置請求參數
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(35000) //設置連接主機服務超時時間
                    .setConnectionRequestTimeout(35000)//設置請求超時時間
                    .setSocketTimeout(60000)//設置數據讀取超時時間
                    .build();
            //為httpGet實例設置配置
            httpGet.setConfig(requestConfig);
            //執行get請求得到返回對象
            response = httpClient.execute(httpGet);
            //通過返回對象獲取返回數據
            HttpEntity entity = response.getEntity();
            //通過EntityUtils中的toString方法將結果轉換為字符串，後續根據需要處理對應的reponse code
            result = EntityUtils.toString(entity);
            System.out.println(result);

        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //關閉資源
            if(response != null){
                try {
                    response.close();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
            if(httpClient != null){
                try{
                    httpClient.close();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String doPost(String url , JSONObject param,String user ,String passowrd){
        //創建httpClient對象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = "";
        try{

            String auth = user + ":" + passowrd;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);

            //創建http請求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization", authHeader);
            httpPost.addHeader("Content-Type", "application/json");
            //創建請求內容
            StringEntity entity = new StringEntity(param.toString());
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(),"utf-8");
            System.out.println(result);
        }catch (Exception e){
            return ErrorCode.exceptionError(e.toString());
        }finally {
            //關閉資源
            if(response != null){
                try {
                    response.close();
                }catch (IOException ioe){
                    return ErrorCode.exceptionError(ioe.toString());
                }
            }
            if(httpClient != null){
                try{
                    httpClient.close();
                }catch (IOException ioe){
                    return ErrorCode.exceptionError(ioe.toString());
                }
            }
        }
        return result;
    }
}
