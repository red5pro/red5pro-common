package com.red5pro.cluster.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * 
 * @author Andy Shaules
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
	 * H264 param for MBR/Preprocessor.<br>
	 * H264 quantize value
	 */
	public static String Param_Video_QP_Min = "videoQPMin";
	/**
	 * H264 quantize value
	 */
	public static String Param_Video_QP_Max = "videoQPMax";
	/**
	 * max bitrate allowed
	 */
	public static String Param_Video_BR_Max = "videoBRMax";
	/**
	 * Re-encoded entropy cabac/calcv output Constrained baseline=0
	 */
	public static String Param_Video_Enc_Profile = "videoEncProfile";
	/**
	 * 0 bitrate, 1 quality
	 */
	public static String Param_Video_Enc_Mode = "videoEncMode";
	/**
	 * Key frame interval, by frame count.
	 */
	public static String Param_Video_Key = "videoKey";

	/**
	 * Returns concatenated context path without leading slashes. Normalizes guid.
	 * 
	 * @param context
	 *            app scope
	 * @param name
	 *            stream name
	 * @return String with leading slash removed from context, concatenated with "/"
	 *         and name.
	 */
	public static String makeGuid(String context, String name) {
		if (context.startsWith("/")) {
			context = context.substring(1);
		}
		if (!context.endsWith("/")) {
			context = context.concat("/");
		}
		if (name.startsWith("/")) {
			name = name.substring(1);
		}
		return context.concat(name);
	}

	private static Object interpret(JsonElement elem) {
		if (elem.isJsonPrimitive()) {
			JsonPrimitive jp = elem.getAsJsonPrimitive();
			if (jp.isNumber()) {
				return jp.getAsInt();
			} else if (jp.isString()) {
				return jp.getAsString();
			}
		}
		return null;
	}

	/**
	 * context / path
	 */
	private final String guid;

	private final String contextPath;

	private final String streamName;

	private final int qualityLevel;

	private final Restrictions restrictions;

	private final Map<String, Object> parameters;

	private List<Ingest> primaries;

	private List<Ingest> secondaries;

	private Provision(String contextPath, String streamName, int qualityLevel, Restrictions restrictions,
			Map<String, Object> parameters) {
		this.guid = makeGuid(contextPath, streamName);
		this.contextPath = contextPath;
		this.streamName = streamName;
		this.qualityLevel = qualityLevel;
		this.restrictions = restrictions;
		this.parameters = parameters;
	}

	private Provision(String guid, String contextPath, String streamName, int qualityLevel, Restrictions restrictions,
			Map<String, Object> parameters) {
		this.guid = guid;
		this.contextPath = contextPath;
		this.streamName = streamName;
		this.qualityLevel = qualityLevel;
		this.restrictions = restrictions;
		this.parameters = parameters;
	}

	public String getGuid() {
		return guid;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getStreamName() {
		return streamName;
	}

	public int getQualityLevel() {
		return qualityLevel;
	}

	public Restrictions getRestrictions() {
		return restrictions;
	}

	public Map<String, Object> getParameters() {
		return parameters;
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

	@Override
	public int hashCode() {
		// can only be one stream instance on this path.
		return Objects.hashCode(new Object[]{this.contextPath, this.streamName});
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Provision) {
			Provision compare = (Provision) other;
			// can only be one stream instance on this path.
			if (compare.contextPath.equals(contextPath) && compare.streamName.equals(streamName)) {
				return true;
			}
		}
		return false;
	}

	public JsonObject toJson() {
		JsonObject ret = new JsonObject();
		ret.addProperty("guid", getGuid());
		ret.addProperty("context", getContextPath());
		ret.addProperty("name", getStreamName());
		ret.addProperty("level", getQualityLevel());
		Iterator<Entry<String, Object>> iter = getParameters().entrySet().iterator();
		JsonObject parameters = new JsonObject();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			parameters.addProperty(entry.getKey(), String.valueOf(entry.getValue()));
		}
		ret.add("parameters", parameters);
		JsonArray restrictions = new JsonArray();
		if (getRestrictions() != null) {
			for (String r : getRestrictions().getConditions()) {
				JsonPrimitive element = new JsonPrimitive(r);
				restrictions.add(element);
			}
			ret.add("restrictions", restrictions);
			ret.addProperty("isRestricted", getRestrictions().isRestricted());
		}
		JsonArray primaries = new JsonArray();
		getPrimaries().forEach(primary -> {
			JsonObject iasJ = new JsonObject();
			iasJ.addProperty("host", primary.getHost());
			iasJ.addProperty("port", primary.getPort());
			primaries.add(iasJ);
		});
		ret.add("primaries", primaries);
		JsonArray secondaries = new JsonArray();
		getSecondaries().forEach(secondary -> {
			JsonObject iasJ = new JsonObject();
			iasJ.addProperty("host", secondary.getHost());
			iasJ.addProperty("port", secondary.getPort());
			secondaries.add(iasJ);
		});
		ret.add("secondaries", secondaries);
		return ret;
	}

	@Override
	public String toString() {
		return "Provision [guid=" + guid + ", contextPath=" + contextPath + ", streamName=" + streamName
				+ ", qualityLevel=" + qualityLevel + ", restrictions=" + restrictions + ", parameters=" + parameters
				+ ", primaries=" + primaries + ", secondaries=" + secondaries + "]";
	}

	public static Provision build(String guid, String contextPath, String streamName, int qualityLevel) {
		return new Provision(guid, contextPath, streamName, qualityLevel, null, new HashMap<>());
	}

	public static Provision build(String guid, String contextPath, String streamName, int qualityLevel,
			Restrictions restrictions, Map<String, Object> parameters) {
		return new Provision(guid, contextPath, streamName, qualityLevel, restrictions, parameters);
	}

	public static Provision build(String contextPath, String streamName, int qualityLevel, Restrictions restrictions,
			Map<String, Object> parameters) {
		return new Provision(makeGuid(contextPath, streamName), contextPath, streamName, qualityLevel, restrictions,
				parameters);
	}

	public static Provision build(JsonObject provObj) {
		String guid = provObj.get("guid").getAsString();
		String contextPath = provObj.get("context").getAsString();
		String streamName = provObj.get("name").getAsString();
		int qualityLevel = provObj.get("level").getAsInt();
		Restrictions rObj = null;
		if (provObj.has("restrictions")) {
			JsonArray rest = provObj.get("restrictions").getAsJsonArray();
			String[] reps = new String[rest.size()];
			int i = 0;
			for (JsonElement match : rest) {
				reps[i++] = match.getAsString();
			}
			rObj = Restrictions.build(provObj.get("isRestricted").getAsBoolean(), reps);
		}
		Map<String, Object> parameters = new HashMap<>();
		if (provObj.has("parameters")) {
			JsonObject params = provObj.get("parameters").getAsJsonObject();
			for (Entry<String, JsonElement> param : params.entrySet()) {
				parameters.put(param.getKey(), interpret(param.getValue()));
			}
		}
		List<Ingest> primaries = new ArrayList<>();
		if (provObj.has("primaries")) {
			JsonArray params = provObj.get("primaries").getAsJsonArray();
			for (JsonElement param : params) {
				Ingest pi = Ingest.build(param.getAsJsonObject().get("host").getAsString(),
						param.getAsJsonObject().get("port").getAsInt());
				primaries.add(pi);
			}
		}
		List<Ingest> secondaries = new ArrayList<>();
		if (provObj.has("secondaries")) {
			JsonArray params = provObj.get("secondaries").getAsJsonArray();
			for (JsonElement param : params) {
				Ingest pi = Ingest.build(param.getAsJsonObject().get("host").getAsString(),
						param.getAsJsonObject().get("port").getAsInt());
				secondaries.add(pi);
			}
		}
		Provision provision = new Provision(guid, contextPath, streamName, qualityLevel, rObj, parameters);
		provision.setPrimaries(primaries);
		provision.setSecondaries(secondaries);
		return provision;
	}

}
