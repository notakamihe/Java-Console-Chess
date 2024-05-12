package pieces;

public class King extends Piece {
    public King(Piece[][] board, int row, int col, boolean isWhite) {
        super(board, row, col, isWhite);
    }

    public Rook getCastlingRook(boolean kingside) {
        if (this.board[this.row][this.col] != null) {
            int j = this.col + (kingside ? 1 : -1);

            while (0 <= j && j < 8) {
                if (this.board[this.row][j] != null) {
                    if (this.board[this.row][j] instanceof Rook rook && rook.isWhite == this.isWhite)
                        return rook;
                    break;
                }
                j += (kingside ? 1 : -1);
            }
        }

        return null;
    }

    @Override
    public boolean[][] getLegalMoves() {
        boolean[][] moves = new boolean[8][8];
        int[][] directions = { {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1} };

        for (int[] direction : directions) {
            int i = this.row + direction[0];
            int j = this.col + direction[1];

            if (0 <= i && i < 8 && 0 <= j && j < 8)
                if (this.board[i][j] == null || this.board[i][j].isWhite != this.isWhite)
                    if (!this.isPieceThreatenedByMove(this, i, j))
                        moves[i][j] = true;
        }

        if (this.numMoves == 0 && !this.isThreatened()) {
            for (int i = -1; i <= 1; i += 2) {
                Rook rook = this.getCastlingRook(i == 1);

                if (rook != null && rook.numMoves == 0)
                    if (!this.isPieceThreatenedByMove(this, row, col + 2 * i))
                        moves[row][col + 2 * i] = true;
            }
        }

        return moves;
    }

    @Override
    public boolean threatens(Piece piece) {
        if (this.isWhite == piece.isWhite)
            return false;

        int[][] directions = { {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1} };

        for (int[] direction : directions)
            if (this.row + direction[0] == piece.row && this.col + direction[1] == piece.col)
                return true;

        return false;
    }

    @Override
    public String toStringShort() {
        return (this.isWhite ? "W" : "B") + "KI";
    }

    @Override
    public String toString() {
        return "King";
    }
}
