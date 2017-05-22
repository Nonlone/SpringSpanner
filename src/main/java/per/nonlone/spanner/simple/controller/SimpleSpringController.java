package per.nonlone.spanner.simple.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
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
	
	@RequestMapping("/spring/indexArray")
	@ResponseBody
	public String[] indexArray(){
		return new String[]{
            RandomStringUtils.randomAlphanumeric(4),
            RandomStringUtils.randomAlphanumeric(4),
            RandomStringUtils.randomAlphanumeric(4),
            RandomStringUtils.randomAlphanumeric(4),
            RandomStringUtils.randomAlphanumeric(4),
            RandomStringUtils.randomAlphanumeric(4)
		};
    }
	
	@SuppressWarnings("serial")
    @RequestMapping("/spring/indexList")
	@ResponseBody
	public List<String> indexList(){
		return new ArrayList<String>(){{
			for(int i=0;i<4;i++){
				this.add(RandomStringUtils.randomAlphanumeric(4));
			}
		}};
    }
	
	
	@RequestMapping("/spring/insert")
    @ResponseBody
    public Object insert(){
        return simpleSpringService.insertTestModel(getValues());
    }
	
	@RequestMapping("/spring/insert2")
    @ResponseBody
    public Object insert2(){
        return simpleSpringService.insertTestModelInterrupted(getValues());
    }
	
	@RequestMapping("/spring/insertWithPropagation")
    @ResponseBody
    public Object insertWithPropagation(){
        return simpleSpringService.insertTestModelWithPropagation(getValues());
    }
    
    @RequestMapping("/spring/insertWithPropagation2")
    @ResponseBody
    public Object insertWithPropagation2(){
        return simpleSpringService.insertTestModelInterruptedWithPropagation(getValues());
    }
	
	@RequestMapping("/spring/insertAllWithPropagation")
    @ResponseBody
    public Object insertAllWithPropagation(){
        return simpleSpringService.insertTestModelAllWithPropagation(getValues());
    }
	
	@RequestMapping("/spring/insertThisAllWithPropagation")
    @ResponseBody
    public Object insertThisAllWithPropagation(){
        return simpleSpringService.insertTestModelThisAllWithPropagation(getValues());
    }
	
	@RequestMapping("/spring/insertThisAllWithPropagationSameClassJump")
    @ResponseBody
    public Object insertThisAllWithPropagatioJump(){
        return simpleSpringService.insertTestModelAllWithPropagationSameClassJump(getValues());
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
