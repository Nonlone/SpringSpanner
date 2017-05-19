package per.nonlone.spanner.simple.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.nutz.ioc.aop.Aop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import per.nonlone.spanner.simple.dao.TestModelDao;
import per.nonlone.spanner.simple.model.TestModel;

@Service
public class SimpleSpringService {
	
    @Autowired
	private SimpleSpringSubService simpleSpringSubService;
    
    @Autowired
    @Lazy
    private SimpleNutzService simpleNutzService;
	
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
    
    @Transactional(propagation=Propagation.REQUIRES_NEW )
    public List<TestModel>  insertTestModelWithPropagation (String... values){
        return testModelDao.insertTestModel(values);
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW )
    public List<TestModel> insertTestModelInterruptedWithPropagation(String... values){
        return testModelDao.insertTestModelInterrupted(values);
    }
    
    @Transactional
    public List<TestModel> insertTestModelAllWithPropagation(String... values){
        List<TestModel> resultList = new ArrayList<TestModel>();
        resultList.addAll(insertTestModelWithPropagation(values));
        resultList.addAll(insertTestModelInterruptedWithPropagation(values));
        return resultList;
    }
}
