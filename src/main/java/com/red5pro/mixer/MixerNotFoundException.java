package com.red5pro.mixer;

public class MixerNotFoundException extends MixerException {
    private static final long serialVersionUID = 1L;

    public MixerNotFoundException() {
        super();
    }

    public MixerNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MixerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MixerNotFoundException(String message) {
        super(message);
    }

    public MixerNotFoundException(Throwable cause) {
        super(cause);
    }

}
