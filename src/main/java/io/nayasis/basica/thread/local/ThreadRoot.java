package io.nayasis.basica.thread.local;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class ThreadRoot implements Observer {

	public static final String WATCHER_KEY = ThreadRoot.class.getName() + ".WATCHER_KEY";

	private static final java.lang.ThreadLocal threadRoot = new InheritableThreadLocal<String>() {

		public String initialValue() {
	        return UUID.randomUUID().toString();
	    }

		protected String childValue( String parentValue ) {
	    	return parentValue == null || parentValue.length() == 0 ? UUID.randomUUID().toString() : parentValue;
	    }

	};

	static {
		ThreadLocal.addObserver( new ThreadRoot() );
	}

	@Override
	public void update( Observable watcher, Object deliveredParameter ) {
		if( ! WATCHER_KEY.equals( deliveredParameter ) ) return;
		threadRoot.remove();
	}

	public static String getKey() {
		return (String) threadRoot.get();
	}


}