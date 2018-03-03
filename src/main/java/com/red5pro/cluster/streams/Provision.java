package com.red5pro.cluster.streams;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 
 * @author Andy Shaules
 *
 */
public class Provision {
	public static String Param_Video_Bitrate = "videoBR";
	public static String Param_Audio_Bitrate = "audioBR";
	public static String Param_Video_Height = "videoHeight";
	public static String Param_Video_Width = "videoWidth";
	public static String Param_Video_Profile = "videoProfile";
	public static String Param_Audio_Sample_Rate = "audioSR";
	public static String Param_Audio_Channel_Count = "audioCh";
	public static String Param_User_Name = "userName";
	public static String Param_Password = "password";
	public static String Param_QOS = "qos";
	
	/**
	 * Returns concatenated context path without leading slashes. Normalizes guid.
	 * @param context app scope
	 * @param name stream name
	 * @return String with leading slash removed from context, 
	 * concatenated with "/" and name.
	 */
	public static String MakeGuid(String context, String name){
		if(context.startsWith("/")){
			context=context.substring(1);
		}
		if(!context.endsWith("/")){
			context=context.concat("/");
		}
		if(name.startsWith("/")){
			name=name.substring(1);
		}
		return context.concat(name);
	}
	/**
	 * context / path
	 */
	private String guid;	 

	private String contextPath;
	
	private String streamName;
	
	private int qualityLevel;
	
	private List<Ingest> primaries;	
	
	private List<Ingest>  secondaries;
	
	private Restrictions restrictions;

	private Map<String,Object> parameters;
	
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

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getStreamName() {
		return streamName;
	}

	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	public int getQualityLevel() {
		return qualityLevel;
	}

	public void setQualityLevel(int qualityLevel) {
		this.qualityLevel = qualityLevel;
	}

	public Map<String,Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String,Object> parameters) {
		this.parameters = parameters;
	}
	@Override
	public int hashCode(){
		// can only be one stream instance on this path.
	    return Objects.hashCode(new Object[]{this.contextPath,this.streamName});
	}
	@Override
	public boolean equals(Object other){
		if(other instanceof Provision){
			Provision compare=(Provision) other;
			// can only be one stream instance on this path.
			if(compare.contextPath.equals(contextPath) && compare.streamName.equals(streamName)){
				return true;
			}
		}
		return false;
	}
}
