package com.github.nayasis.basica.thread.local;

import java.util.Observable;

public class ThreadLocalWatcher extends Observable {

	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}

	public void notifyObservers( String deliveredParameter ) {
		setChanged();
		super.notifyObservers( deliveredParameter );
	}

}
