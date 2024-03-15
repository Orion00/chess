import chess.*;

// Main creates a Client Handler using serverURL
// Client Handler has a ServerFacade and handles switching UIs
public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");

        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new ClientHandler(serverUrl).run();
    }
}