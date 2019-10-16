package io.nayasis.common.basica.exception;

import io.nayasis.common.basica.file.worker.LineReader;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.PrintStream;

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

        stacktrace = Thread.currentThread().getStackTrace();

        depth = Math.max( depth, 0 );
        depth = Math.min( depth, stacktrace.length - 1 );

        StackTraceElement trace = stacktrace[ depth ];

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
