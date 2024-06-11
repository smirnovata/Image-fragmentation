package ru.tasm.image.fragmentation.dao.api;

import ru.tasm.image.fragmentation.model.dao.StatusEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.util.List;
import java.util.UUID;

public interface StatusDao {

    List<StatusEntity> getStatuses() throws DataBaseException;

    void addStatus(StatusEntity status) throws DataBaseException;

    void deleteStatus(String name) throws DataBaseException;

}
