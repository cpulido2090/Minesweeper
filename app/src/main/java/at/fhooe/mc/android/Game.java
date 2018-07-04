package at.fhooe.mc.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Activity{
    private TextView txtMineCount;
    private TextView txtTimer;
    private TextView score;
    private ScoreBoard scoreBoard;
    private TableLayout mineField;
    private Block blocks[][];
    private int numberOfRowsInMineField = 8;
    private int numberOfColumnsInMineField = 9;
    private int blockDimension = 80;
    private int blockPadding = 20;
    private int numberOfMines = 7;
    private int numberOfRemainingMines = numberOfMines;
    private Handler timer = new Handler();
    private int secondsPassed = 0;
    private int actualScore = 0;
    private int clickedBlockCount = 0;
    private int level  = 1;



    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        getActionBar().hide();

        scoreBoard = new ScoreBoard();
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.fragment_game_board);
        score = (TextView)findViewById(R.id.fragment_game_score);
        txtMineCount = (TextView)findViewById(R.id.fragment_game_mineCount);
        txtTimer = (TextView)findViewById(R.id.fragment_game_timer);
        txtMineCount.setText(Integer.toString(numberOfRemainingMines));

        blocks = new Block[numberOfRowsInMineField][numberOfColumnsInMineField];
        createMinefield(numberOfMines);

        mineField = (TableLayout)findViewById(R.id.fragment_game_mineField);

        displayMinefield(mineField);
        startTimer();
    }


    private void displayMinefield(TableLayout mineField){
        TableRow row = null;
        for(int i = 0; i < numberOfRowsInMineField; i++){
            row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams((this.blockDimension + 2 * this.blockPadding) * this.numberOfColumnsInMineField, this.blockDimension + 2 * this.blockPadding));
            for(int j = 0; j < numberOfColumnsInMineField; j++){
                blocks[i][j].setLayoutParams(new TableRow.LayoutParams(this.blockDimension + 2 * this.blockPadding, this.blockDimension + 2 * this.blockPadding));
                blocks[i][j].setPadding(this.blockPadding, this.blockPadding, this.blockPadding, this.blockPadding);
                blocks[i][j].setBackground(getResources().getDrawable(R.drawable.button_not_clicked));
                blocks[i][j].setGravity(Gravity.CENTER);
                blocks[i][j].setTextSize(25);


                //Java asks for indices to be finalised so...
                final int finalJ = j;
                final int finalI = i;
                //Setting the on click listener for each of the blocks
                blocks[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!blocks[finalI][finalJ].checkIfClicked()) {

                            if (blocks[finalI][finalJ].checkIfMine()) {
                                stopTimer();
                                Toast.makeText(Game.this, "Boom! You lost!", Toast.LENGTH_SHORT).show();
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                r.play();
                                blocks[finalI][finalJ].setBackground(getResources().getDrawable(R.drawable.button_mine));
                                displayAllMines();  //If clicked on a mine, display every single mine
                                setHighscoreOnClickListeners();

                                //dataAL.add(new PlayerData(actualScore, 1, secondsPassed));

                                //scoreBoard.redirectToScoreBoard(actualScore, secondsPassed);
                                //Intent i = new Intent(Game.this, ScoreBoard.class);-----
                                //Bundle args = new Bundle();
                                //args.putSerializable("ARRAYLIST",(Serializable)dataAL);
                                //i.putExtra("BUNDLE", args);
                                actualScore += checkForCorrectFlags() * 15;
                                HighScores.data.add(new PlayerData(actualScore, level, secondsPassed));



                            } else {
                                checkIfNeedsClearing(blocks[finalI][finalJ], finalI, finalJ);
                                blocks[finalI][finalJ].setText(Integer.toString(blocks[finalI][finalJ].getNearbyMineCount()));
                                blocks[finalI][finalJ].setBackground(getResources().getDrawable(R.drawable.button_number));
                                blocks[finalI][finalJ].setIsClicked(true);
                                // scoreBoard.addScore(25, 1, secondsPassed);
                                actualScore += 25;
                                score.setText("Score: " + Integer.toString(actualScore));
                                clickedBlockCount++;
                                    checkForVictory();

                            }
                        }
                        blocks[finalI][finalJ].setIsClicked(true);
                        blocks[finalI][finalJ].setOnClickListener(null);
                    }
                });

                //Setting on long click listener to set flags
                blocks[i][j].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(!blocks[finalI][finalJ].checkIfClicked()) {
                            if (blocks[finalI][finalJ].checkIfFlagged()) {
                                blocks[finalI][finalJ].setBackground(getResources().getDrawable(R.drawable.button_not_clicked));
                                blocks[finalI][finalJ].setIsFlagged();
                                numberOfRemainingMines++;
                                txtMineCount.setText(Integer.toString(numberOfRemainingMines));
                            } else {
                                blocks[finalI][finalJ].setBackground(getResources().getDrawable(R.drawable.button_flagged));
                                blocks[finalI][finalJ].setIsFlagged();
                                numberOfRemainingMines--;
                                txtMineCount.setText(Integer.toString(numberOfRemainingMines));
                            }
                        }
                        return true;
                    }
                });

                row.addView(blocks[i][j]);
            }
            mineField.addView(row, new android.widget.TableLayout.LayoutParams((this.blockDimension + 2 * this.blockPadding) * this.numberOfColumnsInMineField, this.blockDimension + 2 * this.blockPadding));
        }
    }

    private void checkIfNeedsClearing(Block block, int row, int column){
        int numberOfZeroes = 0;
        for(int x = row-1; x <= row + 1; x++){
            if(x < 0 || x == numberOfRowsInMineField){ continue; }          //Both of these ifs check if we get out of
            for(int y = column - 1; y <= column + 1; y++){                  //boundaries, which gets a null pointer
                if(y < 0 || y == numberOfColumnsInMineField) { continue; }  //exception.
                if(blocks[x][y].getNearbyMineCount() == 0){
                    numberOfZeroes++;
                }
            }
        }
        if(numberOfZeroes >= 7){
            for(int x = row-1; x <= row + 1; x++){
                if(x < 0 || x == numberOfRowsInMineField){ continue; }          //Both of these ifs check if we get out of
                for(int y = column - 1; y <= column + 1; y++){                  //boundaries, which gets a null pointer
                    if(y < 0 || y == numberOfColumnsInMineField) { continue; }  //exception.
                    if(blocks[x][y].getNearbyMineCount() == 0){
                        blocks[x][y].setText(Integer.toString(blocks[x][y].getNearbyMineCount()));
                        blocks[x][y].setBackground(getResources().getDrawable(R.drawable.button_number));
                        blocks[x][y].setOnClickListener(null);
                        if(!blocks[x][y].checkIfClicked()){
                            clickedBlockCount++;
                            blocks[x][y].setIsClicked(true);
                        }
                    }
                }
            }
            clickedBlockCount--;
        }
    }


    private int checkNearbyMineCount(Block block, int row, int column){
        int mineCount = 0;
        for(int x = row-1; x <= row + 1; x++){
            if(x < 0 || x == numberOfRowsInMineField){ continue; }          //Both of these ifs check if we get out of
            for(int y = column - 1; y <= column + 1; y++){                  //boundaries, which gets a null pointer
                if(y < 0 || y == numberOfColumnsInMineField) { continue; }  //exception.
                if(blocks[x][y].checkIfMine()){
                    mineCount++;
                }
            }
        }
        return mineCount;
    }

    private void checkForVictory() {
        int blockCount = numberOfColumnsInMineField * numberOfRowsInMineField;
        if(clickedBlockCount == blockCount - numberOfMines){
            Toast.makeText(this, "Level complete!", Toast.LENGTH_SHORT).show();
            displayAllMines();

            setOnClickListeners();

        }
    }

    private void victory(){
        actualScore += checkForCorrectFlags() * 15;
        level++;

        for(int i = 1; i <= 10; i++){
            if(i == level) {
                numberOfMines += i * 2;
            }
        }

        if(numberOfRowsInMineField < 12){
            numberOfRowsInMineField++;
        }



        blocks = new Block[numberOfRowsInMineField][numberOfColumnsInMineField];

        mineField.removeAllViews();

        createMinefield(numberOfMines);
        displayMinefield(mineField);
        clickedBlockCount = 0;
        numberOfRemainingMines = numberOfMines;
        txtMineCount.setText(Integer.toString(numberOfRemainingMines));
    }

    //Method for removing every on click listener to not allow the user to click on
    //anything after a mine is hit
    private void setOnClickListeners(){
        for(int i = 0; i < numberOfRowsInMineField; i++){
            for(int j = 0; j < numberOfColumnsInMineField; j++){
                blocks[i][j].setOnLongClickListener(null);
                blocks[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        victory();
                    }
                });
            }
        }
    }

    private int checkForCorrectFlags(){
        int count = 0;
        for(int i = 0; i < numberOfRowsInMineField; i++) {
            for (int j = 0; j < numberOfColumnsInMineField; j++) {
                if(blocks[i][j].checkIfFlagged() && blocks[i][j].checkIfMine()){
                    count++;
                }
            }
        }
        return count;
    }

    private void setHighscoreOnClickListeners(){
        for(int i = 0; i < numberOfRowsInMineField; i++){
            for(int j = 0; j < numberOfColumnsInMineField; j++){
                blocks[i][j].setOnLongClickListener(null);
                blocks[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Game.this, ScoreBoard.class);
                        finish();
                        startActivity(i);
                    }
                });
            }
        }
    }

    //Method for displaying every mine on the screen
    private void displayAllMines(){
        for(int i = 0; i < numberOfRowsInMineField; i++){
            for(int j = 0; j < numberOfColumnsInMineField; j++){
                if(blocks[i][j].checkIfMine()){
                    if(blocks[i][j].checkIfFlagged()){
                        blocks[i][j].setBackground(getResources().getDrawable(R.drawable.button_flagged_mine));
                    } else {
                        blocks[i][j].setBackground(getResources().getDrawable(R.drawable.button_mine));
                    }
                }
            }
        }
    }

    //Function to create a minefield. Idea is to generate a set of random numbers ranging from 0 to number of rows * number of columns.
    //Then we set the boolean of isMined to true if a random number is the number of the block. E.g. if we have 9 rows and 9 columns,
    //a generated number of 10 will set isMined to true for 2nd row, 1st column block.
    private void createMinefield(int numberOfMines) {
        int totalBlocks = numberOfRowsInMineField * numberOfColumnsInMineField;
        Random rand = new Random();
        int mines[] = new int[numberOfMines];

        //Making sure that the generated random number already doesn't exist in our random number array
        for (int i = 0; i < numberOfMines; i++) {
            boolean contains = false;
            while (!contains) {
                int number = rand.nextInt(totalBlocks) + 0;
                if (!checkIfContains(mines, number)) {
                    mines[i] = number;
                    contains = true;
                }
            }
        }

        int index = 0;
        //int index is the number of a block. It gets incremented for each check. If the random number array contains the same number as index
        //It gets its boolean of isMined set to true.
        for (int i = 0; i < numberOfRowsInMineField; i++) {
            for (int j = 0; j < numberOfColumnsInMineField; j++) {
                for (int x = 0; x < numberOfMines; x++) {
                    if (mines[x] == index) {
                        blocks[i][j] = new Block(this, "mine");
                    }
                }
                if (blocks[i][j] == null) {
                    blocks[i][j] = new Block(this, "block");
                }
                index++;
            }
        }
        //Setting a nearby mine number count for each block in the array
        for (int i = 0; i < numberOfRowsInMineField; i++) {
            for (int j = 0; j < numberOfColumnsInMineField; j++) {
                blocks[i][j].setNearbyMineCount(checkNearbyMineCount(blocks[i][j], i, j));
            }
        }
    }


    private boolean checkIfContains(int[] array, int number){
       for(int i = 0; i < array.length; i++){
           if(array[i] == number){
               return true;
           }
       }
        return false;
    }

    //------------------------------------Timer stuff------------------------------------
    public void startTimer()
    {
        if (secondsPassed == 0)
        {
            timer.removeCallbacks(updateTimeElasped);
            // tell timer to run call back after 1 second
            timer.postDelayed(updateTimeElasped, 1000);
        }
    }

    public void stopTimer()
    {
        // disable call backs
        timer.removeCallbacks(updateTimeElasped);
    }

    // timer call back when timer is ticked
    private Runnable updateTimeElasped = new Runnable()
    {
        public void run()
        {
            long currentMilliseconds = System.currentTimeMillis();
            ++secondsPassed;
            txtTimer.setText(Integer.toString(secondsPassed));
            Log.i("vvv", "clicked block count: " + clickedBlockCount);
            // add notification
            timer.postAtTime(this, currentMilliseconds);
            // notify to call back after 1 seconds
            // basically to remain in the timer loop
            timer.postDelayed(updateTimeElasped, 1000);
        }
    };
    //-----------------------------Timer stuff------------------------------------

    public void saveArrayList(List<PlayerData> list, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveArrayList(HighScores.data, "pData");
    }

}
