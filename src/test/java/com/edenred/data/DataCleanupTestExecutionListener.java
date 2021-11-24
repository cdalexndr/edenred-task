package com.edenred.data;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class DataCleanupTestExecutionListener implements TestExecutionListener {
    @Override public void afterTestMethod( TestContext testContext ) {
        ApplicationContext ctx = testContext.getApplicationContext();
        ctx.getBean( UserRepository.class ).deleteAll();
        ctx.getBean( ProductRepository.class ).deleteAll();
        ctx.getBean( OrderRepository.class ).deleteAll();
    }
}
