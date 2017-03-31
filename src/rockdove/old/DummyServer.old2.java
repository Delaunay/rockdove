package com.m3c.del.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DummyServer extends Thread{

    public void run() {
        ByteBuffer out_buffer = ByteBuffer.allocate(100);
        ByteBuffer in_buffer = ByteBuffer.allocate(100);

        try {
            ServerSocketChannel listen_socket = ServerSocketChannel.open();
            listen_socket.socket().bind(new InetSocketAddress("localhost",81));
            listen_socket.configureBlocking(true);
            SocketChannel client = listen_socket.accept();

            while(true){



                if (client != null){
                    //clients.add(client);


                    client.read(out_buffer);
                    in_buffer.flip();

                    printBuffer(out_buffer);
                    out_buffer.clear();

                    String message = "Server got it!";
                    in_buffer.clear();
                    in_buffer.put(message.getBytes());
                    in_buffer.flip();

                    System.out.print("SERVER: Sending Message\n");
                    while (in_buffer.hasRemaining())
                        client.write(in_buffer);
                    System.out.print("SERVER: Message sent\n");

                    in_buffer.clear();

                }

                TimeUnit.MILLISECONDS.sleep(10);
            }

        } catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void printBuffer(ByteBuffer buffer){
        for(int i = 0; i < buffer.position(); ++i)
            System.out.print((char) buffer.get(i));
        System.out.flush();
    }

    private ArrayList<SocketChannel> clients;
}
