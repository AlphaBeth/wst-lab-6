package ru.ifmo.wst.lab1;

import lombok.Getter;
import lombok.Setter;
import ru.ifmo.wst.lab.ExterminatusInfo;
import ru.ifmo.wst.lab.ExterminatusPaths;
import ru.ifmo.wst.lab.ParamNames;
import ru.ifmo.wst.lab1.model.ExterminatusEntity;
import ru.ifmo.wst.lab1.model.Filter;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.List;

import static javax.ws.rs.client.Entity.entity;

public class ExterminatusResourceClient {

    public static final GenericType<List<ExterminatusEntity>> EXTERMINATUS_LIST =
            new GenericType<List<ExterminatusEntity>>() {
            };
    @Getter
    private final String baseUrl;
    private final WebTarget findAllResource;
    private final WebTarget filterResource;
    private final WebTarget rootResource;

    @Setter
    private String username;
    @Setter
    private String password;


    public ExterminatusResourceClient(String baseUrl) {
        this.baseUrl = baseUrl + ExterminatusPaths.ROOT_PATH;
        this.findAllResource = ClientBuilder.newClient().target(url(ExterminatusPaths.FIND_ALL_PATH));
        this.filterResource = ClientBuilder.newClient().target(url(ExterminatusPaths.FILTER_PATH));
        this.rootResource = ClientBuilder.newClient().target(url());
    }

    public List<ExterminatusEntity> findAll() {
        Response response = findAllResource.request().accept(MediaType.APPLICATION_JSON_TYPE).buildGet().invoke();
        return parseResponse(response, EXTERMINATUS_LIST);
    }

    public List<ExterminatusEntity> filter(Filter filter) {
        WebTarget resource = filterResource;
        resource = setParamIfNotNull(resource, ParamNames.ID, filter.getId());
        resource = setParamIfNotNull(resource, ParamNames.INTIATOR, filter.getInitiator());
        resource = setParamIfNotNull(resource, ParamNames.METHOD, filter.getMethod());
        resource = setParamIfNotNull(resource, ParamNames.PLANET, filter.getPlanet());
        resource = setParamIfNotNull(resource, ParamNames.REASON, filter.getReason());
        resource = setParamIfNotNull(resource, ParamNames.DATE, filter.getDate());
        Response response = resource.request().accept(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
        return parseResponse(response, EXTERMINATUS_LIST);
    }

    public int delete(long exterminatusId) {
        Invocation.Builder builder = rootResource.path(String.valueOf(exterminatusId)).request();
        Response response = setAuth(builder).accept(MediaType.TEXT_PLAIN_TYPE).delete(Response.class);
        String body = parseResponse(response, String.class);
        return Integer.parseInt(body);
    }

    public long create(ExterminatusInfo exterminatusInfo) {
        Invocation.Builder builder = rootResource.request();
        Response response = setAuth(builder).accept(MediaType.TEXT_PLAIN_TYPE, MediaType.APPLICATION_JSON_TYPE)
                .put(entity(exterminatusInfo, MediaType.APPLICATION_JSON_TYPE), Response.class);
        String body = parseResponse(response, String.class);
        return Long.parseLong(body);
    }

    public int update(ExterminatusEntity ee) {
        ExterminatusInfo info = fromEntity(ee);
        Invocation.Builder request = rootResource.path(String.valueOf(ee.getId())).request();
        Response response = setAuth(request)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .post(entity(info, MediaType.APPLICATION_JSON_TYPE), Response.class);
        String updateResponse = parseResponse(response, String.class);
        return Integer.parseInt(updateResponse);
    }

    private <T> T parseResponse(Response response, Class<T> entityClass) {
        checkStatusCode(response);
        return response.readEntity(entityClass);
    }

    private <T> T parseResponse(Response response, GenericType<T> gt) {
        checkStatusCode(response);
        return response.readEntity(gt);
    }

    private void checkStatusCode(Response response) {
        Response.StatusType status = response.getStatusInfo();
        Response.Status.Family family = status.getFamily();
        if (family != Response.Status.Family.SUCCESSFUL) {
            throw new ClientException(response.readEntity(String.class), status);
        }
    }

    private ExterminatusInfo fromEntity(ExterminatusEntity ee) {
        return new ExterminatusInfo(ee.getInitiator(), ee.getReason(), ee.getMethod(), ee.getPlanet(), ee.getDate());
    }

    private String url(String endpointAddress) {
        return baseUrl + endpointAddress;
    }

    private String url() {
        return baseUrl;
    }

    private WebTarget setParamIfNotNull(WebTarget resource, String paramName, Object value) {
        if (value == null) {
            return resource;
        }
        return resource.queryParam(paramName, value.toString());
    }

    private Invocation.Builder setAuth(Invocation.Builder requestBuilder) {
        if (username != null && password != null) {
            return requestBuilder.header(ExterminatusPaths.AUTHORIZATION_PROPERTY,
                    ExterminatusPaths.AUTHENTICATION_SCHEME + " " +
                    Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
        }
        return requestBuilder;

    }
}
