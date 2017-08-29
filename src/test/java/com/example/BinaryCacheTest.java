package com.example;

import com.example.util.InnerObjectClass;
import com.example.util.SomeClass;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.cache.processor.MutableEntry;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=IgniteConfigurationTest.class)
public class BinaryCacheTest {

    @Autowired
    protected Ignite ignite;

    @Before
    public void beforeTest(){
        ignite.active(true);
        //указывает ноде играйта, что не надо ждать, пока загрузятся остальные ноды кластера
    }

    @Test
    public void putInPersistentStore(){
        IgniteCache<Integer, SomeClass> cache1 = ignite.getOrCreateCache("cache1");
        Set<Integer> keySet = getKeys();
        for(Integer key: keySet){
            SomeClass tmp = new SomeClass(key*3,"Some string value" + key);
            tmp.setInnerObj(new InnerObjectClass(3+key, "composed string" + key));
            cache1.put(key,tmp);
        }
        getAllFromCacheBinary(cache1.withKeepBinary(), keySet);
    }

    @Test
    public void modificationBinaryCacheContent(){
        IgniteCache<Integer, BinaryObject> cache1 = ignite.getOrCreateCache("cache1").withKeepBinary();
        Set<Integer> keySet = getKeys();

        cache1.invokeAll(keySet, (MutableEntry<Integer,BinaryObject> entry, Object... objects) -> {
                //сами по себе бинарные объекты игнайта - Immutable
                //но можно получить билдер с копией исходного объекта, который уже можно менять
                BinaryObjectBuilder builder = entry.getValue().toBuilder();

                BinaryObject innerObj = entry.getValue().field("composedObj");
                //вложенный объект так же будет бинарным

                BinaryObjectBuilder innerBuilder = innerObj.toBuilder();

                innerBuilder.setField("string","newStringValue2");
                innerBuilder.setField("integer",4+entry.getKey());
                //значения полей и сами поля можно добавлять/удалять в рантайме, не перезапуская ноды с кешом
                InnerObjectClass tmp = innerBuilder.build().deserialize();

                System.out.println("Value type is "+innerObj.type().typeName());
                //есть что-то в духе рефлекшна, то есть можно узнать, к какому классу принадлежал
                //бинарный объект до того, как стать бинарным, названия всех его полей и т.д.
                builder.setField("composedObj",(InnerObjectClass)innerBuilder.build().deserialize());
                builder.setField("integerField",12);
                entry.setValue(builder.build());
                return null;
                //в принципе, на место значения можно подсунуть бинарный объект,
                //который не коответсвует value-классу, но десериализовать его потом не получится
                //ни в объект, классом которого он был, ни в объект, который, по идее, хранится в кеше
            });
        getAllFromCacheBinary(cache1, keySet);
    }

    public void getAllFromCacheBinary(IgniteCache<Integer,BinaryObject> cache1, Set<Integer> keySet){
        for(Integer key:keySet){
            BinaryObject bo = cache1.get(key);
            SomeClass tmp = bo.deserialize();
            tmp.printlnContent();

            //System.out.println(">>>  "+(String)bo.toBuilder().getField("testField"));
            //Можно не десериализовывая объект достать какие-то его поля
            //Даже если это объект другого класса, чем предполагается хранить к кеше
            //Если при десериализации у объекта присутсвуют "лишние" поля, то они будут проигнорированны
            //Бинарная сериализация игнорирует регистр в названиях полей - https://apacheignite.readme.io/docs/binary-marshaller
        }
    }

    private Set<Integer> getKeys(){
        Set<Integer> keySet = new HashSet<>();
        keySet.add(3);
        keySet.add(4);
        return keySet;
    }


}