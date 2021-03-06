package adam.android.project;

import android.graphics.Point;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sac.game.GameState;
import sac.game.GameStateImpl;

public class Checkers extends GameStateImpl{
    private static final int m = 8;
    private static final int n = 8;
    private static final int whiteTile = -1;
    private static final int empty = 0;
    private static final int whitePiece = 1;
    private static final int blackPiece = 2;
    private static final int whiteDame = 3;
    private static final int blackDame = 4;
    private int whitePiecesAmount;
    private int blackPiecesAmount;

    public byte [][] board = null;
    private int turn = 0;    // white = 0, black = 1
    private boolean isTurnWhite = true;
    public List<String> possibleCaptures;
    public List<String> possibleDameCaptures;
    public String winningCommunicate;

    public Checkers() {
        board = new byte [m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i <= 2 && (i+j)%2 == 1) {
                    board[i][j] = blackPiece;
                }
                else if (i >= 5 && (i+j)%2 == 1) {
                    board[i][j] = whitePiece;
                }
                else if ((i+j)%2 == 1){
                    board[i][j] = empty;
                }
                else {
                    board[i][j] = whiteTile;
                }
            }
        }
        whitePiecesAmount = 12;
        blackPiecesAmount = 12;
    }

    private Checkers(Checkers parent) {
        board = new byte[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = parent.board[i][j];
            }
        }
        whitePiecesAmount = parent.whitePiecesAmount;
        blackPiecesAmount = parent.blackPiecesAmount;
    }

    public List<GameState> generateChildren() {
        List<GameState> children = new ArrayList<GameState>();
        for (int i = 0; i < n; i++) {
            Checkers child = new Checkers(this);


            children.add(child);
        }

        return children;
    }

    private byte [][] cloneBoard(byte [][] board) {
        byte [][] newBoard = new byte[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        return  newBoard;
    }

    void checkBoard() {
        possibleCaptures = new ArrayList<>();
        possibleDameCaptures = new ArrayList<>();
        byte [][] boardCopy = cloneBoard(board);
        int piece;
        if (isTurnWhite) {
            piece = whitePiece;
        }
        else {
            piece = blackPiece;
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (boardCopy[i][j] == piece) {
                    canCapture2(cloneBoard(boardCopy), i, j, "");
                }
            }
        }
        for (int i = 0; i < possibleCaptures.size(); i++) {
            System.out.println(i + ": " + possibleCaptures.get(i));
        }
        for (int i = 0; i < possibleDameCaptures.size(); i++) {
            System.out.println("Dame " + i + ": " + possibleDameCaptures.get(i));
        }
    }

    private void becameDame() {

    }

    private List<Point> toCoordinates(String capturePath) {
        List<Point> coordinates = new ArrayList<Point>();
        for (int i = 0; i < capturePath.length() - 1; ++i) {
            Point point = new Point(Character.getNumericValue(capturePath.charAt(i)), Character.getNumericValue(capturePath.charAt(i+1)));
            coordinates.add(point);
        }

        return coordinates;
    }

    private void dameCanCapture(byte [][] board, int x, int y, String capturePath) {
        int oppPiece;
        int oppDame;
        if (isTurnWhite) {
            oppPiece = blackPiece;
            oppDame = blackDame;
        }
        else {
            oppPiece = whitePiece;
            oppDame = whiteDame;
        }

        try {
            for (int i = 1, j = 1; ; i++, j++) {
                if ((board[x - i][y + j] == oppPiece || board[x - i][y + j] == oppDame) && board[x - (i + 1)][y + (j + 1)] == empty) {
                    for (int k = 1, l = 1; ; k++, l++) {
                        simDameExecuteCapture(board, x, y, x - i, y + j, x - (i + k), y + (j + l));
                        dameCanCapture(board, x - (i + k), y + (j + l), capturePath + x + y + (x - (i + k)) + (y + (j + l)));
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        try {
            for (int i = 1, j = 1; ; i++, j++) {
                if ((board[x - i][y - j] == oppPiece || board[x - i][y - j] == oppDame) && board[x - (i + 1)][y - (j + 1)] == empty) {
                    for (int k = 1, l = 1; ; k++, l++) {
                        simDameExecuteCapture(board, x, y, x - i, y + j, x - (i + k), y + (j + l));
                        dameCanCapture(board, x - (i + k), y - (j + l), capturePath + x + y + (x - (i + k)) + (y - (j + l)));
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        try {
            for (int i = 1, j = 1; ; i++, j++) {
                if ((board[x + i][y - j] == oppPiece || board[x + i][y - j] == oppDame) && board[x + (i + 1)][y - (j + 1)] == empty) {
                    for (int k = 1, l = 1; ; k++, l++) {
                        simDameExecuteCapture(board, x, y, x - i, y + j, x - (i + k), y + (j + l));
                        dameCanCapture(board, x + (i + k), y - (j + l), capturePath + x + y + (x + (i + k)) + (y - (j + l)));
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}
        try {
            for (int i = 1, j = 1; ; i++, j++) {
                if ((board[x + i][y + j] == oppPiece || board[x + i][y + j] == oppDame) && board[x + (i + 1)][y + (j + 1)] == empty) {
                    for (int k = 1, l = 1; ; k++, l++) {
                        simDameExecuteCapture(board, x, y, x - i, y + j, x - (i + k), y + (j + l));
                        dameCanCapture(board, x + (i + k), y + (j + l), capturePath + x + y + (x + (i + k)) + (y + (j + l)));
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {}

        if (!capturePath.equals("")) {
            possibleDameCaptures.add(capturePath);
        }
    }

    private void canCapture2(byte [][] board, int x, int y, String capturePath) {
        int oppPiece;
        int oppDame;
        if (isTurnWhite) {
            oppPiece = blackPiece;
            oppDame = blackDame;
        }
        else {
            oppPiece = whitePiece;
            oppDame = whiteDame;
        }

        try {
            if ((board[x - 1][y + 1] == oppPiece || board[x - 1][y + 1] == oppDame) && board[x - 2][y + 2] == empty) {
                simExecuteCapture(board, x, y, x - 1, y + 1, x - 2, y + 2);
                canCapture2(board, x - 2, y + 2, capturePath + x + y + (x - 2) + (y + 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("Skip");
        }

        try {
            if ((board[x - 1][y - 1] == oppPiece || board[x - 1][y - 1] == oppDame) && board[x - 2][y - 2] == empty) {
                simExecuteCapture(board, x, y, x - 1, y - 1, x - 2, y - 2);
                canCapture2(board, x - 2, y - 2, capturePath + x + y + (x - 2) + (y - 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("Skip");
        }

        try {
            if ((board[x + 1][y - 1] == oppPiece || board[x + 1][y - 1] == oppDame) && board[x + 2][y - 2] == empty) {
                simExecuteCapture(board, x, y, x + 1, y - 1, x + 2, y - 2);
                canCapture2(board, x + 2, y - 2, capturePath + x + y + (x + 2) + (y - 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("Skip");
        }

        try {
            if ((board[x + 1][y + 1] == oppPiece || board[x + 1][y + 1] == oppDame) && board[x + 2][y + 2] == empty) {
                simExecuteCapture(board, x, y, x + 1, y + 1, x + 2, y + 2);
                canCapture2(board, x + 2, y + 2, capturePath + x + y + (x + 2) + (y + 2));
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("Skip");
        }

        if (!capturePath.equals("")) {
            possibleCaptures.add(capturePath);
        }
    }

    private void simDameExecuteCapture(byte [][] board, int x1, int y1, int x2, int y2, int x3, int y3) {
        if (isTurnWhite) {
            board[x2][y2] = empty;
            board[x3][y3] = whiteDame;
        }
        else {
            board[x2][y2] = empty;
            board[x3][y3] = blackDame;
        }
        board[x1][y1] = empty;
    }

    private void simExecuteCapture(byte [][] board, int x1, int y1, int x2, int y2, int x3, int y3) {
        if (isTurnWhite) {
            board[x2][y2] = empty;
            board[x3][y3] = whitePiece;
        }
        else {
            board[x2][y2] = empty;
            board[x3][y3] = blackPiece;
        }
        board[x1][y1] = empty;
        //return board;
    }

    //deprecated
    /*public void canCapture(int x1, int y1, int x2, int y2) {
        if (turn == 0 && board[x1][y1] == whitePiece && board[x2][y2] == empty) {
            if (x1-x2 == 2 && y1-y2 == 2) {
                executeCapture(x1, y1, x2, y2, 1);
            }
            else if (x1-x2 == 2 && y1-y2 == -2) {
                executeCapture(x1, y1, x2, y2, -1);
            }
        }
        if (turn == 1 && board[x1][y1] == blackPiece && board[x2][y2] == empty) {
            if (x1 - x2 == -2 && y1 - y2 == 2) {
                executeCapture(x1, y1, x2, y2, 1);
            } else if (x1 - x2 == -2 && y1 - y2 == -2) {
                executeCapture(x1, y1, x2, y2, -1);
            }
        }
    }*/

    public void checkMove(int x1, int y1, int x2, int y2) {
        checkBoard();
        List<String> bestCaptures = new ArrayList<>();
        if (!possibleCaptures.isEmpty()) {
            int max = 0;
            for (int i = 0; i < possibleCaptures.size(); ++i) {
                if (max < possibleCaptures.get(i).length()) {
                    max = possibleCaptures.get(i).length();
                    bestCaptures.clear();
                    bestCaptures.add(possibleCaptures.get(i));
                }
                else if (max == possibleCaptures.get(i).length()) {
                    bestCaptures.add(possibleCaptures.get(i));
                }
            }

            for (int i = 0; i < bestCaptures.size(); ++i) {
                List<Point> coords = toCoordinates(bestCaptures.get(i));
                if (x1 == coords.get(0).x && y1 == coords.get(0).y) {
                    if (x2 == coords.get(coords.size() - 1).x && y2 == coords.get(coords.size() - 1).y) {
                        executeComplexCapture(coords);
                    }
                }
                /*if (x1 == Character.getNumericValue(bestCaptures.get(i).charAt(0)) && y1 == Character.getNumericValue(bestCaptures.get(i).charAt(1))) {
                    if (x2 == Character.getNumericValue(bestCaptures.get(i).charAt(bestCaptures.get(i).length()-2)) && y2 == Character.getNumericValue(bestCaptures.get(i).charAt(bestCaptures.get(i).length()-1))) {
                        executeComplexCapture(bestCaptures.get(i));
                    }
                }*/
            }

        }
        else {
            if (isTurnWhite && board[x1][y1] == whitePiece && board[x2][y2] == empty) {
                if ((x1-1 == x2 && y1-1 == y2) || (x1-1 == x2 && y1+1 == y2)) {
                    makeMove(x1, y1, x2, y2);
                }
            }
            else if (!isTurnWhite && board[x1][y1] == blackPiece && board[x2][y2] == empty) {
                if ((x1+1 == x2 && y1-1 == y2) || (x1+1 == x2 && y1+1 == y2)) {
                    makeMove(x1, y1, x2, y2);
                }
            }
        }
    }


    private void makeMove(int x1, int y1, int x2, int y2) {
        board[x1][y1] = empty;
        if (isTurnWhite) {
            board[x2][y2] = whitePiece;
        }
        else {
            board[x2][y2] = blackPiece;
        }
        isTurnWhite = !isTurnWhite;
    }

    //deprecated
    /*private void executeCapture(int x1, int y1, int x2, int y2, int dir) {
        board[x1][y1] = empty;
        if (turn == 0) {
            board[x2][y2] = whitePiece;
            board[x1-1][y1-dir] = empty;
            turn = 1;
        }
        else {
            board[x2][y2] = blackPiece;
            board[x1+1][y1-dir] = empty;
            turn = 0;
        }
    }*/

    private void executeComplexCapture(List<Point> coords) {
        byte pieceKind = board[coords.get(0).x][coords.get(0).y];
        board[coords.get(0).x][coords.get(0).y] = empty;
        for (int i = 0; i < coords.size(); i += 2) {
            int x1, y1, x2, y2;
            x1 = coords.get(i).x;
            y1 = coords.get(i).y;
            x2 = coords.get(i+1).x;
            y2 = coords.get(i+1).y;

            if (board[x1 - ((x1-x2)/2)][y1 - ((y1-y2)/2)] == whitePiece)
                --whitePiecesAmount;
            else if (board[x1 - ((x1-x2)/2)][y1 - ((y1-y2)/2)] == whiteDame)
                whitePiecesAmount -= 3;
            else if (board[x1 - ((x1-x2)/2)][y1 - ((y1-y2)/2)] == blackPiece)
                --blackPiecesAmount;
            else if (board[x1 - ((x1-x2)/2)][y1 - ((y1-y2)/2)] == blackDame)
                blackPiecesAmount -= 3;
            else
                System.err.println("Error: None piece to capture");

            board[x1 - ((x1-x2)/2)][y1 - ((y1-y2)/2)] = empty;
        }
        board[coords.get(coords.size() - 1).x][coords.get(coords.size() - 1).y] = pieceKind;
        isTurnWhite = !isTurnWhite;
    }

    // deprecated
    /*private void executeComplexCapture(String capture) {
        board[Character.getNumericValue(capture.charAt(0))][Character.getNumericValue(capture.charAt(1))] = empty;
        for (int i = 0; i < capture.length(); i += 4) {
            int x1, y1, x2, y2;
            x1 = Character.getNumericValue(capture.charAt(i));
            y1 = Character.getNumericValue(capture.charAt(i+1));
            x2 = Character.getNumericValue(capture.charAt(i+2));
            y2 = Character.getNumericValue(capture.charAt(i+3));

            board[x1 - ((x1-x2)/2)][y1 - ((y1-y2)/2)] = empty;
            if (isTurnWhite) {
                --blackPiecesAmount;
            }
            else {
                --whitePiecesAmount;
            }
        }
        if (isTurnWhite) {
            board[Character.getNumericValue(capture.charAt(capture.length() - 2))][Character.getNumericValue(capture.charAt(capture.length() - 1))] = whitePiece;
        }
        else {
            board[Character.getNumericValue(capture.charAt(capture.length() - 2))][Character.getNumericValue(capture.charAt(capture.length() - 1))] = blackPiece;
        }
        isTurnWhite = !isTurnWhite;

    }*/

    public void start() {

    }

    public int hashCode() {
        byte[] lin = new byte[m*n];
        int k = 0;
        for (int i = 0; i<n; i++)
            for (int j = 0; j<m; j++)
                lin[k++] = board[i][j];
        return Arrays.hashCode(lin);
    }

    public boolean isEnd() {
        if (whitePiecesAmount == 0) {
            winningCommunicate = "Black wins!";
            return true;
        }
        else if (blackPiecesAmount == 0) {
            winningCommunicate = "White wins!";
            return true;
        }
        return false;
    }
}

//          |
//      II  |   I
//          |
//  --------+--------
//          |
//     III  |  IV
//          |