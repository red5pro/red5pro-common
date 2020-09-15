//
// Copyright © 2020 Infrared5, Inc. All rights reserved.
//
// The accompanying code comprising examples for use solely in conjunction with Red5 Pro (the "Example Code")
// is  licensed  to  you  by  Infrared5  Inc.  in  consideration  of  your  agreement  to  the  following
// license terms  and  conditions.  Access,  use,  modification,  or  redistribution  of  the  accompanying
// code  constitutes your acceptance of the following license terms and conditions.
//
// Permission is hereby granted, free of charge, to you to use the Example Code and associated documentation
// files (collectively, the "Software") without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The Software shall be used solely in conjunction with Red5 Pro. Red5 Pro is licensed under a separate end
// user  license  agreement  (the  "EULA"),  which  must  be  executed  with  Infrared5,  Inc.
// An  example  of  the EULA can be found on our website at: https://account.red5pro.com/assets/LICENSE.txt.
//
// The above copyright notice and this license shall be included in all copies or portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,  INCLUDING  BUT
// NOT  LIMITED  TO  THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR  A  PARTICULAR  PURPOSE  AND
// NONINFRINGEMENT.   IN  NO  EVENT  SHALL INFRARED5, INC. BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM,  OUT  OF  OR  IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.red5pro.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.red5pro.cluster.streams.Ingest;
import com.red5pro.cluster.streams.Provision;
import com.red5pro.cluster.streams.Restrictions;

/**
 * Provides serialize and deserialization for Provision model objects using
 * Gson.
 * 
 * @author Paul Gregoire
 */
public class ProvisionAdapter implements JsonSerializer<Provision>, JsonDeserializer<Provision> {

	@Override
	public JsonElement serialize(final Provision provision, final Type type, final JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.addProperty("guid", provision.getGuid());
		result.addProperty("context", provision.getContextPath());
		result.addProperty("name", provision.getStreamName());
		result.addProperty("level", provision.getQualityLevel());
		JsonObject parameters = new JsonObject();
		provision.getParameters().forEach((key, value) -> {
			if (value instanceof Number) {
				parameters.addProperty(key, (Number) value);
			} else if (value instanceof String) {
				parameters.addProperty(key, (String) value);
			} else if (value instanceof Boolean) {
				parameters.addProperty(key, (Boolean) value);
			} else {
				parameters.addProperty(key, String.valueOf(value));
			}
		});
		result.add("parameters", parameters);
		if (provision.getRestrictions() != null) {
			JsonArray restrictions = new JsonArray();
			for (String r : provision.getRestrictions().getConditions()) {
				JsonPrimitive element = new JsonPrimitive(r);
				restrictions.add(element);
			}
			result.add("restrictions", restrictions);
			result.addProperty("isRestricted", provision.getRestrictions().isRestricted());
		}
		JsonArray primaries = new JsonArray();
		provision.getPrimaries().forEach(primary -> {
			JsonObject iasJ = new JsonObject();
			iasJ.addProperty("host", primary.getHost());
			iasJ.addProperty("port", primary.getPort());
			primaries.add(iasJ);
		});
		result.add("primaries", primaries);
		JsonArray secondaries = new JsonArray();
		provision.getSecondaries().forEach(secondary -> {
			JsonObject iasJ = new JsonObject();
			iasJ.addProperty("host", secondary.getHost());
			iasJ.addProperty("port", secondary.getPort());
			secondaries.add(iasJ);
		});
		result.add("secondaries", secondaries);
		return result;
	}

	@Override
	public Provision deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject provObj = json.getAsJsonObject();
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
		Provision provision = Provision.build(guid, contextPath, streamName, qualityLevel, rObj, parameters);
		provision.setPrimaries(primaries);
		provision.setSecondaries(secondaries);
		return provision;
	}

	private Object interpret(JsonElement elem) {
		if (elem.isJsonPrimitive()) {
			JsonPrimitive jp = elem.getAsJsonPrimitive();
			if (jp.isNumber()) {
				return jp.getAsInt();
			} else if (jp.isString()) {
				return jp.getAsString();
			} else if (jp.isBoolean()) {
				return jp.getAsBoolean();
			}
		}
		return null;
	}

}