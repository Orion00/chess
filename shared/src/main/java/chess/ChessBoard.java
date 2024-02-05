package chess;

import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Copy;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {
    ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {

    }

    public ChessBoard(ChessBoard cboard) {
        this.board = cboard.board;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
//        System.out.println("Orion tries to add a piece");
//        System.out.printf("It's %s at position %s%n", piece, position);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }


    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Chessboard \n");
        for (int r = 8; r > 0; r--) {
            output.append("| ");
            for (int c = 1; c < 9; c++) {
                output.append(getPiece(new ChessPosition(r,c)));
                output.append(" |");
            }
            output.append("\n");
        }

        return output.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        int startingRow = 1;
        int secondRow = 2;
        for (int team = 0; team < 2; team++) {
            // Backline
            addPiece(new ChessPosition(startingRow,1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
            addPiece(new ChessPosition(startingRow,2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
            addPiece(new ChessPosition(startingRow,3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
            addPiece(new ChessPosition(startingRow,4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
            addPiece(new ChessPosition(startingRow,5), new ChessPiece(color, ChessPiece.PieceType.KING));
            addPiece(new ChessPosition(startingRow,6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
            addPiece(new ChessPosition(startingRow,7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
            addPiece(new ChessPosition(startingRow,8), new ChessPiece(color, ChessPiece.PieceType.ROOK));

            // Pawns
            for (int col = 1; col < 9; col++) {
                addPiece(new ChessPosition(secondRow,col), new ChessPiece(color, ChessPiece.PieceType.PAWN));
            }
            color = ChessGame.TeamColor.BLACK;
            startingRow = 8;
            secondRow = 7;
        }
    }

    @Override
    public ChessBoard clone() {
        ChessBoard temp = new ChessBoard();

        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                temp.addPiece(new ChessPosition(r,c),this.getPiece(new ChessPosition(r,c)));
            }
        }
        return temp;
    }
}
