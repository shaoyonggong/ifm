package com.syg.ifmclient.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Component
public class AsyncConfig implements AsyncConfigurer {
    protected static Integer integer= 50;

    @Override
    public Executor getAsyncExecutor() {
        ExecutorService service = Executors.newFixedThreadPool(integer);
        return service;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new MyAsyncExceptionHandler();
    }

    class MyAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        }
    }
}
