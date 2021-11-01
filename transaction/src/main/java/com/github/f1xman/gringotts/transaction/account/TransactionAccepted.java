package com.github.f1xman.gringotts.transaction.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import java.util.Date;

@RequiredArgsConstructor(onConstructor_ = {@JsonCreator})
@Getter
public class TransactionAccepted {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<TransactionAccepted> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.github.f1xman.gringotts.account.transaction/TransactionAccepted"),
            mapper::writeValueAsBytes,
            bytes -> mapper.readValue(bytes, TransactionAccepted.class)
    );

    @JsonProperty("acceptedAt")
    private final Date acceptedAt;
}
