package ru.tasm.image.fragmentation.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.atmosphere.inject.annotation.ApplicationScoped;
import ru.tasm.image.fragmentation.model.Status;
import ru.tasm.image.fragmentation.model.dto.UpdateStatus;
import ru.tasm.image.fragmentation.model.exception.DataBaseException;
import ru.tasm.image.fragmentation.model.response.SuccessResponse;
import ru.tasm.image.fragmentation.service.api.ProcessingService;
import ru.tasm.image.fragmentation.web.api.StatusApi;

import java.util.UUID;

@ApplicationScoped
@Slf4j
public class StatusController implements StatusApi {

    @Inject
    ProcessingService processingService;

//    public StatusController(ProcessingService processingService) {
//        this.processingService = processingService;
//    }

    @Override
    public Response updateStatus(UUID id, UpdateStatus status) {
        try {
            processingService.updateStatus(id, Status.valueOf(status.status().toUpperCase()));
            return Response.ok(new SuccessResponse("Status is updated")).build();
        } catch (DataBaseException e) {
            log.error(e.getMessage(), e);
            return Response.serverError().build();
        }
    }
}
