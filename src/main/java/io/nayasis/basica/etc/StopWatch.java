package io.nayasis.basica.etc;

import io.nayasis.basica.model.NList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StopWatch implements Serializable {

	private String       id        = null;
	private long         startTime = 0;
	private String       taskName  = null;
	private List<Log>    logs      = new ArrayList<>();

	public StopWatch() {}

	public StopWatch( String id ) {
		this.id = id;
	}

	public StopWatch start() throws IllegalStateException {
		return start( "" );
	}

	public StopWatch start( String taskName ) throws IllegalStateException {
		if ( this.taskName != null ) {
			this.stop();
		}
		this.taskName = taskName;
		this.startTime = System.nanoTime();
		return this;
	}

	public String currentTaskName() {
		return taskName;
	}

	public StopWatch stop() throws IllegalStateException {
		if ( this.taskName == null ) {
			String error = null;
			if( id == null ) {
				error = "StopWatch is not running.";
			} else {
				error = String.format( "StopWatch[%s] is not running.", id );
			}
			throw new IllegalStateException( error );
		}

		logs.add( new Log(taskName, elapsedMiliSeconds()) );

		this.taskName  = null;
		this.startTime = 0;

		return this;

	}


	public long elapsedNanoSeconds() throws IllegalStateException {
		if( startTime == 0 ) {
			throw new IllegalStateException( "StopWatch is not running." );
		}
		return System.nanoTime() - startTime;
	}

	public long elapsedMiliSeconds() throws IllegalStateException {
		return elapsedNanoSeconds() / 1_000_000;
	}

	public double elapsedSeconds() throws IllegalStateException {
		return elapsedNanoSeconds() / 1_000_000_000.0;
	}

	public StopWatch reset() {
	    startTime = System.nanoTime();
		logs.clear();
	    return this;
	}

	public String toString() {

		double total = 0.;

		for( Log log : logs )
			total += log.timeMillis;

		int remainPercent = 100;

		for( int i = 0, last = logs.size() - 1; i <= last; i++ ) {
			Log log = logs.get( i );
			if( i == last ) {
				log.percent = remainPercent;
			} else {
				log.percent = (int) ( log.timeMillis / total * 100 );
				remainPercent -= log.percent;
			}
		}

		NList list = new NList();
		for( Log log : logs ) {
			list.add( "ms",   String.format( "%6d", log.timeMillis ) );
			list.add( "%",    String.format("%3d", log.percent )     );
			list.add( "Task", log.taskName );
		}

		list.add( "ms",   String.format( "%6d", (long) total ) );
		list.add( "%",    ""                                   );
		list.add( "Task", "TOTAL"                              );

		return list.toString( true, true );

	}

	private static class Log implements Serializable {

		public long   timeMillis;
		public String taskName;
		public int    percent;

		public Log( String taskName, long timeMillis ) {
			this.taskName = taskName;
			this.timeMillis = timeMillis;
		}

	}

}
