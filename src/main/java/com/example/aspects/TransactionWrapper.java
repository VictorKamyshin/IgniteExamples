package com.example.aspects;

import java.lang.annotation.*;

@Target(value= ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface TransactionWrapper {

}
