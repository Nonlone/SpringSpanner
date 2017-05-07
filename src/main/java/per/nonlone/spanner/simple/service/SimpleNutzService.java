package per.nonlone.spanner.simple.service;

import java.util.List;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import per.nonlone.spanner.simple.dao.TestModelDao;
import per.nonlone.spanner.simple.model.TestModel;

@IocBean
public class SimpleNutzService {

    @Inject
    private SimpleNutzSubService simpleNutzSubService;

    @Inject
    private TestModelDao testModelDao;


    @Aop({"simpleInterceptor", "simpleSpringInterceptor"})
    public String process() {
        return "this is Nutz Service hashcode>" + hashCode();
    }

    @Aop("simpleNutTransactionInterceptor")
    public List<TestModel>  insertTestModel(String... values){
        return testModelDao.insertTestModel(values);
    }

    @Aop("simpleNutTransactionInterceptor")
    public List<TestModel> insertTestModelInterrupted(String... values){
        return testModelDao.insertTestModelInterrupted(values);
    }
    
    @Aop("simpleSpringTransactionInterceptor")
    public List<TestModel>  springInsertTestModel(String... values){
        return testModelDao.insertTestModel(values);
    }

    @Aop("simpleSpringTransactionInterceptor")
    public List<TestModel> springInsertTestModelInterrupted(String... values){
        return testModelDao.insertTestModelInterrupted(values);
    }
}
