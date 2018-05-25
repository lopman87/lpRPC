package com.lprpc.netty.server;


import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import io.netty.bootstrap.ServerBootstrap;
import org.springframework.stereotype.Service;

import java.util.Map;

import javax.annotation.PreDestroy;

/**
 * Created by Krisztian on 2016. 10. 31..
 */
@Service
public class TCPServer{



    private Channel serverChannel;
    private static final RPCServerDecode DECODER = new RPCServerDecode();
    private static final RPCServerEncode ENCODER = new RPCServerEncode();
    public void start() throws Exception {
    	ServerBootstrap b = new ServerBootstrap();
        b.group( new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						// TODO Auto-generated method stub

				        ChannelPipeline pipeline = socketChannel.pipeline();
				        pipeline.addLast(ENCODER);
				        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536,0,4,0,0));
				        pipeline.addLast(DECODER);
				      
				        pipeline.addLast("tcpServerHandler",serverHandler);
					}

				});
        serverChannel =  b.bind(8989).sync().channel().closeFuture().sync().channel();
    }
    @Autowired
    @Qualifier("tcpServerHandler")
    private TCPServerHandler serverHandler;
    @PreDestroy
    public void stop() throws Exception {
        serverChannel.close();
        serverChannel.parent().close();
    }
}
