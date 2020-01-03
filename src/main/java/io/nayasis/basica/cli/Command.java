package io.nayasis.basica.cli;

import io.nayasis.basica.base.Strings;
import io.nayasis.basica.cli.parser.CommandParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Command
 *
 * @author nayasis@gmail.com
 *
 */
public class Command {

	private List<String> command = new ArrayList<>();

	private StringBuffer outputPipe;
	private StringBuffer errorPipe;

	private File workingDirectory;

	public Command() {}

	public Command( String command ) {
		set( command );
	}

	public List<String> get() {
		return command;
	}

	public Command add( String command ) {
		this.command.add( command );
		return this;
	}

	/**
	 * add command in double quote.
	 *
	 * <pre>
	 *   addQuote( "mailto:google@gmail.com" );
	 *     -&gt; stored as "\"mailto:google@gmail.com\"".
	 * </pre>
	 * @param command	command
	 * @return self instance
	 */
	public Command addQuote( String command ) {
		this.command.add( String.format("\"%s\"", Strings.nvl(command).replaceAll("\"", "\\\"") ));
		return this;
	}

	public String toString() {
		return Strings.join( command, " " );
	}

	public Command set( String command ) {
		this.command = new CommandParser().parse( command );
		return this;
	}

	public Command set( List<String> command ) {
		this.command = command;
		return this;
	}

	public boolean hasCommand() {
		return command != null && command.size() > 0;
	}

	public StringBuffer getOutputPipe() {
		return outputPipe;
	}

	public Command setOutputPipe( StringBuffer redirectPipe ) {
		this.outputPipe = redirectPipe;
		return this;
	}

	public StringBuffer getErrorPipe() {
		return errorPipe;
	}

	public Command setErrorPipe( StringBuffer redirectPipe ) {
		this.errorPipe = redirectPipe;
		return this;
	}

	public File getWorkingDirectory() {
		if( workingDirectory == null ) return null;
		if( workingDirectory.isDirectory() ) return workingDirectory;
		if( workingDirectory.isFile() ) return workingDirectory.getParentFile();
		return null;
	}

	public Command setWorkingDirectory( File workingDirectory ) {
		this.workingDirectory = workingDirectory;
		return this;
	}

	public Command setWorkingDirectory( String workingDirectory ) {
	    if( Strings.isNotEmpty( workingDirectory ) ) {
	        this.workingDirectory = new File( workingDirectory );
        }
		return this;
	}


}
