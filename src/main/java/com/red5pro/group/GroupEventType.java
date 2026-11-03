package com.red5pro.group;

/**
 * Identifier for events occurring within a group.
 *
 * @author Paul Gregoire
 *
 */
public enum GroupEventType {

    /** Conference / group start. */
    START,
    /** Conference / group end. */
    END,
    /** Conference / group join. */
    JOIN,
    /** Conference / group leave. */
    LEAVE,
    /** Action / control event. */
    ACTION,
    /** Media event. */
    MEDIA,
    /** Status event. */
    STATUS;

}
