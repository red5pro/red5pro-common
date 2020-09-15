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

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Andy Shaules
 */
public interface ProfileLevelResolver {
	/**
	 * used to determine the priority level of a multi bitrate stream. Parses the
	 * stream name to extract the bitrate priority.
	 */
	static final CopyOnWriteArraySet<ProfileLevelResolver> levelResolvers = new CopyOnWriteArraySet<ProfileLevelResolver>();

	/**
	 * looks at the parsers and returns the first non zero returned.
	 * 
	 * @param context
	 *            path of stream
	 * @param name
	 *            name of stream
	 * @return 0 for single/unknown, or priority level.
	 */
	public static int getProfileLevel(String context, String name) {
		int level = 0;
		for (ProfileLevelResolver resolver : levelResolvers) {
			level = resolver.getLevel(context, name);
			if (level > 0) {
				break;
			}
		}
		return level;
	}

	/**
	 * Available bitrates for a particular stream are designated by a common format.
	 * Use this class to translate the name to an integer priority level. Return
	 * higher numbers for lower bitrates. (1 is the highest, 2 is less than 1, 3 is
	 * less than 2.)
	 * 
	 * @param context
	 *            stream path
	 * @param name
	 *            stream name
	 * @return priority level. return 0 for single/unknown bitrate, or priority
	 *         level.
	 */
	int getLevel(String context, String name);

	/**
	 * Designates pattern added to stream names to separate the index number from
	 * the original name.
	 * 
	 * @param pattern
	 *            String pattern used prior to quality index.
	 */
	void setSpliteration(String pattern);

}
