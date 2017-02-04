package per.nonlone.spanner.simple.aop;

import org.apache.log4j.Logger;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class SimpleInterceptor implements MethodInterceptor{

	private static Logger logger = Logger.getLogger(SimpleInterceptor.class);
	
	public void filter(InterceptorChain chain) throws Throwable {
		logger.info("Nutz Aop start");
		chain.doChain();
		logger.info("Nutz Aop end");
	}

}
