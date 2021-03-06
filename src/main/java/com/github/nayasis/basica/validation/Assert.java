package com.github.nayasis.basica.validation;

import com.github.nayasis.basica.base.Strings;
import com.github.nayasis.basica.file.Files;

import java.io.File;

/**
 * Assertion checker to assists in validation arguments
 */
public class Assert {

	public static void isNull( Object object, Object... errorMessage ) throws IllegalArgumentException {
		if( object != null ) throwException( errorMessage );
	}

	public static void notNull( Object object, Object... errorMessage ) throws IllegalArgumentException {
		if( object == null ) throwException( errorMessage );
	}

	public static void exists( File filePath, Object... errorMessage ) throws IllegalArgumentException {
		if( Files.notExists(filePath) ) throwException( errorMessage );
	}

	public static void notExists( File filePath, Object... errorMessage ) throws IllegalArgumentException {
		if( Files.exists(filePath) ) throwException( errorMessage );
	}

	public static void exists( String filePath, Object... errorMessage ) throws IllegalArgumentException {
		if( Files.notExists(filePath) ) throwException( errorMessage );
	}

	public static void notExists( String filePath, Object... errorMessage ) throws IllegalArgumentException {
		if( Files.exists(filePath) ) throwException( errorMessage );
	}

	public static void beTrue( boolean result, Object... errorMessage ) throws IllegalArgumentException {
		if( ! result ) throwException( errorMessage );
	}

	public static void notTrue( boolean result, Object... errorMessage ) throws IllegalArgumentException {
		if( result ) throwException( errorMessage );
	}

	public static void beTrue( BiFunction logic, Object... errorMessage ) throws IllegalArgumentException {
		try {
			if( ! logic.run() ) throwException( errorMessage );
		} catch( Throwable t ) {
			throwException( t, errorMessage );
		}
	}

	public static void notTrue( BiFunction logic, Object... errorMessage ) throws IllegalArgumentException {
		try {
			if( logic.run() ) throwException( errorMessage );
		} catch( Throwable t ) {
			throwException( t, errorMessage );
		}
	}

	public static void empty( Object object, Object... errorMessage ) throws IllegalArgumentException {
		if( Validator.isNotEmpty(object) ) throwException( errorMessage );
	}

	public static void notEmpty( Object object, Object... errorMessage ) throws IllegalArgumentException {
		if( Validator.isEmpty(object) ) throwException( errorMessage );
	}


	private static void throwException( Object... errorMessage  ) throws IllegalArgumentException {

		if( errorMessage == null || errorMessage.length == 0 ) {
			throw new IllegalArgumentException();
		} else if( errorMessage[0] instanceof Throwable ) {
			if( errorMessage.length == 1 ) {
				if( errorMessage[0] instanceof RuntimeException ) {
					throw (RuntimeException) errorMessage[0];
				} else {
					throw new IllegalArgumentException( (Throwable) errorMessage[0] );
				}
			} else {
				throw new IllegalArgumentException( Strings.format(errorMessage[1], shiftParam( errorMessage, 2 )), (Throwable) errorMessage[0] );
			}
		} else if( errorMessage.length == 1 ) {
			throw new IllegalArgumentException( Strings.format(errorMessage[0]) );
		} else {
			throw new IllegalArgumentException( Strings.format(errorMessage[0], shiftParam( errorMessage )) );
		}

	}

	private static Object[] shiftParam( Object[] parameter ) {
		return shiftParam( parameter, 1 );
	}

	private static Object[] shiftParam( Object[] parameter, int shiftCount ) {
		int size = parameter.length - shiftCount;
		if( size <= 0 ) return new Object[0];
		Object[] param = new Object[size];
		System.arraycopy( parameter, shiftCount, param, 0, size );
		return param;
	}

}