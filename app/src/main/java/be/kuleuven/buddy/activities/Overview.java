package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.buddy.R;

public class Overview extends AppCompatActivity {

    LineChart moistChart, lightChart, tempChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_overview);

        moistChart = findViewById(R.id.moistChart);
        lightChart = findViewById(R.id.lightChart);
        tempChart = findViewById(R.id.tempChart);

        createCharts();
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void createCharts() {

        // Create new dataSet from values
        LineDataSet moistDataSet = new LineDataSet(getMoistDataSet(), "moisture");
        LineDataSet lightDataSet = new LineDataSet(getLightDataSet(), "light");
        LineDataSet tempDataSet = new LineDataSet(getTempDataSet(), "temperature");

        // Customize dataSet settings
        dataSetSettings(moistDataSet);
        dataSetSettings(lightDataSet);
        dataSetSettings(tempDataSet);

        // Add dataSet to a lineData
        LineData moistLineData = new LineData(moistDataSet);
        LineData lightLineData = new LineData(lightDataSet);
        LineData tempLineData = new LineData(tempDataSet);

        // Add data to right chart
        moistChart.setData(moistLineData);
        lightChart.setData(lightLineData);
        tempChart.setData(tempLineData);

        // Customize chart
        chartSettings(moistChart, moistDataSet);
        chartSettings(lightChart, lightDataSet);
        chartSettings(tempChart, tempDataSet);

        // Initialize chart
        moistChart.invalidate();
        lightChart.invalidate();
        tempChart.invalidate();
    }

    public void dataSetSettings(LineDataSet dataSet) {

        int green = ContextCompat.getColor(this, R.color.green);
        int beige = ContextCompat.getColor(this, R.color.beige);

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setHighlightEnabled(true);
        dataSet.setHighlightLineWidth(1);
        dataSet.setColor(R.color.black);
        dataSet.setLineWidth(4);
        dataSet.setColor(green);
        dataSet.setCircleColor(green);
        dataSet.setCircleHoleColor(beige);
        dataSet.setCircleHoleRadius(3);
        dataSet.setCircleRadius(6);
        dataSet.setValueTextSize(9);
        dataSet.setValueTextColor(green);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth line
    }

    public void chartSettings(LineChart chart, LineDataSet dataSet) {

        Typeface tf = ResourcesCompat.getFont(this, R.font.mulish_regular);
        int dark_beige = ContextCompat.getColor(this, R.color.dark_beige);
        int brown = ContextCompat.getColor(this, R.color.brown);

        chart.setNoDataText("No Data");
        chart.setNoDataTextColor(R.color.black);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(false);
        chart.getAxisRight().setEnabled(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setTypeface(tf);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setGranularity(20f); // Intervals
        yAxis.setTextColor(brown);
        yAxis.setGridColor(dark_beige);
        yAxis.setDrawAxisLine(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tf);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(24f);
        xAxis.setLabelCount(13, true);
        xAxis.setTextColor(brown);
        xAxis.setDrawGridLines(false);

        chart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}

            @Override
            public void onChartLongPressed(MotionEvent me) {}

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                dataSet.setDrawCircles(!dataSet.isDrawCirclesEnabled());
                dataSet.setDrawValues(!dataSet.isDrawValuesEnabled());
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {}

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {}

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {}
        });
    }

    private List<Entry> getMoistDataSet() {
        // TODO insert values from database
        List<Entry> moistEntries = new ArrayList<>();
        moistEntries.add(new Entry(4, 42));
        moistEntries.add(new Entry(6, 90));
        moistEntries.add(new Entry(8, 78));
        moistEntries.add(new Entry(10, 67));
        moistEntries.add(new Entry(12, 56));
        moistEntries.add(new Entry(16, 22));
        moistEntries.add(new Entry(18, 84));
        return moistEntries;
    }

    private List<Entry> getLightDataSet() {
        // TODO insert values from database
        List<Entry> lightEntries = new ArrayList<>();
        lightEntries.add(new Entry(4, 42));
        lightEntries.add(new Entry(6, 90));
        lightEntries.add(new Entry(8, 78));
        lightEntries.add(new Entry(10, 67));
        lightEntries.add(new Entry(12, 56));
        lightEntries.add(new Entry(16, 22));
        lightEntries.add(new Entry(18, 84));
        return lightEntries;
    }

    private List<Entry> getTempDataSet() {
        // TODO insert values from database
        List<Entry> tempEntries = new ArrayList<>();
        tempEntries.add(new Entry(4, 42));
        tempEntries.add(new Entry(6, 90));
        tempEntries.add(new Entry(8, 78));
        tempEntries.add(new Entry(10, 67));
        tempEntries.add(new Entry(12, 56));
        tempEntries.add(new Entry(16, 22));
        tempEntries.add(new Entry(18, 84));
        return tempEntries;
    }
}