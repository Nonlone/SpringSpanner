package per.nonlone.spanner.simple.controller;

import org.apache.commons.lang3.RandomStringUtils;
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
		return simpleNutzService.insertTestModel(getValues());
	}

	
	@At("/nutz/insert2")
	@Ok("Json")
	public Object insert2(){
		return simpleNutzService.insertTestModelInterrupted(getValues());
	}
	
	@At("/nutz/insertspring")
	@Ok("Json")
	public Object insertSpring(){
		return simpleSpringService.insertTestModel(getValues());
	}
	
	@At("/nutz/insertspring2")
	@Ok("Json")
	public Object insertSpring2(){
		return simpleSpringService.insertTestModelInterrupted(getValues());
	}
	
	@At("/nutz/insertnutzspring")
	@Ok("Json")
	public Object insertNutzSpring(){
		return simpleNutzService.springInsertTestModel(getValues());
	}

	
	@At("/nutz/insertnutzspring2")
	@Ok("Json")
	public Object insertNutzSpring2(){
		return simpleNutzService.springInsertTestModelInterrupted(getValues());
	}
	
	@At("/nutz/insertnutzspringwithpropagation")
    @Ok("Json")
    public Object insertNutzSpringWithPropagation(){
        return simpleNutzService.springInsertTestModelWithPropagation(getValues());
    }
	
	private String[] getValues(){
		return new String[]{
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4),
				RandomStringUtils.randomAlphanumeric(4)
		};
	}
}
