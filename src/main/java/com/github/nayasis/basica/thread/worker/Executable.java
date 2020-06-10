package com.github.nayasis.basica.thread.worker;

@FunctionalInterface
public interface Executable {
	void run() throws Throwable;
}
