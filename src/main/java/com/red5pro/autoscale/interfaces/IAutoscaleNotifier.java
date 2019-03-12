package com.red5pro.autoscale.interfaces;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.ParseException;

import com.red5pro.autoscale.exceptions.AutoscaleNotificationException;

public interface IAutoscaleNotifier {

	String makeCall(String host, String action, String body)
			throws ParseException, IOException, URISyntaxException, AutoscaleNotificationException;

}
