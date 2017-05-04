package per.nonlone.spanner.simple.service;

import org.apache.commons.lang3.RandomUtils;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import per.nonlone.spanner.simple.dao.TestModelDao;
import per.nonlone.spanner.simple.model.TestModel;

import java.util.ArrayList;
import java.util.List;

@IocBean
public class SimpleNutzService {

    @Inject
    private SimpleNutzSubService simpleNutzSubService;

    @Inject
    private TestModelDao testModelDao;

    private interface RandomInterruptable{
        void interrupt();
    }

    @Aop({"simpleInterceptor", "simpleSpringInterceptor"})
    public String process() {
        return "this is Nutz Service hashcode>" + hashCode();
    }

    @Aop("simpleNutTranscationInterceptor")
    public List<TestModel>  insertTestModel(String... values){
        return doInsertTestModel(null,values);
    }

    @Aop("simpleNutTranscationInterceptor")
    public List<TestModel> insertTestModelInterrupted(String... values){
        return doInsertTestModel(new RandomInterruptable() {
            public void interrupt() {
                throw new RuntimeException("Interrupted Transcation");
            }
        },values);
    }

    private List<TestModel> doInsertTestModel(final RandomInterruptable randomInterruptable, String... values){
        int size = values.length;
        int randomSize = RandomUtils.nextInt(0,size);
        final List<TestModel> result = new ArrayList<TestModel>();
        for(int i=0;i<values.length;i++){
            String value = values[i];
            TestModel testModel = new TestModel();
            testModel.setValue(value);
            result.add(testModelDao.insert(testModel));
            if(randomInterruptable!=null&&i==randomSize){
                randomInterruptable.interrupt();
            }
        }
        return result;
    }
}
