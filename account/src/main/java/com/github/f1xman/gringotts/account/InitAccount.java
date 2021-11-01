package com.github.f1xman.gringotts.account;

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
public class InitAccount {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<InitAccount> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.github.f1xman.gringotts.account.command/InitAccount"),
            mapper::writeValueAsBytes,
            bytes -> mapper.readValue(bytes, InitAccount.class)
    );

    @JsonProperty("amount")
    private final Long amount;
}
