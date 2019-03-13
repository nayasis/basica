package io.nayasis.common.cli;

import io.nayasis.common.etc.Platform;
import io.nayasis.common.exception.unchecked.CommandLineException;
import io.nayasis.common.file.Files;
import io.nayasis.common.file.worker.LineReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

@Slf4j
public class WindowsCommandExecutorTest {

    @Test
    public void runExcelWithCmdAsync() {

        if( canNotTest() ) return;

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

        if( canNotTest() ) return;

        CommandExecutor executor = new CommandExecutor();

        StringBuffer output = new StringBuffer();

        // cmd /c start /wait 로 명령을 실행시키면 종료를 기다린다.
        executor.run( "cmd.exe /c start /wait excel.exe", output );

        executor.waitFor();

        log.debug( "Done !!\n{}", output );

    }

    @Test
    public void runExcel() {

        if( canNotTest() ) return;

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

        if( canNotTest() ) return;

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

        if( canNotTest() ) return;

        CommandExecutor executor = new CommandExecutor();

        StringBuffer output = new StringBuffer();

        executor.run( "cmd /c c: && cd \"c:\\Windows\" && dir", output );

        executor.waitFor();

        log.debug( "Done !!\n{}", output );

    }

    @Test
    public void readDirWithListCommand() {

        if( canNotTest() ) return;

        CommandExecutor executor = new CommandExecutor();

        StringBuffer output = new StringBuffer();

        Command command = new Command();

        command.set( Arrays.asList( "cmd", "/c", "c:", "&&", "cd", "c:\\Windows", "&&", "dir" ) );
        command.setOutputPipe( output );

        executor.run( command );

        executor.waitFor();

        log.debug( "Done !!\n{}", output );

    }

    @Test
    public void readDirWithCommandInjection() {

        if( canNotTest() ) return;

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

        if( canNotTest() ) return;

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

    private boolean canNotTest() {
        if( ! Platform.isWindows ) {
            log.info( "Only windows can test" );
            return true;
        }
        return false;
    }

}