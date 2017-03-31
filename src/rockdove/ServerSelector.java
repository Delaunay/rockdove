package rockdove;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class ServerSelector extends Server {
    public ServerSelector() {
        super();
        try {
            _selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runOnce() throws IOException{
        SocketChannel client = _receiver.accept();

        if (client != null){
            _log.info("Server: Accepting Connection");
            client.configureBlocking(false);
            client.register(_selector, SelectionKey.OP_READ);
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

            if (key.isConnectable()) {
                if (c.isConnectionPending()){
                    _log.info("Pending");
                    //printString("Connection Pending: " + c.toString());
                }
                else {
                    _log.info("we are connected to a client");
                    OutDevice.printString("We connected to: " + c.toString());
                    c.register(_selector, SelectionKey.OP_WRITE);
                }

            } else if (key.isReadable()) {
                _log.info("Receiving data");
                readData(c);
                _log.info("We received data");
                //OutDevice.printString("Receiving data: " + c.toString());
                //c.register(_selector, SelectionKey.OP_WRITE);

            } else if (key.isWritable()) {
                _log.info("Useless Branch ? " + c.toString());
            } else if (key.isAcceptable()){
                _log.info("Useless Branch ? " + c.toString());
            }

            key_it.remove();
        }
    }

    //  Network
    // ------------------------------------------------------------------------
    private Selector _selector;
}
