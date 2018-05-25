package com.lprpc.netty.client;

import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.lprpc.netty.unit.RPCResponse;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Component
@Qualifier("tcpClientHandler")
@ChannelHandler.Sharable
public class TCPClientHandler extends SimpleChannelInboundHandler<RPCResponse> {
	private static Logger logger = Logger.getLogger(TCPClientHandler.class.getName());

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		this.remotePeer = this.clientChannel.remoteAddress();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		this.clientChannel = ctx.channel();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("client caught exception", cause);
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RPCResponse msg) throws Exception {
		// TODO Auto-generated method stub
		logger.info(JSON.toJSONString(msg));
	}

	

	private volatile Channel clientChannel;
	private SocketAddress remotePeer;

	public Channel getChannel() {
		return clientChannel;
	}

	public SocketAddress getRemotePeer() {
		return remotePeer;
	}

}
