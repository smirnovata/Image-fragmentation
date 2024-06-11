package ru.tasm.image.fragmentation.dao.api;

import ru.tasm.image.fragmentation.model.dao.IFFileEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.util.List;
import java.util.UUID;

public interface IFFileDao {

        List<IFFileEntity> getFiles() throws DataBaseException;

        List<IFFileEntity> getFilesBySession(UUID session) throws DataBaseException;

        void addIFFile(IFFileEntity entity) throws DataBaseException;

        void deleteIFFiles(UUID session) throws DataBaseException;
}
