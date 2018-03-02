package com.red5pro.cluster.streams;
/**
 * List of conditions for restricted access.
 * @author Andy Shaules
 *
 */
public class Restrictions {
	
	boolean restricted;
	
	String[] conditions;
	
	public boolean isRestricted() {
		return restricted;
	}
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}
	public String[] getConditions() {
		return conditions;
	}
	public void setConditions(String[] conditions) {
		this.conditions = conditions;
	}
}
