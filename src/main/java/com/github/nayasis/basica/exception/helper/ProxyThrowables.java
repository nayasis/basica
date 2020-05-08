package com.github.nayasis.basica.exception.helper;

import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;

public class ProxyThrowables {

    public String toString( Throwable throwable ) {
        ThrowableProxy proxy = new ThrowableProxy( throwable );
        proxy.calculatePackagingData();
        return ThrowableProxyUtil.asString( proxy );
    }

}
