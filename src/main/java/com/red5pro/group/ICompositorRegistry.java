package com.red5pro.group;

public interface ICompositorRegistry {
	void release(String path);
	void register(String path, String clazz);
}
