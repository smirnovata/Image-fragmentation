package ru.tasm.image.fragmentation.dao.api;

import ru.tasm.image.fragmentation.model.dao.ProcessingEntity;
import ru.tasm.image.fragmentation.model.dao.StatusEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.util.List;
import java.util.UUID;

public interface ProcessingDao {
    List<ProcessingEntity> getAllProcessing() throws DataBaseException;

    List<ProcessingEntity> getProcessingBySession(UUID session) throws DataBaseException;

    void addProcessing(ProcessingEntity entity) throws DataBaseException;

    void deleteAllSessionProcessing(UUID session) throws DataBaseException;

    void updateProcessing(UUID id, StatusEntity status) throws DataBaseException;

}
