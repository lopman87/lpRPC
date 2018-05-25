package com.lprpc;

import com.lprpc.netty.client.TCPClient;
import com.lprpc.netty.unit.RPCRequest;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.retry.annotation.EnableRetry;
@EnableRetry
@SpringBootApplication
public class SpringBootTcpApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(SpringBootTcpApplication.class, args);
		TCPClient tcpClient = context.getBean(TCPClient.class);
		RPCRequest res = new RPCRequest();
		res.setClassName(RPCRequest.class.getSimpleName());
		res.setMethodName("get");
		res.setRequestId(""+RandomUtils.nextInt(1000, 50055));
		tcpClient.sendRequest(res);
		System.out.println("-------------------------");
	}
}
