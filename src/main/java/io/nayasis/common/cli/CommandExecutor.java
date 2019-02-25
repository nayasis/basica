package io.nayasis.common.cli;

import io.nayasis.common.etc.Platform;
import io.nayasis.common.exception.unchecked.CommandLineException;
import io.nayasis.common.file.worker.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * OS command line executor
 *
 * @author nayasis@gmail.com
 */
public class CommandExecutor {

	private static Logger log = LoggerFactory.getLogger( CommandExecutor.class );

	private Process        process         = null;
	private BufferedWriter processPipe     = null;

	private ProcessOutputThread outputThread    = null;
	private ProcessOutputThread errorThread     = null;

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
	 * @param worker      worker to execute something based on process output
	 * @return self instance
	 */
	public CommandExecutor run( String commandLine, LineReader worker ) {
		return run( commandLine, null, worker );
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
	 * @param worker        worker to execute something based on process output
	 * @return self instance
	 */
	public CommandExecutor run( String commandLine, StringBuffer output, LineReader worker ) {

		Command command = new Command();

		command.set( commandLine );
		command.setOutputPipe( output );
		command.setWorker( worker );

		return run( command );

	}

	/**
	 * run command
	 *
	 * @param command command to execute
	 * @return self instance
	 */
	public CommandExecutor run( Command command ) {

		if( command == null ) return this;

		if( isAlive() ) throw new CommandLineException( "pre-command is still running" );

		if( ! command.hasCommand() ) throw new CommandLineException( "there is no command to execute" );

		log.debug( "Command Line : {}", command );

		try {

			ProcessBuilder builder = new ProcessBuilder( command.get() );

			if( command.getWorkingDirectory() != null ) {
				builder.directory( command.getWorkingDirectory() );
			}

			process = builder.start();

			errorThread = new ProcessOutputThread( process.getErrorStream(), command.getErrorPipe(), command.getWorker() );
			errorThread.setDaemon( true );

			outputThread = new ProcessOutputThread( process.getInputStream(), command.getOutputPipe(), command.getWorker() );
			outputThread.setDaemon( true );

			errorThread.start();
			outputThread.start();

			processPipe = new BufferedWriter( new OutputStreamWriter( process.getOutputStream(), Platform.osCharset ) );

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

		waitThread( outputThread, timeout );
		waitThread( errorThread, timeout );

		destroy();

		return exitValue;

	}

	private void waitThread( ProcessOutputThread thread, Integer timeOut ) {
		if( thread == null || ! thread.isAlive() ) return;
		try {
			if( timeOut == null ) {
				thread.join();
			} else {
				thread.join( timeOut );
			}
        } catch( InterruptedException e ) {
			thread.interrupt();
        }
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

		if( processPipe != null ) {
			try {
				processPipe.close();
			} catch( IOException e ) {
				log.error( e.getMessage(), e );
			} finally {
				processPipe = null;
			}
		}

	}

	private void destroyThread( ProcessOutputThread thread ) {
		if( thread == null ) return;
		thread.interrupt();
	}

	/**
	 * send command to process
	 *
	 * @param command command
	 * @return self instance
	 */
	public CommandExecutor sendCommand( String command ) {

		if( processPipe == null ) return this;

		log.trace( "command to send : {}", command );

		try {

			processPipe.write( command );
			processPipe.write( "\n" );
	        processPipe.flush();


		} catch( IOException e ) {
        	log.error( e.getMessage(), e );
        }

		return this;

	}

}
