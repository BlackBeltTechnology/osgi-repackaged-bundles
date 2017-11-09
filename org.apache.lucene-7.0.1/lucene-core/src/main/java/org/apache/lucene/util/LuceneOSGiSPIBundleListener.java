package org.apache.lucene.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component(immediate = true)
public class LuceneOSGiSPIBundleListener implements SynchronousBundleListener {

	private ServiceMediator services;

	private ConfigurationAdmin configurationAdmin;

	private LuceneOSGiSPIRegistrationService luceneOSGiSPIRegistrationService;

	private final Map<Bundle, Map<Class, List<Class>>> bundleLoadedInterfaces = new ConcurrentHashMap<Bundle, Map<Class, List<Class>>>();

	@Override
	public void bundleChanged(BundleEvent bundleEvent) {
		//
		// NOTE:
		// This is synchronous - take care to not block the system !!
		//

		switch (bundleEvent.getType()) {
			case BundleEvent.STARTING:
				try {
					registerBundle(bundleEvent.getBundle());
				} catch (Throwable t) {
					services.error("bundleChanged: Problem loading header information of bundle " + bundleEvent.getBundle().getSymbolicName()
							+ " (" + bundleEvent.getBundle().getBundleId() + ")", t);
				} finally {
				}
				break;
			case BundleEvent.STOPPED:
				try {
					unregisterBundle(bundleEvent.getBundle());
				} catch (Throwable t) {
					services.error("bundleChanged: Problem unloading header information of bundle " + bundleEvent.getBundle().getSymbolicName()
							+ " (" + bundleEvent.getBundle().getBundleId() + ")", t);
				} finally {
				}
				break;
		}

	}

	@Activate
	protected void activate(ComponentContext context) {
		services = new ServiceMediator(context.getBundleContext());
		configurationAdmin = services.getConfigurationAdminService(ServiceMediator.NO_WAIT);
		context.getBundleContext().addBundleListener(this);

		int ignored = 0;
		try {
			Bundle[] bundles = context.getBundleContext().getBundles();
			for (int i = 0; i < bundles.length; i++) {
				Bundle bundle = bundles[i];

				if ((bundle.getState() & (Bundle.ACTIVE)) != 0) {
					try {
						registerBundle(bundle);
					} catch (Throwable t) {
						services.error("bundleChanged: Problem loading header information of bundle " + bundle.getSymbolicName()
								+ " (" + bundle.getBundleId() + ")", t);
					} finally {
					}
				} else {
					ignored++;
				}

				if ((bundle.getState() & (Bundle.ACTIVE)) == 0) {
					try {
						unregisterBundle(bundle);
					} catch (Throwable t) {
						services.error("bundleChanged: Problem unloading header information of bundle " + bundle.getSymbolicName()
								+ " (" + bundle.getBundleId() + ")", t);
					} finally {
					}
				} else {
					ignored++;
				}

			}
			services.debug("Out of " + bundles.length + " bundles, " + ignored
					+ " were not in a suitable state for lucene SPI loading");
		} catch (Throwable t) {
			services.error("activate: Problem while loading lucene SPI", t);
		} finally {
		}
	}

	@Deactivate
	public void deactivate(BundleContext context) throws Exception {
		int ignored = 0;
		try {
			Bundle[] bundles = context.getBundles();
			for (int i = 0; i < bundles.length; i++) {
				Bundle bundle = bundles[i];

				if ((bundle.getState()) == 0) {
					// remove configurations from bundles which are not ACTIVE
					try {
						unregisterBundle(bundle);
					} catch (Throwable t) {
						services.error("Problem unloading header information of bundle " + bundle.getSymbolicName() + " ("
								+ bundle.getBundleId() + ")", t);
					} finally {
					}
				} else {
					ignored++;
				}

			}
			services.debug("Out of " + bundles.length + " bundles, " + ignored
					+ " were not in a suitable state for lucene SPI loading");
		} catch (Throwable t) {
			services.error("activate: Problem while loading lucene SPI loader", t);
		} finally {
		}

		for (Bundle bundle : bundleLoadedInterfaces.keySet()) {
			unregisterBundle(bundle);
		}

		context.removeBundleListener(this);

		if (services != null) {
			services.deactivate();
			services = null;
		}
	}

	public void registerBundle(final Bundle bundle) throws Exception {
		if (bundle.getSymbolicName().startsWith("lucene")) {
			Enumeration<URL> spiEntries =  bundle.findEntries("META-INF/services", "*", false);

			bundleLoadedInterfaces.put(bundle, new ConcurrentHashMap<Class, List<Class>>());

			while (spiEntries != null && spiEntries.hasMoreElements()) {
				URL url = spiEntries.nextElement();
				String className = url.getFile().substring(url.getFile().lastIndexOf("/")+1, url.getFile().length());
				Class iface = bundle.loadClass(className);
				// Loading entry lines
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
								services.info("Registering implementation: "+line+" for interface: "+iface.getName());
								Class impl = bundle.loadClass(line);
								if (!bundleLoadedInterfaces.get(bundle).containsKey(iface)) {
									bundleLoadedInterfaces.get(bundle).put(iface, new CopyOnWriteArrayList<Class>());
								}
								bundleLoadedInterfaces.get(bundle).get(iface).add(impl);
								luceneOSGiSPIRegistrationService.add(iface, impl);
							}
						}
						success = true;
					} finally {
						if (success) {
							IOUtils.close(in);
						} else {
							services.error("Could not load implementations - "+className);
							IOUtils.closeWhileHandlingException(in);
						}
					}
				} catch (IOException ioe) {
					throw new ServiceConfigurationError("Error loading SPI class list from URL: " + url, ioe);
				}
			}
		}
	}

	public void unregisterBundle(final Bundle bundle) throws Exception {
		if (bundle.getSymbolicName().startsWith("lucene")) {
			if (bundleLoadedInterfaces.get(bundle) != null) {
				for (Class iface : bundleLoadedInterfaces.get(bundle).keySet()) {
					if (bundleLoadedInterfaces.get(bundle).get(iface) != null) {
						for (Class impl : bundleLoadedInterfaces.get(bundle).get(iface)) {
							services.info("Unregistering implementation: " + impl.getName() + " for interface: " + iface.getName());
							luceneOSGiSPIRegistrationService.remove(iface, impl);
						}
						bundleLoadedInterfaces.get(bundle).get(iface).clear();
					}
				}
				bundleLoadedInterfaces.remove(bundle);
			}
		}
	}

	@Reference(
			name = "luceneOSGiSPIRegistrationService.service",
			service = LuceneOSGiSPIRegistrationService.class,
			cardinality = ReferenceCardinality.MANDATORY,
			policy = ReferencePolicy.STATIC,
			unbind = "unsetLuceneOSGiSPIRegistrationService"
	)
	protected void setLuceneOSGiSPIRegistrationService(LuceneOSGiSPIRegistrationService service) {
		this.luceneOSGiSPIRegistrationService = service;
	}

	protected void unsetLuceneOSGiSPIRegistrationService(LuceneOSGiSPIRegistrationService service) {
		this.luceneOSGiSPIRegistrationService = null;
	}

}