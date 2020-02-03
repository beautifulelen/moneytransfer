package com.moneytransfer.services;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.Constants;
import com.moneytransfer.model.DataStore;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountService {

    /**
     * Create an account
     *
     * @param account
     * @return Response
     */
    @POST
    public Response create(Account account) {
        if (DataStore.getInstance().getAccount(account.getClientName()) != null)
            throw new WebApplicationException(Constants.ACCOUNT_WAS_NOT_ADDED, Response.Status.BAD_REQUEST);
        DataStore.getInstance().createAccount(account);
        return Response.status(Response.Status.OK).build();
    }

  

    /**
     * Get single account info
     *
     * @param clientName
     * @return Account object for selected clientName
     */
    @GET
    @Path("/{clientName}")
    public Account getAccount(@PathParam("clientName") String clientName) {
        Account account = DataStore.getInstance().getAccount(clientName);
        if (account == null)
            throw new WebApplicationException(Constants.NOT_FOUND_ACCOUNT, Response.Status.NOT_FOUND);
        return account;
    }
}
