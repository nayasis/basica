package io.nayasis.common.cache.implement;

import io.nayasis.common.cache.Cache;
import io.nayasis.common.etc.StopWatch;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K,V> implements Cache<K,V> {

	protected Map<K,V>         map            = null;
	protected Map<K,StopWatch> creationTimes  = new HashMap<>();
	private   int              flushCycle     = Integer.MAX_VALUE;
	private   boolean          hasFlushCycle  = false;

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
		if( hasFlushCycle ) {
			if( getWatcher(key).elapsedSeconds() >= flushCycle ) {
				clear( key );
				return null;
			}
		}
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

}
