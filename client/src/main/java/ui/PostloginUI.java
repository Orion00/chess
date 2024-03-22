package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.GameData;

import java.util.Arrays;
import java.util.List;

public class PostloginUI implements ClientUI {
    private final String serverUrl;
    private final ServerFacade server;
    private boolean loggedIn;

    private Integer currentGameId;

    public PostloginUI(String url, ServerFacade server) {
        serverUrl = url;
        this.server = server;
        loggedIn = true;
        currentGameId = null;
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
        if (response.isEmpty()) {
            result.append("  No games currently being played");
            return result.toString();
        }

        int gameIdIterator = 1;
        for (GameData game : response) {
            result.append("Game Number: ").append(gameIdIterator).append("\n");
            result.append(game.prettyToString());
            gameIdIterator++;
//            result.append('\n');
        }
        return result.toString();
    }

    private String join(String authToken, String... params) throws ResponseException {
        if (params.length > 2) {
            throw new ResponseException(400, "Too many inputs entered. Try again.");
        } else if (params.length < 2) {
            throw new ResponseException(400, "Please enter a game number and color to play as. Try again.");
        }
        String playerColor = params[1].toUpperCase();

        if (playerColor.equals("W")) {
            playerColor = "WHITE";
        } else if (playerColor.equals("B")) {
            playerColor = "BLACK";
        }
        if (!(playerColor.equals("WHITE") || playerColor.equals("BLACK"))) {
            throw new ResponseException(400, "Please enter a valid color. Options are \"WHITE\" and \"BLACK\". Try again.");
        }

        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]) - 1; // Because user isn't using 0 based indexing
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Please enter a valid number. Try again.");
        }

        List<GameData> currentGames = server.listGames(authToken);
        if (gameNumber < 0 || gameNumber > currentGames.size() - 1) {
            throw new ResponseException(400, "Invalid game number. Use \"list\" to view available game numbers. Try again.");
        }

        Integer gameID = currentGames.get(gameNumber).gameID();
        server.joinGame(authToken, playerColor, gameID);
        currentGameId = gameID;
        return "Now joining game " + params[0] + ".";
    }

    private String watch(String authToken, String... params) throws ResponseException{
        if (params.length > 1) {
            throw new ResponseException(400, "Too many inputs entered. Try again.");
        } else if (params.length < 1) {
            throw new ResponseException(400, "Please enter a game number. Try again.");
        }


        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]) - 1; // Because user isn't using 0 based indexing
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Please enter a valid number. Try again.");
        }

        List<GameData> currentGames = server.listGames(authToken);
        if (gameNumber < 0 || gameNumber > currentGames.size()-1) {
            throw new ResponseException(400, "Invalid game number. Use \"list\" to view available game numbers. Try again.");
        }
        Integer gameID = currentGames.get(gameNumber).gameID();

        server.joinGame(authToken, null, gameID);
        currentGameId = gameID;
        return "Now observing game "+params[0]+".";
    }

    private String create(String authToken, String... params) throws ResponseException{
        if (params.length < 1) {
            throw new ResponseException(400, "Please enter a new game name");
        } else if (params.length > 1) {
            throw new ResponseException(400, "Too many inputs entered. Try again.");
        }
        int response = server.createGame(authToken, params[0]).gameID();
//        return MessageFormat.format("Game created successfully. Game ID is {0}", response);
        return "Game created successfully. Use \"list\" to view it.";
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
                case "join" -> join(authToken, params);
                case "watch" -> watch(authToken, params);
                case "observe" -> watch(authToken, params);
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

    public Integer getCurrentGameId() {
        return currentGameId;
    }
    public void resetCurrentGameId() {
        currentGameId = null;
    }
}