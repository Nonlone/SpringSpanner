package per.nonlone.spanner.nutz;

import org.nutz.trans.NutTransaction;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.jta.ManagedTransactionAdapter;

import per.nonlone.spanner.simple.exception.AbortTransactionException;

@Component
public class NutzTransactionAdapter extends NutTransaction implements ApplicationContextAware,FactoryBean<NutzTransactionAdapter>{
	
//	@Autowired
	private ManagedTransactionAdapter managedTransactionAdapter;
	
	private static ApplicationContext applicationContext;

	private NutzTransactionAdapter() {	}

	@Override
	protected void commit() {
		if(managedTransactionAdapter!=null){
			try{
				managedTransactionAdapter.commit();
			}catch (Exception e) {
				throw new AbortTransactionException(e);
			}
		}else{
			super.commit();
		}
	}

	@Override
	protected void rollback() {
		if(managedTransactionAdapter!=null){
			try{
				managedTransactionAdapter.rollback();
			}catch (Exception e) {
				throw new AbortTransactionException(e);
			}
		}else{
			super.rollback();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	/**
	 * 仅限于Nutz的Mirror方法调用，其他调用不保证获取实
	 * @return
	 */
	public static NutTransaction getInstance(){
		return applicationContext.getBean(NutTransaction.class);
	}

	@Override
	public NutzTransactionAdapter getObject() throws Exception {
		return new NutzTransactionAdapter();
	}

	@Override
	public Class<?> getObjectType() {
		return NutzTransactionAdapter.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
