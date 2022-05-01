package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.other.InfoFragment;

public class Overview extends AppCompatActivity {

    LineDataSet moistDataSet, lightDataSet, tempDataSet;
    LineChart moistChart, lightChart, tempChart;
    TextView dateView, day, week, month, year;
    Calendar calendar;
    SimpleDateFormat dayFormat, weekFormatBegin, weekFormatEnd, monthFormat, yearFormat;
    String dayNow, date, dayToday, weekNowBegin, weekNowEnd, weekBegin, weekEnd, monthNow, yearNow;
    ImageView previous, next, infoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_overview);

        moistChart = findViewById(R.id.moistChart);
        lightChart = findViewById(R.id.lightChart);
        tempChart = findViewById(R.id.tempChart);
        dateView = findViewById(R.id.overviewDate);
        previous = findViewById(R.id.arrowIconLeft);
        next = findViewById(R.id.arrowIconRight);
        day = findViewById(R.id.overviewDay);
        week = findViewById(R.id.overviewWeek);
        month = findViewById(R.id.overviewMonth);
        year = findViewById(R.id.overviewYear);
        infoBtn = findViewById(R.id.infoIconOverview);

        infoBtn.setOnClickListener(view -> {
            String title = getResources().getString(R.string.howTo);
            String body = getResources().getString(R.string.graphUse);
            InfoFragment info = InfoFragment.newInstance(title, body);
            info.show(getSupportFragmentManager(), "infoFragment");
        });

        int green = ContextCompat.getColor(this, R.color.dark_green);
        int beige = ContextCompat.getColor(this, R.color.beige);
        Drawable btnBg = ContextCompat.getDrawable(this, R.drawable.bg_beige_second);

        calendar = Calendar.getInstance();
        dayFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        weekFormatBegin = new SimpleDateFormat("MMM d", Locale.getDefault());
        weekFormatEnd = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

        dayNow = dayFormat.format(calendar.getTime());
        dayToday = dayNow + " (Today)";
        dateView.setText(dayToday);

        // Create chart
        updateChart("day");

        // Customize chart
        chartSettings(moistChart, moistDataSet);
        chartSettings(lightChart, lightDataSet);
        chartSettings(tempChart, tempDataSet);

        day.setOnClickListener(view -> {
            day.setBackground(btnBg);
            week.setBackgroundResource(0);
            month.setBackgroundResource(0);
            year.setBackgroundResource(0);
            day.setTextColor(green);
            week.setTextColor(beige);
            month.setTextColor(beige);
            year.setTextColor(beige);

            dateView.setText(dayToday);
            next.setVisibility(View.GONE);
            updateChart("day");
        });

        week.setOnClickListener(view -> {
            day.setBackgroundResource(0);
            week.setBackground(btnBg);
            month.setBackgroundResource(0);
            year.setBackgroundResource(0);
            day.setTextColor(beige);
            week.setTextColor(green);
            month.setTextColor(beige);
            year.setTextColor(beige);

            calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            weekNowBegin = weekFormatBegin.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK, 6);
            weekNowEnd = weekFormatEnd.format(calendar.getTime());
            date = weekNowBegin + " - " + weekNowEnd;
            dateView.setText(date);
            next.setVisibility(View.GONE);
            updateChart("week");
        });

        month.setOnClickListener(view -> {
            day.setBackgroundResource(0);
            week.setBackgroundResource(0);
            month.setBackground(btnBg);
            year.setBackgroundResource(0);
            day.setTextColor(beige);
            week.setTextColor(beige);
            month.setTextColor(green);
            year.setTextColor(beige);

            calendar = Calendar.getInstance();
            monthNow = monthFormat.format(calendar.getTime());
            dateView.setText(monthNow);
            next.setVisibility(View.GONE);
            updateChart("month");
        });

        year.setOnClickListener(view -> {
            day.setBackgroundResource(0);
            week.setBackgroundResource(0);
            month.setBackgroundResource(0);
            year.setBackground(btnBg);
            day.setTextColor(beige);
            week.setTextColor(beige);
            month.setTextColor(beige);
            year.setTextColor(green);

            calendar = Calendar.getInstance();
            yearNow = yearFormat.format(calendar.getTime());
            dateView.setText(yearNow);
            next.setVisibility(View.GONE);
            updateChart("year");
        });

        previous.setOnClickListener(view -> {
            // TODO set that you can't go back to before you added the plant
            changeDate(-1, green);
            next.setVisibility(View.VISIBLE);
        });

        next.setOnClickListener(view -> changeDate(1, green));
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private void changeDate(int sign, int green) {
        String currentDate = dateView.getText().toString();
        try {
            if(week.getCurrentTextColor() == green) {
                String[] parts = currentDate.split(" - ");
                calendar.setTime(Objects.requireNonNull(weekFormatEnd.parse(parts[1])));
            }
            else calendar.setTime(Objects.requireNonNull(dayFormat.parse(currentDate)));
        } catch (ParseException e) { e.printStackTrace(); }

        if(day.getCurrentTextColor() == green) {
            calendar.add(Calendar.DATE, sign);
            date = dayFormat.format(calendar.getTime());
            if(date.equals(dayNow)) {
                next.setVisibility(View.GONE);
                date = dayToday;
            }
            updateChart("day");

        } else if(week.getCurrentTextColor() == green) {
            calendar.add(Calendar.WEEK_OF_YEAR, sign);
            weekEnd = weekFormatEnd.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK, -6);
            weekBegin = weekFormatBegin.format(calendar.getTime());
            date = weekBegin + " - " + weekEnd;
            if(weekEnd.equals(weekNowEnd)) next.setVisibility(View.GONE);
            updateChart("week");

        } else if(month.getCurrentTextColor() == green) {
            calendar.add(Calendar.MONTH, sign);
            date = monthFormat.format(calendar.getTime());
            if(date.equals(monthNow)) next.setVisibility(View.GONE);
            updateChart("month");

        } else if(year.getCurrentTextColor() == green) {
            calendar.add(Calendar.YEAR, sign);
            date = yearFormat.format(calendar.getTime());
            if(date.equals(yearNow)) next.setVisibility(View.GONE);
            updateChart("year");
        }

        dateView.setText(date);
    }

    private void dataSetSettings(LineDataSet dataSet) {

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

    private void chartSettings(LineChart chart, LineDataSet dataSet) {

        Typeface tf = ResourcesCompat.getFont(this, R.font.mulish_regular);
        int dark_beige = ContextCompat.getColor(this, R.color.dark_beige);
        int brown = ContextCompat.getColor(this, R.color.brown);

        chart.setNoDataText("No Data");
        chart.setNoDataTextColor(R.color.black);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.animateX(1200, Easing.Linear);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setTypeface(tf);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(102f);
        yAxis.setGranularity(20f); // Intervals
        yAxis.setTextColor(brown);
        yAxis.setGridColor(dark_beige);
        yAxis.setDrawAxisLine(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tf);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(24f);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(brown);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);

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

        chart.invalidate();
    }

    private void axisSettings(String type, LineChart chart, LineDataSet dataSet) {
        XAxis xAxis = chart.getXAxis();

        switch(type) {
            case "day": {
                List<String> xAxisValues = new ArrayList<>(Arrays.asList("0h", "1h", "2h", "3h", "4h", "5h", "6h", "7h", "8h", "9h", "10h", "11h", "12h", "13h", "14h", "15h", "16h", "17h", "18h", "19h", "20h", "21h", "22h", "23h", "0h"));
                chart.animateX(1200, Easing.Linear);
                xAxis.setAxisMinimum(0f);
                xAxis.setAxisMaximum(24f);
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
                break;
            }
            case "week": {
                List<String> xAxisValues = new ArrayList<>(Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
                chart.animateX(1200, Easing.Linear);
                xAxis.setAxisMinimum(0f);
                xAxis.setAxisMaximum(6f);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(7);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
                break;
            }
            case "month": {
                chart.animateX(1200, Easing.Linear);
                xAxis.setValueFormatter(null);
                xAxis.setAxisMinimum(1f);
                xAxis.setAxisMaximum(31f);
                xAxis.setGranularity(1f);
                break;
            }
            case "year": {
                chart.animateX(1200, Easing.Linear);
                xAxis.setValueFormatter(null);
                xAxis.setAxisMinimum(1f);
                xAxis.setAxisMaximum(12f);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(12);
                break;
            }
        }
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
        chart.invalidate();
    }

    private void updateChart(String type){

        // Create new dataSet from values
        moistDataSet = new LineDataSet(getMoistDataSet(type), "moisture");
        lightDataSet = new LineDataSet(getLightDataSet(type), "light");
        tempDataSet = new LineDataSet(getTempDataSet(type), "temperature");

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

        // Axis settings
        axisSettings(type, moistChart, moistDataSet);
        axisSettings(type, lightChart, lightDataSet);
        axisSettings(type, tempChart, tempDataSet);
    }

    private List<Entry> getMoistDataSet(String type) {
        // TODO insert values from database
        List<Entry> moistEntries = new ArrayList<>();

        switch(type) {
            case "day": {
                moistEntries.add(new Entry(0, 0));
                moistEntries.add(new Entry(1, 21));
                moistEntries.add(new Entry(2, 28));
                moistEntries.add(new Entry(3, 35));
                moistEntries.add(new Entry(4, 42));
                moistEntries.add(new Entry(5, 94));
                moistEntries.add(new Entry(6, 90));
                moistEntries.add(new Entry(7, 88));
                moistEntries.add(new Entry(8, 78));
                moistEntries.add(new Entry(9, 71));
                moistEntries.add(new Entry(10, 67));
                moistEntries.add(new Entry(11, 61));
                moistEntries.add(new Entry(12, 56));
                moistEntries.add(new Entry(13, 52));
                moistEntries.add(new Entry(14, 40));
                moistEntries.add(new Entry(15, 36));
                moistEntries.add(new Entry(16, 22));
                moistEntries.add(new Entry(17, 4));
                moistEntries.add(new Entry(18, 84));
                moistEntries.add(new Entry(19, 83));
                moistEntries.add(new Entry(20, 78));
                moistEntries.add(new Entry(21, 70));
                moistEntries.add(new Entry(22, 65));
                moistEntries.add(new Entry(23, 60));
                moistEntries.add(new Entry(24, 52));
                break;
            }
            case "week": {
                moistEntries.add(new Entry(0, 12));
                moistEntries.add(new Entry(1, 21));
                moistEntries.add(new Entry(2, 28));
                moistEntries.add(new Entry(3, 100));
                moistEntries.add(new Entry(4, 42));
                moistEntries.add(new Entry(5, 94));
                moistEntries.add(new Entry(6, 90));
                break;
            }

            case "month": {
                moistEntries.add(new Entry(1, 100));
                moistEntries.add(new Entry(2, 28));
                moistEntries.add(new Entry(3, 35));
                moistEntries.add(new Entry(4, 42));
                moistEntries.add(new Entry(5, 94));
                moistEntries.add(new Entry(6, 90));
                moistEntries.add(new Entry(7, 88));
                moistEntries.add(new Entry(8, 78));
                moistEntries.add(new Entry(9, 71));
                moistEntries.add(new Entry(10, 67));
                moistEntries.add(new Entry(11, 61));
                moistEntries.add(new Entry(12, 56));
                moistEntries.add(new Entry(13, 52));
                moistEntries.add(new Entry(14, 40));
                moistEntries.add(new Entry(15, 36));
                moistEntries.add(new Entry(16, 22));
                moistEntries.add(new Entry(17, 4));
                moistEntries.add(new Entry(18, 84));
                moistEntries.add(new Entry(19, 83));
                moistEntries.add(new Entry(20, 78));
                moistEntries.add(new Entry(21, 70));
                moistEntries.add(new Entry(22, 65));
                moistEntries.add(new Entry(23, 60));
                moistEntries.add(new Entry(24, 52));
                moistEntries.add(new Entry(25, 48));
                moistEntries.add(new Entry(26, 42));
                moistEntries.add(new Entry(27, 36));
                moistEntries.add(new Entry(28, 35));
                moistEntries.add(new Entry(29, 31));
                moistEntries.add(new Entry(30, 20));
                moistEntries.add(new Entry(31, 100));
                break;
            }
            case "year": {
                moistEntries.add(new Entry(1, 21));
                moistEntries.add(new Entry(2, 28));
                moistEntries.add(new Entry(3, 35));
                moistEntries.add(new Entry(4, 42));
                moistEntries.add(new Entry(5, 94));
                moistEntries.add(new Entry(6, 90));
                moistEntries.add(new Entry(7, 88));
                moistEntries.add(new Entry(8, 78));
                moistEntries.add(new Entry(9, 71));
                moistEntries.add(new Entry(10, 67));
                moistEntries.add(new Entry(11, 61));
                moistEntries.add(new Entry(12, 12));
                break;
            }
        }
        return moistEntries;
    }

    private List<Entry> getLightDataSet(String type) {
        // TODO insert values from database
        List<Entry> lightEntries = new ArrayList<>();

        switch(type) {
            case "day": {
                lightEntries.add(new Entry(0, 12));
                lightEntries.add(new Entry(1, 21));
                lightEntries.add(new Entry(2, 28));
                lightEntries.add(new Entry(3, 35));
                lightEntries.add(new Entry(4, 42));
                lightEntries.add(new Entry(5, 94));
                lightEntries.add(new Entry(6, 90));
                lightEntries.add(new Entry(7, 88));
                lightEntries.add(new Entry(8, 78));
                lightEntries.add(new Entry(9, 71));
                lightEntries.add(new Entry(10, 67));
                lightEntries.add(new Entry(11, 61));
                lightEntries.add(new Entry(12, 56));
                lightEntries.add(new Entry(13, 52));
                lightEntries.add(new Entry(14, 40));
                lightEntries.add(new Entry(15, 36));
                lightEntries.add(new Entry(16, 22));
                lightEntries.add(new Entry(17, 4));
                lightEntries.add(new Entry(18, 84));
                lightEntries.add(new Entry(19, 83));
                lightEntries.add(new Entry(20, 78));
                lightEntries.add(new Entry(21, 70));
                lightEntries.add(new Entry(22, 65));
                lightEntries.add(new Entry(23, 60));
                lightEntries.add(new Entry(24, 52));
                break;
            }
            case "week": {

                lightEntries.add(new Entry(0, 12));
                lightEntries.add(new Entry(1, 21));
                lightEntries.add(new Entry(2, 28));
                lightEntries.add(new Entry(3, 35));
                lightEntries.add(new Entry(4, 42));
                lightEntries.add(new Entry(5, 94));
                lightEntries.add(new Entry(6, 90));
                break;
            }

            case "month": {
                lightEntries.add(new Entry(1, 21));
                lightEntries.add(new Entry(2, 28));
                lightEntries.add(new Entry(3, 35));
                lightEntries.add(new Entry(4, 42));
                lightEntries.add(new Entry(5, 94));
                lightEntries.add(new Entry(6, 90));
                lightEntries.add(new Entry(7, 88));
                lightEntries.add(new Entry(8, 78));
                lightEntries.add(new Entry(9, 71));
                lightEntries.add(new Entry(10, 67));
                lightEntries.add(new Entry(11, 61));
                lightEntries.add(new Entry(12, 56));
                lightEntries.add(new Entry(13, 52));
                lightEntries.add(new Entry(14, 40));
                lightEntries.add(new Entry(15, 36));
                lightEntries.add(new Entry(16, 22));
                lightEntries.add(new Entry(17, 4));
                lightEntries.add(new Entry(18, 84));
                lightEntries.add(new Entry(19, 83));
                lightEntries.add(new Entry(20, 78));
                lightEntries.add(new Entry(21, 70));
                lightEntries.add(new Entry(22, 65));
                lightEntries.add(new Entry(23, 60));
                lightEntries.add(new Entry(24, 52));
                lightEntries.add(new Entry(25, 48));
                lightEntries.add(new Entry(26, 42));
                lightEntries.add(new Entry(27, 36));
                lightEntries.add(new Entry(28, 35));
                lightEntries.add(new Entry(29, 31));
                lightEntries.add(new Entry(30, 20));
                lightEntries.add(new Entry(31, 12));
                break;
            }
            case "year": {
                lightEntries.add(new Entry(1, 21));
                lightEntries.add(new Entry(2, 28));
                lightEntries.add(new Entry(3, 35));
                lightEntries.add(new Entry(4, 42));
                lightEntries.add(new Entry(5, 94));
                lightEntries.add(new Entry(6, 90));
                lightEntries.add(new Entry(7, 88));
                lightEntries.add(new Entry(8, 78));
                lightEntries.add(new Entry(9, 71));
                lightEntries.add(new Entry(10, 67));
                lightEntries.add(new Entry(11, 61));
                lightEntries.add(new Entry(12, 12));
                break;
            }
        }
        return lightEntries;
    }

    private List<Entry> getTempDataSet(String type) {
        // TODO insert values from database
        List<Entry> tempEntries = new ArrayList<>();
        switch(type) {
            case "day": {
                tempEntries.add(new Entry(0, 12));
                tempEntries.add(new Entry(1, 21));
                tempEntries.add(new Entry(2, 28));
                tempEntries.add(new Entry(3, 35));
                tempEntries.add(new Entry(4, 42));
                tempEntries.add(new Entry(5, 94));
                tempEntries.add(new Entry(6, 90));
                tempEntries.add(new Entry(7, 88));
                tempEntries.add(new Entry(8, 78));
                tempEntries.add(new Entry(9, 71));
                tempEntries.add(new Entry(10, 67));
                tempEntries.add(new Entry(11, 61));
                tempEntries.add(new Entry(12, 56));
                tempEntries.add(new Entry(13, 52));
                tempEntries.add(new Entry(14, 40));
                tempEntries.add(new Entry(15, 36));
                tempEntries.add(new Entry(16, 22));
                tempEntries.add(new Entry(17, 4));
                tempEntries.add(new Entry(18, 84));
                tempEntries.add(new Entry(19, 83));
                tempEntries.add(new Entry(20, 78));
                tempEntries.add(new Entry(21, 70));
                tempEntries.add(new Entry(22, 65));
                tempEntries.add(new Entry(23, 60));
                tempEntries.add(new Entry(24, 52));
                break;
            }
            case "week": {
                tempEntries.add(new Entry(0, 12));
                tempEntries.add(new Entry(1, 21));
                tempEntries.add(new Entry(2, 28));
                tempEntries.add(new Entry(3, 35));
                tempEntries.add(new Entry(4, 42));
                tempEntries.add(new Entry(5, 94));
                tempEntries.add(new Entry(6, 90));
                break;
            }

            case "month": {
                tempEntries.add(new Entry(1, 21));
                tempEntries.add(new Entry(2, 28));
                tempEntries.add(new Entry(3, 35));
                tempEntries.add(new Entry(4, 42));
                tempEntries.add(new Entry(5, 94));
                tempEntries.add(new Entry(6, 90));
                tempEntries.add(new Entry(7, 88));
                tempEntries.add(new Entry(8, 78));
                tempEntries.add(new Entry(9, 71));
                tempEntries.add(new Entry(10, 67));
                tempEntries.add(new Entry(11, 61));
                tempEntries.add(new Entry(12, 56));
                tempEntries.add(new Entry(13, 52));
                tempEntries.add(new Entry(14, 40));
                tempEntries.add(new Entry(15, 36));
                tempEntries.add(new Entry(16, 22));
                tempEntries.add(new Entry(17, 4));
                tempEntries.add(new Entry(18, 84));
                tempEntries.add(new Entry(19, 83));
                tempEntries.add(new Entry(20, 78));
                tempEntries.add(new Entry(21, 70));
                tempEntries.add(new Entry(22, 65));
                tempEntries.add(new Entry(23, 60));
                tempEntries.add(new Entry(24, 52));
                tempEntries.add(new Entry(25, 48));
                tempEntries.add(new Entry(26, 42));
                tempEntries.add(new Entry(27, 36));
                tempEntries.add(new Entry(28, 35));
                tempEntries.add(new Entry(29, 31));
                tempEntries.add(new Entry(30, 20));
                tempEntries.add(new Entry(31, 21));
                break;
            }
            case "year": {
                tempEntries.add(new Entry(1, 21));
                tempEntries.add(new Entry(2, 28));
                tempEntries.add(new Entry(3, 35));
                tempEntries.add(new Entry(4, 42));
                tempEntries.add(new Entry(5, 94));
                tempEntries.add(new Entry(6, 90));
                tempEntries.add(new Entry(7, 88));
                tempEntries.add(new Entry(8, 78));
                tempEntries.add(new Entry(9, 71));
                tempEntries.add(new Entry(10, 67));
                tempEntries.add(new Entry(11, 61));
                tempEntries.add(new Entry(12, 21));
                break;
            }
        }
        return tempEntries;
    }
}