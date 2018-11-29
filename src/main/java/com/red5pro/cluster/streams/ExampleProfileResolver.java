package com.red5pro.cluster.streams;

/**
 * This example class parses the variant index. It anticipates name_1, name_2,
 * or name. Lower non-zero indexes are higher quality. Zero specifies one single
 * quality available. An FMLE type GUI would use '_%i' added to the stream name.
 * Wirecast would enter specific names with '_1', '_2' added where '_1' is
 * number one quality. When providing quality variant names, Red5Pro requires
 * lower indexes to be higher quality.
 * 
 * @author Andy Shaules
 *
 */
public class ExampleProfileResolver implements ProfileLevelResolver {

	private String spliterator = "_";;

	public ExampleProfileResolver() {
		ProfileLevelResolver.levelResolvers.add(this);
	}

	@Override
	public int getLevel(String contect, String name) {
		// Parse name to determine which bitrate level priority this stream is.
		// lowest name_3
		String[] parts = name.split(spliterator);
		// last part should be integer.
		int level = 0;
		try {
			level = Integer.parseInt(parts[parts.length - 1]);
		} catch (NumberFormatException nfe) {
		}
		return level;
	}

	@Override
	public void setSpliteration(String pattern) {
		this.spliterator = pattern;
	}

}
