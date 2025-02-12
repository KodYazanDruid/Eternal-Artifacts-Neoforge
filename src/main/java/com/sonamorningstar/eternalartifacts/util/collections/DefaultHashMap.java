package com.sonamorningstar.eternalartifacts.util.collections;

import java.util.HashMap;

public class DefaultHashMap<K,V> extends HashMap<K,V> {
	private final V defaultValue;
	
	public DefaultHashMap(V defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@Override
	public V get(Object key) {
		return containsKey(key) ? super.get(key) : defaultValue;
	}
}
