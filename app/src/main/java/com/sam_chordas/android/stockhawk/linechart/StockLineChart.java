package com.sam_chordas.android.stockhawk.linechart;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.sam_chordas.android.stockhawk.R;

import java.util.Arrays;
import java.util.Collections;


public class StockLineChart {


    private static float min;
    private static float max;

    private final LineChartView mChart;


    private final Context mContext;


    private  String[] mLabels= {"Jan", "Fev", "Mar", "Apr", "Jun", "May", "Jul", "Aug", "Sep"};
    private float[] mValues = {3.5f, 4.7f, 4.3f, 8f, 6.5f, 9.9f, 7f, 8.3f, 7.0f};

    private Tooltip mTip;

    private Runnable mBaseAction;


    public StockLineChart(LineChartView lineChartView, Context context, String[] mLabels, float[] mValues){

        mContext = context;
        mChart = lineChartView;
        this.mLabels = mLabels;
        this.mValues = mValues;
    }


    public void show() {

        mChart.setXAxis(true);
        mChart.setYAxis(true);
        //mChart.setAxisBorderValues(1, 10, 1);
        mChart.setAxisColor(Color.WHITE);
        mChart.setXLabels(AxisController.LabelPosition.OUTSIDE);
        mChart.setYLabels(AxisController.LabelPosition.OUTSIDE);
        mChart.setFontSize(30);
        mChart.setLabelsColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        mChart.setGrid(ChartView.GridType.FULL, paint);



        // Tooltip
        mTip = new Tooltip(mContext, R.layout.linechart_tooltip, R.id.price);

        final TextView dateTextView = (TextView) mTip.findViewById(R.id.dateTv);

        final TextView priceTextView = ((TextView) mTip.findViewById(R.id.price));
        priceTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Semibold.ttf"));


        mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
        mTip.setDimensions((int) Tools.fromDpToPx(160), (int) Tools.fromDpToPx(70));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);

            mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);

            mTip.setPivotX(Tools.fromDpToPx(170) / 2);
            mTip.setPivotY(Tools.fromDpToPx(80));
        }

        mChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                dateTextView.setText(mLabels[entryIndex]);
                priceTextView.setText(String.valueOf(mValues[entryIndex]));

            }
        });
        mChart.setTooltips(mTip);

        findMinMax(mValues);
        int divider;
        if(countDigits(Math.round(max))==4){
            divider = 100;
        }else {
            divider = 10;
        }
        int maxVal = (int)((max+divider)/divider)*divider;
        int minVal = (int)(min/divider)*divider;

        int step = (maxVal - minVal)/divider;
        //reverse lables and values:
        Collections.reverse(Arrays.asList(mLabels));
        Collections.reverse(Arrays.asList(mValues));

        LineSet dataset = new LineSet(mLabels, mValues);

        dataset.setColor(Color.parseColor("#84FFFF"))
                .setGradientFill(new int[]{Color.parseColor("#673AB7"), Color.parseColor("#64B5F6")}, null);


        mChart.addData(dataset);

        // Chart
        mChart.setBorderSpacing(1)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.WHITE)
                .setXAxis(false)
                .setYAxis(true);
        mChart.setAxisBorderValues(minVal, maxVal, step);


        mChart.show();
    }

    private int countDigits(int max) {
        int c = 0;
        while (max>0){
            max = (int)max/10;
            c = c + 1;
        }
        return c;
    }

    public static void findMinMax(float[] arr){
        min=arr[0];
        max=arr[0];
        for(int ii=0;ii<arr.length;ii++){
            if(arr[ii]<min){
                min=arr[ii];
            }
            else if(arr[ii]>max){
                max=arr[ii];
            }
        }

    }
}
