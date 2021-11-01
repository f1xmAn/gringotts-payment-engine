package com.github.f1xman.gringotts.transaction;

import org.apache.flink.statefun.sdk.java.StatefulFunctionSpec;
import org.apache.flink.statefun.sdk.java.StatefulFunctions;
import org.apache.flink.statefun.sdk.java.handler.RequestReplyHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }

    @Bean
    RequestReplyHandler requestReplyHandler(Transaction transaction) {
        StatefulFunctions statefulFunctions = new StatefulFunctions();
        StatefulFunctionSpec accountSpec = StatefulFunctionSpec.builder(Transaction.TYPE)
                .withSupplier(() -> transaction)
                .withValueSpecs(Transaction.AMOUNT, Transaction.CREDITOR, Transaction.DEBTOR)
                .build();
        statefulFunctions.withStatefulFunction(accountSpec);
        return statefulFunctions.requestReplyHandler();
    }

}
