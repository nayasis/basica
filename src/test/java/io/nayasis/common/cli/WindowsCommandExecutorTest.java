package io.nayasis.common.cli;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import io.nayasis.common.etc.Platform;
import io.nayasis.common.exception.unchecked.CommandLineException;
import io.nayasis.common.file.Files;
import io.nayasis.common.file.worker.LineReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class WindowsCommandExecutorTest {

    @Before
    public void checkPlatform() {
        Assume.assumeTrue( Platform.isWindows );
    }

    @Test
    public void basic() {

        new Thread() {
            @Override public void run() {
                try {
                    Runtime.getRuntime().exec("cmd /c start notepad" );
                } catch ( IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.run();

    }

    @Test
    public void runExcelWithCmdAsync() {

        CommandExecutor executor = new CommandExecutor();

        StringBuffer output = new StringBuffer();

        // cmd /c start 로 명령을 실행시키면 무조건 비동기로 실행된다.
        executor.run( "cmd.exe /c start excel.exe", output );

        executor.waitFor();

        log.debug( "Done !!" );
        log.debug( "Output :\n{}", output );

    }

    @Test
    public void runExcelWithCmdSync() {

        CommandExecutor executor = new CommandExecutor();

        StringBuffer output = new StringBuffer();

        // cmd /c start /wait 로 명령을 실행시키면 종료를 기다린다.
        executor.run( "cmd.exe /c start /wait excel.exe", output );

        executor.waitFor();

        log.debug( "Done !!\n{}", output );

    }

    @Test
    public void runExcel() {

        CommandExecutor executor = new CommandExecutor();

        StringBuffer output = new StringBuffer();

        String path = "C:\\Program Files\\Microsoft Office\\root\\Office16\\EXCEL.EXE";
        if( Files.notExists(path) ) {
            log.warn( "can not test" );
            return;
        }

        executor.run( path, output );

        executor.waitFor();

        log.debug( "Done !!\n{}", output );

    }

    @Test( expected = CommandLineException.class )
    public void invalidExecutionTest() {

        CommandExecutor executor = new CommandExecutor();

        StringBuffer output = new StringBuffer();

        String path = "C:\\Program Files\\Microsoft Office\\root\\Office16\\EXCEL.EXE";
        if( Files.notExists(path) ) {
            log.warn( "can not test" );
            return;
        }

        // 하나의 CommandExecutor로 동시에 2개의 Command를 실행하지 못해야 한다.

        executor.run( path, output );
        executor.run( path, output );

        executor.waitFor();

        log.debug( "Done !!\n{}", output );

    }

    @Test
    public void readDirWithPipe() {

        CommandExecutor executor = new CommandExecutor();

        StringBuffer output = new StringBuffer();

        executor.run( "cmd /c c: && cd \"c:\\Windows\" && dir", output );

        executor.waitFor();

//        log.debug( "Done !!\n{}", output );

    }

    @Test
    public void readDirWithListCommand() {

        CommandExecutor executor = new CommandExecutor();

        StringBuffer output = new StringBuffer();

        executor.run( "cmd /c c: && cd \"c:\\Windows\" && dir", output );

        executor.waitFor();

        log.debug( "Done !!\n{}", output );

    }

    @Test
    public void readDirWithLanterna() {

        List<String> command = Arrays.asList( "cmd", "/c", "c:", "&&", "cd", "c:\\Windows", "&&", "dir" );

        ProcessBuilder builder = new ProcessBuilder( command );

        builder.redirectOutput( ProcessBuilder.Redirect.INHERIT );

        try {
            Process process = builder.start();
            // wait for termination.
            process.waitFor();

        } catch ( IOException | InterruptedException e ) {
            e.printStackTrace();
        }

    }

    @Test
    public void readDirWithCommandInjection() {

        CommandExecutor executor = new CommandExecutor();

        StringBuffer output = new StringBuffer();

        executor.run( "cmd", output );

        executor.sendCommand( "c:" );
        executor.sendCommand( "cd c:\\Users" );
        executor.sendCommand( "dir" );
        executor.sendCommand( "exit" );

        executor.waitFor();

        log.debug( "Done !!\n{}", output );

    }

    @Test
    public void createLanternaTerminal() throws IOException {

        Terminal terminal = new DefaultTerminalFactory( System.out, System.in, Charset.forName(Platform.osCharset) ).createTerminal();

        terminal.enterPrivateMode();

        terminal.setCursorPosition( 10, 5 );
        terminal.putCharacter( 'H' );
        terminal.putCharacter( 'e' );
        terminal.putCharacter( 'l' );
        terminal.putCharacter( 'l' );
        terminal.putCharacter( 'o' );
        terminal.putCharacter( '!' );

        terminal.flush();

//        terminal.exitPrivateMode();


    }

    @Test
    public void readDirWithWorker() {

        CommandExecutor executor = new CommandExecutor();

        StringBuffer output = new StringBuffer();
        StringBuffer pipe   = new StringBuffer();

        LineReader worker = readLine -> {
            if( readLine.contains( "디렉터리" ) ) {
                pipe.append( readLine ).append( '\n' );
                log.debug( ">> This is worker's thread ! : {}", readLine );
            }
        };

        executor.run( "cmd", output, worker );

        executor.sendCommand( "c:" );
        executor.sendCommand( "cd c:\\Users" );
        executor.sendCommand( "dir" );
        executor.sendCommand( "exit" );

        executor.waitFor();

        log.debug( "Done !!\n{}", output );

        log.debug( "Print pipe !!\n{}", pipe );

    }

    @Test
    public void openEmail() {

        Command command = new Command();
        command.add("explorer");
        command.addQuote("mailto:nayasis@gmail.com?subject=merong");

        CommandExecutor executor = new CommandExecutor();

        executor.run( command );

    }

}