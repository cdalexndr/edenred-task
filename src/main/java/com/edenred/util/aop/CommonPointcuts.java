package com.edenred.util.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CommonPointcuts {

    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional) || " +
            "@annotation(javax.transaction.Transactional)")
    public void isTransactionalMethod() {}

    @Pointcut("@target(org.springframework.transaction.annotation.Transactional) || " +
            "@target(javax.transaction.Transactional)")
    public void isTransactionalClass() {}

    @Pointcut("execution(public * *(..))")
    public void isPublicExecution() {}

    @Pointcut("execution(* com.edenred.data..*(..))")
    public void isDataLayer() {}
}
