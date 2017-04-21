package per.nonlone.spanner.nutz;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.nutz.aop.interceptor.ioc.TransIocLoader;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.impl.DefaultValueProxyMaker;
import org.nutz.ioc.loader.annotation.AnnotationIocLoader;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.ioc.loader.xml.XmlIocLoader;
import org.nutz.ioc.meta.IocObject;
import org.springframework.beans.FatalBeanException;

public class CrackedComboIocLoader extends ComboIocLoader implements IocObjectGettable {

	private final static Logger logger = Logger.getLogger(CrackedComboIocLoader.class);

	public CrackedComboIocLoader(String... args) throws ClassNotFoundException {
		super(args);
	}

	public CrackedComboIocLoader(IocLoader... loaders) {
		super(loaders);
	}

	/**
	 * 获取Loader配置的IocObject Map
	 */
	public Map<String, IocObject> obtainIocObjectMap(String... args) {
		Map<String, IocObject> resultMap = new LinkedHashMap<String, IocObject>();
		try {
			Field iocLoadersField = ComboIocLoader.class.getDeclaredField("iocLoaders");
			if (null != iocLoadersField) {// 反射获取LoaderList
				iocLoadersField.setAccessible(true);
				List<IocLoader> loaderList = (List<IocLoader>) iocLoadersField.get(this);
				for (IocLoader iocLoader : loaderList) {
					Field mapField = null;
					boolean isIocObjectMap = true;
					if (iocLoader instanceof AnnotationIocLoader) {
						mapField = AnnotationIocLoader.class.getDeclaredField("map");
					} else if (iocLoader instanceof XmlIocLoader) {
						mapField = XmlIocLoader.class.getDeclaredField("iocmap");
					} else if (iocLoader instanceof MapLoader || iocLoader instanceof JsonLoader) {
						// Map载入器，IocObject 以Map的形式保存，需要转换出来
						mapField = iocLoader.getClass().getDeclaredField("map");
						isIocObjectMap = false;
					} else if (iocLoader instanceof TransIocLoader) {
						// 事务载入器，继承JsonLoader，传入事务控制Bean
						Field proxyField = TransIocLoader.class.getDeclaredField("proxy");
						mapField = proxyField.getType().getDeclaredField("map");
						isIocObjectMap = false;
					}
					if (null == mapField)
						continue;
					mapField.setAccessible(true);
					if (isIocObjectMap) {
						// 获取IocObject 的 Map
						Map<String, IocObject> map = (Map<String, IocObject>) mapField.get(iocLoader);
						if (null != map && !map.isEmpty()) {
							for (IocObject iocObject : map.values()) {
								if (!resultMap.containsKey(iocObject.getClass().getName())) {
									resultMap.put(iocObject.getClass().getName(), iocObject);
								} else {
									logger.warn(String.format("Nut Found Duplicate beanName=%s, pls check you config!", iocObject.getClass().getName()));
								}
							}
						}
					} else {
						Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) mapField.get(iocLoader);
						IocLoading iocLoading = new IocLoading(new HashSet<String>(Arrays.asList(new DefaultValueProxyMaker().supportedTypes())));
						if (null != map && !map.isEmpty()) {
							Map<String, IocObject> tmpResultMap = new HashMap<String, IocObject>();
							for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
								tmpResultMap = createIocObjectMapFromMap(iocLoading, entry, map, tmpResultMap);
							}
							for(IocObject iocObject:tmpResultMap.values()){
								if (!resultMap.containsKey(iocObject.getClass().getName())) {
									resultMap.put(iocObject.getClass().getName(), iocObject);
								} else {
									logger.warn(String.format("Nut Found Duplicate beanName=%s, pls check you config!", iocObject.getClass().getName()));
								}
							}
						}
					}
				}
			}
		} catch (NoSuchFieldException e) {
			throw new FatalBeanException(String.format("refect ComboIocLoader get field  error message<%s>", e.getMessage()), e);
		} catch (SecurityException e) {
			throw new FatalBeanException(String.format("refect ComboIocLoader get field error message<%s>", e.getMessage()), e);
		} catch (IllegalArgumentException e) {
			throw new FatalBeanException(String.format("refect ComboIocLoader get map error message<%s>", e.getMessage()), e);
		} catch (IllegalAccessException e) {
			throw new FatalBeanException(String.format("refect ComboIocLoader get map error message<%s>", e.getMessage()), e);
		}
		return resultMap;
	}

	/**
	 * 检查继承关系,如果发现循环继承,或其他错误的继承关系,则抛出FatalBeanException
	 * 
	 * @param name
	 *            beanId
	 * @throws ObjectLoadException
	 *             if Inheritance errors or Inheritance cycle founded.
	 */
	private void checkParents(Map<String, Map<String, Object>> map, String name) throws FatalBeanException {
		Set<String> beanNameSet = new HashSet<String>();
		beanNameSet.add(name);
		String currentParent = map.get(name).get("parent").toString();
		while (currentParent != null) {
			if (beanNameSet.contains(currentParent))
				throw new FatalBeanException(String.format("Nutz Bean Map Configure Inheritance cycle! beanName '%s'", name));
			beanNameSet.add(currentParent);
			Object obj = map.get(currentParent);
			if (obj != null && obj instanceof Map)
				currentParent = (String) ((Map<String, Object>) obj).get("parent");
			else
				new FatalBeanException(String.format("Nutz Bean Map Configure Inheritance errors! beanName '%s'", name));
		}
	}

	/**
	 * 递归父类创建IocObject
	 * 
	 * @param loading
	 *            Map到IocObject转换器
	 * @param entry
	 *            转换Map目标Entry<String,Map<String,Object>> Key为BeanName
	 * @param map
	 *            所有的配置合集
	 * @param resultMap
	 *            结果集，递归回传
	 * @return
	 */
	private Map<String, IocObject> createIocObjectMapFromMap(IocLoading loading, Entry<String, Map<String, Object>> entry, Map<String, Map<String, Object>> map, Map<String, IocObject> resultMap) {
		if (null == resultMap)
			resultMap = new HashMap<String, IocObject>();
		if (null != map && !map.isEmpty()) {
			String name = entry.getKey();
			Map<String, Object> iocMap = entry.getValue();
			logger.debug("Loading define for name=" + name);
			// If has parent
			Object parent = iocMap.get("parent");
			if (null != iocMap.get("parent")) {
				checkParents(map, name);
				if (!map.containsKey(parent.toString())) {
					// 引用父类依赖不存在定义
					throw new FatalBeanException(String.format("Nutz Bean Parent Object '%s' without define in creating Nutz Bean Object '%s'", parent.toString(), name));
				}
				if (!resultMap.containsKey(parent.toString())) {
					// 不包含父类
					Entry<String, Map<String, Object>> parentEntry = new AbstractMap.SimpleEntry<String, Map<String, Object>>(parent.toString(), map.get(parent.toString()));
					resultMap.putAll(createIocObjectMapFromMap(loading, parentEntry, map, resultMap));
				}
			}
			try {
				IocObject iocObject = loading.map2iobj(iocMap);
				resultMap.put(name, iocObject);
			} catch (ObjectLoadException e) {
				throw new FatalBeanException(String.format("Nutz Bean Object '%s' error in converting IocObject", name));
			}
		}
		return resultMap;
	}
}
