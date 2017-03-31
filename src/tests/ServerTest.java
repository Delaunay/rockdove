package tests;

import org.junit.Before;
import rockdove.Server;
import rockdove.ServerList;

/**
 * Created by user on 3/31/2017.
 */
public class ServerTest {

    @Before
    public void init(){
        Server.RECEIVER_PORT = 80;
        Server s = new ServerList();
    }


}
