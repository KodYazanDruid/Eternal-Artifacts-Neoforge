package com.sonamorningstar.eternalartifacts.util.collections;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {
	private final V defaultValue;
	
	public DefaultConcurrentHashMap(V defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@Override
public V get(Object key) {
	V value = super.get(key);
	return value != null ? value : defaultValue;
}
}
