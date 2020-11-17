package com.red5pro.group;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.red5.server.api.scope.IGlobalScope;
import org.red5.server.plugin.PluginRegistry;
import org.red5.server.plugin.Red5Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.cluster.streams.Provision;
import com.red5pro.cluster.streams.ProvisionIndex;

/**
 * Bootstrap group experiences. <i>Previously named Cores</i>
 * 
 * @author Andy Shaules
 * @author Paul Gregoire
 *
 */
public class GroupRegistry {

	private static final Logger log = LoggerFactory.getLogger(GroupRegistry.class);

	// provision resolver service as a provision index to perform resolving
	private static ProvisionIndex provisionResolverSvc;

	// registered groups
	public static Map<String, IGroupCore> groups = new ConcurrentHashMap<>();

	static {
		// get a provision resolver service from the global scope on the server
		Red5Plugin proPlugin = (Red5Plugin) PluginRegistry.getPlugin("Red5-Professional");
		// get default global scope
		IGlobalScope globalScope = proPlugin.getServer().getGlobal("default");
		// get the provision resolver service
		provisionResolverSvc = (ProvisionIndex) globalScope.getServiceHandler("provisionResolverService");
	}

	/**
	 * Add a group. Default compositor is 'webrtc'.(provision parameter map:
	 * group=webrtc)
	 * 
	 * @param path
	 *            Group conference path
	 * @param forClient
	 *            optional client, IGroupPublisher or IGroupSubscriber
	 * @return The group experience interface.
	 * @throws Exception
	 */
	public static IGroupCore addGroup(String path, IGroupCore group) throws Exception {
		if (!groups.containsKey(path)) {
			/*
			 * String clazzFormal = "com.red5pro.webrtc.group.BasicRTCCompositor"; String
			 * clazzKey = null; Provision prov = null; if (provisionResolverSvc != null) {
			 * List<Provision> provisions = provisionResolverSvc.resolveProvisions(path);
			 * for (Provision p : provisions) { if (p.getGuid().equals(p.getContextPath()))
			 * { String[] spl = p.getContextPath().split("/"); if (spl.length > 1) { if
			 * (spl[1].trim().equals(p.getStreamName())) { prov = p; if
			 * (prov.getParameters().containsKey("group")) { clazzKey =
			 * prov.getParameters().get("group").toString(); } } } } } if (prov != null) {
			 * // log.info("Adding group for path {} of type {}", path, clazz); if (clazzKey
			 * != null && !registry.containsKey(clazzKey)) { throw new
			 * Exception("No such group handler: ".concat(clazzKey)); } else { clazzFormal =
			 * registry.get(clazzKey); } // Create the handler Class<ExpressionCompositor>
			 * classTypeFinal = (Class<ExpressionCompositor>) Class .forName(clazzFormal);
			 * ExpressionCompositor exp = (ExpressionCompositor)
			 * classTypeFinal.newInstance(); exp.setProvision(prov); GroupImpl gimp = new
			 * GroupImpl(exp, path); if (forClient != null) {
			 * log.info("Adding client with creation :{}", forClient); if (forClient
			 * instanceof IGroupPublisher) { gimp.addPublisher((IGroupPublisher) forClient);
			 * } else if (forClient instanceof IGroupSubscriber) {
			 * gimp.addSubscriber((IGroupSubscriber) forClient); } } groups.put(path, gimp);
			 * return gimp; } else { return null; } } } else { IGroupCore gimp =
			 * parties.get(path); if (forClient != null) {
			 * log.info("Adding client to existing group :{}", forClient); if (forClient
			 * instanceof IGroupPublisher) { gimp.addPublisher((IGroupPublisher) forClient);
			 * } else if (forClient instanceof IGroupSubscriber) {
			 * gimp.addSubscriber((IGroupSubscriber) forClient); } } return gimp;
			 */
		}
		return null;
	}

	/**
	 * Remove a group by its path identifier.
	 * 
	 * @param path
	 *            group path
	 */
	public static void removeGroup(String path) {
		if (groups.containsKey(path)) {
			log.info("Removing group :{}", path);
			groups.remove(path);
		}
	}

	/**
	 * Returns a group matching the given path identifier.
	 * 
	 * @param path
	 *            group path
	 * @return group matching path if found or null if not found
	 */
	public static IGroupCore getGroup(String path) {
		return groups.get(path);
	}

	/**
	 * Look up if subscriber path is a provisioned conference scope. A group is
	 * defined be a provision with the guid matching the context path, and the
	 * stream name matching the room name. If may or may not have a 'group'
	 * parameter containing the specific class handler. Default is
	 * 'DefaultGroupCompositor'
	 * 
	 * @param path
	 *            of the request
	 * @return Provision or null if not found or not of group type
	 */
	public static Provision isGroup(String path) {
		if (provisionResolverSvc != null) {
			List<Provision> provisions = provisionResolverSvc.resolveProvisions(path);
			for (Provision provision : provisions) {
				// skip non-group provisions
				Map<String, Object> params = provision.getParameters();
				if (params.containsKey("group") && ((Boolean) params.get("group"))) {
					String contextPath = provision.getContextPath();
					if (provision.getGuid().equals(contextPath)) {
						String[] spl = contextPath.split("/");
						if (spl.length > 1) {
							if (spl[1].trim().equals(provision.getStreamName())) {
								return provision;
							}
						}
					}
				}
			}
		}
		return null;
	}

}
