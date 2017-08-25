package com.example.aspects;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.transactions.Transaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;

@Aspect
public class TransactionAspect {

    //Аспект, который ищет методы, отмеченные аннотацией @TransactionalWrapper
    // и, собственно, оборачивает их в транзакцию
    //сам по себе малополезен, потому что в игнайте есть SpringTransactionalManager
    @Autowired
    private Ignite ignite;

    @Around("@annotation(com.example.aspects.TransactionWrapper)")
    public void wrapIgniteAccess(ProceedingJoinPoint joinPoint) throws Throwable{
        Transaction transaction = ignite.transactions().txStart();

        try{
            System.out.println(">>> Begin transaction");
            joinPoint.proceed();

            System.out.println(">>> Commit transaction");
            transaction.commit();

        } catch (Throwable throwable){
            System.out.println(">>> Catch throwable -> rollback transaction");
            transaction.rollback();

            throw throwable;

        } finally {
            System.out.println(">>> Close transaction");
            transaction.close();
        }
    }
}
