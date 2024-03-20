package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public class PostloginUI implements ClientUI {
    private final String serverUrl;
    private final ServerFacade server;
    private boolean loggedIn;
    public PostloginUI(String url, ServerFacade server) {
        serverUrl = url;
        this.server = server;
        loggedIn = true;
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

    private String logout(String authToken, String... params) throws ResponseException{
        if (params.length > 0) {
            throw new ResponseException(400, "Too many inputs entered");
        }
        String response = server.logout(authToken);
        if (response != "{}") {
            throw new ResponseException(403, "Something has gone horribly wrong");
        }
        loggedIn = false;
        return "Logout successful. Type \"help\" to view new commands";
    }

    private String list(String authToken, String... params) throws ResponseException{
        if (params.length > 0) {
            throw new ResponseException(400, "Too many inputs entered. Try again.");
        }
        List<GameData> response = server.listGames(authToken);

        StringBuilder result = new StringBuilder();

        result.append("Current Games\n");
        for (GameData game : response) {
            result.append(game.prettyToString());
//            result.append('\n');
        }

        return result.toString();
    }

    private String create(String authToken, String... params) throws ResponseException{
        if (params.length < 1) {
            throw new ResponseException(400, "Please enter a new game name");
        } else if (params.length > 1) {
            throw new ResponseException(400, "Too many inputs entered. Try again.");
        }
        int response = server.createGame(authToken, params[0]).gameID();
        return MessageFormat.format("Game create successfully. Game ID is {0}", response);
    }

    public String eval(String authToken, String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(authToken, params);
                case "list" -> list(authToken, params);
                case "create" -> create(authToken, params);
//                case "register" -> register(params);
                case "help" -> help();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public boolean isAuthorized() {
        return loggedIn;
    }
}