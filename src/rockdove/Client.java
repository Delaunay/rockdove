package rockdove;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class Client implements Runnable {

    public Client()                 {    init();    }

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

    public void runOnce() throws Exception {
        getInput();
    }

    public void sendData(ByteBuffer buffer) throws IOException {
        _log.info("Client: Sending data");
        while(buffer.hasRemaining()){
            int written = _sender.write(buffer);
            _log.info("Client: " + Integer.toString(written) + " bytes were written to Remote");
        }
    }

    public boolean finishConnect() throws IOException {
        return _sender.finishConnect();
    }

    public boolean connected(){
        return _sender.isConnected();
    }

    // Private
    // ------------------------------------------------------------------------

    private void getInput() throws IOException {
        int size;

        if (_in.available() <= 0)
            return;

        size = _in.read(_in_buffer, 0, 100);

        if (size == 0) {
            _log.info("Read empty input!");
            return;
        }

        OutDevice.printLineInfo();
        sendData(ByteBuffer.wrap(_in_buffer));
        OutDevice.line();
    }

    //  std::io
    // ------------------------------------------------------------------------
    private BufferedInputStream _in;
    private byte[]              _in_buffer;
    private SocketChannel       _sender;
    private Logger              _log;

    // Constants
    // ------------------------------------------------------------------------
    public static int    SENDER_PORT     = 81;
    public static String SENDER_ADD      = "localhost";
    public static int    IN_BUFFER_SIZE  = 100;


    // ------------------------------------------------------------------------
    private void init()
    {
        _log = LogManager.getLogger("Debug");
        _in = new BufferedInputStream(System.in);
        _in_buffer = new byte[IN_BUFFER_SIZE];

        try{
            _sender = SocketChannel.open();
            _sender.configureBlocking(false);
            _sender.connect(new InetSocketAddress(SENDER_ADD, SENDER_PORT));
            _log.info("Client.init done");

            if (_sender.finishConnect())
                _log.info("Client: Connected to remote");
            else
                _log.info("Client: Connection to remote pending");
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
