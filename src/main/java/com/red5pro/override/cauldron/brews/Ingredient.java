/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.override.cauldron.brews;

import java.util.Map.Entry;

/**
 * This class is used to pass parameters to the native modules. <br>
 * 
 * <pre>
 * iProStream.getPotion().add(new Ingredient("maskShape", "round"));
 * </pre>
 * 
 * @author Andy Shaules
 */
public class Ingredient implements Entry<String, Object> {

	private String key;

	private Object value;

	/**
	 * Collectible key/value pair for setting properties to native modules.
	 * 
	 * @param propertyName
	 *            key
	 * @param propertyValue
	 *            value
	 */
	public Ingredient(String propertyName, Object propertyValue) {
		this.key = propertyName;
		this.value = propertyValue;
	}

	/**
	 * Returns the property name
	 * 
	 * @return key
	 */
	@Override
	public String getKey() {
		return key;
	}

	/**
	 * Returns the property value.
	 * 
	 * @return value
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * Sets new value and returns old value.
	 * 
	 * @param value
	 *            new value to replace existing value
	 * @return previous value
	 */
	@Override
	public Object setValue(Object value) {
		Object old = value;
		this.value = value;
		return old;
	}
}
