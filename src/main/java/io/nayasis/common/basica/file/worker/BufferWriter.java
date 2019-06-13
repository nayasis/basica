package io.nayasis.common.basica.file.worker;

import java.io.BufferedWriter;
import java.io.IOException;

@FunctionalInterface
public interface BufferWriter {

    void write( BufferedWriter buffer ) throws IOException;

}
