package com.red5pro.cluster.streams;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Provision services and config handlers implement this interface.
 * @author Andy Shaules
 */
public interface ProvisionIndex {
	/**
	 * Add data sources to this list. 
	 */
	public static CopyOnWriteArraySet<ProvisionIndex> Providers = new CopyOnWriteArraySet<ProvisionIndex>();
	/**
	 * Returns publisher perspective. 
	 * @param context path of stream
	 * @param name name of stream
	 * @return provision with ingest
	 */
	public Provision getProvisionForPublisher(String context, String name);
	/**
	 * Returns server node perspective. This interface is used to create the topography of the cluster. 
	 * @param host host of server needing the provision.
	 * @param port port of server needing the provision.
	 * @param context context in question.
	 * @param name stream name in question.
	 * @return Provision if it exists
	 */
	public Provision getProvisionForNode( String host, int port,String context, String name);
	/**
	 * Look up a provision based on a stream name and path. The name may be appended with multi bit rate parameters.
	 * @param context stream path
	 * @param name stream name
	 * @return Provision if it exists
	 */
	public Provision resolveProvision(String context, String name);

}
