package io.nayasis.common.file.worker;

@FunctionalInterface
public interface BufferWriter {

    void write( BufferWriter writer );

}
