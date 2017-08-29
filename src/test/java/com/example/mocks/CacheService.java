package com.example.mocks;

import com.example.aspects.TransactionWrapper;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * просто бин для демонстрации работы аннотаций
 */
public class CacheService {


    @Autowired
    private Ignite ignite;

    private IgniteCache<Integer,String> cache;

    public void createCache(String cacheName){
        CacheConfiguration<Integer, String> cfg = new CacheConfiguration<>(cacheName);

        cache = ignite.getOrCreateCache(cfg);
    }

//    @TransactionWrapper
    @Transactional
    public void put(Integer key, String value){
        cache.put(key,value);
    }

//    @TransactionWrapper
    @Transactional(rollbackFor = Exception.class)
    public void putWithException(Integer key, String value) throws Exception{
        cache.put(key, value);
        throw new Exception("oops");
    }

    public String get(Integer key){
        return cache.get(key);
    }
}
