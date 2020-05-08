package com.github.nayasis.basica.file.worker;

import java.io.BufferedWriter;
import java.io.IOException;

@FunctionalInterface
public interface BufferWriter {

    void write( BufferedWriter buffer ) throws IOException;

}
