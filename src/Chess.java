import pieces.*;
import java.util.Scanner;


public class Chess {
    private static final Scanner scanner = new Scanner(System.in);
    private static Piece[][] board;

    public static void main(String[] args) {
        initBoard();

        boolean isWhiteTurn = true;
        Piece captured = null;
        Pawn enPassantPawn = null;

        while (true) {
            Piece piece = null;
            boolean[][] legalMoves = new boolean[8][8];
            int row = -1, col = -1;
            int destRow = -1, destCol = -1;

            King king = Piece.getKing(board, isWhiteTurn);
            boolean check = king.isThreatened();

            int movablePiecesCount = 0;
            int movablePieceRow = -1;
            int movablePieceCol = -1;
            boolean allKings = true;

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board[i][j] != null) {
                        if (!(board[i][j] instanceof King))
                            allKings = false;
                        if (board[i][j].getIsWhite() == isWhiteTurn && board[i][j].numLegalMoves() > 0) {
                            movablePiecesCount++;
                            movablePieceRow = i;
                            movablePieceCol = j;
                        }
                    }
                }
            }

            printBoard();

            if (check && movablePiecesCount == 0) {
                System.out.println("Checkmate! " + (isWhiteTurn ? "Black" : "White") + " has won!");
                break;
            } else if (allKings || movablePiecesCount == 0) {
                System.out.println("Stalemate!");
                break;
            }

            if (captured != null) {
                System.out.println(captured + " captured.");
                captured = null;
            }

            System.out.print(isWhiteTurn ? "White's turn. " : "Black's turn. ");
            System.out.println(check ? "King is in check." : "");

            while (true) {
                if (movablePiecesCount == 1) {
                    row = movablePieceRow;
                    col = movablePieceCol;
                    piece = board[row][col];
                    legalMoves = piece.getLegalMoves();
                } else {
                    boolean validPiece = false;

                    while (!validPiece) {
                        System.out.print("Select a piece [A1-H8]:  ");
                        int[] square = parseSquareInput(scanner.next());
                        row = square[0];
                        col = square[1];

                        if (row == -1 || col == -1) {
                            System.out.print("Invalid square position. ");
                        } else {
                            piece = board[row][col];

                            if (piece == null) {
                                System.out.print("There is no piece at this square. ");
                            } else if (piece.getIsWhite() != isWhiteTurn) {
                                System.out.print("Cannot select an opponent's piece. ");
                            } else {
                                if (piece.numLegalMoves() > 0) {
                                    legalMoves = piece.getLegalMoves();
                                    validPiece = true;
                                } else {
                                    System.out.print("Selected piece has no legal moves. ");
                                }
                            }
                        }
                    }
                }

                printBoard(legalMoves);
                boolean validMove = false;

                while (!validMove) {
                    String prompt = "Select a square to move to [A1-H8]";
                    if (movablePiecesCount > 1)
                        prompt += " or deselect piece [CANCEL]";
                    System.out.print(prompt + ":  ");

                    String input = scanner.next();

                    if (input.equalsIgnoreCase("Cancel") && movablePiecesCount > 1)
                        break;

                    int[] square = parseSquareInput(input);
                    destRow = square[0];
                    destCol = square[1];

                    if (destRow == -1 || destCol == -1)
                        System.out.print("Invalid square position. ");
                    else if (!legalMoves[destRow][destCol])
                        System.out.print("Illegal move. ");
                    else
                        validMove = true;
                }

                if (validMove)
                    break;

                printBoard();
            }

            captured = board[destRow][destCol];

            if (enPassantPawn != null) {
                enPassantPawn.setAllowEnPassant(false);
                enPassantPawn = null;
            }

            if (piece instanceof Pawn) {
                if (destRow - row == (piece.getIsWhite() ? -1 : 1) && Math.abs(destCol - col) == 1) {
                    if (board[destRow][destCol] == null && destRow == (isWhiteTurn ? 2 : 5)) {
                        int behindRow = destRow + (isWhiteTurn ? 1 : -1);
                        Piece behind = board[behindRow][destCol];

                        if (behind != null && behind.getIsWhite() != isWhiteTurn) {
                            if (behind instanceof Pawn || piece.getNumMoves() == 1) {
                                captured = behind;
                                board[behindRow][destCol] = null;
                            }
                        }
                    }
                } else if (destRow - row == (piece.getIsWhite() ? -2 : 2) && destCol == col && piece.getNumMoves() == 0) {
                    enPassantPawn = (Pawn)piece;
                    enPassantPawn.setAllowEnPassant(true);
                }
            } else if (piece == king) {
                if (king.getNumMoves() == 0 && !check && destRow == row && Math.abs(destCol - col) == 2) {
                    Rook rook = king.getCastlingRook(destCol > col);
                    if (rook != null && rook.getNumMoves() == 0)
                        rook.move(destRow, destCol + (destCol < col ? 1 : -1));
                }
            }

            piece.move(destRow, destCol);

            if (piece instanceof Pawn pawn) {
                if (piece.getIsWhite() && destRow == 0 || !piece.getIsWhite() && destRow == 7) {
                    String prompt = "Pawn has reached the other side. Promote to bishop [B], " +
                            "knight [K], rook [R], or queen [Q]?  ";

                    while (true) {
                        System.out.print(prompt);

                        String choice = scanner.next().toUpperCase();
                        Piece promotedPiece = null;

                        if (!choice.isEmpty()) {
                            switch (choice.charAt(0)) {
                                case 'B':
                                    promotedPiece = new Bishop(pawn);
                                    break;
                                case 'K':
                                    promotedPiece = new Knight(pawn);
                                    break;
                                case 'R':
                                    promotedPiece = new Rook(pawn);
                                    break;
                                case 'Q':
                                    promotedPiece = new Queen(pawn);
                                    break;
                            }
                        }

                        if (promotedPiece != null)
                            break;

                        System.out.print("Invalid choice. ");
                    }
                }
            }

            isWhiteTurn = !isWhiteTurn;
        }
    }

    public static void initBoard() {
        board = new Piece[8][8];

        for (int i = 0; i < 8; i++) {
            new Pawn(board, 1, i, false);
            new Pawn(board, 6, i, true);
        }

        for (int i = 0; i < 2; i++) {
            new Rook(board, i * 7, 0, i == 1);
            new Knight(board, i * 7, 1, i == 1);
            new Bishop(board, i * 7, 2, i == 1);
            new Queen(board, i * 7, 3, i == 1);
            new King(board, i * 7, 4, i == 1);
            new Bishop(board, i * 7, 5, i == 1);
            new Knight(board, i * 7, 6, i == 1);
            new Rook(board, i * 7, 7, i == 1);
        }
    }

    public static int[] parseSquareInput(String input) {
        if (input.length() >= 2) {
            int row = 7 - ((int)input.toUpperCase().charAt(1) - 49);
            int col = (int)input.toUpperCase().charAt(0) - 65;
            if (0 <= row && row < 8 && 0 <= col && col < 8)
                return new int[] { row, col };
        }

        return new int[] { -1, -1 };
    }

    public static void printBoard() {
        printBoard(new boolean[8][8]);
    }

    public static void printBoard(boolean[][] moves) {
        System.out.println();

        for (int i = 0; i < 9; i++) {
            System.out.print(i > 0 ? (9 - i) + " " : "  ");

            for (int j = 0; j < 8; j++) {
                if (i == 0) {
                    System.out.printf("   " + (char)(65 + j) + "   ");
                } else {
                    boolean blackTile = (j + (i % 2 == 0 ? 1 : 0)) % 2 == 0;
                    char start = blackTile ? ' ' : '[';
                    char end = blackTile ? ' ' : ']';

                    Piece piece = board[i - 1][j];
                    String str = piece != null ? piece.toStringShort() : "   ";

                    if (moves[i - 1][j]) {
                        if (piece != null)
                            System.out.printf("%s<%s>%s", start, str, end);
                        else
                            System.out.printf("%s  •  %s", start, end);
                    } else {
                        System.out.printf("%s %s %s", start, str, end);
                    }
                }
            }

            System.out.println(i > 0 ? " " + (9 - i) : "");
        }

        System.out.println();
    }
}
