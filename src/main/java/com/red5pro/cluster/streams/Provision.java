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
package com.red5pro.cluster.streams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.red5pro.util.ProvisionAdapter;

/**
 * Provision model object.
 *
 * @author Andy Shaules
 * @author Paul Gregoire
 */
public class Provision {

    public final static String Param_Video_Bitrate = "videoBR";

    public final static String Param_Audio_Bitrate = "audioBR";

    public final static String Param_Video_Height = "videoHeight";

    public final static String Param_Video_Width = "videoWidth";

    /**
     * Codec dependent support for 'baseline', 'main', 'high'.
     */
    public final static String Param_Video_Profile = "videoProfile";

    public final static String Param_Audio_Sample_Rate = "audioSR";

    public final static String Param_Audio_Channel_Count = "audioCh";

    public final static String Param_User_Name = "userName";

    public final static String Param_Password = "password";

    public final static String Param_QOS = "qos";

    /**
     * H264 param for MBR/Preprocessor.<br>
     * H264 quantize value
     */
    public final static String Param_Video_QP_Min = "videoQPMin";

    /**
     * H264 quantize value
     */
    public final static String Param_Video_QP_Max = "videoQPMax";

    /**
     * max bitrate allowed
     */
    public final static String Param_Video_BR_Max = "videoBRMax";

    /**
     * Encoding entropy cabac=1/calcv=0.
     */
    public final static String Param_Video_Entropy_Profile = "videoEntropyProfile";

    /**
     * 0 bitrate, 1 quality
     */
    public final static String Param_Video_Enc_Mode = "videoEncMode";

    /**
     * Key frame interval, by frame count.
     */
    public final static String Param_Video_Key = "videoKey";

    /**
     * Video Framerate.
     */
    public final static String Param_Video_FPS = "videoFPS";

    /**
     *
     */
    public final static String Param_HardwareKey = "hardware";

    /**
     *
     */
    public final static String Param_Strict = "strict";

    /**
     * Gson for serialize and deserialize ops.
     */
    private static Gson gson;

    static {
        // construct the builder that we'll re-use; it is thread-safe
        gson = new GsonBuilder().registerTypeAdapter(Provision.class, new ProvisionAdapter()).create();
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

    // stream name alias for publishing
    private String streamNameAlias;

    // stream name aliases for subscription
    private Set<String> aliases;

    private Provision(String contextPath, String streamName, int qualityLevel, Restrictions restrictions, Map<String, Object> parameters) {
        this.guid = makeGuid(contextPath, streamName);
        this.contextPath = contextPath;
        this.streamName = streamName;
        this.qualityLevel = qualityLevel;
        this.restrictions = restrictions;
        this.parameters = parameters;
    }

    private Provision(String guid, String contextPath, String streamName, int qualityLevel, Restrictions restrictions, Map<String, Object> parameters) {
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

    public String getStreamNameAlias() {
        return streamNameAlias;
    }

    public void setStreamNameAlias(String streamNameAlias) {
        this.streamNameAlias = streamNameAlias;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

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

    /**
     * Returns a Gson instance based on the Provision enabled GsonBuilder.
     *
     * @return Gson instance
     */
    public static Gson getGson() {
        // Gson is thread-safe
        return gson;
    }

    public static Provision buildFromJson(String json) {
        return gson.fromJson(json, Provision.class);
    }

    public static Provision build(String guid, String contextPath, String streamName, int qualityLevel) {
        return new Provision(guid, contextPath, streamName, qualityLevel, null, new HashMap<>());
    }

    public static Provision build(String guid, String contextPath, String streamName, int qualityLevel, Restrictions restrictions, Map<String, Object> parameters) {
        return new Provision(guid, contextPath, streamName, qualityLevel, restrictions, parameters);
    }

    public static Provision build(String contextPath, String streamName, int qualityLevel, Restrictions restrictions, Map<String, Object> parameters) {
        return new Provision(makeGuid(contextPath, streamName), contextPath, streamName, qualityLevel, restrictions, parameters);
    }

    /**
     * Returns JSON string representing this object instance.
     *
     * @return JSON string
     */
    public String toJson() {
        return gson.toJson(this);
    }

    @Override
    public int hashCode() {
        // can only be one stream instance on this path.
        return Objects.hashCode(new Object[] { this.contextPath, this.streamName });
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

    @Override
    public String toString() {
        return "Provision [guid=" + guid + ", contextPath=" + contextPath + ", streamName=" + streamName + ", qualityLevel=" + qualityLevel + ", restrictions=" + restrictions + ", parameters=" + parameters + ", primaries=" + primaries + ", secondaries=" + secondaries + ", nameAlias=" + streamNameAlias + ", aliases=" + aliases + "]";
    }

}
