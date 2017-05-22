package per.nonlone.spanner.simple.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import per.nonlone.spanner.simple.model.TestModel;

@IocBean
public class TestModelDao {

    private static final Logger logger = Logger.getLogger(TestModelDao.class);

    private Class<?> entityClass = TestModel.class;

    @Inject
    private Dao dao;
    
    public interface RandomInterruptable{
        void interrupt();
    }

    public TestModel insert(TestModel testModel) {
        return dao.insert(testModel);
    }

    public Integer delete(TestModel testModel){
        return  dao.delete(testModel);
    }
    
    public List<TestModel>  insertTestModel(String... values){
        return doInsertTestModel(null,values);
    }

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
        boolean interruptuedFlag = false;
        if(randomInterruptable!=null)
            interruptuedFlag = true;
        final List<TestModel> result = new ArrayList<TestModel>();
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            TestModel testModel = new TestModel();
            testModel.setValue(value);
            result.add(insert(testModel));
            if (interruptuedFlag && i == randomSize) {
                logger.info(String.format("start interrupt excepiton randomSize<%d>",randomSize));
                randomInterruptable.interrupt();
            }
        }
        return result;
    }

}
