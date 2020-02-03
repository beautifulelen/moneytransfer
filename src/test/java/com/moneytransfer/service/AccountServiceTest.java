package com.moneytransfer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneytransfer.Application;
import com.moneytransfer.model.Account;
import com.moneytransfer.model.DataStore;
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
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.SuppressCode.suppressConstructor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({com.moneytransfer.model.DataStore.class})
@PowerMockIgnore({"javax.net.ssl.*"})
public class AccountServiceTest {

    private static Server server;

    private static HttpClient client ;

    private ObjectMapper mapper = new ObjectMapper();
    private URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:" + PORT_TEST);

    Account account1 = new Account("A", 100);

    private static DataStore mockedDataStore;


    @BeforeClass
    public static void before() throws Exception {
        server = Application.initServer(PORT_TEST);
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
    public void testGetAccountFailWithNotFound() throws URISyntaxException, IOException {
        URI uri = builder.setPath("/account/B").build();
        HttpGet request = new HttpGet(uri);
        request.setHeader("Content-type", "application/json");
        when(mockedDataStore.getAccount("B")).thenReturn(null);

        HttpResponse response = client.execute(request);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatusLine().getStatusCode());

        Mockito.verify(mockedDataStore).getAccount("B");
    }

    @Test
    public void testGetAccountSuccess() throws URISyntaxException, IOException {
        URI uri = builder.setPath("/account/A").build();
        HttpGet request = new HttpGet(uri);
        request.setHeader("Content-type", "application/json");
        when(mockedDataStore.getAccount("A")).thenReturn(account1);

        HttpResponse response = client.execute(request);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());

        Mockito.verify(mockedDataStore).getAccount("A");
    }

    @Test
    public void testCreateExistingAccountFail() throws URISyntaxException, IOException {
        URI uri = builder.setPath("/account").build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");
        when(mockedDataStore.getAccount("A")).thenReturn(account1);

        String jsonInString = mapper.writeValueAsString(account1);//new Account("B", 100));
        StringEntity entity = new StringEntity(jsonInString);
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusLine().getStatusCode());

        Mockito.verify(mockedDataStore).getAccount("A");
        Mockito.verify(mockedDataStore, Mockito.times(0)).createAccount(account1);
    }

    @Test
    public void testCreateSuccess() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/account").build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-type", "application/json");

        String jsonInString = mapper.writeValueAsString(account1);
        StringEntity entity = new StringEntity(jsonInString);
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusLine().getStatusCode());

        Mockito.verify(mockedDataStore).createAccount(account1);
    }


}
