package com.red5pro.override.cauldron;

import com.red5pro.override.IProStream;

public interface IPreprocessorFactory {

	static final String BeanName = "ipreprocessorFactory";
	/**
	 * When called, use IProStream.usePreprocessor(clazz,params);<br>
	 * Call with nulls for defaults. Uses same Provision parameters as mbr but with
	 * optionally width/height of zero for canceling resize.<br>
	 * 
	 * @param stream
	 *            stream to configure and scrub/re-key
	 */
	void configure(IProStream stream);
}
