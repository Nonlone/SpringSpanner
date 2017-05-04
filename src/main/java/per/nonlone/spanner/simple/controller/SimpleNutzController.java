package per.nonlone.spanner.simple.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import per.nonlone.spanner.simple.service.SimpleNutzService;
import per.nonlone.spanner.simple.service.SimpleSpringService;

@IocBean
public class SimpleNutzController {

	@Inject
	private SimpleNutzService simpleNutzService;

	@Inject
	private SimpleSpringService simpleSpringService;


	@At("/nutz/index")
	@Ok("json")
	@Aop("simpleInterceptor")
	public String index(){
		String result = simpleNutzService.process();
		return result;
	}

	@At("/nutz/index2")
	@Ok("json")
	public String index2(){
		String result = simpleNutzService.process()+":"+simpleNutzService.hashCode()+" , "+simpleSpringService.process() + ":"+simpleSpringService.hashCode();
		return result;
	}

	@At("/nutz/insert")
	@Ok("Json")
	public Object insert(){
		String[] values = new String[]{
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4)
		};
		return simpleNutzService.insertTestModel(values);
	}


	@At("/nutz/insert2")
	@Ok("Json")
	public Object insert2(){
		String[] values = new String[]{
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4)
		};
		return simpleNutzService.insertTestModelInterrupted(values);
	}
}
