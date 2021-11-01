package com.github.f1xman.gringotts.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

@RequiredArgsConstructor(onConstructor_ = {@JsonCreator})
@Getter
public class ExecuteTransaction {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<ExecuteTransaction> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.github.f1xman.gringotts.transaction.command/ExecuteTransaction"),
            mapper::writeValueAsBytes,
            bytes -> mapper.readValue(bytes, ExecuteTransaction.class)
    );

    @JsonProperty("debtor")
    private final String debtor;
    @JsonProperty("creditor")
    private final String creditor;
    @JsonProperty("amount")
    private final Long amount;

}
