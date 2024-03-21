package ui;

import client.ServerFacade;

public class GameplayUI implements ClientUI{

    public GameplayUI(String url, ServerFacade server) {

    }

    @Override
    public String help() {
        return """
            (Functionality limited)
            help - Display available commands
            print - Show your board
            move - Move selected piece
            show - Show available moves for selected piece
            quit - Return to previous window. Can resume later
            resign - Surrender the game
            """;
    }


    public String eval(String currentAuthToken, String line) {
        // TODO: Add this
        return line;
    }
}
