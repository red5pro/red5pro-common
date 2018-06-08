package com.red5pro.cluster.streams;

/**
 * List of conditions for restricted access.
 * 
 * @author Andy Shaules
 */
public class Restrictions {

    private final boolean restricted;

    private final String[] conditions;

    private Restrictions(boolean restricted, String[] conditions) {
        this.restricted = restricted;
        this.conditions = conditions;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public String[] getConditions() {
        return conditions;
    }

    public static Restrictions build(boolean restricted, String[] conditions) {
        return new Restrictions(restricted, conditions);
    }

}
