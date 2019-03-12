package org.ioexnetwork.ioexweb;


import org.eclipse.jetty.server.Server;

/**
 * start web service
 * @param  port Service port
 */
public class HttpServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8989);
        server.setHandler(new ioeXHandle());
        server.start();
        server.join();
    }
}


