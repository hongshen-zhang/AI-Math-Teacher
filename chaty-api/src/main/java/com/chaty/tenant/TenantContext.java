package com.chaty.tenant;

public class TenantContext {
    private static final ThreadLocal<Boolean> IGNORE_TENANT = new ThreadLocal<>();

    public static void setIgnoreTenant(Boolean ignore) {
        IGNORE_TENANT.set(ignore);
    }

    public static Boolean getIgnoreTenant() {
        return IGNORE_TENANT.get();
    }

    public static void clear() {
        IGNORE_TENANT.remove();
    }
}

