package per.nonlone.spanner.simple.service;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class SimpleNutzService {
	
	@Inject
	private SimpleNutzSubService simpleNutzSubService;
	
	@Aop({"simpleInterceptor","simpleSpringInterceptor"})
	public String process(){
		return "this is Nutz Service hashcode>"+hashCode();
	}
}
