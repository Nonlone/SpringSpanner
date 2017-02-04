package per.nonlone.spanner.simple;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;

import per.nonlone.spanner.nutz.SpringIocProviderSingleton;

@IocBy(type = SpringIocProviderSingleton.class, args = {
//		"*org.nutz.ioc.loader.json.JsonLoader", "/conf/datasource.json",
		"*anno","per.nonlone.spanner.simple" })
@Modules(value = { MainModule.class}, scanPackage = true)
public class MainModule {

}
