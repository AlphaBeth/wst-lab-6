package ru.ifmo.wst.lab1.rs;

import lombok.SneakyThrows;
import ru.ifmo.wst.lab.ExterminatusInfo;
import ru.ifmo.wst.lab.ExterminatusPaths;
import ru.ifmo.wst.lab.ParamNames;
import ru.ifmo.wst.lab1.dao.ExterminatusDAO;
import ru.ifmo.wst.lab1.model.ExterminatusEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.List;

@RequestScoped
@Path(ExterminatusPaths.ROOT_PATH)
@Produces({MediaType.APPLICATION_JSON})
public class ExterminatusResource {

    public static ExterminatusDAO GLOBAL_DAO;

    @Inject
    private ExterminatusDAO exterminatusDAO;

    public ExterminatusResource() {
        exterminatusDAO = GLOBAL_DAO;
    }

    @GET
    @Path(ExterminatusPaths.FIND_ALL_PATH)
    @SneakyThrows
    public List<ExterminatusEntity> findAll() {
        return exterminatusDAO.findAll();
    }

    @GET
    @Path(ExterminatusPaths.FILTER_PATH)
    @SneakyThrows
    public List<ExterminatusEntity> filter(@QueryParam(ParamNames.ID) Long id, @QueryParam(ParamNames.INTIATOR) String initiator,
                                           @QueryParam(ParamNames.REASON) String reason, @QueryParam(ParamNames.METHOD) String method,
                                           @QueryParam(ParamNames.PLANET) String planet, @QueryParam(ParamNames.DATE) Date date) {
        return exterminatusDAO.filter(id, initiator, reason, method, planet, date);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @SneakyThrows
    public Response create(ExterminatusInfo exterminatusInfo, @Context UriInfo uriInfo) {
        long createdId = exterminatusDAO.create(exterminatusInfo.getInitiator(), exterminatusInfo.getReason(), exterminatusInfo.getMethod(),
                exterminatusInfo.getPlanet(), exterminatusInfo.getDate());
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(String.valueOf(createdId));
        return Response.created(builder.build()).entity(String.valueOf(createdId)).build();
    }

    @DELETE
    @SneakyThrows
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public String delete(@PathParam("id") long id) {
        return String.valueOf(exterminatusDAO.delete(id));
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @SneakyThrows
    @Path("/{id}")
    public String update(@PathParam("id") long updateId, ExterminatusInfo exterminatusInfo) {
        return String.valueOf(exterminatusDAO.update(updateId, exterminatusInfo.getInitiator(), exterminatusInfo.getReason(),
                exterminatusInfo.getMethod(), exterminatusInfo.getPlanet(), exterminatusInfo.getDate()));
    }
}
