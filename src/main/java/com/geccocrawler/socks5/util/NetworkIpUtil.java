package com.geccocrawler.socks5.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tanyuanpeng
 * @desciption
 * @date 2020/6/25 4:31 下午
 **/
public class NetworkIpUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetworkIpUtil.class);
    /**
     * 外网ip地址
     */
    private static String publicIp;

    /**
     * 下面url返回地址都包含ip地址，为防止某个url失效，
     * 遍历url获取ip地址，有一个能成功获取就返回
     */
    private static String[] urls = {
            "http://whatismyip.akamai.com",
            "http://icanhazip.com",
            "http://members.3322.org/dyndns/getip",
            "http://checkip.dyndns.com/",
            "http://pv.sohu.com/cityjson",
            "http://ip.taobao.com/service/getIpInfo.php?ip=myip",
            "http://www.ip168.com/json.do?view=myipaddress",
            "http://www.net.cn/static/customercare/yourip.asp",
            "http://ipecho.net/plain",
            "http://myip.dnsomatic.com",
            "http://tnx.nl/ip",
            "http://ifconfig.me"
    };

    /**
     * ip地址的匹配正则表达式
     */
    private static String regEx = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

    private static Pattern pattern = Pattern.compile(regEx);


    /**
     * 获取本机外网地址
     *
     * @return
     */
    public static String getSelfPublicIp() {
        if (publicIp != null && !"".equals(publicIp.trim())) {
            return publicIp;
        }
        for (String url : urls) {
            //http访问url获取带ip的信息
            String result = getUrlResult(url);
            //正则匹配查找ip地址
            Matcher m = pattern.matcher(result);
            while (m.find()) {
                publicIp = m.group();
                return publicIp;
            }
        }
        return null;
    }

    /**
     * http访问url
     */
    private static String getUrlResult(String url) {
        StringBuilder sb = new StringBuilder();
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            return "";
        }
        return sb.toString();
    }

}
