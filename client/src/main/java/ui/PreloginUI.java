package ui;

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
}
