import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by newton on 2017-03-31.
 */
public class ServerList extends Server{
    ServerList(){
        _clients = new LinkedList<>();
    }

    @Override
    public void runOnce() throws IOException{
        SocketChannel client = _receiver.accept();

        if (client != null){
            client.configureBlocking(false);
            _clients.add(client);
        }

        handleClient();
    }

    // Private
    // ------------------------------------------------------------------------
    @Override
    protected void handleClient() throws IOException{
        for(SocketChannel client : _clients){
            if (client.isConnected()){
                // This might be blocking
                readData(client);
            } else {
                _clients.remove(client);
            }
        }
    }

    //  Network
    // ------------------------------------------------------------------------
    private List<SocketChannel> _clients;
}
