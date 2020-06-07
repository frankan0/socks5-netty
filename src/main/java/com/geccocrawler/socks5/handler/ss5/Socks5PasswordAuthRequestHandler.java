package com.geccocrawler.socks5.handler.ss5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geccocrawler.socks5.auth.PasswordAuth;
import com.geccocrawler.socks5.handler.ProxyChannelTrafficShapingHandler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;

import java.net.InetSocketAddress;

public class Socks5PasswordAuthRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5PasswordAuthRequest> {
	
	private static final Logger logger = LoggerFactory.getLogger(Socks5PasswordAuthRequestHandler.class);

	private PasswordAuth passwordAuth;
	
	public Socks5PasswordAuthRequestHandler(PasswordAuth passwordAuth) {
		this.passwordAuth = passwordAuth;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5PasswordAuthRequest msg) throws Exception {

		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIP = insocket.getAddress().getHostAddress();
		logger.info("用户名:{}  密码:{}  IP:{}",msg.username(), msg.password(),clientIP);
		if(passwordAuth.auth(msg.username(), msg.password())) {
			ProxyChannelTrafficShapingHandler.username(ctx, msg.username());
			Socks5PasswordAuthResponse passwordAuthResponse = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS);
			ctx.writeAndFlush(passwordAuthResponse);
		} else {
			ProxyChannelTrafficShapingHandler.username(ctx, "unauthorized");
			Socks5PasswordAuthResponse passwordAuthResponse = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.FAILURE);
			//发送鉴权失败消息，完成后关闭channel
			ctx.writeAndFlush(passwordAuthResponse).addListener(ChannelFutureListener.CLOSE);
		}
	}

}
