package com.red5pro.cluster.streams;

import java.util.concurrent.CopyOnWriteArraySet;
/**
 * @author Andy Shaules
 *
 */
public interface ProfileLevelResolver {
	/**
	 * used to determine the priority level of a multi bitrate stream. Parses the stream name to extract the bitrate priority. 
	 */
	public static CopyOnWriteArraySet<ProfileLevelResolver> LevelResolvers = new CopyOnWriteArraySet<ProfileLevelResolver>();
	/**
	 * looks at the parsers and returns the first non zero returned.
	 * @param context path of stream
	 * @param name name of stream
	 * @return 0 for single/unknown, or priority level.
	 */
	public static int getProfileLevel(String context, String name){
		int level = 0;
		for(ProfileLevelResolver resolver :LevelResolvers){
			level = resolver.getLevel(context, name);
			if(level>0){
				break;
			}
		}
		return level;
	}
	
	/**
	 * Available bitrates for a particular stream are designated by a common format. Use this class to translate the name to an integer priority level. 
	 * Return higher numbers for lower bitrates. (1 is the highest, 2 is less than 1, 3 is less than 2.) 
	 * @param context stream path
	 * @param name stream name
	 * @return priority level. return 0 for single/unknown bitrate, or priority level. 
	 */
	int getLevel(String context, String name);
	/**
	 * Designates pattern added to stream names to separate the index number from the original name. 
	 * @param pattern String pattern used prior to quality index.
	 */
	void setSpliteration(String pattern);

}
