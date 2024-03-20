package model;

import java.util.List;

public record ListGameResult(List<GameData> games) {
//    @Override
//    public String toString() {
//        StringBuilder result = new StringBuilder();
//        result.append("Games:\n");
//        for (GameData game : games) {
//            result.append(game.toString());
//            result.append("\n");
//        }
//        return result.toString();
//    }
}
