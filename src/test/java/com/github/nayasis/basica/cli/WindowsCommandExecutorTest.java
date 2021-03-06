package com.github.nayasis.basica.cli;

import com.github.nayasis.basica.cli.Command;
import com.github.nayasis.basica.cli.CommandExecutor;
import com.github.nayasis.basica.exception.unchecked.CommandLineException;
import com.github.nayasis.basica.file.Files;
import com.github.nayasis.basica.file.worker.LineReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@Disabled("excluded because of platform dependency")
public class WindowsCommandExecutorTest {

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

    @Test
    public void invalidExecutionTest() {

        assertThrows( CommandLineException.class, () -> {

            CommandExecutor executor = new CommandExecutor();

            StringBuffer output = new StringBuffer();

            String path = "C:\\Program Files\\Microsoft Office\\root\\Office16\\EXCEL.EXE";
            if( Files.notExists(path) ) {
                throw new CommandLineException( "can not test" );
            }

            // 하나의 CommandExecutor로 동시에 2개의 Command를 실행하지 못해야 한다.

            executor.run( path, output );
            executor.run( path, output );

            executor.waitFor();

            log.debug( "Done !!\n{}", output );

        });

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