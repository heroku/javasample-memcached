package com.heroku.memcached.cached;


import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class Spy {

    MemcachedClient mc;

    public Spy(String server, String username, String password) {

        AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"},
                new PlainCallbackHandler(username, password));

        try {
            mc = new MemcachedClient(
                    new ConnectionFactoryBuilder().setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                            .setAuthDescriptor(ad)
                            .build(), Collections.singletonList(new InetSocketAddress(server, 11211))
            );

        } catch (IOException ex) {
            System.err.println("Couldn't create a connection, bailing out: \nIOException " + ex.getMessage());
        }
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String server = System.getenv("MEMCACHE_SERVERS");
        String username = System.getenv("MEMCACHE_USERNAME");
        String password = System.getenv("MEMCACHE_PASSWORD");
        Spy spy = new Spy(server, username, password);
        spy.run();
    }

    public void run() throws ExecutionException, InterruptedException {
        for (int i = 0; i < 1000; i++) {
            String key = "key" + i;
            String value = "value" + i;
            if (mc.add(key, 0, value).get()) {
                System.out.printf("added %s -> %s\n", key, value);
            } else {
                System.out.printf("ERROR ADDING %s -> %s\n", key, value);
            }
        }
        for (int i = 0; i < 1000; i++) {
            String key = "key" + i;
            String value = "value" + (i * i);
            if (mc.set(key, 0, value).get()) {
                System.out.printf("set %s -> %s\n", key, value);
            } else {
                System.out.printf("ERROR SETTING %s -> %s\n", key, value);
            }
        }
        for (int i = 0; i < 1000; i++) {
            String key = "key" + i;
            String value = (String) mc.get(key);
            System.out.printf("GET %s -> %s\n", key, value);

        }
        for (int i = 0; i < 1000; i++) {
            String key = "key" + i;
            if (mc.delete(key).get()) {
                System.out.printf("deleted %s\n", key);
            } else {
                System.out.printf("ERROR DELETING %s\n", key);
            }
        }
        mc.shutdown();
    }

}
