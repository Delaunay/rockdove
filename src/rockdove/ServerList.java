package rockdove;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;


public class ServerList extends Server{
    public ServerList(){
        super();
        _clients = new LinkedList<>();
    }

    @Override
    public void runOnce() throws IOException{
        SocketChannel client = _receiver.accept();

        if (client != null){
            _log.info("Server: Accepting Connection");
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
