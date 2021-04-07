package com.red5pro.group;

/**
 * Identifier for events occurring within a group.
 * 
 * @author Paul Gregoire
 *
 */
public enum GroupEventType {

	START, // conference / group start
	END, // conference / group end
	JOIN, // conference / group join/ed
	LEAVE, // conference / group leave / left
	ACTION, // action / control event
	MEDIA, // media event
	STATUS; // status event

}
