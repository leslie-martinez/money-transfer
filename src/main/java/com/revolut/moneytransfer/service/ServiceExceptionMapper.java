package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.exception.ProjectException;
import com.revolut.moneytransfer.exception.ResponseError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<ProjectException> {
    private static final Logger log = Logger.getLogger("ServiceExceptionMapper");

    public ServiceExceptionMapper() {
    }

    public Response toResponse(ProjectException projectException) {
        log.info(" @@@@@ projectException : " + projectException.getMessage());
        ResponseError responseError = new ResponseError();
        responseError.setCodeError(projectException.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseError).type(MediaType.APPLICATION_JSON).build();
    }

}