package com.myfx;

import com.alibaba.fastjson.JSONArray;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import com.alibaba.fastjson.JSON;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.http.message.BasicNameValuePair;

public class Controller {
    @FXML
    private Button btn_1;
    @FXML
    private TextArea text1;

    @FXML
    private TextArea text2;

    private static final int SUCCESS_CODE = 200;
    private static final String url="https://fanyi-api.baidu.com/api/trans/vip/translate";
    private static final String salt = "MyFirst";
    private static final String appid = "";//填入你自己的id
    private static final String secretKey  = "";//密钥
    private List<NameValuePair> list = new ArrayList<>();



    //英译汉
    public void EnToZh(ActionEvent event) throws Exception {

        String q = text1.getText();
        if(q!=null){
            //System.out.println(text1.getText());
            //text2.setText("OK!");
            //计算sign
            //拼接appid=2015063000000001+q=apple+salt=1435660288+密钥=12345678
            String s = appid+q+salt+secretKey;
            System.out.println(s);
            String sign = stringToMD5(s);
            System.out.println(sign);

            //list.add(new BasicNameValuePair("q", URLEncoder.encode(q, "utf-8")));
            list.add(new BasicNameValuePair("q", q));
            list.add(new BasicNameValuePair("from","auto"));
            list.add(new BasicNameValuePair("to","zh"));
            list.add(new BasicNameValuePair("appid",appid));
            list.add(new BasicNameValuePair("salt",salt));
            list.add(new BasicNameValuePair("sign",sign));

            //发送请求
            String response = sendPost(url,list);
            System.out.println(response);
            //json解析
            String result = jsonResult(response);
            System.out.println(result);
            text2.setText(result);
        }
    }

    //汉译英
    public void ZhToEn(ActionEvent event) throws Exception {

        String q = text2.getText();
        if(q!=null){
            //System.out.println(text1.getText());
            //text2.setText("OK!");
            //计算sign
            //拼接appid=2015063000000001+q=apple+salt=1435660288+密钥=12345678
            String s = appid+q+salt+secretKey;
            System.out.println(s);
            String sign = stringToMD5(s);
            System.out.println(sign);

            //list.add(new BasicNameValuePair("q", URLEncoder.encode(q, "utf-8")));
            list.add(new BasicNameValuePair("q", q));
            list.add(new BasicNameValuePair("from","auto"));
            list.add(new BasicNameValuePair("to","en"));
            list.add(new BasicNameValuePair("appid",appid));
            list.add(new BasicNameValuePair("salt",salt));
            list.add(new BasicNameValuePair("sign",sign));

            //发送请求
            String response = sendPost(url,list);
            System.out.println(response);
            //json解析
            String result = jsonResult(response);
            System.out.println(result);
            text1.setText(result);
        }
    }

    // http请求
    private String sendPost(String url, List<NameValuePair> nameValuePairList) throws Exception{

        JSONObject jsonObject = null;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            //创建一个httpclient对象
            client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);

            //包装成一个Entity对象
            StringEntity entity = new UrlEncodedFormEntity(nameValuePairList, "UTF-8");

            //设置请求的内容
            post.setEntity(entity);

            //设置请求的报文头部的编码
            post.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
            post.setHeader(new BasicHeader("Accept", "text/plain;charset=utf-8"));

            //执行post请求
            response = client.execute(post);

            //获取响应码
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode){

                //通过EntityUitls获取返回内容
                return EntityUtils.toString(response.getEntity(),"UTF-8");

            }else{
                return("{\"trans_result\":[{\"dst\":\"POST请求失败2\"}]");
            }
        }catch (Exception e){
           return("{\"trans_result\":[{\"dst\":\"POST请求失败1\"}]");
        }finally {
            assert response != null;
            response.close();
            client.close();
        }
    }

    // md5实现方法
    private static String stringToMD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return Hex.encodeHexString(md.digest(data.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    // json解析实现
    private String jsonResult(String response){
        String result="";
        JSONObject outJson = JSONObject.parseObject(response);
        Set<String> jsonSet = outJson.keySet();
        //通过迭代器可以取到外部json的key
        for (String json : jsonSet) {
            if("trans_result".equals(json)) {
                //取得内部json字符串
                String string = outJson.getString(json);
                JSONArray objects = JSON.parseArray(string);
                System.out.println(objects);
                for (Object o : objects) {
                    JSONObject object = (JSONObject) o;
                    String pick = JSONObject.toJSONString(object.getString("dst")).replaceAll("\"", "");
                    if(pick.equals("null")) continue;
                    result += pick;
                }
                return result;

            }
        }
        return "次数不够啦！";
    }


}
