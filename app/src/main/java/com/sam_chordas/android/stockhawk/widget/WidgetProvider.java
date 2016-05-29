package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by shani on 5/4/16.
 */
public class WidgetProvider extends AppWidgetProvider {
    public static final String CLICK_ACTION = "com.example.android.stackwidget.CLICK_ACTION";
    public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        int n = appWidgetIds.length;

        for(int i=0;i<n;i++){
            int appWidgetId = appWidgetIds[i];

            RemoteViews remoteViews = updateListView(context,appWidgetId);


            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Checks to see whether the intent's action is CLICK_ACTION. If it is, the app widget
    // displays a Toast message for the current item.
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(CLICK_ACTION)) {
            String symbol = intent.getStringExtra(EXTRA_ITEM);
            Intent i = new Intent(context, MyStocksActivity.class);
            i.putExtra(EXTRA_ITEM,symbol);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }if(StockTaskService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stockListView);
        }

    }


    private RemoteViews updateListView(Context context, int appWidgetId) {

        //Get remoteview with initial layout of widget

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        //Call WidgetService
        Intent intent = new Intent(context,WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        //set remote adapter intent here takes care of updating the list content
        remoteViews.setRemoteAdapter(R.id.stockListView,intent);
        remoteViews.setEmptyView(R.id.stockListView,R.id.empty_textview);

        Intent toastIntent = new Intent(context, WidgetProvider.class);
        // Set the action for the intent.
        // When the user touches a particular view, it will have the effect of
        // broadcasting CLICK_ACTION.
        toastIntent.setAction(WidgetProvider.CLICK_ACTION);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.stockListView, toastPendingIntent);

        return remoteViews;

    }
}
