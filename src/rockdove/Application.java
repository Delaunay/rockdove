package rockdove;

public class Application {
    public static void main(String[] args){
        Application a = new Application();
        a.mainLoop();
    }

    public void mainLoop(){
        _client = new Client();
        _server = new ServerList();

        try {
            while(true)
                runOnce();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runOnce() throws Exception {
        _server.runOnce();
        _client.runOnce();
    }

    private Client _client;
    private Server _server;
}
