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

    private String serverIp;

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = (OkHttpClient) OkHttpClientObject.CLIENT
            .getClientInstance();

    @Override
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    @Override
    public boolean auth(String user, String password) {
        //用户名是token,password是loginDevice
        Map<String,String> data = new HashMap<>();
        data.put("proxyToken","Xs99928@#009kc11");
        data.put("deviceToken",password);
        data.put("serverIp",this.serverIp);
        data.put("userId",user);
        //请求体传输json格式的数据
        RequestBody requestBody = RequestBody.create(JSON, com.alibaba.fastjson.JSON.toJSONString(data));
        //创建请求
        Request request = new Request.Builder()
                .url("http://http://shuttle.twg.life/proxy/auth")
                .header("User-Agent", "sock5-proxy")
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
                }else {
                    logger.error("auth failed .user :{} reason:{}",user,baseResponse.getMsg());
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
