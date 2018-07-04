package at.fhooe.mc.android;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class ScoreBoardWidget extends AppWidgetProvider {

    @Override

    public void onUpdate(Context _context, AppWidgetManager _appWidgetManager, int[] _appWidgetIds) {
        final RemoteViews views = new RemoteViews(_context.getPackageName(), R.layout.score_board_widget);
        Intent i = new Intent(_context, ScoreBoard.class);
        PendingIntent configPendingIntent = PendingIntent.getActivity(_context, 0, i, 0);
        views.setRemoteAdapter(R.id.widget_list , i);
    }

    @Override
    public void onReceive(Context _context, Intent _intent) {
        ComponentName provider = new ComponentName(_context, ScoreBoardWidget.class);
        AppWidgetManager mgr = AppWidgetManager.getInstance(_context);
        int [] ids = mgr.getAppWidgetIds(provider);
        onUpdate(_context, mgr, ids);
    }
}


