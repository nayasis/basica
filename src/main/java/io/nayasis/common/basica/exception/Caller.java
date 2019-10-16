package io.nayasis.common.basica.exception;

import io.nayasis.common.basica.file.worker.LineReader;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.PrintStream;
import java.util.Arrays;

@Value
@Accessors(fluent = true)
public class Caller {

    private String className;
    private int    lineNumber;
    private String fileName;
    private String methodName;

    @ToString.Exclude
    private StackTraceElement[] stacktrace;

    public Caller( int depth ) {

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

        int length = stacktrace.length;
        int index  = Math.max( 0, depth + 2 );
            index  = Math.min( index, length - 1 );

        this.stacktrace = Arrays.copyOfRange(stacktrace, index, length - 1);

        StackTraceElement trace = stacktrace[ index ];

        className  = trace.getClassName();
        fileName   = trace.getFileName();
        methodName = trace.getMethodName();
        lineNumber = trace.getLineNumber();

    }

    public void printStackTrace( PrintStream printer ) {
        for( StackTraceElement trace : stacktrace ) {
            printer.println( trace );
        }
    }

    @SneakyThrows
    public void printStackTrace( LineReader reader ) {
        for( StackTraceElement trace : stacktrace ) {
            reader.read( trace.toString() );
        }
    }

}
