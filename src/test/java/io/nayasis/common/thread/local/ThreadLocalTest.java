package io.nayasis.common.thread.local;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ThreadLocalTest {

    @Test
    public void test() throws InterruptedException {

        Map<String,Integer> result = new HashMap<>();

        Thread threadA = makeTestThread( "A",  5, result );
        Thread threadB = makeTestThread( "A", 10, result );

        threadA.start();
        threadB.start();

        Thread.sleep( 1_000 );

        Assert.assertEquals( 10, (int) result.get( "A-10" ) );
        Assert.assertEquals(  5, (int) result.get( "A-5"  ) );

        log.debug( result.toString() );

    }

    private Thread makeTestThread( String key, int count, Map<String,Integer> result ) {

        return new Thread( () -> {

            ThreadLocal.set( key, 0 );

            Thread child = new Thread( () -> {
                for( int i = 0; i < count; i++ ) {
                    Integer val = ThreadLocal.get( key );
                    ThreadLocal.set( key, ++val );
                    log.debug( "key : {}, count : {}, val : {}", key, count, val );
                    try {
                        Thread.sleep( 10 );
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
                result.put( key + "-" + count, ThreadLocal.get(key) );
            });

            child.start();

        });

    }


}

