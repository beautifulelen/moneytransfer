package com.moneytransfer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneytransfer.Application;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.DataStore;
import com.moneytransfer.model.Transaction;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.moneytransfer.model.Constants.PORT_TEST;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.SuppressCode.suppressConstructor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({com.moneytransfer.model.DataStore.class})
@PowerMockIgnore({"javax.net.ssl.*"})
public class TransactionServiceTest {

    private static HttpClient client ;

    private ObjectMapper mapper = new ObjectMapper();
    private URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:" + PORT_TEST);

    private Transaction transaction = new Transaction(10, "A", "B");

    private static DataStore mockedDataStore;


    @BeforeClass
    public static void before() throws Exception {
        Server server = Application.initServer(PORT_TEST);
        server.start();
        client = HttpClients.custom().build();
    }

    @Before
    public void beforeTest() {
        suppressConstructor(DataStore.class);
        mockStatic(DataStore.class);
        mockedDataStore = mock(DataStore.class);
        when(DataStore.getInstance()).thenReturn(mockedDataStore);
    }

    @AfterClass
    public static void after() {
        HttpClientUtils.closeQuietly(client);
    }

    @Test
    public void testTransactionSuccess() throws URISyntaxException, IOException {

        Account account1 = new Account("A", 100);
        Account account2 = new Account("B", 100);

        when(mockedDataStore.getAccount("A")).thenReturn(account1);
        when(mockedDataStore.getAccount("B")).thenReturn(account2);
        when(mockedDataStore.isValidTransaction(any())).thenReturn(true);
        when(mockedDataStore.lock(any())).thenReturn(true);
        when(mockedDataStore.unlock(any())).thenReturn(true);

        URI uri = builder.setPath("/transaction").build();
        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());

        Mockito.verify(mockedDataStore).doTransaction(any());
    }

    @Test
    public void testTransactionFailedWithNotEnoughMoney() throws IOException, URISyntaxException {
        Account account1 = new Account("A", 100);
        Account account2 = new Account("B", 100);

        when(mockedDataStore.getAccount("A")).thenReturn(account1);
        when(mockedDataStore.getAccount("B")).thenReturn(account2);
        when(mockedDataStore.isValidTransaction(any())).thenReturn(false);
        when(mockedDataStore.lock(any())).thenReturn(true);
        when(mockedDataStore.unlock(any())).thenReturn(true);

        URI uri = builder.setPath("/transaction").build();

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);

        HttpResponse response = client.execute(request);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusLine().getStatusCode());

        Mockito.verify(mockedDataStore, Mockito.times(0)).doTransaction(any());
    }

    @Test
    public void testTransactionMulticlient() throws IOException, URISyntaxException {
        HttpClient client1 = HttpClients.custom().build();
        Account account1 = new Account("A", 100);
        Account account2 = new Account("B", 100);
        when(mockedDataStore.getAccount("A")).thenReturn(account1);
        when(mockedDataStore.getAccount("B")).thenReturn(account2);
        when(mockedDataStore.isValidTransaction(any())).thenReturn(true);
        when(mockedDataStore.unlock(any())).thenReturn(true);

        URI uri = builder.setPath("/transaction").build();
        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        when(mockedDataStore.lock(any())).thenReturn(true);
        HttpResponse response = client.execute(request);
        when(mockedDataStore.lock(any())).thenReturn(false);
        HttpResponse response1 = client1.execute(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response1.getStatusLine().getStatusCode());
    }

    @Test
    public void testGetAllTransaction() throws URISyntaxException, IOException {
        URI uri = builder.setPath("/transaction").build();
        HttpGet request = new HttpGet(uri);
        request.setHeader("Content-type", "application/json");

        HttpResponse response = client.execute(request);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());

        Mockito.verify(mockedDataStore).getTransactions();
    }

}
