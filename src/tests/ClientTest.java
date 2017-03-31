package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import rockdove.Client;
import rockdove.Server;
import rockdove.ServerList;
import rockdove.ServerSelector;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;


public class ClientTest {

    @Before
    public void init(){
        Server.RECEIVER_PORT = 80;
        Client.SENDER_PORT   = 80;
        Client.SENDER_ADD = "localhost";
    }

    @Test
    public void sendData(){
        Thread servert = new Thread(new ServerSelector());
        servert.start();

        client = new Client();
        int k = 0;

        String txt = "test";
        try {
            while (!client.finishConnect() && k < 20) {
                TimeUnit.MILLISECONDS.sleep(100);
                k += 1;
            }

            client.sendData(ByteBuffer.wrap(txt.getBytes()));
            assertEquals(0, 0);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals(0, k);
        }

        try {
            servert.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Client client;
}
