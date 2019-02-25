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
	private StringBuffer message;
	private LineReader   worker;

	/**
	 * constructor
	 *
	 * @param processOutputStream 	process output stream
	 * @param messageStorage 		memory to pile up process output
	 * @param worker 				worker to execute something based on process output
	 */
	public ProcessOutputThread( InputStream processOutputStream, StringBuffer messageStorage, LineReader worker ) {
		this.inputStream = processOutputStream;
		this.message     = messageStorage;
		this.worker      = worker;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		log.trace( "ProcessOuputThread({}) is start", Thread.currentThread().getName() );

		try {

			readInputStream( inputStream, message, worker );

		} catch ( Exception e ) {
			log.error( e.getMessage(), e );

		} finally {
			try { if (inputStream != null) inputStream.close(); } catch ( IOException e ) {}
			log.trace( "ProcessOuputThread({}) is closed", Thread.currentThread().getName() );
		}
	}

	/**
	 * convert input stream to text and put it to worker
	 *
	 * @param inputStream process output stream
	 * @param message     memory to pile up process output
	 * @return 문자
	 */
	private void readInputStream( InputStream inputStream, StringBuffer message, LineReader worker ) {
		String  buffer;
		try (
			BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream, Platform.osCharset) )
		){
			while ( ! isInterrupted() && (buffer = reader.readLine()) != null ) {
				if( message != null ) {
					message.append( buffer ).append( '\n' );
				}
				if( worker != null ) {
					worker.read( buffer );
				}
			}
		} catch ( IOException e ) {
			log.error( e.getMessage(), e );
			interrupt();
		}
	}

}
