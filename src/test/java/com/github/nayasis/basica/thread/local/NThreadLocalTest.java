package com.github.nayasis.basica.thread.local;

import com.github.nayasis.basica.thread.local.NThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class NThreadLocalTest {

    @Test
    public void test() throws InterruptedException {

        Map<String,Integer> result = new HashMap<>();

        Thread threadA = makeTestThread( "A",  5, result );
        Thread threadB = makeTestThread( "A", 10, result );

        threadA.start();
        threadB.start();

        Thread.sleep( 5_000 );

        assertEquals( 10, (int) result.get( "A-10" ) );
        assertEquals(  5, (int) result.get( "A-5"  ) );

        log.debug( result.toString() );

    }

    private Thread makeTestThread( String key, int count, Map<String,Integer> result ) {

        return new Thread( () -> {

            NThreadLocal.set( key, 0 );

            Thread child = new Thread( () -> {
                for( int i = 0; i < count; i++ ) {
                    Integer val = NThreadLocal.get( key );
                    NThreadLocal.set( key, ++val );
                    log.debug( "key : {}, count : {}, val : {}", key, count, val );
                    try {
                        Thread.sleep( 10 );
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
                result.put( key + "-" + count, NThreadLocal.get(key) );
            });

            child.start();

        });

    }


}

