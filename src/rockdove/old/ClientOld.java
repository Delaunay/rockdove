
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class ClientOld {

    public ClientOld()                 {    init(false);    }
    public ClientOld(boolean blocking) {    init(blocking); }

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
            _running = true;

            while (_running){
                runOnce();
            }
    }

    public void runOnce() throws Exception {
        SocketChannel client = _receiver.accept();

        if (client != null){
            client.configureBlocking(false);
            client.register(_selector, SelectionKey.OP_CONNECT);
        }

        // select(10) *blocks* for 10 ms
        int client_ready = _selector.selectNow(); //.select(10);

        if (client_ready > 0)
            handleClient();

        getInput();

        TimeUnit.MILLISECONDS.sleep(10);
    }

    // Private
    // ------------------------------------------------------------------------
    private void handleClient() throws IOException{
        Set<SelectionKey> keys = _selector.selectedKeys();
        Iterator<SelectionKey> key_it = keys.iterator();

        while(key_it.hasNext()){
            SelectionKey key = key_it.next();
            SocketChannel c = (SocketChannel) key.channel();

            if (key.isAcceptable()){
                printString("We are ready to accept new connection: " + c.toString());
                c.register(_selector, SelectionKey.OP_READ);

            } else if (key.isConnectable()) {
                if (c.isConnectionPending()){
                    //printString("Connection Pending: " + c.toString());
                }
                else {
                    printString("We connected to: " + c.toString());
                    c.register(_selector, SelectionKey.OP_WRITE);
                }

            } else if (key.isReadable()) {
                readData(c);
                printString("Receiving data: " + c.toString());
                c.register(_selector, SelectionKey.OP_READ);

            } else if (key.isWritable()) {
                printString("Useless? " + c.toString());
            }

            key_it.remove();
        }
    }

    private void getInput() throws IOException {
        int size;

        if (_in.available() == 0)
            return;

        size = _in.read(_in_buffer, 0, 100);

        if (size == 0)
            return;

        sendData(ByteBuffer.wrap(_in_buffer));
        _line += 1;
    }

    private void sendData(ByteBuffer buffer) throws IOException {
        buffer.flip();
        while(buffer.hasRemaining()){
            _sender.write(buffer);
        }
    }

    private void readData(SocketChannel channel) throws IOException{
        int size = channel.read(_out_buffer);

        if (size == 0)
            return;

        _out_buffer.flip();
        printData(_out_buffer);
    }

    private void printString(String a){
        if (a.length() > 0) {
            System.out.println(a);
            printLineInfo();
        }
    }

    private void printLineInfo(){
        System.out.print(" [");
        System.out.print(intToString(_line));
        System.out.print("] >> ");
    }

    private void printData(ByteBuffer b){
        System.out.println(b.asCharBuffer().toString());
        printLineInfo();
        _line += 1;
    }

    static String intToString(int i){   return intToString(i, 3); }
    static String intToString(int i, int col){
        int size = (int) Math.log10(i) + 1;

        if (size > col)
            return makeLine('9', col);

        if (size == col)
            return Integer.toString(i);

        return makeLine(' ', col - size) + Integer.toString(i);
    }

    static String makeLine(char a, int col){
        String s = new String(new char[col]);
        return s.replace('\0', a);
    }

    // ------------------------------------------------------------------------
    private boolean             _running;
    private int                 _line;

    //  std::io
    // ------------------------------------------------------------------------
    private BufferedInputStream _in;
    private byte[]              _in_buffer;
    private ByteBuffer          _out_buffer;

    //  Network
    // ------------------------------------------------------------------------
    private Selector            _selector;
    private SocketChannel       _sender;
    private ServerSocketChannel _receiver;

    // Constants
    // ------------------------------------------------------------------------
    public static final int    RECEIVER_PORT   = 80;
    public static final int    SENDER_PORT     = 81;
    public static final String URL             = "localhost";
    public static final int    IN_BUFFER_SIZE  = 100;
    public static final int    OUT_BUFFER_SIZE = 100;

    // ------------------------------------------------------------------------
    private void init(boolean blocking)
    {
        _running = true;
        _line = 1;
        _in = new BufferedInputStream(System.in);
        _in_buffer = new byte[IN_BUFFER_SIZE];
        _out_buffer = ByteBuffer.allocate(OUT_BUFFER_SIZE);

        printLineInfo();

        try{
            _selector = Selector.open();
            _sender   = SocketChannel.open();
            _receiver = ServerSocketChannel.open();

            _sender.configureBlocking(false);
            _sender.register(_selector, SelectionKey.OP_CONNECT);
            _sender.connect(new InetSocketAddress(URL, SENDER_PORT));

            _receiver.bind(new InetSocketAddress(RECEIVER_PORT));
            _receiver.configureBlocking(blocking);

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
