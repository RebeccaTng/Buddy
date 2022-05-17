package be.kuleuven.buddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import be.kuleuven.buddy.R;
import be.kuleuven.buddy.account.AccountInfo;
import be.kuleuven.buddy.other.InfoFragment;

public class Overview extends AppCompatActivity {

    LineDataSet moistDataSet, lightDataSet, tempDataSet;
    LineChart moistChart, lightChart, tempChart;
    TextView dateView, day, week, month, year, userMessage;
    Calendar calendar, calendarMin, calendarMinWeek;
    SimpleDateFormat dayFormat, weekFormatBegin, weekFormatEnd, monthFormat, dbFormat;
    String dayNow, date, dayToday, weekNowBegin, weekNowEnd, weekBegin, weekEnd, monthNow, yearNow;
    ImageView previous, next;
    AccountInfo accountInfo;
    ProgressBar loading;
    ScrollView scrollView;
    int plantId;

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
        loading = findViewById(R.id.loading_overview);
        scrollView = findViewById(R.id.scroll);
        userMessage = findViewById(R.id.userMessage_overview);

        dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String plantDate = null;
        if(getIntent().hasExtra("accountInfo")) { accountInfo = getIntent().getExtras().getParcelable("accountInfo"); }
        if(getIntent().hasExtra("plantId")) { plantId = getIntent().getExtras().getInt("plantId"); }
        if(getIntent().hasExtra("plantDate")) { plantDate = getIntent().getExtras().getString("plantDate"); }
        calendarMin = Calendar.getInstance();
        calendarMinWeek = Calendar.getInstance();
        try {
            assert plantDate != null;
            calendarMin.setTime(Objects.requireNonNull(dbFormat.parse(plantDate)));
            calendarMinWeek.setTime(Objects.requireNonNull(dbFormat.parse(plantDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendarMinWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendarMinWeek.add(Calendar.DAY_OF_MONTH, 7);

        findViewById(R.id.infoIconOverview).setOnClickListener(view -> {
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

        dayNow = dayFormat.format(calendar.getTime());
        String dayDb = dbFormat.format(calendar.getTime());
        dayToday = dayNow + " (Today)";
        dateView.setText(dayToday);
        if(dayNow.equals(dayFormat.format(calendarMin.getTime()))) previous.setVisibility(View.GONE);

        // Create chart
        getChartData("day", dayDb, null);

        // Customize chart
        chartSettings(moistChart, moistDataSet, false);
        chartSettings(lightChart, lightDataSet, false);
        chartSettings(tempChart, tempDataSet, true);

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
            if(dayNow.equals(dayFormat.format(calendarMin.getTime()))) previous.setVisibility(View.GONE);
            else previous.setVisibility(View.VISIBLE);
            getChartData("day", dayDb, null);
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
            String weekBeginDb = dbFormat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK, 6);
            weekNowEnd = weekFormatEnd.format(calendar.getTime());
            date = weekNowBegin + " - " + weekNowEnd;
            dateView.setText(date);
            next.setVisibility(View.GONE);
            if(weekNowEnd.equals(weekFormatEnd.format(calendarMinWeek.getTime()))) previous.setVisibility(View.GONE);
            else previous.setVisibility(View.VISIBLE);
            getChartData("week", weekBeginDb, dbFormat.format(calendar.getTime()));
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
            if(monthNow.equals(monthFormat.format(calendarMinWeek.getTime()))) previous.setVisibility(View.GONE);
            else previous.setVisibility(View.VISIBLE);
            getChartData("month", String.valueOf(calendar.get(Calendar.YEAR)), String.valueOf(calendar.get(Calendar.MONTH)+1));
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
            yearNow = String.valueOf(calendar.get(Calendar.YEAR));
            dateView.setText(yearNow);
            next.setVisibility(View.GONE);
            if(yearNow.equals(String.valueOf(calendarMin.get(Calendar.YEAR)))) previous.setVisibility(View.GONE);
            else previous.setVisibility(View.VISIBLE);
            getChartData("year", yearNow, null);
        });

        previous.setOnClickListener(view -> {
            changeDate(-1, green);
            next.setVisibility(View.VISIBLE);
        });

        next.setOnClickListener(view -> {
            changeDate(1, green);
            previous.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onBackPressed() {
        Intent goToPlantStatistics = new Intent(this, PlantStatistics.class);
        goToPlantStatistics.putExtra("accountInfo", accountInfo);
        goToPlantStatistics.putExtra("plantId", plantId);
        startActivity(goToPlantStatistics);
    }

    public void goBack(View caller) {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private void changeDate(int sign, int green) {
        String currentDate = dateView.getText().toString();

        if(day.getCurrentTextColor() == green) {
            try {
                calendar.setTime(Objects.requireNonNull(dayFormat.parse(currentDate)));
            } catch (ParseException e) { e.printStackTrace(); }

            calendar.add(Calendar.DATE, sign);
            date = dayFormat.format(calendar.getTime());
            if(date.equals(dayNow)) {
                next.setVisibility(View.GONE);
                date = dayToday;
            }
            else if(date.equals(dayFormat.format(calendarMin.getTime()))) previous.setVisibility(View.GONE);
            getChartData("day", dbFormat.format(calendar.getTime()), null);

        } else if(week.getCurrentTextColor() == green) {
            try{
                String[] parts = currentDate.split(" - ");
                calendar.setTime(Objects.requireNonNull(weekFormatEnd.parse(parts[1])));
            } catch (ParseException e) { e.printStackTrace(); }

            calendar.add(Calendar.WEEK_OF_YEAR, sign);
            weekEnd = weekFormatEnd.format(calendar.getTime());
            String weekEndDb = dbFormat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK, -6);
            weekBegin = weekFormatBegin.format(calendar.getTime());
            date = weekBegin + " - " + weekEnd;
            if(weekEnd.equals(weekNowEnd)) next.setVisibility(View.GONE);
            else if(weekEnd.equals(weekFormatEnd.format(calendarMinWeek.getTime()))) previous.setVisibility(View.GONE);
            getChartData("week", dbFormat.format(calendar.getTime()), weekEndDb);

        } else if(month.getCurrentTextColor() == green) {
            try {
                calendar.setTime(Objects.requireNonNull(monthFormat.parse(currentDate)));
            } catch (ParseException e) { e.printStackTrace(); }

            calendar.add(Calendar.MONTH, sign);
            date = monthFormat.format(calendar.getTime());
            if(date.equals(monthNow)) next.setVisibility(View.GONE);
            else if(date.equals(monthFormat.format(calendarMin.getTime()))) previous.setVisibility(View.GONE);
            getChartData("month", String.valueOf(calendar.get(Calendar.YEAR)), String.valueOf(calendar.get(Calendar.MONTH)+1));

        } else if(year.getCurrentTextColor() == green) {
            calendar.set(Calendar.YEAR, Integer.parseInt(currentDate));

            calendar.add(Calendar.YEAR, sign);
            date = String.valueOf(calendar.get(Calendar.YEAR));
            if(date.equals(yearNow)) next.setVisibility(View.GONE);
            else if(date.equals(String.valueOf(calendarMin.get(Calendar.YEAR)))) previous.setVisibility(View.GONE);
            getChartData("year", date, null);
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

    private void chartSettings(LineChart chart, LineDataSet dataSet, Boolean isTemp) {

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
        if(isTemp){
            yAxis.setAxisMinimum(-10f);
            yAxis.setAxisMaximum(45f);
            yAxis.setGranularity(10f);
        } else {
            yAxis.setAxisMinimum(0f);
            yAxis.setAxisMaximum(102f);
            yAxis.setGranularity(20f); // Intervals
        }
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

    private void axisSettings(String type, LineChart chart, LineDataSet dataSet, int size) {
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
        if(size == 1) {
            dataSet.setDrawCircles(true);
            dataSet.setDrawValues(true);
        }
        chart.invalidate();
    }

    private void updateChart(String type, List<Entry> moistEntries, List<Entry> lightEntries, List<Entry> tempEntries){

        // Create new dataSet from values
        moistDataSet = new LineDataSet(moistEntries, "moisture");
        lightDataSet = new LineDataSet(lightEntries, "light");
        tempDataSet = new LineDataSet(tempEntries, "temperature");

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
        axisSettings(type, moistChart, moistDataSet, moistEntries.size());
        axisSettings(type, lightChart, lightDataSet, moistEntries.size());
        axisSettings(type, tempChart, tempDataSet, moistEntries.size());
    }

    private void getChartData(String type, String date1, String date2){
        userMessage.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        // Connect to database
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://a21iot03.studev.groept.be/public/api/home/plantStatistics/overview/" + plantId + "/" + type + "/" + date1 + "/" + date2;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.GET, url, null,
                response -> {
                    //process the response
                    try {
                        String Rmessage = response.getString("message");
                        String Rcomment = response.getString("comment");

                        //check if login is valid
                        if(Rmessage.equals("OverviewLoaded")) {
                            JSONArray data = response.getJSONArray("data");
                            JSONObject dataObject;
                            int moist, light, temp, timestamp;
                            List<Entry> moistEntries = new ArrayList<>();
                            List<Entry> lightEntries = new ArrayList<>();
                            List<Entry> tempEntries = new ArrayList<>();

                            for(int i = 0; i < data.length(); i++) {
                                dataObject = data.getJSONObject(i);
                                moist = dataObject.getInt("moistData");
                                light = dataObject.getInt("lightData");
                                temp = dataObject.getInt("tempData");
                                timestamp = dataObject.getInt("timestamp");

                                moistEntries.add(new Entry(timestamp, moist));
                                lightEntries.add(new Entry(timestamp, light));
                                tempEntries.add(new Entry(timestamp, temp));
                            }

                            updateChart(type, moistEntries, lightEntries, tempEntries);
                            loading.setVisibility(View.INVISIBLE);
                            scrollView.setVisibility(View.VISIBLE);

                        }else if(Rmessage.equals("OverviewLoadedNoData")) {
                            loading.setVisibility(View.GONE);
                            userMessage.setText(Rcomment);
                            userMessage.setTextColor(ContextCompat.getColor(this, R.color.beige));
                            userMessage.setVisibility(View.VISIBLE);

                        } else{
                            loading.setVisibility(View.GONE);
                            userMessage.setTextColor(ContextCompat.getColor(this, R.color.red));
                            userMessage.setText(R.string.error);
                            userMessage.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e){ e.printStackTrace(); }},

                error -> {
                    //process an error
                    loading.setVisibility(View.GONE);
                    userMessage.setTextColor(ContextCompat.getColor(this, R.color.red));
                    userMessage.setText(R.string.error);
                    userMessage.setVisibility(View.VISIBLE);
                });
        requestQueue.add(jsonObjectRequest);
    }
}