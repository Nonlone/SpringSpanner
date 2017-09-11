package per.nonlone.spanner.aop;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import per.nonlone.spanner.simple.service.SimpleSpringService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/app*.xml")
public class SpringTest {

    @Autowired
    private SimpleSpringService simpleSpringService;

    @Test
    public void testAop(){
        System.out.println(simpleSpringService.getClass());
        simpleSpringService.process();
    }

}
