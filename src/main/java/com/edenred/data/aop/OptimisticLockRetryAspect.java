package com.edenred.data.aop;

import com.edenred.data.util.DbUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OptimisticLockRetryAspect {
    private static final Logger log = LoggerFactory.getLogger( OptimisticLockRetryAspect.class );
    private final int MAX_RETRIES = 5;
    private final long RETRY_DELAY_MS = 100;

    @Around("com.edenred.util.aop.CommonPointcuts.isDataLayer() && " +
            "com.edenred.util.aop.CommonPointcuts.isPublicExecution() && " +
            "com.edenred.util.aop.CommonPointcuts.isTransactionalMethod()")
    public Object onTransactionalMethod( ProceedingJoinPoint joinPoint ) throws Throwable {
        return handle( joinPoint );
    }

    @Around("com.edenred.util.aop.CommonPointcuts.isDataLayer() && " +
            "com.edenred.util.aop.CommonPointcuts.isPublicExecution() && " +
            "com.edenred.util.aop.CommonPointcuts.isTransactionalClass()")
    public Object onTransactionalClass( ProceedingJoinPoint joinPoint ) throws Throwable {
        return handle( joinPoint );
    }

    private boolean startsNewTransaction( ProceedingJoinPoint joinPoint ) {
        if (!DbUtils.isInsideTransaction())
            return true;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return startsNewTransaction( method );
    }

    private boolean startsNewTransaction( Method method ) {
        Transactional springTransactional = method.getAnnotation( Transactional.class );
        if (springTransactional != null)
            return springTransactional.propagation() == Propagation.REQUIRES_NEW;
        return false;
    }

    private Object handle( ProceedingJoinPoint joinPoint ) throws Throwable {
        if (!startsNewTransaction( joinPoint ))
            return joinPoint.proceed();

        int retries = MAX_RETRIES;
        while (retries > 0) {
            try {
                return joinPoint.proceed();
            } catch (OptimisticLockingFailureException ex) {
                log.info( String.format( "Optimistic lock exception. Retrying %d/%d", MAX_RETRIES - retries + 1, MAX_RETRIES ) );
                --retries;
                if (retries == 0)
                    throw ex;
                Thread.sleep( RETRY_DELAY_MS );
            }
        }
        throw new AssertionError( "Should not get here" );
    }
}
