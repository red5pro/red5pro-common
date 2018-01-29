package com.red5pro.cluster.streams;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
	static List<RestrictorEval> Restrictions = new CopyOnWriteArrayList<RestrictorEval>();
	/**
	 * 
	 * @param provision stream provision
	 * @param conn the subscriber
	 * @return boolean if it is restricted
	 */
	static boolean isRestricted(Restrictions provision,IConnection conn){
		Iterator<RestrictorEval> iter = Restrictions.iterator();
		boolean allow = false;
		String allowIfEquals = null;//no permissions needed. 
		while(iter.hasNext()){
			RestrictorEval restriction = iter.next();
			allowIfEquals = restriction.getRestriction(conn);
			if(allowIfEquals!=null){
				allow=false;//override default;
			}
			String []perms = provision.getConditions();
			if(perms!=null && allowIfEquals!=null){
				for(String condition:perms){
					if(condition.equals(allowIfEquals)){
						return true;
					}
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
