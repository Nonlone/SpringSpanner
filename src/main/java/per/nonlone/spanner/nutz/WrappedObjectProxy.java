package per.nonlone.spanner.nutz;

import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.meta.IocObject;
/**
 * ObjectProxy 包装类，持有对应的IocObject，和具体Object（Bean）
 * @author leishunyang
 *
 */
public class WrappedObjectProxy extends ObjectProxy {
	
	private Object obj;
	
	private IocObject iocObject;
	
	
	public WrappedObjectProxy(IocObject iocObject){
		this.iocObject = iocObject;
	}

	public IocObject getIocObject() {
		return iocObject;
	}

	public void setIocObject(IocObject iocObject) {
		this.iocObject = iocObject;
	}

	public Object getObj() {
		return obj;
	}

	@Override
	public ObjectProxy setObj(Object obj) {
		this.obj = obj;
		return this;
	}
}
