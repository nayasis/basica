package com.github.nayasis.basica.etc;

import com.github.nayasis.basica.base.Strings;
import com.github.nayasis.basica.model.NList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class StopWatch implements Serializable {

	private long      startTime = 0;
	private List<Log> logs      = new ArrayList<>();

	@Getter @Setter @Accessors(fluent=true)
	private String task = null;

	@Getter @Setter @Accessors(fluent=true)
	private boolean logEnable = true;

	public StopWatch simple() {
		return logEnable(false).start();
	}

	public StopWatch start() {
		stop();
		this.startTime = System.nanoTime();
		return this;
	}

	public StopWatch start( String task ) {
		stop();
		this.task      = task;
		this.startTime = System.nanoTime();
		return this;
	}

	public boolean isRunning() {
		return startTime != 0;
	}

	public boolean isNotRunning() {
		return ! isRunning();
	}

	public StopWatch stop() {

		if( isNotRunning() ) return this;

		if( logEnable )
			logs.add( new Log( task, elapsedMilliSeconds()) );

		this.task = null;
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

	public long elapsedMilliSeconds() throws IllegalStateException {
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

		if( logEnable ) {

			double total = 0.;

			for( Log log : logs )
				total += log.milisec;

			int remainPercent = 100;

			for( int i = 0, last = logs.size() - 1; i <= last; i++ ) {
				Log log = logs.get( i );
				if( i == last ) {
					log.percent = remainPercent;
				} else {
					log.percent = (int) ( log.milisec / total * 100 );
					remainPercent -= log.percent;
				}
			}

			NList list = new NList();
			for( Log log : logs ) {
				list.addData( "ms",   String.format( "%6d", log.milisec ) );
				list.addData( "%",    String.format("%3d",  log.percent ) );
				list.addData( "Task", log.task );
			}

			list.addData( "ms",   String.format( "%6d", (long) total ) );
			list.addData( "%",    ""                                   );
			list.addData( "Task", "TOTAL"                              );

			return list.toString( true, true );

		} else {
			if( task != null ) {
				return String.format( "%s : %d ms", task, elapsedMilliSeconds() );
			} else {
				return String.format( "%d ms", elapsedMilliSeconds() );
			}
		}

	}

	private static class Log implements Serializable {

		public long   milisec;
		public String task;
		public int    percent;

		public Log( String task, long millis ) {
			this.task    = Strings.nvl( task );
			this.milisec = millis;
		}

	}

}
