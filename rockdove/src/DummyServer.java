import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by newton on 2017-03-30.
 */
public class DummyServer {

    public DummyServer()                 {    init();    }

    // Public
    // ------------------------------------------------------------------------
    static public void main(String[] args){

        DummyServer a = new DummyServer();
        try {
            a.mainLoop();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void mainLoop() throws Exception {
        while (true){
            runOnce();
        }
    }

    public void runOnce() throws Exception {
        SocketChannel client = _receiver.accept();

        if (client != null){
            System.out.println("CONNECTED");
            client.configureBlocking(false);
            client.register(_selector, SelectionKey.OP_CONNECT);

            String str = "SERVER HERE";
            client.write(ByteBuffer.wrap(str.getBytes()));
        }

        // select(10) *blocks* for 10 ms
        int client_ready = _selector.selectNow(); //.select(10);

        if (client_ready > 0)
            handleClient();

        TimeUnit.MILLISECONDS.sleep(10);
    }

    public void handleClient() throws Exception{
        Set<SelectionKey> keys = _selector.selectedKeys();
        Iterator<SelectionKey> key_it = keys.iterator();

        while(key_it.hasNext()){
            SelectionKey key = key_it.next();
            SocketChannel c = (SocketChannel) key.channel();

            if (key.isConnectable()) {
                if (c.isConnectionPending()){
                }
                else {
                    c.register(_selector, SelectionKey.OP_WRITE);
                }

            } else if (key.isReadable()) {
                System.out.println("READABLE");
                c.register(_selector, SelectionKey.OP_READ);

            } else if (key.isWritable()) {
                System.out.println("WRITABLE");
            }

            key_it.remove();
        }
    }



    //  Network
    // ------------------------------------------------------------------------
    private Selector            _selector;
    private ServerSocketChannel _receiver;

    private void init(){
        try{
            _selector = Selector.open();

            _receiver = ServerSocketChannel.open();
            _receiver.bind(new InetSocketAddress(Client.SENDER_PORT));
            _receiver.configureBlocking(false);

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
