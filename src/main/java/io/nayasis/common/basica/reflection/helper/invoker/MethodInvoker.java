package io.nayasis.common.basica.reflection.helper.invoker;

import java.lang.reflect.Method;

public interface MethodInvoker {
	Object invoke( Object proxy, Method method, Object[] arguments ) throws Throwable;
}
