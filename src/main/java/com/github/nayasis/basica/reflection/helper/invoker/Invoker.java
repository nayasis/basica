package com.github.nayasis.basica.reflection.helper.invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

public class Invoker<T> implements InvocationHandler {

    private T             originalInstance;
    private MethodInvoker methodInvoker;

    public Invoker( T instance, MethodInvoker methodInvoker ) {
        this.originalInstance = instance;
        this.methodInvoker = methodInvoker;
    }

    public T getOriginalInstance() {
        return originalInstance;
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] arguments ) throws Throwable {
        try {
            return methodInvoker.invoke( proxy, method, arguments );
        } catch ( Throwable e ) {
            throw unwrapInvokeThrowable( e );
        }
    }

    private Throwable unwrapInvokeThrowable( Throwable throwable ) {

        Throwable unwrappedThrowable = throwable;

        while (true) {

            if ( unwrappedThrowable instanceof InvocationTargetException) {
                unwrappedThrowable = ((InvocationTargetException) unwrappedThrowable).getTargetException();

            } else if ( unwrappedThrowable instanceof UndeclaredThrowableException) {
                unwrappedThrowable = ((UndeclaredThrowableException) unwrappedThrowable).getUndeclaredThrowable();

            } else {
                return unwrappedThrowable;
            }

        }

    }

}
