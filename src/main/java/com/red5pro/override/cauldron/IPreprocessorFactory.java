package com.red5pro.override.cauldron;

import com.red5pro.override.IProStream;

public interface IPreprocessorFactory {
    
    static final String BeanName = "ipreprocessorFactory";
    /**
     * When called, call IProStream.usePreprocessor(clazz,params);<br>
     * Call with nulls for defaults. 
     * Uses same parameters as mbr but with width/height of zero for canceling resize.<br>
     * Provision.Param_Video_Bitrate =1000000 <br>
     * @param stream stream to configure
     */
    void configure(IProStream stream);
}
