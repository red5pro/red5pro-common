package com.red5pro.cluster.streams;
/**
 * This example class parses the bitrate levels.
 * @author Andy Shaules
 *
 */
public class ExampleProfileResolver implements ProfileLevelResolver {

	
	public ExampleProfileResolver(){
		ProfileLevelResolver.LevelResolvers.add(this);
	}
	
	@Override
	public int getLevel(String contect, String name) {
		//Parse name to determine which bitrate level priority this stream is.
		//lowest  name_3  
		if(name.endsWith("_3"))
		    return 3;
		//middle  name_3  
		if(name.endsWith("_2"))
		    return 2;
		//highest
		if(name.endsWith("_1"))
		    return 1;
		//single bitrate
		return 0;
	}

}
