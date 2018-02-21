package com.red5pro.cluster.streams;

import java.util.concurrent.CopyOnWriteArraySet;

import org.red5.server.api.IConnection;
/**
 * This class is used to allow or disallow playback based on permissive string value.  
 * @author Andy Shaules
 *
 */
public interface RestrictorEval {

	/**
	 * Add restrictions to this list.
	 */
	static CopyOnWriteArraySet<RestrictorEval> Restrictions = new CopyOnWriteArraySet<RestrictorEval>();
	/**
	 * 
	 * @param provision stream provision
	 * @param conn the subscriber
	 * @return boolean true if access is allowed
	 */
	static boolean isAllowed(Restrictions provision,IConnection conn){
		
		boolean allow = provision.restricted;
		if(allow){
			return true;
		}
		String allowIfEquals = null;//no permissions needed. 
		for(RestrictorEval restriction:Restrictions){			
			allowIfEquals = restriction.getRestriction(conn);
			if(allowIfEquals==null){
				
			}
			String []perms = provision.getConditions();
			if(allowIfEquals==null || perms== null){
				continue;
			}
			
			for(String condition:perms){
				if(condition.equals(allowIfEquals)){
					return true;
				}
			}
			
		}		
		return allow;
	}
	/**
	 * Called on play attempt. Use with geo location or similar restrictions. 
	 * @param conn IConnection calling play.
	 * @return null if permission will use default.
	 */
	String getRestriction(IConnection conn);

}
