package at.fhooe.mc.android;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlayerDataAdapter extends ArrayAdapter<PlayerData> {
    public PlayerDataAdapter(Context _context) {
        super(_context, -1);
    }
    public static View _scoreView;
    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {

        if (_convertView == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            _convertView = inflater.inflate(R.layout.activity_score_board, null);
        }

        final PlayerData data = getItem(_position);
        TextView tv = null;

        tv = (TextView)_convertView.findViewById(R.id.list_element_time);
        tv.setText(Integer.toString(data.getTime()));

        tv = (TextView)_convertView.findViewById(R.id.list_element_level);
        tv.setText(Integer.toString(data.getLevel()));

        tv = (TextView)_convertView.findViewById(R.id.list_element_score);
        tv.setText(Integer.toString(data.getScore()));

        _scoreView  =_convertView;
        return _convertView;

    }

    public static View sendScoreBoardView() {
        return _scoreView;
    }
}
