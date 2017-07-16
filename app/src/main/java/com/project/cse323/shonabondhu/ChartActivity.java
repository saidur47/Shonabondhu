package com.project.cse323.shonabondhu;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.kosalgeek.asynctask.AsyncResponse;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {
    String dataArray[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        SharedPreferences settings = getSharedPreferences("Data", 0);
        String unprocessedData = settings.getString("UnprocessedData", "");
        dataArray = unprocessedData.split(" ");
        if (dataArray.length > 4) {
            setAndShowChart();
        }
    }

    protected void setAndShowChart() {
        LineChart chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();
        int i = 0;
        while (i < dataArray.length) {
            entries.add(new Entry(i, Float.parseFloat(dataArray[i])));
            i += 5;
        }


        LineDataSet dataSet = new LineDataSet(entries, "Sensor Value"); // add entries to dataset
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.RED);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();

        Description ds = new Description();
        ds.setText("Flat Flood Sensor Data Graph");
        chart.setDescription(ds);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

       /* xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dataArray[i];
            }
        });*/
    }
}
