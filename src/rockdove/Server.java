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

public abstract class Server {
    public Server()                 {    init();    }

    // Public
    // ------------------------------------------------------------------------
    public void mainLoop() throws Exception{
        while (true){
            runOnce();
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    public void runOnce() throws IOException{
        SocketChannel client = _receiver.accept();

        if (client != null){
            client.configureBlocking(false);
        }

        handleClient();
    }

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
        System.out.print(b.asCharBuffer().toString());
    }

    //  Network
    // ------------------------------------------------------------------------
    protected ServerSocketChannel _receiver;
    protected ByteBuffer          _out_buffer;
    protected int                 _port_offset;
    protected Logger              _log;

    // Constants
    // ------------------------------------------------------------------------
    public static final int    RECEIVER_PORT   = 80;
    public static final int    OUT_BUFFER_SIZE = 100;

    // ------------------------------------------------------------------------
    private void initServerSocket(){
        _port_offset = 0;

        while(true) {
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

    private void init()
    {
        _out_buffer = ByteBuffer.allocate(OUT_BUFFER_SIZE);

        initLog();
        initServerSocket();
    }
}
