package com.github.nayasis.basica.etc;

import com.github.nayasis.basica.model.NList;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StopWatch implements Serializable {

	private String       id        = null;
	private long         startTime = 0;
	private List<Log>    logs      = new ArrayList<>();

	@Getter @Setter @Accessors(fluent=true)
	private String taskName = null;

	@Getter @Setter @Accessors(fluent=true)
	private boolean enableLog = true;

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
		this.taskName  = taskName;
		this.startTime = System.nanoTime();
		return this;
	}

	public boolean isRunning() {
		return startTime != 0;
	}

	public boolean isNotRunning() {
		return ! isRunning();
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

		if( enableLog )
			logs.add( new Log(taskName, elapsedMiliSeconds()) );

		this.taskName  = null;
		this.startTime = 0;

		return this;

	}


	public long elapsedNanoSeconds() throws IllegalStateException {
		if( isNotRunning() ) {
			throw new IllegalStateException( "StopWatch is not running." );
		}
		return System.nanoTime() - startTime;
	}

	public long elapsedMicroSeconds() throws IllegalStateException {
		return elapsedNanoSeconds() / 1_000;
	}

	public long elapsedMiliSeconds() throws IllegalStateException {
		return elapsedNanoSeconds() / 1_000_000;
	}

	public double elapsedSeconds() throws IllegalStateException {
		return elapsedNanoSeconds() / 1_000_000_000.0;
	}

	public StopWatch reset() {
	    startTime = 0;
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
			list.addData( "ms",   String.format( "%6d", log.timeMillis ) );
			list.addData( "%",    String.format("%3d", log.percent )     );
			list.addData( "Task", log.taskName );
		}

		list.addData( "ms",   String.format( "%6d", (long) total ) );
		list.addData( "%",    ""                                   );
		list.addData( "Task", "TOTAL"                              );

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
