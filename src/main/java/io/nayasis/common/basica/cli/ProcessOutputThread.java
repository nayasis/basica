package io.nayasis.common.basica.cli;

import io.nayasis.common.basica.etc.Platform;
import io.nayasis.common.basica.file.worker.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

public class ProcessOutputThread extends Thread {

	private static Logger log = LoggerFactory.getLogger( ProcessOutputThread.class );

	private InputStream    inputStream;
	private StringBuffer   output;
	private LineReader     lineReader;
	private CountDownLatch latch;

	/**
	 * constructor
	 *
	 * @param processOutputStream 	process output stream
	 * @param output 				memory to pile up process output
	 * @param lineReader 			lineReader to execute something based on process output
	 * @param latch 			    thread counter
	 */
	public ProcessOutputThread( InputStream processOutputStream, StringBuffer output, LineReader lineReader, CountDownLatch latch ) {
		this.inputStream = processOutputStream;
		this.output      = output;
		this.lineReader  = lineReader;
		this.latch       = latch;
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
			latch.countDown();
		} catch ( IOException e ) {
			log.error( e.getMessage(), e );
			interrupt();
		}

	}

}