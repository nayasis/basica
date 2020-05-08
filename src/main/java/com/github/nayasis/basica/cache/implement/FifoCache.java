package com.github.nayasis.basica.cache.implement;

import java.util.LinkedHashMap;
import java.util.Map;

public class FifoCache<K,V> extends LruCache<K,V> {

	@Override
	public void setCapacity( int capacity ) {
		map = new LinkedHashMap<K,V>( capacity, .75F, false ) {
            private static final long serialVersionUID = -1041533883153692789L;
			@Override
			protected boolean removeEldestEntry( Map.Entry<K,V> eldest ) {
				return size() > capacity;
			}
		};
	}

}
