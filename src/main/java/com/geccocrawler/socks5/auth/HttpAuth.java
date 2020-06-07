package com.geccocrawler.socks5.auth;

import com.alibaba.fastjson.JSON;
import com.geccocrawler.socks5.bean.BaseResponse;
import com.geccocrawler.socks5.handler.ss5.Socks5PasswordAuthRequestHandler;
import com.geccocrawler.socks5.http.OkHttpClientObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tanyuanpeng
 * @desciption
 * @date 2020/6/7 10:00 上午
 **/
public class HttpAuth implements PasswordAuth {

    private static final Logger logger = LoggerFactory.getLogger(HttpAuth.class);


    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = (OkHttpClient) OkHttpClientObject.CLIENT
            .getClientInstance();

    @Override
    public boolean auth(String user, String password) {
        //用户名是token,password是loginDevice
        Map<String,String> data = new HashMap<>();
        data.put("proxyToken","Xs99928@#009kc11");
        data.put("deviceToken",password);
        //请求体传输json格式的数据
        RequestBody requestBody = RequestBody.create(JSON, com.alibaba.fastjson.JSON.toJSONString(data));
        //创建请求
        Request request = new Request.Builder()
                .url("http://127.0.0.1:8080/proxy/auth")
                .header("User-Agent", "sock5-proxy")
                .addHeader("access-token", user)
                .post(requestBody)
                .build();

        //同步请求
        try {
            Call call = client.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                //解析结果
                ResponseBody body = response.body();
                String string = body.string();
                BaseResponse baseResponse = com.alibaba.fastjson.JSON.parseObject(string, BaseResponse.class);
                if (baseResponse.getCode() == 200){
                    return true;
                }
            }
            logger.info("auth failed ,user:{} deviceToken:{}",user,password);
            return false;
        }catch (Exception e){
            logger.error("request auth api error",e);
        }
        return false;
    }
}
