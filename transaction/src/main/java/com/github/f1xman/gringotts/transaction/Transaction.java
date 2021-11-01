package com.github.f1xman.gringotts.transaction;

import com.github.f1xman.gringotts.transaction.account.AccountAddress;
import com.github.f1xman.gringotts.transaction.account.ApplyTransaction;
import com.github.f1xman.gringotts.transaction.account.TransactionAccepted;
import lombok.RequiredArgsConstructor;
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

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class Transaction implements StatefulFunction {

    public static final TypeName TYPE = TypeName.typeNameFromString("com.github.f1xman.gringotts.transaction/Transaction");
    public static final ValueSpec<String> DEBTOR = ValueSpec.named("debtor").withUtf8StringType();
    public static final ValueSpec<String> CREDITOR = ValueSpec.named("creditor").withUtf8StringType();
    public static final ValueSpec<Long> AMOUNT = ValueSpec.named("amount").withLongType();

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) {
        if (message.is(ExecuteTransaction.TYPE)) {
            ExecuteTransaction executeTransaction = message.as(ExecuteTransaction.TYPE);
            initTransaction(executeTransaction, context);
            withdrawFromDebtor(executeTransaction, context);
        } else if (message.is(TransactionAccepted.TYPE) && isFromDebtor(context)) {
            putToCreditor(context);
        } else if (message.is(TransactionAccepted.TYPE) && isFromCreditor(context)) {
            completeTransaction(context);
        }
        return context.done();
    }

    private void initTransaction(ExecuteTransaction executeTransaction, Context context) {
        AddressScopedStorage storage = context.storage();
        storage.set(AMOUNT, executeTransaction.getAmount());
        storage.set(DEBTOR, executeTransaction.getDebtor());
        storage.set(CREDITOR, executeTransaction.getCreditor());
    }

    private void withdrawFromDebtor(ExecuteTransaction executeTransaction, Context context) {
        Message applyTransaction = MessageBuilder.forAddress(AccountAddress.fromId(executeTransaction.getDebtor()))
                .withCustomType(ApplyTransaction.TYPE, new ApplyTransaction(
                        executeTransaction.getAmount() * -1,
                        context.self().id()
                ))
                .build();
        context.send(applyTransaction);
    }

    private boolean isFromDebtor(Context context) {
        AddressScopedStorage storage = context.storage();
        String debtor = storage.get(DEBTOR).orElseThrow();
        String caller = context.caller().map(Address::id).orElseThrow();
        return debtor.equals(caller);
    }

    private void putToCreditor(Context context) {
        AddressScopedStorage storage = context.storage();
        String creditor = storage.get(CREDITOR).orElseThrow();
        Long amount = storage.get(AMOUNT).orElseThrow();
        Message applyTransaction = MessageBuilder.forAddress(AccountAddress.fromId(creditor))
                .withCustomType(ApplyTransaction.TYPE, new ApplyTransaction(amount, context.self().id()))
                .build();
        context.send(applyTransaction);
    }

    private boolean isFromCreditor(Context context) {
        AddressScopedStorage storage = context.storage();
        String creditor = storage.get(CREDITOR).orElseThrow();
        String caller = context.caller().map(Address::id).orElseThrow();
        return creditor.equals(caller);
    }

    private void completeTransaction(Context context) {
        String id = context.self().id();
        log.info("Transaction {} completed", id);
    }
}












