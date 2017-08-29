package com.example.util;

/**
*  Допустим, был какой-то очень полезный класс
* А потом мы захотели сделать его еще более полезным, добавив поле и изменив метод для вывода содержимого
* Но объекты этого класса уже лежат в кеше
* Не проблема - уже имеющиеся объекты можно изменить и кеш будет не против
 */
public class SomeClass {

    private Integer integerField;
    private String stringField;
    private InnerObjectClass composedObj;

    public SomeClass(Integer integerField, String stringField){
        this.integerField = integerField;
        this.stringField = stringField;
    }

    public void setIntegerField(Integer integerField) {
        this.integerField = integerField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public void setInnerObj(InnerObjectClass composedObj) {
        this.composedObj = composedObj;
    }

    private String newField;

    public void printlnContent(){
        System.out.println(">>> Integer field = " + integerField + " and stringField = " + stringField
                + " and newField = " + newField);
    }

    public void setNewField(String newField) {
        this.newField = newField;
    }

}
