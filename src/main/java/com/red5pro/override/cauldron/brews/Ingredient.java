/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.override.cauldron.brews;

import java.util.Map.Entry;
/**
 * This class is used to pass parameters to the native modules.
 * <p /> 
 * <pre>
 * iProStream.getPotion().add(new Ingredient("maskShape","round"));
 * </pre> 
 * @author Andy Shaules
 *
 */
public class Ingredient implements Entry<String, Object> {

	private String key;
	
	private Object value;
	/**
	 * Collectible key/value pair for setting properties to native modules. 
	 * @param propertyName
	 * @param propertyValue
	 */
	public Ingredient(String propertyName, Object propertyValue){
		this.key=propertyName;
		this.value=propertyValue;
	}
	/**
	 * Returns the property name
	 * 
	 */
	@Override
	public String getKey() {		
		return key;
	}
	/**
	 * Returns the property value.
	 * 
	 */
	@Override
	public Object getValue() {		
		return value;
	}
	/**
	 * Sets new Value and returns old value.
	 * 
	 */
	@Override
	public Object setValue(Object value) {		
		Object old = value;
		this.value=value;
		return old;
	}
}
