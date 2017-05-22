package per.nonlone.spanner.simple.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import per.nonlone.spanner.simple.dao.TestModelDao;
import per.nonlone.spanner.simple.model.TestModel;

@Service
public class SimpleSpringSubService {
    
    @Autowired
    @Lazy
    private TestModelDao testModelDao;
    
    @Transactional(propagation=Propagation.REQUIRES_NEW )
    public List<TestModel>  insertTestModelWithPropagation (String... values){
        return testModelDao.insertTestModel(values);
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW )
    public List<TestModel> insertTestModelInterruptedWithPropagation(String... values){
        return testModelDao.insertTestModelInterrupted(values);
    }

}
