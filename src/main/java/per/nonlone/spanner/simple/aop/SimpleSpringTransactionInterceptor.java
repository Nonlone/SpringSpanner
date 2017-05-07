package per.nonlone.spanner.simple.aop;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.trans.Trans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.ManagedTransactionAdapter;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import per.nonlone.spanner.nutz.NutzTransactionAdapter;
import per.nonlone.spanner.simple.exception.AbortTransactionException;

@Component
public class SimpleSpringTransactionInterceptor implements MethodInterceptor {

	private final static Logger logger = Logger.getLogger(SimpleSpringTransactionInterceptor.class);

	@Autowired
	private TransactionTemplate transactionTemplate;

	@PostConstruct
	public void init() {
//		Trans.setup(NutzTransactionAdapter.class);
	}

	public void filter(InterceptorChain chain) throws Throwable {
		final InterceptorChain transChain = chain;
		Trans.begin();
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					transChain.doChain();
				} catch (Throwable throwable) {
					logger.error(throwable);
					status.setRollbackOnly();
//	                    throw new AbortTransactionException(throwable);
				} finally {
					try {
						Trans.close();
					} catch (Exception e) {
						throw new AbortTransactionException(e);
					}
				}
			}
		});
	}

}
