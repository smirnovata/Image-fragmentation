package ru.tasm.image.fragmentation.web.client;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
//import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import ru.tasm.image.fragmentation.model.ImageInfo;
import ru.tasm.image.fragmentation.model.ui.ProcessForm;

import java.io.Serializable;
import java.util.UUID;

@RegisterRestClient(configKey = "python-server-api")
public interface PythonServerClient extends Serializable {

    @POST
    @Path("/processing/{id}/start")
    @Produces(MediaType.APPLICATION_JSON)
    @NonBlocking
//    2024-06-02 13:20:25,852 WARN  [org.jbo.res.rea.com.pro.EndpointIndexer] (build-85)
//    '@Blocking' and '@NonBlocking' annotations are not necessary (or supported)
//    on REST Client interfaces. Offending class is 'ru.tasm.image.fragmentation.web.client.PythonServerClient'.
//    Whether or not the call blocks the calling thread depends on the return type of the method
//    - returning 'Uni', 'Multi' or 'CompletionStage' results in the implementation being non-blocking.
    void start(@PathParam("id") UUID id, ProcessForm form);

    @GET
    @Path("/images/info")
    @Consumes(MediaType.APPLICATION_JSON)
    ImageInfo getOrigImageInfo(@QueryParam("path") String path);

}
