package com.lprpc.netty.client;

import java.util.List;

import org.apache.commons.lang3.SerializationUtils;

import com.lprpc.netty.unit.RPCRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
@ChannelHandler.Sharable
public class RPCClientEncode extends MessageToByteEncoder<RPCRequest>{


	@Override
	protected void encode(ChannelHandlerContext ctx, RPCRequest msg, ByteBuf out) throws Exception {
		// TODO Auto-generated method stub
		byte[] data = SerializationUtils.serialize(msg);
		out.writeInt(data.length);
		out.writeBytes( data );
	}
}
