package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.linechart.StockLineChart;
import com.sam_chordas.android.stockhawk.models.Quote;
import com.sam_chordas.android.stockhawk.service.FetchStockPriceOverTime;

import java.util.ArrayList;

public class StockGraphActivity extends AppCompatActivity {

    public static final String EXTRA_PERIOD = "extra_period";
    private ArrayList<String> mLabels = new ArrayList<>();
    private ArrayList<Float> mValues = new ArrayList<>();

    private String symbol;
    private float[] floatArray;
    private String[] mLabelArray;
    private boolean isGraphDrawn;
    private TextView symbolTv;
    private Button oneMBtn;
    private Button sixMBtn;
    private Button oneYBtn;
    private LineChartView lineChartView;
    private String mPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_graph);

        symbolTv = (TextView) findViewById(R.id.symbolTv);
        lineChartView = (LineChartView) findViewById(R.id.stock_linechart);
        oneMBtn = (Button) findViewById(R.id.oneMonthBtn);
        sixMBtn = (Button) findViewById(R.id.sixMonthBtn);
        oneYBtn = (Button) findViewById(R.id.oneYrBtn);

        Intent intent = getIntent();

        if(intent!=null){
            symbol = intent.getStringExtra(MyStocksActivity.EXTRA_SYMBOL);
            symbolTv.setText(symbol);
            isGraphDrawn = intent.getBooleanExtra(MyStocksActivity.EXTRA_ISGRAPHDRAWN,true);
            mPeriod = intent.getStringExtra(EXTRA_PERIOD);
            if(!isGraphDrawn) {
                showGraph("oneMonth");
            }else {

            }
        }
        if(mPeriod!=null) {
            if (mPeriod.equals("sixMonth")) {
                sixMBtn.setBackgroundResource(R.drawable.legent_green_btn);
            } else if (mPeriod.equals("oneYear")) {
                oneYBtn.setBackgroundResource(R.drawable.legent_green_btn);
            } else {
                oneMBtn.setBackgroundResource(R.drawable.legent_green_btn);
            }
        }else {
            oneMBtn.setBackgroundResource(R.drawable.legent_green_btn);
        }
        showLineChart(intent,lineChartView);

    }
    private void showLineChart(Intent intent, LineChartView lineChartView) {
        ArrayList<Quote> quoteList = intent.getParcelableArrayListExtra(FetchStockPriceOverTime
                .QUOTE_EXTRA);
        if(quoteList!=null) {
            for (int i = 0; i < quoteList.size(); i++) {
                Quote q = quoteList.get(i);
                mLabels.add((q.getDate()));
                mValues.add(q.getClose());
            }
            mLabelArray = mLabels.toArray(new String[mLabels.size()]);
            floatArray = new float[mValues.size()];
            int i = 0;

            for (Float f : mValues) {
                floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
            }
            if (mLabelArray.length > 0 && floatArray.length > 0) {
                new StockLineChart(lineChartView, StockGraphActivity.this, mLabelArray, floatArray).show();
            }
        }
    }
    public void onbuttonClick(View view){
        switch (view.getId()){
            case R.id.oneMonthBtn:
                showGraph("oneMonth");
                break;
            case R.id.sixMonthBtn:
                showGraph("sixMonth");
                break;
            case R.id.oneYrBtn:
                showGraph("oneYear");
                break;
            default:
                showGraph("oneMonth");
                break;

        }
    }

    private void showGraph(String period) {
        FetchStockPriceOverTime fetchStockPriceOverTime = new FetchStockPriceOverTime(this,period);
        if(symbol!=null) {
            fetchStockPriceOverTime.execute(symbol);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
            Intent intent = new Intent(this,MyStocksActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}

