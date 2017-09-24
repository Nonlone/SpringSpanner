package per.nonlone.spanner.simple.service;

import java.util.ArrayList;
import java.util.List;

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
		return String.format("this is spring service hashcode > %s class %s",hashCode(),getClass());
	}
	
	@Transactional
    public List<TestModel>  insertTestModel(String... values){
        return testModelDao.insertTestModel(values);
    }

    @Transactional
    public List<TestModel> insertTestModelInterrupted(String... values){
        return testModelDao.insertTestModelInterrupted(values);
    }
    
    public List<TestModel>  insertTestModelWithPropagation (String... values){
        return simpleSpringSubService.insertTestModelWithPropagation(values);
    }

    public List<TestModel> insertTestModelInterruptedWithPropagation(String... values){
        return simpleSpringSubService.insertTestModelInterruptedWithPropagation(values);
    }
    
    @Transactional
    public List<TestModel> insertTestModelAllWithPropagation(String... values){
        List<TestModel> resultList = new ArrayList<TestModel>();
        resultList.addAll(simpleSpringSubService.insertTestModelWithPropagation(values));
        testModelDao.insertTestModelInterrupted(values);
        resultList.addAll(simpleSpringSubService.insertTestModelInterruptedWithPropagation(values));
        return resultList;
    }
    
    @Transactional(propagation=Propagation.REQUIRES_NEW )
    public List<TestModel>  insertTestModelThisWithPropagation (String... values){
        return testModelDao.insertTestModel(values);
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW )
    public List<TestModel> insertTestModelInterruptedThisWithPropagation(String... values){
        return testModelDao.insertTestModelInterrupted(values);
    }
    
    
    @Transactional
    public List<TestModel> insertTestModelThisAllWithPropagation(String... values){
        List<TestModel> resultList = new ArrayList<TestModel>();
        resultList.addAll(insertTestModelThisWithPropagation(values));
        resultList.addAll(insertTestModelInterruptedThisWithPropagation(values));
        return resultList;
    }
    
    public List<TestModel> insertTestModelAllWithPropagationSameClassJump(String... values){
        return insertTestModelAllWithPropagation(values);
    }
}
