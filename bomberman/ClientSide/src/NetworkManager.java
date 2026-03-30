public class NetworkManager {

    private static NetworkManager network;

    public static synchronized NetworkManager getInstance (){
        if (network == null){
            new NetworkManager();
        }
        return network;
    }

    private NetworkManager (){
        network = new NetworkManager();
    }
}
