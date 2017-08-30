package com.example;

import com.example.util.InnerObjectClass;
import com.example.util.SomeClass;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.cache.processor.MutableEntry;
import java.util.HashSet;
import java.util.Set;


public class BinaryCacheTest extends AbstractIgniteCacheTest{

    @Autowired
    protected Ignite ignite;

    @Before
    public void beforeTest(){
        //указывает ноде играйта, что не надо ждать, пока загрузятся остальные ноды кластера
        ignite.active(true);
    }

    @Test
    public void modificationBinaryCacheContent(){
        putInPersistentStore();

        IgniteCache<Integer, BinaryObject> cache1 = ignite.getOrCreateCache("cache1").withKeepBinary();
        Set<Integer> keySet = getKeys();

        cache1.invokeAll(keySet, (MutableEntry<Integer,BinaryObject> entry, Object... objects) -> {

                //сами по себе бинарные объекты игнайта - Immutable
                //но можно получить Builder с копией исходного объекта, который уже можно менять
                BinaryObjectBuilder builder = entry.getValue().toBuilder();

                //вложенный объект так же будет бинарным
                BinaryObject innerObj = entry.getValue().field("composedObj");

                BinaryObjectBuilder innerBuilder = innerObj.toBuilder();

                innerBuilder.setField("string","newStringValue2");
                innerBuilder.setField("integer",4+entry.getKey());

                //значения полей и сами поля можно добавлять/удалять в рантайме, не перезапуская ноды с кешом
                //но как воспроизвести ситуацию с изменением определения класса в рамках одного теста я не знаю

                //Теоретически, возможен следующий сценарий:
                //берем конфигурацию игнайта с сохранением данных на диск
                //за первый запуск теста пишем в кеш объект класса SomeClass
                //потом меняем определение SomeClass в коде, например, добавляем поле
                //во втором запуске добавляем это поле в объекты, уже лежащие в кеше
                //и проверяем, что у объектов, которые мы достали из кеша это поле тоже есть
                InnerObjectClass tmp = innerBuilder.build().deserialize();

                //есть что-то в духе рефлекшна, то есть можно узнать, к какому классу принадлежал
                //бинарный объект до того, как стать бинарным, названия всех его полей и т.д.
                System.out.println("Value type is "+innerObj.type().typeName());

                //при этом чтобы присвоить новое значение полю надо передавать его значение как есть
                //то есть строку, число, объект - билдер сам все сериализует как надо
                builder.setField("composedObj",(InnerObjectClass)innerBuilder.build().deserialize());

                builder.setField("integerField",13);

                //в момент заполнения кеша данными поле newField в объекте могло вообще не существовать
                //или не иметь значения
                //но с помощью Builder-а в него можно записать что угодно
                //за правилностью типов и имен полей мы следим сами
                //Builder с окай-фейсом запишет что угодно куда угодно
                builder.setField("newField","newFieldValue");

                //поля можно удалять в рантайме
                //но удалить поле типа String и потом создать поле с таким же именем и типом Integer нельзя
                //builder.removeField("newField");


                //в принципе, в метод setValue можно подсунуть бинарный объект,
                //который не коответсвует value-классу, но десериализовать его потом не получится
                //ни в объект, классом которого он был, ни в объект, который должен был хранится в кеше

                //Если, например, присвоить полю с типом String число, то метод build() выбросит исключение
                entry.setValue(builder.build());
                return null;

            });


        for(Integer key:keySet){
            BinaryObject bo = cache1.get(key);

            //Можно не десериализовывая объект достать какие-то его поля
            //Даже если это объект другого класса, чем предполагается хранить к кеше
            //Если при десериализации у объекта присутсвуют "лишние" поля, то они будут проигнорированны
            //Бинарная сериализация игнорирует регистр в названиях полей - https://apacheignite.readme.io/docs/binary-marshaller
            SomeClass tmp = bo.deserialize();
            tmp.printlnContent();
            //убеждаемся, что в поле, не имеющем сеттеров появилось добавленное через кеш значение
            assert("newFieldValue".equals(tmp.getNewField()));
        }
    }

    public void putInPersistentStore(){
        IgniteCache<Integer, SomeClass> cache1 = ignite.getOrCreateCache("cache1");
        Set<Integer> keySet = getKeys();
        for(Integer key: keySet){
            SomeClass tmp = new SomeClass(key*3,"Some string value" + key);
            tmp.setInnerObj(new InnerObjectClass(3+key, "composed string" + key));
            cache1.put(key,tmp);
        }
    }

    private Set<Integer> getKeys(){
        Set<Integer> keySet = new HashSet<>();
        keySet.add(3);
        keySet.add(4);
        return keySet;
    }

}