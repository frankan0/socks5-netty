package com.geccocrawler.socks5.test;

import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpProxyClient {
	
	public static void main(String[] args) throws Exception {
		final String user = "test111";
		final String password = "11111111";
		
		Proxy proxyTest = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("49.233.26.227", 1080));
		
		java.net.Authenticator.setDefault(new java.net.Authenticator()
		{
			private PasswordAuthentication authentication = new PasswordAuthentication(user, password.toCharArray());

			@Override
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return authentication;
			}
		});

		
		OkHttpClient client = new OkHttpClient.Builder().proxy(proxyTest).build();
		Request request = new Request.Builder().url("https://www.skebooks.com/").build();
		Response response = client.newCall(request).execute();
		System.out.println(response.code());
		System.out.println(response.body().string());
		
		client.dispatcher().executorService().shutdown();
		client.connectionPool().evictAll();
	}

}
