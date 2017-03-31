package com.m3c.del.Server;

import java.io.BufferedInputStream;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Client {

    Client(){
        _out_buffer = ByteBuffer.allocate(100);
        _in_buffer  = new byte[100];
        _clients    = new LinkedList<>();

        try {
            //_selector   = Selector.open();

            _listen_socket = ServerSocketChannel.open();
            _listen_socket.socket().bind(new InetSocketAddress(LISTEN_PORT));
            _listen_socket.configureBlocking(false);
            //_listen_socket.register(_selector, SelectionKey.OP_ACCEPT);

            _send_socket = SocketChannel.open();
            //_send_socket.register(_selector, SelectionKey.OP_CONNECT);
            _send_socket.connect(new InetSocketAddress(URL, SEND_PORT));

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    void sendMessage(ByteBuffer msg) throws IOException{
        while(msg.hasRemaining()) {
            _send_socket.write(msg);
        }
    }

    void mainLoop() throws Exception{
        System.out.print(" >> ");

        while(true){
            SocketChannel socket = _listen_socket.accept();

            if(socket != null){
                //socket.register(_selector, SelectionKey.OP_ACCEPT);
                _clients.add(socket);
            }

            handleClients();

            getInput();

            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    public void getInput() throws IOException{
        int size = 0;

        if (_in.available() > 0)
            size = _in.read(_in_buffer, 0, 100);

        if (size != 0){
            System.out.print(" >> ");
            sendMessage(ByteBuffer.wrap(_in_buffer));
        }
    }

    public void readInput(Channel channel){

    }

    public void handleClients() throws IOException{
        /*
        int ready = selector.select();

        if (ready == 0)
            return

        Set<SelectionKey> selectedKeys = _selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while(keyIterator.hasNext()) {

            SelectionKey key = keyIterator.next();

            if(key.isAcceptable()) {

                // a connection was accepted by a ServerSocketChannel.
                System.out.print("\n New client connected \n");
                key.channel().register(_selector, SelectionKey.OP_READ);

            } else if (key.isConnectable()){

                // a connection was established with a remote server.
                System.out.print("\n Connected to remote Server \n");
                key.channel().register(_selector, SelectionKey.OP_WRITE);

            } else if (key.isReadable()){// a channel is ready for reading

                readInput(key.channel());
                key.channel().register(_selector, SelectionKey.OP_READ);

            } else if (key.isWritable()){// a channel is ready for writing

                System.out.print("\n Useless ? \n");
            }
            keyIterator.remove();
        }*/

        for(SocketChannel c : _clients){
            if (c.isConnected()) {
                _out_buffer.clear();
                int size = c.read(_out_buffer);

                if (size != 0) {
                    System.out.print("Server: ");
                    printBuffer(_out_buffer);
                    System.out.print("\n");

                    if (_out_buffer.remaining() == 0)
                        _out_buffer.clear();
                }
            } else {
                _clients.remove(c);
            }
        }
    }

    public void printBuffer(ByteBuffer buffer){
        for(int i = 0; i < buffer.position(); ++i)
            System.out.print((char) buffer.get(i));
        System.out.flush();
    }

    public static void main(String[] args){
        Client c = new Client();

        try {
            c.mainLoop();
        } catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------------
    private ServerSocketChannel _listen_socket;
    private SocketChannel       _send_socket;
    private ByteBuffer          _out_buffer;

    private BufferedInputStream _in = new BufferedInputStream(System.in);
    private byte[]              _in_buffer;
    private List<SocketChannel> _clients;

    //private Selector            _selector;
    // ------------------------------------------------------------------------
    static private final String URL = "10.83.16.16";
    static private final int    SEND_PORT = 3782;
    static private final int    LISTEN_PORT = 81;

    //static private final String URL = "localhost"; 10.83.16.23
}
