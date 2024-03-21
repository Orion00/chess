package model;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.Arrays;
import java.util.Objects;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public String prettyToString() {
        StringBuilder result = new StringBuilder();
//        String a = "GameData{" +
//                "gameID=" + gameID +
//                ", whiteUsername='" + whiteUsername + '\'' +
//                ", blackUsername='" + blackUsername + '\'' +
//                ", gameName='" + gameName + '\'' +
//                ", game=" + game +
//                '}';
        result.append("  Game Name: ");
        result.append(gameName);
        result.append("\n  Players: ");
        if (whiteUsername != null) {
            result.append(whiteUsername);
        } else {
            result.append("EMPTY");
        }
        result.append(" (White), ");

        if (blackUsername != null) {
            result.append(blackUsername);
        } else {
            result.append("EMPTY");
        }
        result.append(" (Black)\n");

        return result.toString();
    }
}
