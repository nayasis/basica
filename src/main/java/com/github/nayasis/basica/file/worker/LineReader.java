package com.github.nayasis.basica.file.worker;

import java.io.IOException;
import java.io.UncheckedIOException;

@FunctionalInterface
public interface LineReader {

    void read( String line ) throws UncheckedIOException, IOException;

}
