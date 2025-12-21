package com.xperia.xpense_tracker.converter;


public abstract class AbstractAttachmentProcessor<T, R> {

    abstract R convertAttachment(T attachment, String path, String fileName);

    public abstract void saveAttachment(T attachmentToSave, String path, String fileName);
}
