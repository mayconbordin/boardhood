/* Copyright (c) 2009 Matthias Kaeppler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.boardhood.mobile.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.github.ignition.support.IgnitedStrings;
import com.google.common.collect.MapMaker;

/**
 * <p>
 * A simple 2-level cache consisting of a small and fast in-memory cache (1st level cache) and an
 * (optional) slower but bigger disk cache (2nd level cache). For disk caching, either the
 * application's cache directory or the SD card can be used. Please note that in the case of the app
 * cache dir, Android may at any point decide to wipe that entire directory if it runs low on
 * internal storage. The SD card cache <i>must</i> be managed by the application, e.g. by calling
 * {@link #wipe} whenever the app quits.
 * </p>
 * <p>
 * When pulling from the cache, it will first attempt to load the data from memory. If that fails,
 * it will try to load it from disk (assuming disk caching is enabled). If that succeeds, the data
 * will be put in the in-memory cache and returned (read-through). Otherwise it's a cache miss.
 * </p>
 * <p>
 * Pushes to the cache are always write-through (i.e. the data will be stored both on disk, if disk
 * caching is enabled, and in memory).
 * </p>
 * 
 * @author Matthias Kaeppler
 */
public abstract class AbstractCache<KeyT, ValT> implements Map<KeyT, ValT> {
    public static final int DISK_CACHE_INTERNAL = 0;
    public static final int DISK_CACHE_SDCARD   = 1;

    private static final String LOG_TAG = "Droid-Fu[CacheFu]";

    private boolean isDiskCacheEnabled     = false;
    private boolean isDiskCacheSynchronous = false;
    private boolean removeExpiredMemItems  = false;
    private boolean removeExpiredDiskItems = false;
    
    private long memExpirationInMillis  = 60 * 1000;
    private long diskExpirationInMillis = 60 * 60 * 1000;
    private long lastSavedTime;

    protected String diskCacheDirectory;

    private ConcurrentMap<KeyT, CacheItem<ValT>> cache;

    private String name;

    /**
     * Creates a new cache instance.
     * 
     * @param name
     *            a human readable identifier for this cache. Note that this value will be used to
     *            derive a directory name if the disk cache is enabled, so don't get too creative
     *            here (camel case names work great)
     * @param initialCapacity
     *            the initial element size of the cache
     * @param maxConcurrentThreads
     *            how many threads you think may at once access the cache; this need not be an exact
     *            number, but it helps in fragmenting the cache properly
     */
    public AbstractCache(String name, int initialCapacity, int maxConcurrentThreads) {
        this.name = name;
        MapMaker mapMaker = new MapMaker();
        mapMaker.initialCapacity(initialCapacity);
        mapMaker.concurrencyLevel(maxConcurrentThreads);
        mapMaker.softValues();
        this.cache = mapMaker.makeMap();
    }
    
    public AbstractCache<KeyT, ValT> setIsDiskCacheSynchronous(boolean isDiskCacheSynchronous) {
    	this.isDiskCacheSynchronous = isDiskCacheSynchronous;
    	return this;
    }
    
    public AbstractCache<KeyT, ValT> setRemoveExpiredMemItems(boolean removeExpiredMemItems) {
    	this.removeExpiredMemItems = removeExpiredMemItems;
    	return this;
    }
    
    public AbstractCache<KeyT, ValT> setRemoveExpiredDiskItems(boolean removeExpiredDiskItems) {
    	this.removeExpiredDiskItems = removeExpiredDiskItems;
    	return this;
    }
    
    public AbstractCache<KeyT, ValT> setMemExpirationInMillis(long memExpirationInMillis) {
    	this.memExpirationInMillis = memExpirationInMillis;
    	return this;
    }
    
    public AbstractCache<KeyT, ValT> setDiskExpirationInMillis(long diskExpirationInMillis) {
    	this.diskExpirationInMillis = diskExpirationInMillis;
    	return this;
    }

    /**
     * Sanitize disk cache. Remove files which are older than expirationInMinutes.
     */
    private void sanitizeDiskCache() {
        List<File> cachedFiles = getCachedFiles();
        for (File f : cachedFiles) {
        	long lastModified = f.lastModified();
        	if (System.currentTimeMillis() >= (lastModified + diskExpirationInMillis)) {
        		Log.d(name, "DISK cache expiration for file " + f.toString());
        		f.delete();
        	}
        }
	}

	/**
     * Enable caching to the phone's internal storage or SD card.
     * 
     * @param context
     *            the current context
     * @param storageDevice
     *            where to store the cached files, either {@link #DISK_CACHE_INTERNAL} or
     *            {@link #DISK_CACHE_SDCARD})
     * @return
     */
    public boolean enableDiskCache(Context context, int storageDevice, boolean isDiskCacheSynchronous) {
        Context appContext = context.getApplicationContext();

        String rootDir = null;
        if (storageDevice == DISK_CACHE_SDCARD
                && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // SD-card available
            rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/"
                    + appContext.getPackageName() + "/cache";
        } else {
            File internalCacheDir = appContext.getCacheDir();
            // apparently on some configurations this can come back as null
            if (internalCacheDir == null) {
                return (isDiskCacheEnabled = false);
            }
            rootDir = internalCacheDir.getAbsolutePath();
        }

        setRootDir(rootDir);

        File outFile = new File(diskCacheDirectory);
        if (outFile.mkdirs()) {
            File nomedia = new File(diskCacheDirectory, ".nomedia");
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed creating .nomedia file");
            }
        }

        isDiskCacheEnabled = outFile.exists();

        if (!isDiskCacheEnabled) {
            Log.w(LOG_TAG, "Failed creating disk cache directory " + diskCacheDirectory);
        } else {
            Log.d(name, "enabled write through to " + diskCacheDirectory);
            
            this.isDiskCacheSynchronous = isDiskCacheSynchronous;

            if (removeExpiredDiskItems) {
            	Log.d(name, "sanitize DISK cache");
            	sanitizeDiskCache();
            }
        }

        return isDiskCacheEnabled;
    }

    private void setRootDir(String rootDir) {
        this.diskCacheDirectory = rootDir + "/cachefu/"
                + IgnitedStrings.underscore(name.replaceAll("\\s", ""));
    }

    /**
     * Only meaningful if disk caching is enabled. See {@link #enableDiskCache}.
     * 
     * @return the full absolute path to the directory where files are cached, if the disk cache is
     *         enabled, otherwise null
     */
    public String getDiskCacheDirectory() {
        return diskCacheDirectory;
    }

    /**
     * Only meaningful if disk caching is enabled. See {@link #enableDiskCache}. Turns a cache key
     * into the file name that will be used to persist the value to disk. Subclasses must implement
     * this.
     * 
     * @param key
     *            the cache key
     * @return the file name
     */
    public abstract String getFileNameForKey(KeyT key);

    /**
     * Only meaningful if disk caching is enabled. See {@link #enableDiskCache}. Restores a value
     * previously persisted to the disk cache.
     * 
     * @param file
     *            the file holding the cached value
     * @return the cached value
     * @throws IOException
     */
    protected abstract ValT readValueFromDisk(File file) throws IOException;

    /**
     * Only meaningful if disk caching is enabled. See {@link #enableDiskCache}. Persists a value to
     * the disk cache.
     * 
     * @param ostream
     *            the file output stream (buffered).
     * @param value
     *            the cache value to persist
     * @throws IOException
     */
    protected abstract void writeValueToDisk(File file, ValT value) throws IOException;

    private boolean cacheToDisk(KeyT key, ValT value) {
        File file = new File(diskCacheDirectory + "/" + getFileNameForKey(key));
        try {
            file.createNewFile();
            file.deleteOnExit();

            writeValueToDisk(file, value);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Write all memory cache entries to disk synchronously
     */
    public synchronized boolean save() {
    	Log.d(name, "Saving MEM items to disk");
    	boolean success = true;
    	
    	for (Map.Entry<KeyT, CacheItem<ValT>> entry : cache.entrySet()) {
    		if (!cacheToDisk(entry.getKey(), entry.getValue().value)) {
    			success = false;
    			Log.w(name, "Failed to save MEM item " + entry.getKey().toString() + " to disk");
    		}
    	}
    	
    	lastSavedTime = System.currentTimeMillis();
    	return success;
    }

    public long getLastSavedTime() {
    	return lastSavedTime;
    }
    
    private File getFileForKey(KeyT key) {
        return new File(diskCacheDirectory + "/" + getFileNameForKey(key));
    }
    
    private boolean isExpired(long expiration) {
    	return (System.currentTimeMillis() >= expiration);
    }
    
    /**
     * Reads a value from the cache by first probing the in-memory cache. If not found, the the disk
     * cache will be probed. If it's a hit, the entry is written back to memory and returned.
     * 
     * @param elementKey the cache key
     * @return the cached value, or null if element was not cached
     */
    public synchronized ValT get(Object elementKey) {
    	return get(elementKey, true);
    }

    /**
     * Reads a value from the cache by first probing the in-memory cache.
     * If not found, the the disk cache can be probed if chosen. If it's a hit, 
     * the entry is written back to memory and returned.
     * 
     * @param elementKey the cache key
     * @param tryDiskCache try to fetch the key from disk cache too
     * @return the cached value, or null if element was not cached
     */
    @SuppressWarnings("unchecked")
    public synchronized ValT get(Object elementKey, boolean tryDiskCache) {
        KeyT key = (KeyT) elementKey;
        CacheItem<ValT> item = cache.get(key);
        
        if (item != null) {
        	if (isExpired(item.expiration)) {
        		Log.d(name, "MEM cache item " + key.toString() + " expired");
        		if (removeExpiredMemItems) {
        			Log.d(name, "Removing MEM cache item " + key.toString());
        			cache.remove(elementKey);
        		}
        	} else {
        		Log.d(name, "MEM cache hit for " + key.toString());
                return item.value;
        	}
        } else {
        	Log.d(name, "MEM cache miss for " + key.toString());
        }

        if (tryDiskCache) {
	        // memory miss, try reading from disk
	        File file = getFileForKey(key);
	        if (file.exists()) {
	        	if (removeExpiredDiskItems) {
	        		long lastModified = file.lastModified();
	        		if (System.currentTimeMillis() >= (lastModified + diskExpirationInMillis)) {
	        			Log.d(name, "DISK cache expiration for file " + file.toString());
		        		file.delete();
		        		return null;
	        		}
	        	}
	        	
	        	// disk hit
	            Log.d(name, "DISK cache hit for " + key.toString());
	            ValT value;
	            
	            try {
	            	value = readValueFromDisk(file);
	            } catch (IOException e) {
	                // treat decoding errors as a cache miss
	                e.printStackTrace();
	                return null;
	            }
	            
	            if (value == null) {
	                return null;
	            }
	            
	            // cache from disk have only one expiration time
	            item = new CacheItem<ValT>(value);
	            item.expiration = System.currentTimeMillis() + diskExpirationInMillis;
	            cache.put(key, item);
	            
	            return value;
	        } else {
	        	Log.d(name, "DISK cache miss for " + key.toString());
	        }
        }

        // cache miss
        return null;
    }
    
    /**
     * Writes an element to the cache with the default expiration time.
     * 
     * NOTE: If disk caching is enabled and is synchronous, this will write through to
     * the disk, which may introduce a performance penalty.
     * 
     * @param key the key to be stored
     * @param value the value to be stored
     * @return the stored value
     */
    public synchronized ValT put(KeyT key, ValT value) {
    	return put(key, value, memExpirationInMillis);
    }

    /**
     * Writes an element to the cache.
     * 
     * NOTE: If disk caching is enabled and is synchronous, this will write through to
     * the disk, which may introduce a performance penalty.
     * 
     * @param key the key to be stored
     * @param value the value to be stored
     * @param expiration the life time of the item in milliseconds
     * @return the stored value
     */
    public synchronized ValT put(KeyT key, ValT value, long expiration) {
        if (isDiskCacheEnabled && isDiskCacheSynchronous) {
            cacheToDisk(key, value);
        }
        
        Log.i(name, "Storing item in MEM cache");
        
        CacheItem<ValT> item = new CacheItem<ValT>(value);
        item.expiration = System.currentTimeMillis() + expiration;
        cache.put(key, item);
        return item.value;
    }

    public synchronized void putAll(Map<? extends KeyT, ? extends ValT> t) {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if a value is present in the cache. If the disk cached is enabled, this will also
     * check whether the value has been persisted to disk.
     * 
     * @param key the cache key
     * @return true if the value is cached in memory or on disk, false otherwise
     */
    public synchronized boolean containsKey(Object key) {
        return cache.containsKey(key) || containsKeyOnDisk(key);
    }

    /**
     * Checks if a value is present in the in-memory cache. This method ignores the disk cache.
     * 
     * @param key
     *            the cache key
     * @return true if the value is currently hold in memory, false otherwise
     */
    public synchronized boolean containsKeyInMemory(Object key) {
        return cache.containsKey(key);
    }

    /**
     * Checks if a value is present in the disk cache. This method ignores the memory cache.
     * 
     * @param key
     *            the cache key
     * @return true if the value is currently hold on disk, false otherwise. Always false if disk
     *         cache is disabled.
     */
    @SuppressWarnings("unchecked")
    public synchronized boolean containsKeyOnDisk(Object key) {
        return isDiskCacheEnabled && getFileForKey((KeyT) key).exists();
    }

    /**
     * Checks if the given value is currently held in memory. For performance reasons, this method
     * does NOT probe the disk cache.
     */
    public synchronized boolean containsValue(Object value) {
        return cache.containsValue(value);
    }
    
    /**
     * Removes an entry from memory. Removes from disk only if disk cache enabled
     * and synchronous.
     * 
     * @see AbstractCache#remove(Object, boolean)
     */
    public synchronized ValT remove(Object key) {
    	return remove(key, false);
    }

    /**
     * Removes an entry from both memory and disk.
     */
    @SuppressWarnings("unchecked")
    public synchronized ValT remove(Object key, boolean forceDisk) {
        ValT value = removeKey(key);

        if ((isDiskCacheEnabled && isDiskCacheSynchronous) || forceDisk) {
            File cachedValue = getFileForKey((KeyT) key);
            if (cachedValue.exists()) {
                cachedValue.delete();
            }
        }

        return value;
    }

    /**
     * Removes an entry from memory.
     * 
     * @param key the cache key
     * @return the element removed or null
     */
    public ValT removeKey(Object key) {
    	CacheItem<ValT> item = cache.remove(key);
        return (item == null) ? null : item.value;
    }

    public Set<KeyT> keySet() {
        return cache.keySet();
    }
    
    private Map<KeyT, ValT> toValueMap() {
    	Map<KeyT, ValT> map = new HashMap<KeyT, ValT>();
    	
    	for (Map.Entry<KeyT, CacheItem<ValT>> entry : cache.entrySet()) {
    		map.put(entry.getKey(), entry.getValue().value);
    	}
    	
        return map;
    }

    public Set<Map.Entry<KeyT, ValT>> entrySet() {
        return toValueMap().entrySet();
    }

    public synchronized int size() {
        return cache.size();
    }

    public synchronized boolean isEmpty() {
        return cache.isEmpty();
    }

    public boolean isDiskCacheEnabled() {
        return isDiskCacheEnabled;
    }

    /**
     * Retrieves the list of files that are currently cached to disk. Guarantees to never return
     * null.
     * 
     * @return the list of files on disk
     */
    public List<File> getCachedFiles() {
        File[] cachedFiles = new File(diskCacheDirectory).listFiles();
        if (cachedFiles == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(cachedFiles);
        }
    }

    /**
     * 
     * @param rootDir a folder name to enable caching or null to disable it.
     */
    public void setDiskCacheEnabled(String rootDir) {
        if (rootDir != null && rootDir.length() > 0) {
            setRootDir(rootDir);
            this.isDiskCacheEnabled = true;
        } else {
            this.isDiskCacheEnabled = false;
        }
    }

    /**
     * Clears the entire cache (memory and disk).
     */
    public synchronized void clear() {
        clear(isDiskCacheEnabled);
    }

    /**
     * Clears the memory cache, as well as the disk cache if it's enabled and
     * <code>removeFromDisk</code> is <code>true</code>.
     * 
     * @param removeFromDisk whether or not to wipe the disk cache, too
     */
    public synchronized void clear(boolean removeFromDisk) {
        cache.clear();

        if (removeFromDisk && isDiskCacheEnabled) {
            File[] cachedFiles = new File(diskCacheDirectory).listFiles();
            if (cachedFiles == null) {
                return;
            }
            for (File f : cachedFiles) {
                f.delete();
            }
        }

        Log.d(LOG_TAG, "Cache cleared");
    }

    public Collection<ValT> values() {
        return toValueMap().values();
    }
    
    private static class CacheItem<ValT> {
    	public ValT value;
    	public long expiration = -1; // unix epoch time
    	
    	public CacheItem(ValT value) {
    		this.value = value;
    	}
    }
}
