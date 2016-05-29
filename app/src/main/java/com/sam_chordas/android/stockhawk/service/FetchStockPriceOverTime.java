package com.sam_chordas.android.stockhawk.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.models.Quote;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.StockGraphActivity;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by shani on 4/18/16.
 */
public class FetchStockPriceOverTime extends AsyncTask<String,Void,ArrayList<Quote>>
{
    private static final String LOG_TAG = FetchStockPriceOverTime.class.getSimpleName();
    public static final String QUOTE_EXTRA = "quote_extra";
    private final Context mContext;
    private OkHttpClient client = new OkHttpClient();
    public static ArrayList<Quote> quoteList = new ArrayList<>();
    String startDate,endDate;
    private String symbolName;
    private String mPeriod;

    public FetchStockPriceOverTime(Context context, String period){
        mContext = context;
        mPeriod = period;

    }

    ProgressDialog progressDialog;


    @Override
    protected void onPreExecute() {
        try {
            progressDialog = ProgressDialog.show(mContext,null,null,true,false);
            progressDialog.setContentView(R.layout.spinning_wheel);
            progressDialog.setIndeterminate(true);

        }catch (WindowManager.BadTokenException e){
            //do nothing
        }

        super.onPreExecute();
    }

    @Override
    protected ArrayList<Quote> doInBackground(String... params) {
        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");

        try {
            symbolName = params[0];
            Calendar calendar = Calendar.getInstance();
            int YEAR = calendar.get(Calendar.YEAR);
            int MONTH = calendar.get(Calendar.MONTH) + 1;
            int DAY = calendar.get(Calendar.DAY_OF_MONTH);
            DecimalFormat decimalFormat = new DecimalFormat("00");
            String twoDigitMonth = decimalFormat.format(MONTH);
            String twoDigitDay = decimalFormat.format(DAY);

            endDate = YEAR+ "-"+twoDigitMonth+"-"+twoDigitDay;

            /* We will show 1 month data by default*/

            startDate = calculateStartDate(calendar);

            Log.v("Test",startDate);

            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = ","UTF-8"));
            urlStringBuilder.append(URLEncoder.encode("\'" + symbolName + "\'" + " and startDate=\'" + startDate+
                    "\' and endDate=\'" +
                    endDate+"\'", "UTF-8"));
            urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");

            Log.v("Test",urlStringBuilder.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = urlStringBuilder.toString();

        try {
            String jsonResponse = fetchData(url);

            return getQuoteDetails(jsonResponse);
        }catch (IOException e){
            Log.e(LOG_TAG,e.getMessage());
        }
        return null;
    }

    private String calculateStartDate(Calendar calendar) {
        String startDate = null;
        if(mPeriod==null){
            //Default because at very first time it is called from main activity so we are passing it null
            calendar.add(Calendar.MONTH,-1);
        }else{
            if(mPeriod.equals("oneYear")){
                //Both dates same
                startDate = endDate;
                calendar.add(Calendar.YEAR,-1);

            }else if(mPeriod.equals("oneMonth")){
                calendar.add(Calendar.MONTH,-1);
            }else {
                calendar.add(Calendar.MONTH,-6);
            }
        }

        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH) + 1;
        int DAY = calendar.get(Calendar.DAY_OF_MONTH);

        DecimalFormat decimalFormat = new DecimalFormat("00");
        String twoDigitMonth = decimalFormat.format(MONTH);
        String twoDigitDay = decimalFormat.format(DAY);

        startDate = YEAR+ "-"+twoDigitMonth+"-"+twoDigitDay;

        return startDate;
    }

    @Override
    protected void onPostExecute(ArrayList<Quote> quotes) {
        super.onPostExecute(quotes);
        quoteList = quotes;
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }

        if(quotes!=null){

            Intent intent = new Intent(mContext, StockGraphActivity.class);
            intent.putParcelableArrayListExtra(QUOTE_EXTRA, quotes);
            intent.putExtra(MyStocksActivity.EXTRA_SYMBOL,symbolName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(StockGraphActivity.EXTRA_PERIOD,mPeriod);
            mContext.startActivity(intent);

        }

    }

    private ArrayList<Quote> getQuoteDetails(String jsonResponse) {

        final String KEY_DATE = "Date";
        final String KEY_OPEN = "Open";
        final String KEY_CLOSE = "Close";
        final String KEY_HIGH = "High";
        final String KEY_LOW = "Low";
        Quote []quotesArray = null;
        clearQuoteList();
        try {

            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (jsonObject != null && jsonObject.length() != 0) {

                jsonObject = jsonObject.getJSONObject("query");
                jsonObject = jsonObject.getJSONObject("results");
                JSONArray quoteArray = jsonObject.getJSONArray("quote");

                for (int i = 0; i < quoteArray.length(); i++) {
                    JSONObject quoteObject = quoteArray.getJSONObject(i);

                    String date = quoteObject.getString(KEY_DATE);
                    float close = (float) quoteObject.getDouble(KEY_CLOSE);

                    Quote quote = new Quote();
                    quote.setClose(close);
                    quote.setDate(date);
                    quoteList.add(quote);

                }
                Log.v(LOG_TAG, "quoteList =" + quoteList.size());
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return quoteList;
    }

    private void clearQuoteList() {
        quoteList.clear();
    }

    String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        Log.v(LOG_TAG, "url = " + url);

        return response.body().string();
    }



}
