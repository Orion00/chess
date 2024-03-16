package ui;

import client.ServerFacade;

public class PostloginUI implements ClientUI {
    private final String serverUrl;
    private final ServerFacade server;
    public PostloginUI(String url, ServerFacade server) {
        serverUrl = url;
        this.server = server;
    }

    @Override
    public String help() {
        return """
            help - Display available commands
            list - Shows all games being played
            join <GAME NUMBER> <WHITE/BLACK> - Join a game being played
            watch <GAME NUMBER> - Watch a game being played
            create <GAME NAME> - Create a new game
            logout - Return to first window
            """;
    }

    @Override
    public String eval(String line) {
        return null;
    }
}