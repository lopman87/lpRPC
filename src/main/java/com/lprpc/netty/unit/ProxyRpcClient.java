package com.lprpc.netty.unit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import com.dianping.orderdish.idp.biz.request.ReSendJobRequest;


public class ProxyRpcClient {

	@SuppressWarnings("unchecked")
	public <T> T create(final Class<T> service) {
		if (!service.isInterface()) {
			throw new IllegalArgumentException("service must be an interface");
		}

		return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
				new MyInvocationHandler<T>(service));
	}

	static class MyInvocationHandler<T> implements InvocationHandler {

		private Class<T> clazz;

		public MyInvocationHandler(Class<T> ary) {
			this.clazz = ary;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (Object.class == method.getDeclaringClass()) {
	            String name = method.getName();
	            if ("equals".equals(name)) {
	                return proxy == args[0];
	            } else if ("hashCode".equals(name)) {
	                return System.identityHashCode(proxy);
	            } else if ("toString".equals(name)) {
	                return proxy.getClass().getName() + "@" +
	                        Integer.toHexString(System.identityHashCode(proxy)) +
	                        ", with InvocationHandler " + this;
	            } else {
	                throw new IllegalStateException(String.valueOf(method));
	            }
	        }
			
			RPCRequest request = new RPCRequest();
	        request.setRequestId(UUID.randomUUID().toString());
	        request.setClassName(method.getDeclaringClass().getName());
	        request.setMethodName(method.getName());
	        request.setParameterTypes(method.getParameterTypes());
	        request.setParameters(args);
	        // Debug
			return method.invoke(clazz, args);
		}
	}
}
