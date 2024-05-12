package pieces;

public class Queen extends Piece {
    public Queen(Piece[][] board, int row, int col, boolean isWhite) {
        super(board, row, col, isWhite);
    }

    public Queen(Pawn pawn) {
        super(pawn.board, pawn.row, pawn.col, pawn.isWhite);
        this.numMoves = pawn.numMoves;
    }

    @Override
    public boolean[][] getLegalMoves() {
        boolean[][] moves = new boolean[8][8];
        int[][] directions = { {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1} };

        for (int[] direction : directions) {
            int i = this.row + direction[0];
            int j = this.col + direction[1];

            while (0 <= i && i < 8 && 0 <= j && j < 8) {
                if (this.board[i][j] == null) {
                    moves[i][j] = true;
                } else {
                    if (this.board[i][j].isWhite != this.isWhite)
                        moves[i][j] = true;
                    break;
                }

                i += direction[0];
                j += direction[1];
            }
        }

        return preventCheckmate(moves);
    }

    @Override
    public boolean threatens(Piece piece) {
        if (this.isWhite == piece.isWhite)
            return false;

        int[][] directions = { {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1} };

        for (int[] direction : directions) {
            int i = this.row + direction[0];
            int j = this.col + direction[1];

            while (0 <= i && i < 8 && 0 <= j && j < 8) {
                if (i == piece.row && j == piece.col)
                    return true;
                if (this.board[i][j] != null)
                    break;

                i += direction[0];
                j += direction[1];
            }
        }

        return false;
    }

    @Override
    public String toStringShort() {
        return (this.isWhite ? "W" : "B") + "QU";
    }

    @Override
    public String toString() {
        return "Queen";
    }
}
