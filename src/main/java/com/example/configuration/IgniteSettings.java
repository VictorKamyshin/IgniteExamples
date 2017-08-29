package com.example.configuration;

import com.example.aspects.TransactionAspect;
import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.BinaryConfiguration;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.PersistentStoreConfiguration;
import org.apache.ignite.logger.NullLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.sharedfs.TcpDiscoverySharedFsIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.spring.SpringTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class IgniteSettings {


    //директория, в которой будут храниться файлы на диске
    @Value("work")
    private String path;


//    @Bean
    public TransactionAspect transactionAspect(){
        return new TransactionAspect();
    }

    @Bean
    public SpringTransactionManager transactionManager(IgniteConfiguration configuration){
        SpringTransactionManager transactionManager = new SpringTransactionManager();

        transactionManager.setConfiguration(configuration);

        transactionManager.setTransactionConcurrency(TransactionConcurrency.OPTIMISTIC);

        return transactionManager;
    }

//    @Bean
    public IgniteConfiguration igniteConfiguration(){
        //просто "легкая" конфигурация для тестов
        IgniteConfiguration configuration = new IgniteConfiguration();

        //отключает ведение журнала удаленных элементов в кеше или что-то такое
        //судя по советам на форуме - должно сократить количество запусков сборщика мусора
        System.setProperty(IgniteSystemProperties.IGNITE_ATOMIC_CACHE_DELETE_HISTORY_SIZE,"0");

        //простой ipFinder, который не нагружает ноду поиском других нод
        //другие ноды смогут ее увидеть, но она сама особо пристально никого искать не будет
        TcpDiscoverySharedFsIpFinder ipFinder = new TcpDiscoverySharedFsIpFinder();

        TcpDiscoverySpi spi = new TcpDiscoverySpi();

        spi.setIpFinder(ipFinder);

        configuration.setDiscoverySpi(spi);

        return configuration;
    }

    @Bean
    public IgniteConfiguration persistentConfiguration(){
        //конфигурация с сохранением данных кеша на диск
        IgniteConfiguration configuration = new IgniteConfiguration();

        System.setProperty(IgniteSystemProperties.IGNITE_ATOMIC_CACHE_DELETE_HISTORY_SIZE,"0"); //?

        TcpDiscoverySharedFsIpFinder ipFinder = new TcpDiscoverySharedFsIpFinder();

        TcpDiscoverySpi spi = new TcpDiscoverySpi();

        spi.setIpFinder(ipFinder);

        configuration.setDiscoverySpi(spi);

        PersistentStoreConfiguration pscfg = new PersistentStoreConfiguration();

        pscfg.setPersistentStorePath(path);

        configuration.setPersistentStoreConfiguration(pscfg);

        return configuration;
    }
}
