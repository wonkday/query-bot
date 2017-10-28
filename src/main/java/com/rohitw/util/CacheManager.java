package com.rohitw.util;

import org.apache.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.*;

/**
 * Created by ROHITW on 1/12/2017.
 */
public enum CacheManager
{
    INSTANCE;

    private static Logger logger = Logger.getLogger(CacheManager.class);
    //final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private ConcurrentMap<Object,Object> cacheMap;
    private ConcurrentMap<Object,Object> metadataCache;

    private static final int NTHREDS = 1;
    private static final int intCacheCleanIntervalMillis = 1000 * 60 * 60;

    CacheManager(){
        cacheMap = new ConcurrentHashMap(10,(float) 0.25);
        metadataCache = new ConcurrentHashMap(10,(float) 0.25);

        ScheduledExecutorService scheduledExecutor =  Executors.newScheduledThreadPool(NTHREDS);
        final CacheCleaner cacheCleaner = new CacheCleaner();
        scheduledExecutor.scheduleAtFixedRate
                (
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                               String msg = cacheCleaner.call();
                                logger.info("Execution of " + msg);
                            }
                        },
                        0, (intCacheCleanIntervalMillis), TimeUnit.MILLISECONDS
                );
    }

    class CacheCleaner implements Callable<String>
    {
        @Override
        public String call()
        {
            try
            {
                CacheManager cacheManager = CacheManager.INSTANCE;
                cacheManager.clearCache();
            }
            catch (Exception e)
            {
                logger.error("Exception while cleaning cache", e);
            }
            return "CacheCleaner DONE!";
        }
    }

    public synchronized void clearCache()
    {
        cacheMap.clear();
    }

    public Object getItemFromCache(Object key)
    {
        return cacheMap.get(key);
    }

    public void addItemToCache(Object key, Object value)
    {
        cacheMap.putIfAbsent(key,value);
    }

    public Object getItemFromMetadataCache(Object key)
    {
        return metadataCache.get(key);
    }

    public void addItemToMetadataCache(Object key, Object value)
    {
        metadataCache.put(key,value);
    }

    public void clearMetadataCache(){
        synchronized (metadataCache){
            metadataCache.clear();
        }
    }
}
