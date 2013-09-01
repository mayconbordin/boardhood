package com.boardhood.api.util;

public interface CacheProvider<KeyT, ValT> {
	public ValT get(Object elementKey);
	public ValT get(Object elementKey, boolean tryDiskCache);
	public ValT put(KeyT key, ValT value);
	public ValT put(KeyT key, ValT value, long expiration);
	public ValT remove(Object key);
	public ValT remove(Object key, boolean forceDisk);
	public boolean containsKey(Object key);
	public boolean containsKeyOnDisk(Object key);
	public boolean containsKeyInMemory(Object key);
}
