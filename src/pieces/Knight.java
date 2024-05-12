package pieces;

public class Knight extends Piece {
    public Knight(Piece[][] board, int row, int col, boolean isWhite) {
        super(board, row, col, isWhite);
    }

    public Knight(Pawn pawn) {
        super(pawn.board, pawn.row, pawn.col, pawn.isWhite);
        this.numMoves = pawn.numMoves;
    }

    @Override
    public boolean[][] getLegalMoves() {
        boolean[][] moves = new boolean[8][8];
        int[][] directions = { {-2, -1}, {-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2} };

        for (int[] direction : directions) {
            int i = this.row + direction[0];
            int j = this.col + direction[1];

            if (0 <= i && i < 8 && 0 <= j && j < 8)
                if (this.board[i][j] == null || this.board[i][j].isWhite != this.isWhite)
                    moves[i][j] = true;
        }

        return preventCheckmate(moves);
    }

    @Override
    public boolean threatens(Piece piece) {
        if (this.isWhite == piece.isWhite)
            return false;

        int rowDiff = Math.abs(this.row - piece.row);
        int colDiff = Math.abs(this.col - piece.col);

        return rowDiff == 1 && colDiff == 2 || rowDiff == 2 && colDiff == 1;
    }

    @Override
    public String toStringShort() {
        return (this.isWhite ? "W" : "B") + "KN";
    }

    @Override
    public String toString() {
        return "Knight";
    }
}
