package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.H2Dao;
import com.revolut.moneytransfer.model.Rate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Rate Service Class
 */
@Path("/rates")
@Produces(MediaType.APPLICATION_JSON)
public class RateService {
    private static final Logger log = Logger.getLogger("CustomerService");
    private final H2Dao h2Dao = new H2Dao();

    /**
     * Method returning all rates available
     *
     * @return All rates
     * @throws Exception Error while getting rates
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRates() throws Exception {
        log.info("REST : getAllRates");
        List<Rate> rates;
        try {
            rates = h2Dao.getRateDao().getAllRates();
            if (rates == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No rate found.").build();
            }
        } catch (Exception e) {
            log.severe("Error while getting rates.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(rates).build();
    }

    /**
     * Method returning all effective rates available
     *
     * @return All rates
     * @throws Exception Error while getting rates
     */
    @GET
    @Path("/effective")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEffectiveRates() throws Exception {
        log.info("REST : getEffectiveRates");
        List<Rate> rates;
        try {
            rates = h2Dao.getRateDao().getAllEffectiveRates();
            if (rates == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No rate found.").build();
            }
        } catch (Exception e) {
            log.severe("Error while getting rates.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(rates).build();
    }

    /**
     * Service returning the Rate based on source and destination currency code
     *
     * @param sourceCurrency      Source Currency
     * @param destinationCurrency Destination Currency
     * @return Rate
     * @throws Exception e
     */
    @GET
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRateBySourceAndDestCurrency(@QueryParam("sourceCurrency") String sourceCurrency, @QueryParam("destinationCurrency") String destinationCurrency) throws Exception {
        log.info("REST : getRateBySourceAndDestCurrency");
        if (sourceCurrency == null) {
            return Response.serverError().entity("Source currency cannot be null.").build();
        }
        if (destinationCurrency == null) {
            return Response.serverError().entity("Destination currency cannot be null.").build();
        }
        Rate rate;
        try {
            rate = h2Dao.getRateDao().getRateBySourceAndDestCurrency(sourceCurrency, destinationCurrency);
            if (rate == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No rate found, for " + sourceCurrency + " to " + destinationCurrency).build();
            }
        } catch (Exception e) {
            log.severe("Error while getting rate.");
            throw new Exception(e);
        }
        return Response.status(Response.Status.OK).entity(rate).build();
    }
}
