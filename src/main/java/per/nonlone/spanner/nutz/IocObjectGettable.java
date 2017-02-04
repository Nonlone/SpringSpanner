package per.nonlone.spanner.nutz;

import java.util.Map;

import org.nutz.ioc.meta.IocObject;

/**
 * 获取IocObject Map
 * @author leishunyang
 *
 */
public interface IocObjectGettable {
	
	Map<String,IocObject> obtainIocObjectMap(String... args);
}
