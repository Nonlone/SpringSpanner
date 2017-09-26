package per.nonlone.spanner.simple.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import per.nonlone.spanner.simple.model.TestModel;

import java.util.List;

@Service
public class SpringAopableSimpleNutzService {

    @Autowired
    @Lazy
    private SimpleNutzService simpleNutzService;


    @Transactional
    public List<TestModel> nutzInsert(String... value){
        return simpleNutzService.springInsertTestModel(value);
    }

    @Transactional
    public List<TestModel> nutzInsertWithInterrupted(String... value){
        return simpleNutzService.springInsertTestModelInterrupted(value);
    }
}
