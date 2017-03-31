
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.TimeUnit;


public class Client {

    public Client()                 {    init();    }

    // Public
    // ------------------------------------------------------------------------
    static public void main(String[] args){
        ClientOld a = new ClientOld();
        try {
            a.mainLoop();
        } catch (Exception e){
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

    // Private
    // ------------------------------------------------------------------------

    private void getInput() throws IOException {
        int size;

        if (_in.available() == 0)
            return;

        size = _in.read(_in_buffer, 0, 100);

        if (size == 0)
            return;

        sendData(ByteBuffer.wrap(_in_buffer));
        OutDevice.line();
    }

    private void sendData(ByteBuffer buffer) throws IOException {
        buffer.flip();
        while(buffer.hasRemaining()){
            _sender.write(buffer);
        }
    }

    //  std::io
    // ------------------------------------------------------------------------
    private BufferedInputStream _in;
    private byte[]              _in_buffer;
    private SocketChannel       _sender;

    // Constants
    // ------------------------------------------------------------------------
    public static final int    SENDER_PORT     = 81;
    public static final String SENDER_ADD      = "localhost";
    public static final int    IN_BUFFER_SIZE  = 100;

    // ------------------------------------------------------------------------
    private void init()
    {
        _in = new BufferedInputStream(System.in);
        _in_buffer = new byte[IN_BUFFER_SIZE];

        OutDevice.printLineInfo();

        try{
            _sender = SocketChannel.open();
            _sender.configureBlocking(false);
            _sender.connect(new InetSocketAddress(SENDER_ADD, SENDER_PORT));
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
