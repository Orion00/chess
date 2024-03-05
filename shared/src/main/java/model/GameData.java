package model;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.Arrays;
import java.util.Objects;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

}
