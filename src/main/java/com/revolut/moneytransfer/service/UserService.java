package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.H2Dao;
import com.revolut.moneytransfer.model.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserService {
    private static final Logger log = Logger.getLogger("UserService");
    private final H2Dao h2Dao = new H2Dao();

    /**
     * Method returning all users available
     * @return All users
     * @throws Exception Error while getting users
     */
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() throws Exception {
        log.info("REST : getUsers");
        List<User> users = null;
        try {
            users = h2Dao.getUserDAO().getAllUsers();
            if(users == null){
                return Response.status(Response.Status.NOT_FOUND).entity("No user found.").build();
            }
        } catch (Exception e){
            log.severe("Error while getting users.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(users).build();
    }

    /**
     * Service returning the User object
     * @param accountNo Account Number
     * @return Account owner
     * @throws Exception e
     */
    @GET
    @Path("/{accountNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByAccountNo(@PathParam("accountNo") Long accountNo) throws Exception {
        log.info("REST : getUserByAccountNo");
        if (accountNo == null) {
            return Response.serverError().entity("accountNo cannot be null.").build();
        }
        User user = null;
        try {
            user = h2Dao.getUserDAO().getUserByAccountNo(accountNo);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No account found, accountNo : " + accountNo).build();
            }
        } catch (Exception e) {
            log.severe("Error while getting user.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }
}
