package com.lprpc.netty.server;

import com.alibaba.fastjson.JSON;
import com.lprpc.netty.unit.RPCRequest;
import com.lprpc.netty.unit.RPCResponse;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.log4j.Logger;
import org.assertj.core.internal.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.MapUtils;

@Component
@Qualifier("tcpServerHandler")
@ChannelHandler.Sharable
public class TCPServerHandler extends SimpleChannelInboundHandler<RPCRequest> implements ApplicationContextAware {

	private static Logger logger = Logger.getLogger(TCPServerHandler.class.getName());

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info(ctx.channel().remoteAddress());
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.READER_IDLE) {
				logger.info("READER_IDLE");
				// ctx.writeAndFlush("Close connection because idle state.");
				// ctx.close();
			} else if (e.state() == IdleState.WRITER_IDLE) {
				logger.info("WRITER_IDLE");
				// ctx.writeAndFlush("PING");
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(cause.getMessage(), cause);
		ctx.close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		logger.info("inactive");
	}

	protected void channelRead0(ChannelHandlerContext ctx, RPCRequest msg) throws Exception {
		logger.info(JSON.toJSONString(msg));
		if (StringUtils.isBlank(msg.getRequestId())) {
			return;
		}
		try {
			Object service = serviceBean.get(msg.getClassName());
			if (service == null) {
				logger.error("no service:" + msg.getClassName());
				return;
			}
			Object result = MethodUtils.invokeMethod(service, msg.getMethodName(), msg.getParameters(),
					msg.getParameterTypes());
			RPCResponse tempRPCResponse = new RPCResponse();
			tempRPCResponse.setRequestId(msg.getRequestId());
			tempRPCResponse.setResult(result);
			ctx.writeAndFlush(tempRPCResponse);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getMessage(e));
		}
	}

	private static final Map<String, Object> serviceBean = new HashMap<String, Object>();

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// TODO Auto-generated method stub
		Map<String, Object> allLoaclService = arg0.getBeansWithAnnotation(RpcService.class);
		serviceBean.putAll(allLoaclService);
	}
}
