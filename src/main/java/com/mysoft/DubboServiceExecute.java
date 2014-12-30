package com.mysoft;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.thoughtworks.xstream.XStream;

public class DubboServiceExecute {

	// private static final Logger logger =
	// LoggerFactory.getLogger(DubboServiceExecute.class);

	static ApplicationContext ctx = null;
	static Configure conf = null;
	static List<Configure> configures = null;
	static {
		init();
	}

	private static Object invoke(Configure _conf) throws Exception {
		Object result = null;
		Class<?> dubboService = Class.forName(_conf.getInterfaceName());

		ApplicationConfig application = new ApplicationConfig();
		application.setName("InvokeTesting");

		ReferenceConfig<Object> reference = new ReferenceConfig<Object>();
		reference.setApplication(application);
		reference.setInterface(dubboService);
		reference.setUrl("dubbo://" + _conf.getProviderAddress() + "/" + _conf.getInterfaceName());
		reference.setConnections(1);
		reference.setTimeout(1000);

		Object ds = reference.get();
		result = _conf.getMethod().invoke(ds, _conf.getMethodInvokeArgs());
		return result;
	}

	public static void printResult() throws Exception {
		
		XStream xStream = new XStream();
		
		StringBuffer results = new StringBuffer("<root>\n\n");
		int i = 1;
		for (Configure c : configures) {
			if (c.getMethod() == null) {
				i++;
				continue;
			}
			String title = "<!-- 序号：" + i++ + ",beanId:" + c.getBeanId() + ",接口:" + c.getInterfaceName() + ",方法：" + c.getMethodName() + " -->\n";
			Object invokeResult = null;
			try {
				invokeResult = invoke(c);
			} catch (Exception e) {
				System.err.println(title + "调用异常！将被忽略，异常信息:\n");
				e.printStackTrace();
				continue;
			}
			String result = xStream.toXML(invokeResult);
			results.append(title + "<结果>\n" + result + "\n</结果>" + "\n\n"); 
			
		}
		results.append("\n</root>");
		
		//xStream.toXML(results, new FileOutputStream(new File("调用结果.xml")));
		FileOutputStream fos = new FileOutputStream(new File("调用结果.xml"));
		fos.write(results.toString().getBytes());
		fos.close();
		System.out.println("\n" + results.toString());
	}

	private static void init() {
		try {
			ctx = new FileSystemXmlApplicationContext("spring.xml");
			conf = (Configure) ctx.getBean("configure");
			
			configures = conf.getConfigures();
			if (configures == null || configures.size() < 1) {
				System.err.println("配置错误:未检测到接口配置--调用结束........");
				System.exit(-1);
			}

			checkConfigure();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void checkConfigure() throws Exception {
		String msg = "\n\n序号{0},beanId:{1},配置错误:{2}--将被忽略.....";
		int k = 1; 
		for (Configure cnf : configures) {

			if (StringUtils.isBlank(cnf.getProviderAddress())) {
				System.err.println(MessageFormat.format(msg, k,cnf.getBeanId(),"接口提供者地址为空"));
				continue;
			}
			if (StringUtils.isBlank(cnf.getInterfaceName())) {
				System.err.println(MessageFormat.format(msg, k,cnf.getBeanId(),"接口名为空" ));
				continue;
			}
			if (StringUtils.isBlank(cnf.getMethodName())) {
				System.err.println(MessageFormat.format(msg, k,cnf.getBeanId(),"方法名不能为空"));
				continue;
			}
			
			Class dubboClz = Class.forName(cnf.getInterfaceName());

			for (Method m : dubboClz.getMethods()) {
				Class[] curClz = m.getParameterTypes();
				if (m.getName().equals(cnf.getMethodName()) && curClz != null) {
					if (curClz.length != cnf.getArgsClz().length) {
						continue;
					}
					boolean getM = true;
					for (int i = 0; i < curClz.length; i++) {
						if (!curClz[i].isAssignableFrom(cnf.getArgsClz()[i])) {
							// 如果接口参数是基本类型则再比较对应的包装类型是否一致
							if (curClz[i].isPrimitive() && curClz[i] == cnf.getArgsClz()[i].getDeclaredField("TYPE").get(null)) {
								continue;
							}

							getM = false;
							break;
						}
					}

					if (getM) {
						cnf.setMethod(m);
						break;
					}
				}
				if (m.getName().equals(cnf.getMethodName()) && curClz == null) {
					cnf.setMethod(m);
					break;
				}
			}
			if (cnf.getMethod() == null) {
				System.err.println(MessageFormat.format(msg, k,cnf.getBeanId(),"没有找到相应方法或者方法参数列表与接口参数列表不一致"));
			}
			k++;
		}
	}
}
