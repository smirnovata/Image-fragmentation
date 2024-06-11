package ru.tasm.image.fragmentation.service.api;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.server.StreamResource;
import ru.tasm.image.fragmentation.model.IFFile;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

public interface FileService extends Serializable {
    void saveOrigFile(IFFile file) throws DataBaseException;

    IFFile getOrigFile(UUID id) throws DataBaseException;

    File getResultFolder(UUID id);

    void clearSessionFolder(UUID id) throws DataBaseException;

    IFFile getResultZipFile(UUID id);

    void clearResultDirectoryByUUID(UUID id) throws DataBaseException;

    StreamResource getStreamResourceFromFile(IFFile file);

    StreamResource getStreamResourceFromFile(File file);

    File getOneResultZipFile(UUID uuid, String name);
}
