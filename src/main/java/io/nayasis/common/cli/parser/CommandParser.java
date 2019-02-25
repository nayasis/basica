package io.nayasis.common.cli.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * converter from OS command line to array
 *
 * @author nayasis@gmail.com
 *
 */
public class CommandParser {
	
	List<String>  commandList = null;
	StringBuilder command     = null;

	public List<String> parse( String commandLine ) {

		if( commandLine == null || commandLine.length() == 0 ) return new ArrayList<>();
		
		commandList = new ArrayList<>();
		command     = new StringBuilder();
		
		boolean quotationMode = false;
		
		for( int i = 0, iCnt = commandLine.length(); i < iCnt; i++ ) {
			
			char c = commandLine.charAt( i );
			
			if( quotationMode == true ) {

				if( c == '"' ) {

					quotationMode = false;

					command.append( c );
					addCommandList( command );
					continue;
					
				}
					
			} else {
				
				if( c == '"' ) {
					
					quotationMode = true;

					command.append( c );
					continue;
					
				} else if ( c == ' ' || c == '\t' || c == '\r' || c == '\n'  ) {
					addCommandList( command );
					continue;
				}
				
			}
			
			command.append( c );
			
		}
		
		addCommandList( command );

		if( commandList.size() > 0 ) {
			commandList.set( 0, commandList.get( 0 ).replaceAll( "^\"(.*)\"", "$1" ) );
		}

		return commandList;
		
	}
	
	private void addCommandList( StringBuilder command ) {
		
		if( command == null ) return;
		
		String appendCommand = command.toString().trim();
		
		if( command.length() == 0 ) return;
		
		this.commandList.add( appendCommand );
		
		this.command = new StringBuilder();
		
	}
	
}
