package per.nonlone.spanner.simple.aop;

import org.apache.log4j.Logger;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

/**
 * Created by Thunderbird on 5/5/2017.
 */
@IocBean
public class SimpleNutTranscationInterceptor implements MethodInterceptor{

    private final static Logger logger = Logger.getLogger(SimpleNutTranscationInterceptor.class);

    public class AbortTranscationException extends RuntimeException{

        public AbortTranscationException(Throwable throwable){
            super(throwable);
        }
    }

    public void filter(InterceptorChain chain) throws Throwable {
        final InterceptorChain transChain = chain;
        Trans.exec(new Atom() {
            public void run() {
                try {
                    transChain.doChain();
                } catch (Throwable throwable) {
                    logger.error(throwable);
                    throw new AbortTranscationException(throwable);
                }
            }
        });
    }
}
