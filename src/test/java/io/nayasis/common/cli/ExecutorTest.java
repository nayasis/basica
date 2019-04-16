package io.nayasis.common.cli;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import io.nayasis.common.etc.Platform;

import java.io.IOException;
import java.nio.charset.Charset;

public class ExecutorTest {

    public static void main( String... args ) throws IOException {

        new Thread() {
            @Override public void run() {
                try {
                    Runtime.getRuntime().exec("cmd /k start cmd notepad" );
                } catch ( IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.run();

        Terminal terminal = new DefaultTerminalFactory( System.out, System.in, Charset.forName( Platform.osCharset) ).createTerminal();

        terminal.enterPrivateMode();

        terminal.setCursorPosition( 10, 5 );
        terminal.putCharacter( 'H' );
        terminal.putCharacter( 'e' );
        terminal.putCharacter( 'l' );
        terminal.putCharacter( 'l' );
        terminal.putCharacter( 'o' );
        terminal.putCharacter( '!' );

        terminal.flush();

    }

}
