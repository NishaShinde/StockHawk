package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.models.WidgetStockDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shani on 5/3/16.
 * This class provides list with data populated from content provider.
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private  List<WidgetStockDetails> stockDetailsList = new ArrayList<>();
    private String LOG_TAG = ListProvider.class.getSimpleName();

    private Context mContext;
    private int mAppWidgetId;
    private String [] projection = {
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.ISUP

    };



    public ListProvider(){}

    public ListProvider(Context context, int appWidgetId){
        mContext = context;
        mAppWidgetId = appWidgetId;
    }


    public void onCreate() {
        String selection = QuoteColumns.ISCURRENT+" = ?";
        Cursor cursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,projection,selection,new String[]{"1"},null);
        Log.v(LOG_TAG,"cnt = " +cursor.getCount());
            if(cursor!=null && cursor.getCount()>0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {

                    String symbol = cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL));
                    String bidPrice = cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE));
                    String change = cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
                    int isUp =  cursor.getInt(cursor.getColumnIndex(QuoteColumns.ISUP));

                    Log.v(LOG_TAG, "symbol = " + symbol);

                    WidgetStockDetails stockDetails = new WidgetStockDetails();
                    stockDetails.setSymbol(symbol);
                    stockDetails.setBidPrice(bidPrice);
                    stockDetails.setChange(change);
                    stockDetails.setIsUp(isUp);

                    stockDetailsList.add(stockDetails);
                    cursor.moveToNext();
                }
                cursor.close();
            }
    }

    @Override
    public int getCount() {
        return stockDetailsList==null ? 0 : stockDetailsList.size();
    }


    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),R.layout.widget_list_item);
        WidgetStockDetails stockDetails = stockDetailsList.get(position);
        remoteViews.setTextViewText(R.id.stock_symbol,stockDetails.getSymbol());
        remoteViews.setTextViewText(R.id.bid_price,stockDetails.getBidPrice());
        remoteViews.setTextViewText(R.id.change,stockDetails.getChange());

        if(stockDetails.getIsUp()==1){
            remoteViews.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_green);
        }else {
            remoteViews.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_red);
        }

        //Bundle extras = new Bundle();
        //extras.putString(WidgetProvider.EXTRA_ITEM, stockDetails.getSymbol());
        Intent fillInIntent = new Intent();
        //fillInIntent.putExtras(extras);
        fillInIntent.putExtra(WidgetProvider.EXTRA_ITEM,stockDetails.getSymbol());

        // Make it possible to distinguish the individual on-click
        // action of a given item
        remoteViews.setOnClickFillInIntent(R.id.stock_listItem, fillInIntent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }



    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

}
