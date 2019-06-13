package io.nayasis.common.basica.reflection.mapper;

import java.lang.reflect.Method;

public interface MethodInvocator {
	Object invoke( Object proxy, Method method, Object[] arguments ) throws Throwable;
}
