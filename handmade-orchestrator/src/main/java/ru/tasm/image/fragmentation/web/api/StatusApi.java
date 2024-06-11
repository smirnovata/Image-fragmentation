package ru.tasm.image.fragmentation.web.api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.Body;
import ru.tasm.image.fragmentation.model.dto.UpdateStatus;

import java.util.UUID;

@Path("/status")
public interface StatusApi {

    @Path("/{id}/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Body
    Response updateStatus(@PathParam("id") UUID id,
                          UpdateStatus status);
}
