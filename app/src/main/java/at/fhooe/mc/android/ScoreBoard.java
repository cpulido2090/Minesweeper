package at.fhooe.mc.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreBoard extends Activity implements View.OnClickListener {
    public  ArrayList<PlayerData> scores;
    public  PlayerDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_board_list);
        getActionBar().hide();

        adapter = new PlayerDataAdapter(this);

        addStuff();

        ListView lv = (ListView)findViewById(R.id.score_board_listview_list);
        TextView tv = new TextView(this);
        tv.setTextSize(24);
        // tv.setText(R.id.scoreboard_level + "\t" + R.id.scoreboard_time + "\t" + R.id.scoreboard_score);
        tv.setText("         Time    Level    Score");
        tv.setTextColor(Color.WHITE);
        lv.addHeaderView(tv);

        lv.setAdapter(adapter);

        Button b = null;
        b = (Button) findViewById(R.id.score_board_startNew);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.score_board_return);
        b.setOnClickListener(ScoreBoard.this);

    }

    @Override
    public void onClick(View _view) {
        switch (_view.getId()) {
            case R.id.score_board_startNew: {
                Intent i = new Intent(ScoreBoard.this, Game.class);
                startActivity(i);
            }
            break;
            case R.id.score_board_return: {
                Intent i = new Intent(ScoreBoard.this, MainActivity.class);
                startActivity(i);
            }
            break;
            default:
                Log.i("minesweep", "unexpected id encountared");

        }
    }

    public void addScore(int _s, int _l, int _t) {
        PlayerData data = new PlayerData(_s, _l, _t);
        //scores.add(data);
        adapter.add(data);
    }

    private void addStuff(){
        Collections.sort(HighScores.data, new Comparator<PlayerData>() {
            @Override
            public int compare(PlayerData o1, PlayerData o2) {
                return Integer.valueOf(o2.getScore()).compareTo(o1.getScore());
            }
        });

        for(PlayerData data : HighScores.data){
            adapter.add(data);
        }
    }

    public void redirectToScoreBoard(int _s, int _t) {
        addScore(_s, 1, _t);
        Log.i("ifjdsofisjf", "the score is "+  _s + " and the time si "+ _t);
    }

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
