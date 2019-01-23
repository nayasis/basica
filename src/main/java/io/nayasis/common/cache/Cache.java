package io.nayasis.common.cache;



/**
 * Interface that defines common cache operations.<br><br>
 *
 * <b>Note:</b> Due to the generic use of caching, it is recommended that
 * implementations allow storage of <tt>null</tt> values (for example to
 * cache methods that return {@code null}).
 *
 * @author nayasis@gmail.com
 *
 */
public interface Cache<K,V> {

	int size();

	void setCapacity( int capacity );

	void setFlushCycle( int seconds );

	boolean contains( K key );

	void put( K key, V value );

	void putIfAbsent( K key, V value );

	V get( K key );

	void clear( K key );

	void clear();

}