package com.github.f1xman.gringotts.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

@RequiredArgsConstructor(onConstructor_ = @JsonCreator)
@Getter
@ToString
public class Wallet {

    public static final Wallet EMPTY = new Wallet(0L);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<Wallet> TYPE = SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.github.f1xman.gringotts.account/Wallet"),
            mapper::writeValueAsBytes,
            bytes -> mapper.readValue(bytes, Wallet.class)
    );

    @JsonProperty("amount")
    private final Long amount;

    public Wallet apply(Long delta) {
        Long newAmount = this.amount + delta;
        return new Wallet(newAmount);
    }
}
