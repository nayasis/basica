package io.nayasis.common.cli;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import io.nayasis.common.etc.Platform;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
public class ExecutorTest {

    public static void main( String... args ) throws IOException, InterruptedException {
        useLanterna();
    }

    private static void useLanterna() throws IOException {

        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setTerminalEmulatorTitle( "Merong" );
        factory.setInitialTerminalSize( new TerminalSize( 100, 30 ) );


        SwingTerminal terminal = new SwingTerminal();

        terminal.setSize( 100, 50 );

//        SwingTerminalFrame terminal = (SwingTerminalFrame) factory.createTerminal();
//        terminal.enterPrivateMode();

//        SwingTerminal t = new SwingTerminal(  );
        terminal.setAutoscrolls( true );

//        terminal.setFocusCycleRoot( true );

        terminal.setCursorVisible( true );
//        terminal.getTerminalSize().withColumns( 200 );

        CommandExecutor executor = new CommandExecutor();

        int[] row = new int[ 1 ];

//        Command command = new Command();
//        command.add( "d:\\download\\jediterm-master\\jediterm-master\\jediterm.bat" );
//        command.setWorkingDirectory( "d:\\download\\jediterm-master\\jediterm-master" );
//        command.add( "cmd /c c: && cd \"c:\\Windows\" && dir" );

        executor.run( "cmd /c c: && cd \"c:\\Windows\" && dir", line -> {
//        executor.run( command, line -> {
            for( char c : line.toCharArray() ) {
                terminal.putCharacter( c );
            }
            terminal.putCharacter( '\n' );
            terminal.flush();

            row[0] = row[0] + 1;
            terminal.setCursorPosition( 0, row[0] );
            log.debug( line );
        });

        executor.waitFor();
    }

    private static void runTerminal() throws IOException {

        Terminal terminal = new DefaultTerminalFactory( System.out, System.in, Charset.forName( Platform.osCharset) )
//            .setForceTextTerminal( true )
            .createTerminal();

        terminal.flush();

    }

    @Test
    public void simple() throws IOException {

        Terminal terminal = new DefaultTerminalFactory().createTerminal();

        CommandExecutor executor = new CommandExecutor();

//        executor.run( "cmd /c c: && cd \"c:\\Windows\" && dir", line -> {
        executor.run( "d:\\download\\jediterm-master\\jediterm-master\\gradlew.bat", line -> {
//        executor.run( "dir", line -> {
            for( char c : line.toCharArray() ) {
                terminal.putCharacter( c );
            }
            terminal.flush();
            log.debug( line );
        } );

        executor.waitFor();

//        log.debug( "Done !!\n{}", output );

    }

}
