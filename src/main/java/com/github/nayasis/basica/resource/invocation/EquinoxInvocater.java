package com.github.nayasis.basica.resource.invocation;

import com.github.nayasis.basica.reflection.core.ClassReflector;
import com.github.nayasis.basica.base.Classes;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.URL;

@UtilityClass
@Slf4j
public class EquinoxInvocater {

    private static Method EQUINOX_RESOLVE_METHOD;

    static {
        try {
            // Detect Equinox OSGi (e.g. on WebSphere 6.1)
            Class<?> fileLocator   = Classes.forName( "org.eclipse.core.runtime.FileLocator" );
            EQUINOX_RESOLVE_METHOD = fileLocator.getMethod("resolve", URL.class );
            log.trace("Found Equinox FileLocator for OSGi bundle URL resolution");
        } catch ( Throwable e ) {
            EQUINOX_RESOLVE_METHOD = null;
        }
    }

    public boolean isEquinoxUrl( URL url ) {
        return url != null && EQUINOX_RESOLVE_METHOD != null && url.getProtocol().startsWith("bundle");
    }

    public URL unwrap( URL url ) {
        if( isEquinoxUrl(url) ) {
            Object rtn = ClassReflector.invokeMethod( EQUINOX_RESOLVE_METHOD, null, url );
            return rtn == null ? null : (URL) rtn;
        } else {
            return url;
        }
    }

}
