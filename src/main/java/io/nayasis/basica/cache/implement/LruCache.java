package io.nayasis.basica.cache.implement;

import io.nayasis.basica.cache.Cache;
import io.nayasis.basica.etc.StopWatch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple LRU Cache
 *
 * @param <K> Key type parameter
 * @param <V> Value type parameter
 */
public class LruCache<K,V> implements Cache<K,V>, Serializable {

	protected Map<K,V>         map            = null;
	protected Map<K,StopWatch> creationTimes  = new HashMap<>();
	private   int              flushCycle     = Integer.MAX_VALUE;
	private   boolean          hasFlushCycle  = false;

	public LruCache( int capacity ) {
		setCapacity( capacity );
	}

	public LruCache() {
		setCapacity( 128 );
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public void setCapacity( int capacity ) {
		map = new LinkedHashMap<K,V>( capacity, .75F, true ) {
			private static final long serialVersionUID = 8870565267049463277L;
			@Override
			protected boolean removeEldestEntry( Map.Entry<K,V> eldest ) {
				return size() > capacity;
			}
		};
	}

	@Override
	public void setFlushCycle( int seconds ) {
		this.flushCycle    = seconds;
		this.hasFlushCycle = seconds != Integer.MAX_VALUE;
	}

	@Override
	public boolean contains( K key ) {
		checkFlushTime( key );
		return map.containsKey( key );
	}

	private void checkFlushTime( K key ) {
		if( ! hasFlushCycle ) return;
		if( getWatcher(key).elapsedSeconds() >= flushCycle )
			clear( key );
	}

	@Override
	public void put( K key, V value ) {
		map.put( key, value );
		resetAccessTime( key );
	}

	private StopWatch getWatcher( K key ) {
		if( ! creationTimes.containsKey( key ) ) {
			creationTimes.put( key, new StopWatch().start() );
		}
		return creationTimes.get( key );
	}

	private void resetAccessTime( K key ) {
		if( ! hasFlushCycle ) return;
		getWatcher( key ).reset();
	}

	@Override
	public void putIfAbsent( K key, V value ) {
		map.putIfAbsent( key, value );
		resetAccessTime( key );
	}

	@Override
	public V get( K key ) {
		checkFlushTime( key );
		return map.get( key );
	}

	@Override
	public void clear( K key )	{
		map.remove( key );
		creationTimes.remove( key );
	}

	@Override
	public void clear() {
		map.clear();
		creationTimes.clear();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public void putAll( Map<K,V> map ) {
		map.keySet().forEach( key -> {
			put( key, map.get(key) );
		});
	}

	@Override
	public void putAll( Cache<K, V> cache ) {
		cache.keySet().forEach( key -> {
			put( key, cache.get(key) );
		});
	}

}