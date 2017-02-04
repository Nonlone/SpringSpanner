package per.nonlone.spanner.simple.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import per.nonlone.spanner.simple.service.SimpleNutzService;
import per.nonlone.spanner.simple.service.SimpleSpringService;

@Controller
public class SimpleSpringController {
	
	@Autowired
	private SimpleSpringService simpleSpringService;
	
	@Lazy
	@Autowired
	private SimpleNutzService simpleNutzSerivce;
	
	@RequestMapping("/spring/index")
	@ResponseBody
	public Object index(){
		return simpleSpringService.process();
	}
	
	@RequestMapping("/spring/index2")
	@ResponseBody
	public Object index2(){
		return simpleNutzSerivce.process()+":"+simpleNutzSerivce.hashCode()+" , "+simpleSpringService.process() + ":"+simpleSpringService.hashCode();
	}

}
