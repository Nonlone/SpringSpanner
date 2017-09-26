package per.nonlone.spanner.simple.aop;

import org.apache.log4j.Logger;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.trans.Trans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.*;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import per.nonlone.spanner.nutz.NutzTransDataSource;
import per.nonlone.spanner.simple.exception.AbortTransactionException;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionRolledbackException;

@Component
public class SimpleSpringTransactionInterceptor implements MethodInterceptor {

    private final static Logger logger = Logger.getLogger(SimpleSpringTransactionInterceptor.class);

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private NutzTransDataSource dataSource;

    public void filter(InterceptorChain chain) throws Throwable {
        final InterceptorChain transChain = chain;
        //默认为Spring 事务管理
        PlatformTransactionManager txManager = this.platformTransactionManager;
        try {
            TransactionAspectSupport.currentTransactionStatus();
        } catch (NoTransactionException nte) {
            txManager = new DataSourceTransactionManager(dataSource);
        }
        Trans.begin();
        new TransactionTemplate(txManager).execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    transChain.doChain();
                } catch (Throwable throwable) {
                    logger.error(throwable);
                    status.setRollbackOnly();
                }
            }
        });
        try {
            Trans.close();
        } catch (Exception e) {
            // Nutz 关闭失败
            throw new TransactionSystemException(String.format("Nutz Trans.close error %s", e.getMessage()), e);
        }
    }
}
