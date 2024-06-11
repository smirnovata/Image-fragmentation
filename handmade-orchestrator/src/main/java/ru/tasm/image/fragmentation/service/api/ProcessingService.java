package ru.tasm.image.fragmentation.service.api;

import com.vaadin.flow.component.Component;
import ru.tasm.image.fragmentation.model.ImageInfo;
import ru.tasm.image.fragmentation.model.Processing;
import ru.tasm.image.fragmentation.model.Status;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;

import java.io.Serializable;
import java.util.UUID;

public interface ProcessingService extends Serializable {

    void startProcessing(Processing processing) throws DataBaseException;

    void updateStatus(UUID id, Status status) throws DataBaseException;

    void updateUI(UUID id, Component component);

    ImageInfo getImageInfo(String path);

    Processing getInProgressProcessing(UUID id) throws DataBaseException;
}
