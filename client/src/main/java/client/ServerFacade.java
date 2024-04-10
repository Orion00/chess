package client;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class ServerFacade {
    private final String serverUrl;
    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE",path,null,null,null);
    }
    public AuthData login(String username, String password) throws ResponseException {
        UserData user = new UserData(username, password, null);
        var path = "/session";
        return this.makeRequest("POST", path, user, null,AuthData.class);
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        UserData user = new UserData(username, password, email);
        var path = "/user";
        return this.makeRequest("POST", path, user, null,AuthData.class);
    }

    public String logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, authToken, null);
        return "{}";
    }

    public List<GameData> listGames(String authToken) throws ResponseException {
        var path = "/game";
        ListGameResult result = this.makeRequest("GET", path, null, authToken, ListGameResult.class);
        return result.games();
    }

    public GameData createGame(String authToken,String gameName) throws ResponseException {
        var path = "/game";
//        ChessGame chessGame = new ChessGame();
//        ChessBoard chessBoard = new ChessBoard();
//        chessBoard.resetBoard();
//        chessGame.setBoard(chessBoard);
//        chessGame.setTeamTurn(ChessGame.TeamColor.WHITE);
        return this.makeRequest("POST", path, new GameData(101, null, null, gameName, null), authToken, GameData.class);
    }

    public void joinGame(String authToken,String playerColor, Integer gameID) throws ResponseException {
        // TODO: Add some functionality to this
        var path = "/game";
        this.makeRequest("PUT", path, new JoiningGameData(playerColor, gameID), authToken, null);
    }
    private <T> T makeRequest(String method, String path, Object request, String header, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            if (header != null) {
                http.addRequestProperty("Authorization", header);
            }
            if (request != null) {
                http.setDoOutput(true);
            }
            writeBody(request, http);
            http.connect();
            if (http.getResponseCode() != 200) {
                throw new ResponseException(http.getResponseCode(), "Failure: "+http.getResponseMessage());
            }


            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}
