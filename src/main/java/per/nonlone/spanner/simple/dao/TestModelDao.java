package per.nonlone.spanner.simple.dao;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import per.nonlone.spanner.simple.model.TestModel;

@IocBean
public class TestModelDao {


    private Class<?> entityClass = TestModel.class;

    @Inject
    private Dao dao;

    public TestModel insert(TestModel testModel) {
        return dao.insert(testModel);
    }

    public Integer delete(TestModel testModel){
        return  dao.delete(testModel);
    }

}
