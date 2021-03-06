package com.github.nayasis.basica.reflection.core;

import com.github.nayasis.basica.base.Classes;
import com.github.nayasis.basica.cache.implement.LruCache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Unmodifiable checker for Map or Collection
 *
 * @author nayasis@gmail.com
 * @since 2017-04-03
 */
public class UnmodifiableChecker {

    private static LruCache<Class,Boolean> CACHE = new LruCache<>( 64 );

    @SuppressWarnings("unchecked")
    public static boolean isUnmodifiable( Map map ) {

        if( map == null ) return true;

        Class<? extends Map> klass = map.getClass();

        if( ! CACHE.contains(klass) ) {
            Map instance = Classes.createInstance( klass );
            try {
                instance.putAll( new HashMap() );
                CACHE.putIfAbsent( klass, Boolean.FALSE );
            } catch( Exception e ) {
                CACHE.putIfAbsent( klass, Boolean.TRUE );
            }
        }

        return CACHE.get( klass );

    }

    @SuppressWarnings("unchecked")
    public static boolean isUnmodifiable( Collection collection ) {

        if( collection == null ) return true;

        Class<? extends Collection> klass = collection.getClass();

        if( ! CACHE.contains(klass) ) {
            Collection instance = Classes.createInstance( klass );
            try {
                instance.addAll( Collections.emptyList() );
                CACHE.putIfAbsent( klass, Boolean.FALSE );
            } catch( Exception e ) {
                CACHE.putIfAbsent( klass, Boolean.TRUE );
            }
        }

        return CACHE.get( klass );

    }

}
