package org.apache.lucene.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * OSGI based helper class for loading SPI classes from OSGi registered services (META-INF files).
 * The LuceneOSGiSPIRegistrationService manages the services, which is managed by LuceneOSGiSPIBundleListener
 * This is a light impl of {@link java.util.ServiceLoader} but is guaranteed to
 * be bug-free regarding classpath order and does not instantiate or initialize
 * the classes found.
 *
 * @lucene.internal
 */
public final class SPIClassIterator<S> implements Iterator<Class<? extends S>> {

	private static Logger log = LoggerFactory.getLogger(SPIClassIterator.class);
	private final Class<S> clazz;
	private final Iterator iterator;

	private static final String META_INF_SERVICES = "META-INF/services/";

	private final ClassLoader loader;
	private final Enumeration<URL> profilesEnum;
	private Iterator<String> linesIterator;

	public static <S> SPIClassIterator<S> get(Class<S> clazz) {
		return SPIClassIterator.get(clazz, null);
	}

	public static <S> SPIClassIterator<S> get(Class<S> clazz, ClassLoader loader) {
		return new SPIClassIterator<S>(clazz, SPIClassIterator.class.getClass().getClassLoader(), getService());
	}

	/** Utility method to check if some class loader is a (grand-)parent of or the same as another one.
	 * This means the child will be able to load all classes from the parent, too. */
	public static boolean isParentClassLoader(final ClassLoader parent, ClassLoader child) {
		while (child != null) {
			if (child == parent) {
				return true;
			}
			child = child.getParent();
		}
		return false;
	}

	private SPIClassIterator(Class<S> clazz, ClassLoader loader, LuceneOSGiSPIRegistrationService service) {
		this.clazz = clazz;
		if (service != null) {
			this.iterator = service.get(clazz).iterator();
			this.loader = loader;
			this.profilesEnum = null;
		} else {
			this.iterator = null;
			try {
				final String fullName = META_INF_SERVICES + clazz.getName();
				this.profilesEnum = (loader == null) ? ClassLoader.getSystemResources(fullName) : loader.getResources(fullName);
			} catch (IOException ioe) {
				throw new ServiceConfigurationError("Error loading SPI profiles for type " + clazz.getName() + " from classpath", ioe);
			}
			this.loader = (loader == null) ? ClassLoader.getSystemClassLoader() : loader;
			this.linesIterator = Collections.<String>emptySet().iterator();
		}
	}

	@Override
	public boolean hasNext() {
		if (iterator != null) {
			return iterator.hasNext();
		} else {
			return linesIterator.hasNext() || loadNextProfile();
		}

	}

	@Override
	public Class<? extends S> next() {
		if (iterator != null) {
			return (Class<? extends S>) iterator.next();
		} else {
			// hasNext() implicitely loads the next profile, so it is essential to call this here!
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			assert linesIterator.hasNext();
			final String c = linesIterator.next();
			try {
				// don't initialize the class (pass false as 2nd parameter):
				return Class.forName(c, false, loader).asSubclass(clazz);
			} catch (ClassNotFoundException cnfe) {
				throw new ServiceConfigurationError(String.format(Locale.ROOT, "A SPI class of type %s with classname %s does not exist, "+
						"please fix the file '%s%1$s' in your classpath.", clazz.getName(), c, META_INF_SERVICES));
			}
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private boolean loadNextProfile() {
		ArrayList<String> lines = null;
		while (profilesEnum.hasMoreElements()) {
			if (lines != null) {
				lines.clear();
			} else {
				lines = new ArrayList<String>();
			}
			final URL url = profilesEnum.nextElement();
			try {
				final InputStream in = url.openStream();
				boolean success = false;
				try {
					final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
					String line;
					while ((line = reader.readLine()) != null) {
						final int pos = line.indexOf('#');
						if (pos >= 0) {
							line = line.substring(0, pos);
						}
						line = line.trim();
						if (line.length() > 0) {
							lines.add(line);
						}
					}
					success = true;
				} finally {
					if (success) {
						IOUtils.close(in);
					} else {
						IOUtils.closeWhileHandlingException(in);
					}
				}
			} catch (IOException ioe) {
				throw new ServiceConfigurationError("Error loading SPI class list from URL: " + url, ioe);
			}
			if (!lines.isEmpty()) {
				this.linesIterator = lines.iterator();
				return true;
			}
		}
		return false;
	}

	private static LuceneOSGiSPIRegistrationService getService()
	{
		// get bundle instance via the OSGi Framework Util class
		try {
			Bundle bundle = FrameworkUtil.getBundle(SPIClassIterator.class);
			if (bundle != null) {
				BundleContext ctx = FrameworkUtil.getBundle(SPIClassIterator.class).getBundleContext();
				ServiceReference serviceReference = ctx.getServiceReference(LuceneOSGiSPIRegistrationService.class.getName());
				Object ret = ctx.getService(serviceReference);
				return LuceneOSGiSPIRegistrationService.class.cast(ret);
			} else {
				return null;
			}
		} catch (Throwable th) {
			log.error("Could not get OSGi servcice", th);
			return null;
		}
	}
}
