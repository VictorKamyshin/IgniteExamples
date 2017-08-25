package com.example.util;

public class InnerObjectClass {

    private Integer integer;

    private String string;

    public InnerObjectClass(Integer integer, String string) {
        this.integer = integer;
        this.string = string;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void showContent(){
        System.out.println(">>> Inner object values is: integer = " + integer + " and string = " + string);
        System.out.println();
    }
}
