package adam.android.project;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sac.game.GameStateImpl;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TableLayout mainBoardLayout;
    private ConstraintLayout con;
    private Display display;

    Drawable whiteOnSquareImg;
    Drawable blackOnSquareImg;
    Drawable whiteOnSquareImgFocused;
    Drawable blackOnSquareImgFocused;
    Drawable darkSquareInRedFrame;

    public int currentPieceId = 0;

    Checkers checkers = new Checkers();

    List<ImageButton> imgList = new ArrayList<ImageButton>();

    public Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage, int xShift, int yShift) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, xShift, yShift, null);
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBoardLayout = (TableLayout) findViewById(R.id.boardLayout);
        con = (ConstraintLayout) findViewById(R.id.con);

        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int screenWidth = size.x - (size.x%8);
        int screenHeight = size.y - (size.y%8);

        Bitmap darkSquareImg = BitmapFactory.decodeResource(getResources(), R.drawable.dark_square);
        Bitmap whitePieceImg = BitmapFactory.decodeResource(getResources(), R.drawable.white_piece2);
        Bitmap blackPieceImg = BitmapFactory.decodeResource(getResources(), R.drawable.black_piece2);
        Bitmap redFrame = BitmapFactory.decodeResource(getResources(), R.drawable.red_frame);
        darkSquareInRedFrame = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, redFrame, 0, 0));
        whiteOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, whitePieceImg, 25, 25));
        blackOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, blackPieceImg, 25, 25));

        whiteOnSquareImgFocused = whiteOnSquareImg.getConstantState().newDrawable().mutate();
        whiteOnSquareImgFocused.setColorFilter(Color.WHITE, PorterDuff.Mode.OVERLAY);
        blackOnSquareImgFocused = blackOnSquareImg.getConstantState().newDrawable().mutate();
        blackOnSquareImgFocused.setColorFilter(Color.BLACK, PorterDuff.Mode.OVERLAY);


        for (int i = 0; i < 8; i++)
        {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(lp);
            for (int j = 0; j < 8; j++)
            {
                ImageButton imgButton = new ImageButton(this);
                imgButton.setLayoutParams(new TableRow.LayoutParams(screenWidth/8, screenWidth/8));
                imgButton.setId(i*8+j);

                /*if ((i+j)%2 == 0)
                    imgButton.setBackgroundResource(R.drawable.white_square);
                else
                    imgButton.setBackgroundResource(R.drawable.dark_square);*/

                if (checkers.board[i][j] == -1) {
                    imgButton.setBackgroundResource(R.drawable.white_square);
                }
                else if (checkers.board[i][j] == 0) {
                    imgButton.setBackgroundResource(R.drawable.dark_square);
                }
                else if (checkers.board[i][j] == 1) {
                    imgButton.setBackground(whiteOnSquareImg);
                }
                else
                {
                    imgButton.setBackground(blackOnSquareImg);
                }

                tableRow.addView(imgButton);
                imgList.add(imgButton);
            }
            mainBoardLayout.addView(tableRow, i);
        }


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkers.board[i][j] != -1) {
                    imgList.get(i * 8 + j).setOnClickListener(this);
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(checkers.board[i][j]);
            }
            System.out.println();
        }
    }

    void drawBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkers.board[i][j] == -1) {
                    imgList.get(i*8+j).setBackgroundResource(R.drawable.white_square);
                }
                else if (checkers.board[i][j] == 0) {
                    imgList.get(i*8+j).setBackgroundResource(R.drawable.dark_square);
                }
                else if (checkers.board[i][j] == 1) {
                    imgList.get(i*8+j).setBackground(whiteOnSquareImg);
                }
                else
                {
                    imgList.get(i*8+j).setBackground(blackOnSquareImg);
                }
            }
        }
        if (currentPieceId != 0) {
            if (checkers.board[currentPieceId/8][currentPieceId%8] == 1)
                imgList.get(currentPieceId).setBackground(whiteOnSquareImgFocused);
            else if (checkers.board[currentPieceId/8][currentPieceId%8] == 2)
                imgList.get(currentPieceId).setBackground(blackOnSquareImgFocused);
        }

        try {
            checkers.checkBoard();
            for (int k = 0; k < checkers.possibleCaptures.size(); ++k) {
                if ((currentPieceId/8) == Character.getNumericValue(checkers.possibleCaptures.get(k).charAt(0)) && (currentPieceId%8) == Character.getNumericValue(checkers.possibleCaptures.get(k).charAt(1))) {
                    System.out.println("Gooooooooooooooooood");
                    int x = Character.getNumericValue(checkers.possibleCaptures.get(k).charAt(checkers.possibleCaptures.get(k).length()-2));
                    int y = Character.getNumericValue(checkers.possibleCaptures.get(k).charAt(checkers.possibleCaptures.get(k).length()-1));
                    imgList.get(x*8+y).setBackground(darkSquareInRedFrame);
                }
            }
        }
        catch (Exception e){
            System.out.println("Keep going");
        }



        if (checkers.isEnd()) {
            TextView winningText = new TextView(this);
            winningText.setText(checkers.winningCommunicate);
            winningText.setAllCaps(true);
            winningText.setTextSize(25);
            mainBoardLayout.addView(winningText);
        }
    }

    @Override
    public void onClick(View v) {
        if (currentPieceId == 0 || checkers.board[((int) v.getId()) / 8][((int) v.getId()) % 8] != 0) {
            currentPieceId = v.getId();
        }
        else {
            // Rozwiazac szczegolny przypadek dla bicia w koleczku (pionek konczy bicie na tym samym polu co zaczyna)
            if ((Math.abs(currentPieceId/8 - ((int) v.getId())/8) == 2) && (Math.abs(currentPieceId%8 - ((int) v.getId())%8) == 2)) {
                checkers.checkMove(currentPieceId/8, currentPieceId%8, ((int) v.getId())/8, ((int) v.getId())%8);
                currentPieceId = 0;
            }
            else {
                checkers.checkMove(currentPieceId / 8, currentPieceId % 8, ((int) v.getId()) / 8, ((int) v.getId()) % 8);
                currentPieceId = 0;
            }
        }
        drawBoard();

    }

}
