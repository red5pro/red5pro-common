package com.red5pro.restreamer;

public interface ErrorListener {

	public void operationComplete(ErrorHandler errorHandler);

	public void noValidMedia(ErrorHandler errorHandler);

	public void unknownHostError(ErrorHandler errorHandler);

	public void streamError(ErrorHandler errorHandler);

}
