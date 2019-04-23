package io.nayasis.common.cli;

import io.nayasis.common.etc.Platform;
import io.nayasis.common.file.worker.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessOutputThread extends Thread {

	private static Logger log = LoggerFactory.getLogger( ProcessOutputThread.class );

	private InputStream  inputStream;
	private StringBuffer output;
	private LineReader   lineReader;

	/**
	 * constructor
	 *
	 * @param processOutputStream 	process output stream
	 * @param output 				memory to pile up process output
	 * @param lineReader 			lineReader to execute something based on process output
	 */
	public ProcessOutputThread( InputStream processOutputStream, StringBuffer output, LineReader lineReader ) {
		this.inputStream = processOutputStream;
		this.output      = output;
		this.lineReader  = lineReader;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		log.trace( "ProcessOutputThread({}) is start", Thread.currentThread().getName() );
		try {
			readInputStream( inputStream, lineReader );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		} finally {
			try { if (inputStream != null) inputStream.close(); } catch ( IOException e ) {}
			log.trace( "ProcessOutputThread({}) is closed", Thread.currentThread().getName() );
		}
	}

	/**
	 * convert input stream to text and put it to lineReader
	 *
	 * @param inputStream process output stream
	 * @param lineReader  line reader
	 */
	private void readInputStream( InputStream inputStream, LineReader lineReader ) {

		String buffer;

		try (
			BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream, Platform.osCharset) )
		){
			while ( ! isInterrupted() && (buffer = reader.readLine()) != null ) {
				if( output != null ) {
					output.append( buffer ).append( '\n' );
				}
				if( lineReader != null ) {
					lineReader.read( buffer );
				}
			}
		} catch ( IOException e ) {
			log.error( e.getMessage(), e );
			interrupt();
		}

	}

}