package com.boardhood.mobile.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.boardhood.api.util.CacheProvider;

public class StringCache extends AbstractCache<String, String> implements CacheProvider<String, String> {

    /**
     * Creates an {@link AbstractCache} with params provided and name 'ModelCache'.
     * 
     * @see com.github.droidfu.cachefu.AbstractCache#AbstractCache(java.lang.String, int, long, int)
     */
    public StringCache(int initialCapacity, int maxConcurrentThreads) {
        super("StringCache", initialCapacity, maxConcurrentThreads);
    }

    /**
     * @see com.github.droidfu.cachefu.AbstractCache#getFileNameForKey(java.lang.Object)
     */
    @Override
    public String getFileNameForKey(String url) {
        return CacheHelper.getFileNameFromUrl(url);
    }

    /**
     * @see com.github.droidfu.cachefu.AbstractCache#readValueFromDisk(java.io.File)
     */
    @Override
    protected String readValueFromDisk(File file) throws IOException {
        return convertStreamToString( new FileInputStream(file));
    }

    /**
     * @see com.github.droidfu.cachefu.AbstractCache#writeValueToDisk(java.io.File,
     *      java.lang.Object)
     */
    @Override
    protected void writeValueToDisk(File file, String data) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(data);
        out.close();
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            	throw e;
            }
        }
        return sb.toString();
    }
}