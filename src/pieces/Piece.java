package pieces;

public abstract class Piece {
    protected boolean isWhite;
    protected int numMoves;
    protected Piece[][] board;
    protected int row;
    protected int col;

    public Piece(Piece[][] board, int row, int col, boolean isWhite) {
        this.board = board;
        this.row = row;
        this.col = col;
        this.isWhite = isWhite;
        this.numMoves = 0;
        this.board[row][col] = this;
    }

    public boolean getIsWhite() {
        return this.isWhite;
    }

    public static King getKing(Piece[][] board, boolean isWhite) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (board[i][j] != null && board[i][j] instanceof King && board[i][j].isWhite == isWhite)
                    return (King)board[i][j];

        return null;
    }

    public abstract boolean[][] getLegalMoves();

    public int getNumMoves() {
        return this.numMoves;
    }

    public boolean isPieceThreatenedByMove(Piece piece, int destRow, int destCol) {
        boolean threatened = false;
        Piece occupied = this.board[destRow][destCol];
        int row = this.row;
        int col = this.col;

        this.move(destRow, destCol, true);

        if (piece.isThreatened())
            threatened = true;

        this.move(row, col, true);
        this.board[destRow][destCol] = occupied;

        return threatened;
    }

    public boolean isThreatened() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (board[i][j] != null && board[i][j].threatens(this))
                    return true;

        return false;
    }

    public void move(int destRow, int destCol) {
        this.move(destRow, destCol, false);
    }

    public void move(int destRow, int destCol, boolean test) {
        this.board[destRow][destCol] = this;
        this.board[this.row][this.col] = null;
        this.row = destRow;
        this.col = destCol;

        if (!test)
            this.numMoves++;
    }

    public int numLegalMoves() {
        boolean[][] legal = this.getLegalMoves();
        int numLegal = 0;

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (legal[i][j])
                    numLegal++;

        return numLegal;
    }

    protected boolean[][] preventCheckmate(boolean[][] moves) {
        King king = Piece.getKing(this.board, this.isWhite);

        if (king != null) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (moves[i][j])
                        if (this.isPieceThreatenedByMove(king, i, j))
                            moves[i][j] = false;
                }
            }
        }

        return moves;
    }

    public abstract boolean threatens(Piece piece);

    public abstract String toStringShort();
}
