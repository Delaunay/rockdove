import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class ServerSelector extends Server {
    ServerSelector() throws IOException{
        _selector = Selector.open();
    }

    @Override
    public void runOnce() throws IOException{
        SocketChannel client = _receiver.accept();

        if (client != null){
            client.configureBlocking(false);
            client.register(_selector, SelectionKey.OP_CONNECT);
        }

        int client_ready = _selector.selectNow(); //.select(10);

        if (client_ready > 0)
            handleClient();
    }

    // Private
    // ------------------------------------------------------------------------
    @Override
    protected void handleClient() throws IOException{
        Set<SelectionKey> keys = _selector.selectedKeys();
        Iterator<SelectionKey> key_it = keys.iterator();

        while(key_it.hasNext()){
            SelectionKey key = key_it.next();
            SocketChannel c = (SocketChannel) key.channel();

            if (key.isAcceptable()){
                OutDevice.printString("We are ready to accept new connection: " + c.toString());
                c.register(_selector, SelectionKey.OP_READ);

            } else if (key.isConnectable()) {
                if (c.isConnectionPending()){
                    //printString("Connection Pending: " + c.toString());
                }
                else {
                    OutDevice.printString("We connected to: " + c.toString());
                    c.register(_selector, SelectionKey.OP_WRITE);
                }

            } else if (key.isReadable()) {
                readData(c);
                OutDevice.printString("Receiving data: " + c.toString());
                c.register(_selector, SelectionKey.OP_READ);

            } else if (key.isWritable()) {
                OutDevice.printString("Useless? " + c.toString());
            }

            key_it.remove();
        }
    }

    //  Network
    // ------------------------------------------------------------------------
    private Selector _selector;
}
