package com.mysoft;

import java.lang.reflect.Method;
import java.util.List;


@SuppressWarnings("rawtypes")
public class Configure {

	private String beanId;
	private List<Configure> configures;
	private Method method;
	private String providerAddress;
	private String interfaceName;
	private String methodName;
	private Object[] methodInvokeArgs;
	private Class[] argsClz = new Class []{};

	public Class[] getArgsClz() {
		return argsClz;
	}

	public Object[] getMethodInvokeArgs() {
		return methodInvokeArgs;
	}

	public void setMethodInvokeArgs(Object[] methodInvokeArgs) {
		this.methodInvokeArgs = methodInvokeArgs;

		argsClz = new Class[methodInvokeArgs.length];
		for (int i = 0; i < methodInvokeArgs.length; i++) {
			
			argsClz[i] = methodInvokeArgs[i].getClass();
		}
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		
		this.methodName = methodName;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		
		this.interfaceName = interfaceName;
	}

	public String getProviderAddress() {
		return providerAddress;
	}

	public void setProviderAddress(String providerAddress) {
		this.providerAddress = providerAddress;
	}

	public List<Configure> getConfigures() {
		return configures;
	}

	public void setConfigures(List<Configure> configures) {
		this.configures = configures;
	}

	
	public static void main(String[] args) {
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}
}
