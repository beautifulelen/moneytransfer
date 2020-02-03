package com.moneytransfer.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.moneytransfer.model.Constants;
import com.moneytransfer.model.DataStore;
import com.moneytransfer.model.Transaction;

import java.util.List;


@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionService {

    /**
     *
     * @param transaction object from request
     * @return Response
     */
    @POST
    public Response transfer(Transaction transaction) throws WebApplicationException {
        if (!DataStore.getInstance().lock(transaction.getToClientName()) || !DataStore.getInstance().lock(transaction.getToClientName()))
            throw new WebApplicationException(Constants.BUSY_ACCOUNTS, Response.Status.BAD_REQUEST);
        if (DataStore.getInstance().isValidTransaction(transaction)) {
            DataStore.getInstance().doTransaction(transaction);
        } else {
            throw new WebApplicationException(Constants.BAD_TRANSACTION, Response.Status.BAD_REQUEST);
        }
        if (!DataStore.getInstance().unlock(transaction.getFromClientName()) || !DataStore.getInstance().unlock(transaction.getToClientName()))
            throw new WebApplicationException(Constants.UNLOCK_ERROR, Response.Status.BAD_REQUEST);
        return Response.status(Response.Status.OK).build();
    }

    /**
     * Get transactions info
     *
     * @return List with all transactions
     */
    @GET
    public List getAll() {
        return DataStore.getInstance().getTransactions();
    }

}
