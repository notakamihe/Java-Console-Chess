package pieces;

public class Pawn extends Piece {
    private boolean allowEnPassant;

    public Pawn(Piece[][] board, int row, int col, boolean isWhite) {
        super(board, row, col, isWhite);
        this.allowEnPassant = false;
    }

    @Override
    public boolean[][] getLegalMoves() {
        boolean[][] moves = new boolean[8][8];
        int forward = this.isWhite ? -1 : 1;

        for (int i = 0; i < (this.numMoves == 0 ? 2 : 1); i++) {
            int r = this.row + forward * (i + 1);
            if (0 <= r && r < 8)
                if (this.board[r][this.col] == null)
                    moves[r][this.col] = true;
        }

        for (int i = -1; i <= 1; i += 2) {
            int j = this.col + i;

            if (0 <= j && j < 8) {
                Piece diagonal = this.board[this.row + forward][j];
                Piece adjacent = this.board[this.row][j];

                if (diagonal != null && diagonal.isWhite != this.isWhite)
                    moves[this.row + forward][j] = true;

                if (adjacent instanceof Pawn adjacentPawn && adjacentPawn.allowEnPassant)
                    if (adjacentPawn.isWhite != this.isWhite)
                        if (this.isWhite && this.row == 3 || !this.isWhite && this.row == 4)
                            moves[this.row + forward][j] = true;
            }
        }

        return preventCheckmate(moves);
    }

    public void setAllowEnPassant(boolean allowEnPassant) {
        this.allowEnPassant = allowEnPassant;
    }

    @Override
    public boolean threatens(Piece piece) {
        if (this.isWhite == piece.isWhite)
            return false;
        return this.row - piece.row == (this.isWhite ? 1 : -1) && Math.abs(this.col - piece.col) == 1;
    }

    @Override
    public String toStringShort() {
        return (this.isWhite ? "W" : "B") + "PA";
    }

    @Override
    public String toString() {
        return "Pawn";
    }
}
