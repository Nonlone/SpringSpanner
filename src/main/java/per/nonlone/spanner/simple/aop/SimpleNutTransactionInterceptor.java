package per.nonlone.spanner.simple.aop;

import org.apache.log4j.Logger;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import per.nonlone.spanner.simple.exception.AbortTransactionException;

/**
 * Created by Thunderbird on 5/5/2017.
 */
@IocBean
public class SimpleNutTransactionInterceptor implements MethodInterceptor{

    private final static Logger logger = Logger.getLogger(SimpleNutTransactionInterceptor.class);

    public void filter(InterceptorChain chain) throws Throwable {
        final InterceptorChain transChain = chain;
        Trans.exec(new Atom() {
            public void run() {
                try {
                    transChain.doChain();
                } catch (Throwable throwable) {
                    logger.error(throwable);
                    throw new AbortTransactionException(throwable);
                }
            }
        });
    }
}
