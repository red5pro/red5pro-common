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
