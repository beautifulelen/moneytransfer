package com.moneytransfer;

import com.moneytransfer.model.Constants;
import com.moneytransfer.services.AccountService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.moneytransfer.services.TransactionService;

import java.io.IOException;


/**
 * com.moneytransfer.model.Application Class
 */

public class Application {

    private static String join(String... args) {
        StringBuilder result = new StringBuilder();
        for (String arg: args)
            result.append(arg).append(",");
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static void main(String[] args) throws Exception {
        Server server = initServer(Constants.PORT);
        try {
            server.start();
        } catch (IOException exception) {
            System.out.println(exception.toString());
        }
        server.join();
        server.destroy();
    }

    public static Server initServer(int port) {
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");

        servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                join(AccountService.class.getCanonicalName(),
                        TransactionService.class.getCanonicalName()));
        return server;
    }

}