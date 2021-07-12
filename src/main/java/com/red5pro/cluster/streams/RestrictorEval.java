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

import org.red5.server.api.IConnection;

/**
 * This class is used to allow or disallow playback based on permissive string
 * value.
 * 
 * @author Andy Shaules
 *
 */
public interface RestrictorEval {

    /**
     * Add restrictions to this list.
     */
    static final CopyOnWriteArraySet<RestrictorEval> restrictions = new CopyOnWriteArraySet<RestrictorEval>();

    /**
     * 
     * @param provision
     *            stream provision
     * @param conn
     *            the subscriber
     * @return boolean true if access is allowed
     */
    static boolean isAllowed(Restrictions provision, IConnection conn) {
        boolean allow = provision.isRestricted();
        if (allow) {
            return true;
        }
        String allowIfEquals = null;// no permissions needed.
        for (RestrictorEval restriction : restrictions) {
            allowIfEquals = restriction.getRestriction(conn);
            if (allowIfEquals == null) {

            }
            String[] perms = provision.getConditions();
            if (allowIfEquals == null || perms == null) {
                continue;
            }
            for (String condition : perms) {
                if (condition.equals(allowIfEquals)) {
                    return true;
                }
            }
        }
        return allow;
    }

    /**
     * Called on play attempt. Use with geo location or similar restrictions.
     * 
     * @param conn
     *            IConnection calling play.
     * @return null if permission will use default.
     */
    String getRestriction(IConnection conn);

}
