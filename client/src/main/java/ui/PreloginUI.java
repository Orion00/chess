package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;


import java.util.Arrays;

public class PreloginUI implements  ClientUI{
    private final String serverUrl;
    private final ServerFacade server;

    private String currentAuthToken;

    public PreloginUI(String url, ServerFacade server) {
        serverUrl = url;
        this.server = server;
        this.currentAuthToken = null;
    }

    @Override
    public String help() {
        return """
                help - Display available commands
                login <USERNAME> <PASSWORD> - Start playing
                register <USERNAME> <PASSWORD> <EMAIL> - Make an account to start playing
                quit - Close this window
                """;
    }

    public String eval(String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "help" -> help();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String login(String... params) throws ResponseException {
        if (params.length < 2) {
            throw new ResponseException(400, "Please enter a username and password");
        } else if (params.length > 2) {
            throw new ResponseException(400, "Too many inputs entered. Please enter only a username and password");
        }
        AuthData auth = server.login(params[0],params[1]);
        String authToken = auth.authToken();
        if (authToken == null) {
            throw new ResponseException(403, "invalid login credentials");
        }
        this.currentAuthToken = authToken;
        return "Login successful. Welcome "+params[0]+".\nType \"help\" to view new commands";
    }

    private String register(String... params) throws ResponseException{
        if (params.length < 3) {
            throw new ResponseException(400, "Please enter a username, password, and email");
        } else if (params.length > 3) {
            throw new ResponseException(400, "Too many inputs entered. Please enter only a username, password, and email");
        }
        AuthData auth = server.register(params[0],params[1],params[2]);
/*        if (auth != null) {
            throw new ResponseException(403, "invalid login credentials");
        }*/
        this.currentAuthToken = auth.authToken();
        return "Registration successful. Welcome "+params[0]+".\nType \"help\" to view new commands";
    }

    public boolean isAuthorized() {
        return currentAuthToken != null;
    }

    public String getAuthToken() { return currentAuthToken;}

    public void resetIsAuthorized() {currentAuthToken = null;}
}
