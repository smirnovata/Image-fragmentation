package ru.tasm.image.fragmentation.service;


import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import jakarta.enterprise.context.Dependent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ru.tasm.image.fragmentation.dao.api.ProcessingDao;
import ru.tasm.image.fragmentation.model.ImageInfo;
import ru.tasm.image.fragmentation.model.Processing;
import ru.tasm.image.fragmentation.model.Status;
import ru.tasm.image.fragmentation.model.dao.ProcessingEntity;
import ru.tasm.image.fragmentation.model.dao.StatusEntity;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;
import ru.tasm.image.fragmentation.model.exception.FrontedException;
import ru.tasm.image.fragmentation.model.ui.ProcessForm;
import ru.tasm.image.fragmentation.service.api.ProcessingService;
import ru.tasm.image.fragmentation.web.client.PythonServerClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Dependent
public class ProcessingServiceImpl implements ProcessingService {

    ProcessingDao processingDao;
    PythonServerClient client;
    ObjectMapper objectMapper;

    public ProcessingServiceImpl(@RestClient PythonServerClient client,
                                 ProcessingDao processingDao,
                                 ObjectMapper objectMapper ) {
        this.client = client;
        this.processingDao = processingDao;
        this.objectMapper = objectMapper;
    }

    @Override
    public void startProcessing(Processing processing) throws DataBaseException {
        Integer height = processing.form().getHeight();
        Integer resHeight = switch (processing.form().getHeightSI()) {
            case "м" -> height * 5 * 100;
            case "см" -> height * 5;
            default -> height;
        };

        processingDao.addProcessing(new ProcessingEntity(
                processing.id(),
                new StatusEntity(processing.status()),
                processing.form().getNumbers(),
                resHeight,
                processing.form().getTrySeg(),
                processing.form().getHeightSI(),
                processing.form().getBackgraundColor()
        ));
        client.start(processing.id(), processing.form());
    }

    @Override
    public void updateStatus(UUID id, Status status) throws DataBaseException {
        processingDao.updateProcessing(id, new StatusEntity(status));
        updateUI(id, null);
    }

    @Override
    public void updateUI(UUID id, Component component) {
        log.debug("test");
    }

    @Override
    public Processing getInProgressProcessing(UUID id) throws DataBaseException {
        return processingDao.getProcessingBySession(id).stream()
                .map(processingEntity -> new Processing(
                        processingEntity.session(),
                        Status.from(processingEntity.status()),
                        new ProcessForm(
                                processingEntity.numbers(),
                                processingEntity.height(),
                                processingEntity.formTryOrigSi(),
                                processingEntity.trySeg(),
                                processingEntity.backgroundColor()
                        )
                ))
                .filter(processing -> processing.status().equals(Status.IN_PROGRESS))
                .findFirst().orElse(null);
    }

    @Override
    public ImageInfo getImageInfo(String path) {
        log.info("getting info image; {}", path);
        ImageInfo origImageInfo;
        File file = new File(path);
        String jsonPath = file.getParent() + File.separator
                + FilenameUtils.getBaseName(path) + ".json";
        log.debug("{}", jsonPath);
        if (new File(jsonPath).exists()) {
            try {
                origImageInfo = objectMapper.readValue(new File(jsonPath), ImageInfo.class);
            } catch (IOException e) {
                throw new FrontedException(e);
            }
        } else {
            origImageInfo = client.getOrigImageInfo(path);
        }
        return origImageInfo;
    }
}
