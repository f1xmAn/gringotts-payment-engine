package com.github.f1xman.gringotts.account;

import org.apache.flink.statefun.sdk.java.StatefulFunctionSpec;
import org.apache.flink.statefun.sdk.java.StatefulFunctions;
import org.apache.flink.statefun.sdk.java.handler.RequestReplyHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }

    @Bean
    RequestReplyHandler requestReplyHandler(Account account) {
        StatefulFunctions statefulFunctions = new StatefulFunctions();
        StatefulFunctionSpec accountSpec = StatefulFunctionSpec.builder(Account.TYPE)
                .withValueSpec(Account.BALANCE)
                .withSupplier(() -> account)
                .build();
        statefulFunctions.withStatefulFunction(accountSpec);
        return statefulFunctions.requestReplyHandler();
    }
}
