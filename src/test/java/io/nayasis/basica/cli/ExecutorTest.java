package io.nayasis.basica.cli;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import io.nayasis.basica.etc.Platforms;
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
//        factory.setTerminalEmulatorFontConfiguration( new AWTTerminalFontConfiguration() );


        SwingTerminalFrame terminal = factory.createSwingTerminal();

        Screen screen = new TerminalScreen( terminal );


//        terminal.setCursorVisible( true );
//        terminal.getTerminalSize().withRows( 30 );

//        SwingTerminal swingTerminal = ClassReflector.getValue( terminal, "swingTerminal" );

//        swingTerminal.setAutoscrolls( true );


        screen.startScreen();

        terminal.setVisible( true );

        CommandExecutor executor = new CommandExecutor();

//        Command command = new Command();
//        command.add( "d:\\download\\jediterm-master\\jediterm-master\\jediterm.bat" );
//        command.setWorkingDirectory( "d:\\download\\jediterm-master\\jediterm-master" );
//        command.add( "cmd /c c: && cd \"c:\\Windows\" && dir" );

//        executor.run( "cmd /c c: && cd \"c:\\Windows\" && dir", line -> {
        executor.run( "java -jar \"c:\\NIDE\\workspace\\BuskOn\\WalletCacheServer\\target\\WalletCacheServer-0.0.1-SNAPSHOT.war\"", line -> {
//        executor.run( command, line -> {
            for( char c : line.toCharArray() ) {
                terminal.putCharacter( c );
            }
            terminal.putCharacter( '\n' );
            terminal.flush();
            terminal.bell();

            screen.scrollLines( 0, screen.getTerminalSize().getRows(), 1 );
//            screen.refresh();
            log.debug( line );
        });

        executor.waitFor();
    }

    private static void runTerminal() throws IOException {

        Terminal terminal = new DefaultTerminalFactory( System.out, System.in, Charset.forName( Platforms.osCharset) )
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
