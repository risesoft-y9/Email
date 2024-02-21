package net.risesoft.support;

import com.alibaba.ttl.TransmittableThreadLocal;

public class EmailThreadLocalHolder {

    private static final TransmittableThreadLocal<String> emailAddressThreadLocal =
        new TransmittableThreadLocal<String>();

    public static String getEmailAddress() {
        return emailAddressThreadLocal.get();
    }

    public static void setEmailAddress(String emailAddress) {
        emailAddressThreadLocal.set(emailAddress);
    }

}
