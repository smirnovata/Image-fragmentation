package ru.tasm.image.fragmentation.dao.api;


import ru.tasm.image.fragmentation.model.dao.IFFileEntity;
import ru.tasm.image.fragmentation.model.dao.SessionEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.util.List;
import java.util.UUID;

public interface SessionDao {
    List<SessionEntity> getSessions() throws DataBaseException;

    SessionEntity getSessionEntityBySession(UUID session) throws DataBaseException;

    void addSessionEntity(SessionEntity entity) throws DataBaseException;

    void deleteSessionEntity(UUID session) throws DataBaseException;
}
