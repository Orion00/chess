package ui;

import exception.ResponseException;

import java.util.Arrays;

public class PreloginUI implements  ClientUI{
    private final String serverUrl;
    public PreloginUI(String url) {
        serverUrl = url;
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

    @Override
    public String eval(String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
//                case "signin" -> signIn(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
//                case "quit" -> "quit";
                case null -> "a";
                default -> {
                    help();
                    throw new ResponseException(500, "Oops");
                }
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
}
