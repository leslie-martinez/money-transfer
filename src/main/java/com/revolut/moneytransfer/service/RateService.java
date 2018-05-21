package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.H2Dao;
import com.revolut.moneytransfer.model.Rate;

import javax.ws.rs.*;
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
    private static final Logger log = Logger.getLogger("RateService");
    private final H2Dao h2Dao = new H2Dao();

    /**
     * Method returning all rates available
     *
     * @return All rates
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRates() {
        log.info("REST : getAllRates");
        List<Rate> rates;
        try {
            rates = h2Dao.getRateDao().getAllRates();
            if (rates == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No rate found.").build();
            }
        } catch (Exception e) {
            log.severe("Error while getting rates.");
            return Response.serverError().entity("Getting all rates failed.").build();
        }
        return Response.status(Response.Status.OK).entity(rates).build();
    }

    /**
     * Method returning all effective rates available
     *
     * @return All rates
     */
    @GET
    @Path("/effective")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEffectiveRates() {
        log.info("REST : getEffectiveRates");
        List<Rate> rates;
        try {
            rates = h2Dao.getRateDao().getAllEffectiveRates();
            if (rates == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No rate found.").build();
            }
        } catch (Exception e) {
            log.severe("Error while getting rates.");
            return Response.serverError().entity("Getting effective rates failed.").build();
        }
        return Response.status(Response.Status.OK).entity(rates).build();
    }

    /**
     * Service returning the Rate based on source and destination currency code
     *
     * @param sourceCurrency      Source Currency
     * @param destinationCurrency Destination Currency
     * @return Rate
     */
    @GET
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRateBySourceAndDestCurrency(@QueryParam("sourceCurrency") String sourceCurrency, @QueryParam("destinationCurrency") String destinationCurrency) {
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
            return Response.serverError().entity("Getting rate by source and destination currency failed.").build();
        }
        return Response.status(Response.Status.OK).entity(rate).build();
    }

    /**
     * Service updating the currency rate by id
     *
     * @param rateId rate uid
     * @param rate   rate object
     * @return newly updated rate
     */
    @PUT
    @Path("/{rateId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCurrencyRate(@PathParam("rateId") Long rateId, Rate rate) {
        if (rateId == null) {
            return Response.serverError().entity("Rate Id cannot be null.").build();
        }
        if (rate == null) {
            return Response.serverError().entity("Input data cannot be null.").build();
        }
        if (rate.getEffectiveDt() == null) {
            return Response.serverError().entity("Rate effective date cannot be null.").build();
        }
        if (rate.getRate() == null) {
            return Response.serverError().entity("Currency rate cannot be null.").build();
        }
        Rate newRate;
        try {
            newRate = h2Dao.getRateDao().updateCurrencyRate(rateId, rate);
            if (newRate == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("No rate found, for id : " + rateId).build();
            }
        } catch (Exception e) {
            log.severe("Error while updating rate. Id : " + rateId);
            return Response.serverError().entity("Update rate failed.").build();
        }
        return Response.status(Response.Status.OK).entity(newRate).build();
    }
}
