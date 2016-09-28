package com.snovelli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by Salvatore on 02/05/2016.
 */
@Component
class ShutdownManager {

    @Autowired
    private ApplicationContext appContext;

    public void initiateShutdown(int returnCode) {
        SpringApplication.exit(appContext, () -> returnCode);
    }
}