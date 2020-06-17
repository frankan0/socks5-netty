package com.geccocrawler.socks5.auth;

public interface PasswordAuth {

	boolean auth(String user, String password);

	void setServerIp(String serverIp);
	
}
