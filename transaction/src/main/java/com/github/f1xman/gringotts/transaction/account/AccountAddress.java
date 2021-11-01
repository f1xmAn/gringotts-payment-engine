package com.github.f1xman.gringotts.transaction.account;

import lombok.experimental.UtilityClass;
import org.apache.flink.statefun.sdk.java.Address;
import org.apache.flink.statefun.sdk.java.TypeName;

@UtilityClass
public class AccountAddress {

    private static final TypeName TYPE = TypeName.typeNameFromString("com.github.f1xman.gringotts.account/Account");

    public static Address fromId(String id) {
        return new Address(TYPE, id);
    }

}
