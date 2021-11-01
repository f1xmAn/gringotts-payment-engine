package com.github.f1xman.gringotts.account;

import com.github.f1xman.gringotts.account.transaction.ApplyTransaction;
import com.github.f1xman.gringotts.account.transaction.TransactionAccepted;
import com.github.f1xman.gringotts.account.transaction.TransactionDeclined;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.AddressScopedStorage;
import org.apache.flink.statefun.sdk.java.Context;
import org.apache.flink.statefun.sdk.java.StatefulFunction;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.ValueSpec;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class Account implements StatefulFunction {

    public static final TypeName TYPE = TypeName.typeNameFromString("com.github.f1xman.gringotts.account/Account");
    public static final ValueSpec<Long> BALANCE = ValueSpec.named("balance").withLongType();

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) {
        if (message.is(InitAccount.TYPE)) {
            initializeAccount(message.as(InitAccount.TYPE), context);
        } else if (message.is(ApplyTransaction.TYPE)) {
            applyTransaction(message.as(ApplyTransaction.TYPE), context);
        }
        return context.done();
    }

    private void initializeAccount(InitAccount initAccount, Context context) {
        Long amount = initAccount.getAmount();
        AddressScopedStorage storage = context.storage();
        storage.set(BALANCE, amount);
    }

    private void applyTransaction(ApplyTransaction applyTransaction, Context context) {
        Long amount = applyTransaction.getAmount();
        AddressScopedStorage storage = context.storage();
        Long currentAmount = storage.get(BALANCE).orElseThrow();
        Long newAmount = currentAmount + amount;
        Address caller = context.caller().orElseThrow();
        if (newAmount >= 0) {
            storage.set(BALANCE, newAmount);
            Message transactionAccepted = MessageBuilder.forAddress(caller)
                    .withCustomType(TransactionAccepted.TYPE, new TransactionAccepted(new Date()))
                    .build();
            context.send(transactionAccepted);
        } else {
            Message transactionDeclined = MessageBuilder.forAddress(caller)
                    .withCustomType(TransactionDeclined.TYPE, new TransactionDeclined("The balance is insufficient"))
                    .build();
            context.send(transactionDeclined);
        }
    }

}
