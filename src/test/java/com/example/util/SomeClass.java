package com.example.util;

public class SomeClass {

    private Integer integerField;
    private String stringField;
    private InnerObjectClass composedObj;
    //для поля newField отсутствуют сеттеры или инициализация в конструкторе
    //но значение ему можно присвоить уже после добавления в кэш
    //с помощью средств игнайта и BinaryBuilder
    private String newField;

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

    public void printlnContent(){
        System.out.println(">>> Integer field = " + integerField + " and stringField = " + stringField
                + " and newField = " + newField);
    }

    public String getNewField(){
        return newField;
    }

}
