package com.boardhood.api.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class BHHashMap<T, E> extends HashMap<T, E> implements BHMap<T, E> {

	public BHHashMap() {
		super();
	}

	public BHHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public BHHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public BHHashMap(Map<? extends T, ? extends E> m) {
		super(m);
	}
	
	public Set<T> getKeysByValue(E value) {
	     Set<T> keys = new HashSet<T>();
	     for (Entry<T, E> entry : entrySet()) {
	         if (value.equals(entry.getValue())) {
	             keys.add(entry.getKey());
	         }
	     }
	     return keys;
	}
	
	public T getKeyByValue(E value) {
	    for (Entry<T, E> entry : entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
}
