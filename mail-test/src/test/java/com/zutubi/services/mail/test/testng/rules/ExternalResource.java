package com.zutubi.services.mail.test.testng.rules;

import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;

/**
 *
 */
public abstract class ExternalResource implements IHookable {

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        try {
            try {
                before();
                callBack.runTestMethod(testResult);
            } finally {
                after();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void before() throws Throwable {

    }

    protected void after() throws Throwable {

    }
}
