package per.nonlone.spanner.simple.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import per.nonlone.spanner.simple.dao.TestModelDao;
import per.nonlone.spanner.simple.model.TestModel;

@Service
public class SimpleSpringService {
	
	@Resource(name="simpleSpringSubService")
	private SimpleSpringSubService simpleSpringSubService;
	
	@Autowired
	@Lazy
	private TestModelDao testModelDao;
	
	public String process(){
		return "this is spring service hashcode>"+hashCode();
	}
	
	@Transactional
    public List<TestModel>  insertTestModel(String... values){
        return testModelDao.insertTestModel(values);
    }

    @Transactional
    public List<TestModel> insertTestModelInterrupted(String... values){
        return testModelDao.insertTestModelInterrupted(values);
    }
}
