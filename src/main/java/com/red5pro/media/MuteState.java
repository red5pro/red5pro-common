package com.red5pro.media;

/**
 * Provides a means to express various mute states.
 * <ul>
 * <li>UNDEFINED - has no mute state, it represents anything else such as non-existence</li>
 * <li>NOT_MUTED - is not muted</li>
 * <li>MUTED - is muted</li>
 * </ul>
 *
 * @author Paul Gregoire
 *
 */
public enum MuteState {

    /** No mute state; represents anything else such as non-existence. */
    UNDEFINED,
    /** Not muted. */
    NOT_MUTED,
    /** Muted. */
    MUTED;

}
