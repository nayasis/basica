package io.nayasis.basica.cli;

import io.nayasis.basica.etc.Platforms;
import io.nayasis.basica.exception.unchecked.CommandLineException;
import io.nayasis.basica.file.worker.LineReader;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * OS command line executor
 *
 * @author nayasis@gmail.com
 */
@Slf4j
public class CommandExecutor {

	private Process             process      = null;
	private BufferedWriter      inputPipe    = null;

	private ProcessOutputThread outputThread = null;
	private ProcessOutputThread errorThread  = null;
	private CountDownLatch      latch        = new CountDownLatch(2);

	/**
	 * run command
	 *
	 * @param commandLine command line
	 * @return self instance
	 */
	public CommandExecutor run( String commandLine ) {
		return run( commandLine, null, null );
	}

	/**
	 * run command
	 *
	 * @param commandLine command line
	 * @param lineReader  reader to execute something based on process output
	 * @return self instance
	 */
	public CommandExecutor run( String commandLine, LineReader lineReader ) {
		return run( commandLine, null, lineReader );
	}

	/**
	 * run command
	 *
	 * @param commandLine	command
	 * @param output		memory to pile up process output
	 * @return self instance
	 */
	public CommandExecutor run( String commandLine, StringBuffer output ) {
	    return run( commandLine, output, null );
	}

	/**
	 * run command
	 *
	 * @param commandLine	command line
	 * @param output		memory to pile up process output
	 * @param lineReader    reader to execute something based on process output
	 * @return self instance
	 */
	public CommandExecutor run( String commandLine, StringBuffer output, LineReader lineReader ) {
		Command command = new Command();
		command.set( commandLine );
		command.setOutputPipe( output );
		return run( command, lineReader );
	}

	/**
	 * run command
	 *
	 * @param command command to execute
	 * @return self instance
	 */
	public CommandExecutor run( Command command ) throws CommandLineException {
		return run( command, null );
	}

	/**
	 * run command
	 *
	 * @param command 		command to execute
	 * @param lineReader	line reader
	 * @return self instance
	 */
	public CommandExecutor run( Command command, LineReader lineReader ) throws CommandLineException {

		if( command == null ) return this;

		if( isAlive() ) throw new CommandLineException( "pre-command is still running" );

		if( ! command.hasCommand() ) throw new CommandLineException( "there is no command to execute" );

		log.trace( "Command Line : {}", command );

		try {

			ProcessBuilder builder = new ProcessBuilder( command.get() );

			if( command.getWorkingDirectory() != null ) {
				builder.directory( command.getWorkingDirectory() );
			}

			process = builder.start();

			errorThread = new ProcessOutputThread( process.getErrorStream(), command.getErrorPipe(), lineReader, latch );
			errorThread.setDaemon( true );

			outputThread = new ProcessOutputThread( process.getInputStream(), command.getOutputPipe(), lineReader, latch );
			outputThread.setDaemon( true );

			errorThread.start();
			outputThread.start();

			inputPipe = new BufferedWriter( new OutputStreamWriter( process.getOutputStream(), Platforms.osCharset ) );

			return this;

		} catch ( IOException e ) {
			throw new CommandLineException( e, "It happens ERROR while executing command ({})", command );
		}

	}

	/**
	 * check whether process is executing or not.
	 *
	 * @return true if process is executing.
	 */
	public boolean isAlive() {
		if( process      != null && process.isAlive()      ) return true;
		if( outputThread != null && outputThread.isAlive() ) return true;
		return errorThread != null && errorThread.isAlive();
	}

	/**
	 * get process termination code
	 *
	 * @return the exit value of the subprocess represented by this
     *         {@code Process} object.  By convention, the value
     *         {@code 0} indicates normal termination.
     * @throws IllegalThreadStateException if the subprocess represented
     *         by this {@code Process} object has not yet terminated
	 */
	public int getExitValue() {
		if( process == null ) throw new IllegalThreadStateException( "process is null." );
		return process.exitValue();
	}

	/**
	 * wait until process is closed.
	 *
	 * @param timeout	max wait time (mili-seconds)
	 * @return	process termination code ( 0 : success )
	 */
	public int waitFor( Integer timeout ) {

		if( ! isAlive() ) return 0;

		int exitValue = 0;

		try {
			exitValue = process.waitFor();
		} catch ( InterruptedException e ) {
			process.destroy();
		} finally {
			Thread.interrupted();
		}

		try {
			if( timeout == null ) {
				latch.await();
			} else {
				latch.await( timeout, TimeUnit.MILLISECONDS );
			}
		} catch ( Throwable e ) {
			throw new RuntimeException( e );
		} finally {
			destroy();
		}

		return exitValue;

	}

	/**
	 * wait until process is closed.
	 *
	 * @return	process termination code ( 0 : success )
	 */
	public int waitFor() {
		return waitFor( null );
	}

	/**
	 * terminate process forcibly.
	 */
	public void destroy() {

		if( process != null ) {
			process.destroyForcibly();
			process = null;
		}

		destroyThread( outputThread );
		destroyThread( errorThread );

		if( inputPipe != null ) {
			try {
				inputPipe.close();
			} catch( IOException e ) {
				log.error( e.getMessage(), e );
			} finally {
				inputPipe = null;
			}
		}

	}

	private void destroyThread( ProcessOutputThread thread ) {
		if( thread == null ) return;
		try {
			thread.interrupt();
		} catch ( Throwable e ) {}
	}

	/**
	 * send command to process
	 *
	 * @param command command
	 * @return self instance
	 */
	public CommandExecutor sendCommand( String command ) {

		if( inputPipe == null ) return this;

		log.trace( "command to send : {}", command );

		try {

			inputPipe.write( command );
			inputPipe.write( "\n" );
	        inputPipe.flush();

		} catch( IOException e ) {
        	log.error( e.getMessage(), e );
        }

		return this;

	}

}
