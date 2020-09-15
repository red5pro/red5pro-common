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

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Provision services and config handlers implement this interface.
 * 
 * @author Andy Shaules
 */
public interface ProvisionIndex {
	/**
	 * Add data sources to this list.
	 */
	static final CopyOnWriteArraySet<ProvisionIndex> providers = new CopyOnWriteArraySet<ProvisionIndex>();

	/**
	 * Returns publisher perspective.
	 * 
	 * @param context
	 *            path of stream
	 * @param name
	 *            name of stream
	 * @return provision with ingest
	 */
	Provision getProvisionForPublisher(String context, String name);

	/**
	 * Returns server node perspective. This interface is used to create the
	 * topography of the cluster.
	 * 
	 * @param host
	 *            host of server needing the provision.
	 * @param port
	 *            port of server needing the provision.
	 * @param context
	 *            context in question.
	 * @param name
	 *            stream name in question.
	 * @return Provision if it exists
	 */
	Provision getProvisionForNode(String host, int port, String context, String name);

	/**
	 * Look up a provision based on a stream name and path. The name may be appended
	 * with multi bit rate parameters.
	 * 
	 * @param context
	 *            stream path
	 * @param name
	 *            stream name
	 * @return Provision if it exists
	 */
	Provision resolveProvision(String context, String name);

	/**
	 * Look up all provisions with a given guid.
	 * 
	 * @param guid
	 *            Provision guid
	 * @return group of MBR streams
	 */
	List<Provision> resolveProvisions(String guid);

}
