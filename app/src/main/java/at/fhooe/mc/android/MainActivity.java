package at.fhooe.mc.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private final String TAG = "UMCD";

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        getActionBar().hide();

        setContentView(R.layout.activity_main);
        HighScores.data = getArrayList("pData");

        Button b = null;
        b = (Button) findViewById(R.id.activity_main_button_start);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.activity_main_button_score);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.activity_main_button_quit);
        b.setOnClickListener(this);

    }


    @Override
    public void onClick(View _view) {
        FragmentManager fMgr = getFragmentManager();
        FragmentTransaction fT = fMgr.beginTransaction();
        Fragment f = null;
        switch (_view.getId()) {
            case R.id.activity_main_button_start: {
                Intent i = new Intent(MainActivity.this, Game.class);
                startActivity(i);
            }
            break;
            case R.id.activity_main_button_score: {
                Intent i = new Intent(MainActivity.this, ScoreBoard.class);
                startActivity(i);

            }
            break;
            case R.id.activity_main_button_quit: {
                finishAffinity();
            }
            break;
            default:
                Log.i(TAG, "unexpected id encountared");

        }
    }

    public void saveArrayList(List<PlayerData> list, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public List<PlayerData> getArrayList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<List<PlayerData>>() {
        }.getType();

        if (gson.fromJson(json, type) == null) {
            return HighScores.data;
        } else {
            return gson.fromJson(json, type);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveArrayList(HighScores.data, "pData");
    }
}