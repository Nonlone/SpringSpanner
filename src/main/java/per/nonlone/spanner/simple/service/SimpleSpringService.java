package per.nonlone.spanner.simple.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class SimpleSpringService {
	
	@Resource(name="simpleSpringSubService")
	private SimpleSpringSubService simpleSpringSubService;
	
	public String process(){
		return "this is spring service hashcode>"+hashCode();
	}
}
