package com.xperia.xpense_tracker.converter;


public abstract class AbstractImageProcessor<T, R> {

    abstract R convertImage(T image);

    public abstract void saveImage(T imageToSave, String path, String fileName);
}
