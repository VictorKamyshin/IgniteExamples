package com.example;

import com.example.util.SomeClass;
import com.example.mocks.CacheService;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CacheTest extends AbstractIgniteCacheTest {

    @Autowired
    private CacheService cacheServiceMock;

    @Test
    public void createDestroyCachesTest() throws Exception {

        for (Integer i = 0; i < 3; i++) {
            ignite.getOrCreateCache("cache" + i);
        }

        System.out.println(">>> CacheNames :");
        for (String name : ignite.cacheNames()) {
            System.out.println("CacheName = " + name);
        }

        ignite.destroyCaches(ignite.cacheNames());

        assert (ignite.cacheNames().isEmpty());
    }

    @Test
    public void putGetTest() throws Exception {

        try (IgniteCache<Integer, String> cache = ignite.getOrCreateCache("cache1")) {
            System.out.println();
            System.out.println(">>> Cache put-get example started.");

            final int keyCnt = 20;

            for (int i = 0; i < keyCnt; i++)
                cache.put(i, Integer.toString(i));

            System.out.println(">>> Stored values in cache.");

            for (int i = 0; i < keyCnt; i++) {
                System.out.println("Got [key=" + i + ", val=" + cache.get(i) + ']');
                assert (Integer.toString(i).equals(cache.get(i)));
            }
            cache.close();
        }

    }

    @Test
    public void servicePutGetTest(){
        cacheServiceMock.createCache("cache1");
        cacheServiceMock.put(1,"1");
        assert(cacheServiceMock.get(1).equals("1"));
    }

    @Test
    public void transactionTest(){
        cacheServiceMock.createCache("cache1");
        try {
            cacheServiceMock.putWithException(1, "1");
        }catch (Exception e){
            assert(e.getMessage().equals("oops"));
        }
        assert(cacheServiceMock.get(1)==null);
    }






}
