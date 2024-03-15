import ui.PostloginUI;
import ui.PreloginUI;

// This creates
public class ClientHandler {
    private final String serverUrl;
    private ServerFacade server;
    private PreloginUI preloginUI;
    private PostloginUI postloginUI;
    public ClientHandler(String url) {
        serverUrl = url;
        server = new ServerFacade(url);
        preloginUI = new PreloginUI(url);
        postloginUI = new PostloginUI(url);

    }

    public void run() {
        System.out.print("Welcome to your favorite console based chess player");
    }

}
