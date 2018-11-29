package com.red5pro.override.cauldron;

import java.nio.ByteBuffer;
/**
 * Implementors of IProStream preprocessor classes must call output handler
 * receiveVideo method from native or java. Configure a IPreprocessorFactory
 * bean in webapp red5-web.xml named ipreprocessorFactory defined in
 * IPreprocessorFactory interface.
 * 
 * @author Andy Shaules
 *
 */
public interface IMIRVReceiver {
	void receiveVideo(int index, int timestamp, ByteBuffer buffer);
}
