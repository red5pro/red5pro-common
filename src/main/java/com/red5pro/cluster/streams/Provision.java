package com.red5pro.cluster.streams;

import java.util.List;

/**
 * 
 * @author Andy Shaules
 *
 */
public class Provision {
	/**
	 * Returns concatenated context path.
	 * @param context app scope
	 * @param name stream name
	 * @return String concatenated with "/" between context and name
	 */
	public static String GetGuid(String context, String name){
		if(context.startsWith("/")){
			context=context.substring(1);
		}
		if(!context.endsWith("/")){
			context=context.concat("/");
		}
		return context.concat(name);
	}
	/**
	 * context / path
	 */
	private String guid;
	
	private List<Ingest> primaries;	
	
	private List<Ingest>  secondaries;
	
	private Restrictions restrictions;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public List<Ingest> getPrimaries() {
		return primaries;
	}

	public void setPrimaries(List<Ingest> primaries) {
		this.primaries = primaries;
	}

	public List<Ingest> getSecondaries() {
		return secondaries;
	}

	public void setSecondaries(List<Ingest> secondaries) {
		this.secondaries = secondaries;
	}

	public Restrictions getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(Restrictions restrictions) {
		this.restrictions = restrictions;
	}

}
