package com.boardhood.api.util;

import java.util.Map;
import java.util.Set;

public interface BHMap<T, E> extends Map<T, E> {
	public Set<T> getKeysByValue(E value);
	public T getKeyByValue(E value);
}
