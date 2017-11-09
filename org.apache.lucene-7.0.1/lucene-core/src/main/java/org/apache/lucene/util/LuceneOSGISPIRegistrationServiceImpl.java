package org.apache.lucene.util;

import org.osgi.service.component.annotations.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component(immediate = true)
public class LuceneOSGISPIRegistrationServiceImpl implements LuceneOSGiSPIRegistrationService {

	Map<Class, List<Class>> map = new ConcurrentHashMap<Class, List<Class>>();

	@Override
	public List<Class> get(Class iface) {
		return map.get(iface);
	}

	public void remove(Class iface, Class impl) {
		map.get(iface).remove(impl);
		if (map.get(iface).size() == 0) {
			map.remove(iface);
		}
	}

	public void add(Class iface, Class impl) {
		if (!map.containsKey(iface)) {
			map.put(iface, new CopyOnWriteArrayList<Class>());
		}
		map.get(iface).add(impl);
	}
}
