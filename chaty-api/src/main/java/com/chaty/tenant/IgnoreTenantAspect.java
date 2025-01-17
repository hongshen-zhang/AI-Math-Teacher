package com.chaty.tenant;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class IgnoreTenantAspect {

    @Before("@annotation(com.chaty.tenant.IgnoreTenant)")
    public void beforeMethod() {
        TenantContext.setIgnoreTenant(true);
    }

    @After("@annotation(com.chaty.tenant.IgnoreTenant)")
    public void afterMethod() {
        TenantContext.clear();
    }
}

