package io.nayasis.common.cli;

import io.nayasis.common.base.Strings;
import io.nayasis.common.cli.parser.CommandParser;
import io.nayasis.common.file.worker.LineReader;

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

	private LineReader worker;

	private File workingDirectory;

	public List<String> get() {
		return command;
	}

	public void add( String command ) {
		this.command.add( command );
	}

	public void addPath( String path ) {
		this.command.add( String.format("\"%s\"", path) );
	}

	public String toString() {
		return Strings.join( command, " " );
	}

	public void set( String command ) {
		this.command = new CommandParser().parse( command );
	}

	public void set( List<String> command ) {
		this.command = command;
	}

	public boolean hasCommand() {
		return command != null && command.size() > 0;
	}

	public StringBuffer getOutputPipe() {
		return outputPipe;
	}

	public void setOutputPipe( StringBuffer redirectPipe ) {
		this.outputPipe = redirectPipe;
	}

	public StringBuffer getErrorPipe() {
		return errorPipe;
	}

	public void setErrorPipe( StringBuffer redirectPipe ) {
		this.errorPipe = redirectPipe;
	}

	public LineReader getWorker() {
		return worker;
	}

	public void setWorker( LineReader worker ) {
		this.worker = worker;
	}

	public File getWorkingDirectory() {
		if( workingDirectory == null ) return null;
		if( workingDirectory.isDirectory() ) return workingDirectory;
		if( workingDirectory.isFile() ) return workingDirectory.getParentFile();
		return null;
	}

	public void setWorkingDirectory( File workingDirectory ) {
		this.workingDirectory = workingDirectory;
	}

	public void setWorkingDirectory( String workingDirectory ) {
		this.workingDirectory = new File( workingDirectory );
	}


}
