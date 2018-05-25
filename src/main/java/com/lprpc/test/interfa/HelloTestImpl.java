package com.lprpc.test.interfa;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.lprpc.netty.server.RpcService;
@RpcService(value = HelloTest.class)
public class HelloTestImpl implements HelloTest {

	@Override
	public String getTime(String name) {
		// TODO Auto-generated method stub
		return name + " : " + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
	}

}
