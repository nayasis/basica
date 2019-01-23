package io.nayasis.common.file.worker;

import java.io.UncheckedIOException;

@FunctionalInterface
public interface LineReader {

    void read( String line ) throws UncheckedIOException;

}
