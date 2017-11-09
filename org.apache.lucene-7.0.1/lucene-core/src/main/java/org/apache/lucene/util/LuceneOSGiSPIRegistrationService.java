package org.apache.lucene.util;

import java.util.List;

public interface LuceneOSGiSPIRegistrationService {

	List<Class> get(Class iface);

	public void add(Class iface, Class impl);

	public void remove(Class iface, Class impl);

}
