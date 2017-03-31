package rockdove;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class Server implements Runnable {
    public Server()                 {    init();    }

    // Public
    // ------------------------------------------------------------------------
    @Override
    public void run(){
        try {
            mainLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mainLoop() throws Exception{
        while (true){
            runOnce();
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    public abstract void runOnce() throws IOException;

    // Private
    // ------------------------------------------------------------------------
    protected abstract void handleClient() throws IOException;

    protected void readData(SocketChannel channel) throws IOException{
        _out_buffer.clear();
        int size = channel.read(_out_buffer);

        if (size == 0)
            return;

        _out_buffer.flip();
        printData(_out_buffer);
    }

    protected void printData(ByteBuffer b){
        b.flip(); // <- Make sure we print everything
        OutDevice.printString(b.asCharBuffer().toString());
    }

    //  Network
    // ------------------------------------------------------------------------
    protected ServerSocketChannel _receiver;
    protected ByteBuffer          _out_buffer;
    protected int                 _port_offset;
    protected Logger              _log;

    // Constants
    // ------------------------------------------------------------------------
    public static int    RECEIVER_PORT   = 80;
    public static int    OUT_BUFFER_SIZE = 100;

    // ------------------------------------------------------------------------
    private void initServerSocket(){
        _port_offset = 0;

        while(_port_offset < 10) {
            try {
                _receiver = ServerSocketChannel.open();
                _receiver.bind(new InetSocketAddress(RECEIVER_PORT + _port_offset));
                _receiver.configureBlocking(false);
                break;
            } catch (BindException e){
                _port_offset += 1;
                _log.info("Port in use trying: [Port= " +
                        Integer.toString(RECEIVER_PORT + _port_offset) + "]");
            } catch (IOException e) {
                // unhandled error
                e.printStackTrace();
                _log.error("Unhandled exception: " + e.getMessage());
                break;
            }
        }
    }

    private void initLog(){
        BasicConfigurator.configure();
        _log = LogManager.getLogger("debug");
    }

    private void init(){
        _out_buffer = ByteBuffer.allocate(OUT_BUFFER_SIZE);
        initLog();
        initServerSocket();
        _log.info("Server.init done");
    }
}
