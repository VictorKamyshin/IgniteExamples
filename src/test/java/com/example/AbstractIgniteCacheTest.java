package com.example;

import org.apache.ignite.Ignite;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=IgniteConfigurationTest.class)
public abstract class AbstractIgniteCacheTest {

    @Autowired
    protected Ignite ignite;

    @Before
    public void beforeCacheClearing(){
        ignite.destroyCaches(ignite.cacheNames());
    }
    //очистит все кеши перед запуском тестов

}
