package com.lprpc.netty.server;


import org.apache.commons.lang3.SerializationUtils;

import com.lprpc.netty.unit.RPCResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
@ChannelHandler.Sharable
public class RPCServerEncode extends MessageToByteEncoder<RPCResponse>{


	@Override
	protected void encode(ChannelHandlerContext ctx, RPCResponse msg, ByteBuf out) throws Exception {
		// TODO Auto-generated method stub
		byte[] data = SerializationUtils.serialize(msg);
		out.writeInt(data.length);
		out.writeBytes( data );
	}
}
