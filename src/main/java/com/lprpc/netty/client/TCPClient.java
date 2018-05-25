package com.lprpc.netty.client;

import java.util.concurrent.CountDownLatch;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import com.lprpc.netty.unit.RPCRequest;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

@Service
public class TCPClient {

	private static Logger logger = Logger.getLogger(TCPClient.class.getName());

	private Channel clientChannel;
	private static EventLoopGroup group = new NioEventLoopGroup();
	
	@PostConstruct
	public void start() throws Exception {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline cp = ch.pipeline();
					
					cp.addLast(new RPCClientEncode());
					
					cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
					cp.addLast(new RPCClientDecode());
					
					cp.addLast(new TCPClientHandler());
				}
			});
			b.connect("127.0.0.1", 8989).addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					// TODO Auto-generated method stub
					if(future.isSuccess()){
						logger.info("连接成功");
						clientChannel =	future.channel();
						return;
						}
					System.out.println("连接失败");
				}
			});
	}
	public void sendRequest(RPCRequest request) {
		final CountDownLatch latch = new CountDownLatch(1);
		clientChannel.writeAndFlush(request).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				latch.countDown();
				logger.info(future.toString()+"-----");
			}
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
	}
	@PreDestroy
	public void stop() throws Exception {
		clientChannel.close();
	}

}
