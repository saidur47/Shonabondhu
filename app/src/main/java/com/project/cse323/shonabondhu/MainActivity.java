package com.project.cse323.shonabondhu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements AsyncResponse {
    private ProgressDialog pDialog;

    Button getdata, chartButton, clearButton;
    Spinner spinnerYear, spinnerMonth, spinnerDate, spinnerHour, spinnerMinute, spinnerYearEnd, spinnerMonthEnd, spinnerDateEnd, spinnerHourEnd, spinnerMinuteEnd;

    Date startseconds, endseconds;
    Calendar c;


    String hostUrl = "http://192.168.43.34:9860/flate-flood/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        getdata = (Button) findViewById(R.id.updButton);
        chartButton = (Button) findViewById(R.id.chartButton);
        clearButton = (Button) findViewById(R.id.clrData);


        initiatializeSpinner();

        getdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpDialog();
                setCustomDate();
                showData();
            }
        });
        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chartIntent = new Intent(MainActivity.this, ChartActivity.class);
                startActivity(chartIntent);
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpDialog();
                setCustomDate();
                truncateData();
            }
        });

    }

    @Override
    public void processFinish(String s) {

        if (!(s.contains("updated") || s.contains("truncated"))) {
            SharedPreferences settings = getSharedPreferences("Data", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("UnprocessedData", s);
            editor.commit();


            String dataArray[] = s.split(" ");

            int dataArraySize = dataArray.length;
            TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
            int i = 0, rowCount = 1;
            if (dataArraySize > 4) {
                for (i = 0, rowCount = 1; i < dataArraySize; rowCount++) {
                    TableRow row = new TableRow(this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);
                    TextView sensor1 = new TextView(this);
                    sensor1.setBackgroundResource(R.drawable.back);
                    sensor1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    TextView sensor2 = new TextView(this);
                    sensor2.setBackgroundResource(R.drawable.back);
                    sensor2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    TextView sensor3 = new TextView(this);
                    sensor3.setBackgroundResource(R.drawable.back);
                    sensor3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    TextView timespan = new TextView(this);
                    timespan.setBackgroundResource(R.drawable.back);
                    timespan.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    timespan.setPadding(2, 0, 2, 0);

                    sensor1.setText(dataArray[i++]);
                    sensor2.setText(dataArray[i++]);
                    sensor3.setText(dataArray[i++]);
                    timespan.setText(dataArray[i++] + " " + dataArray[i++]);
                    row.addView(sensor1);
                    row.addView(sensor2);
                    row.addView(sensor3);
                    row.addView(timespan);
                    ll.addView(row, rowCount);
                }
            }
            c = Calendar.getInstance();
            endseconds = c.getTime();
            updateRespnseTimeInDB(rowCount - 1);
        }
        hidepDialog();
    }

    public void updateRespnseTimeInDB(int noOfRows) {
        String timspan = "" + (endseconds.getTime() - startseconds.getTime());
        PostResponseAsyncTask task = new PostResponseAsyncTask(this, this);
        String url = hostUrl + "saveuploadtimetime.php?numrow='" + noOfRows + "'&timespan='" + timspan + "'";
        task.execute(url);
        hidepDialog();
    }

    public void truncateData() {
        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
        while (ll.getChildCount() > 1) {
            ll.removeViewAt(1);
        }
        SharedPreferences dataSettings = getSharedPreferences("Data", 0);
        SharedPreferences.Editor editor = dataSettings.edit();
        editor.putString("UnprocessedData", "");
        editor.commit();

        String startTime, endTime;
        SharedPreferences settings = getSharedPreferences("CustomTime", 0);
        startTime = settings.getString("StartTime", "");
        endTime = settings.getString("EndTime", "");
        PostResponseAsyncTask task = new PostResponseAsyncTask(this, this);
        String url = hostUrl + "truncatedata.php?starttime='" + startTime + "'&endtime='" + endTime + "'";
        task.execute(url);
        hidepDialog();
    }

    public void showData() {
        c = Calendar.getInstance();

        startseconds = c.getTime();
        String startTime, endTime;

        SharedPreferences settings = getSharedPreferences("CustomTime", 0);
        startTime = settings.getString("StartTime", "");
        endTime = settings.getString("EndTime", "");

        startTime = startTime.replace(" ", "%20");
        endTime = endTime.replace(" ", "%20");
        PostResponseAsyncTask task = new PostResponseAsyncTask(this, this);
        String url2 = hostUrl + "getdata.php?starttime='" + startTime + "'&endtime='" + endTime + "'";

        task.execute(url2);
    }


    public void initiatializeSpinner() {
        spinnerYear = (Spinner) findViewById(R.id.spinnerYear);
        spinnerYearEnd = (Spinner) findViewById(R.id.spinnerYearEnd);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.year_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapter);
        spinnerYearEnd.setAdapter(adapter);
        //
        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        spinnerMonthEnd = (Spinner) findViewById(R.id.spinnerMonthEnd);
        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter.createFromResource(this,
                R.array.month_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonthEnd.setAdapter(adapterMonth);
        //
        spinnerDate = (Spinner) findViewById(R.id.spinnerDate);
        spinnerDateEnd = (Spinner) findViewById(R.id.spinnerDateEnd);
        ArrayAdapter<CharSequence> adapterDate = ArrayAdapter.createFromResource(this,
                R.array.date_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(adapterDate);
        spinnerDateEnd.setAdapter(adapterDate);
        //
        //
        spinnerHour = (Spinner) findViewById(R.id.spinnerHour);
        spinnerHourEnd = (Spinner) findViewById(R.id.spinnerHourEnd);
        ArrayAdapter<CharSequence> adapterHour = ArrayAdapter.createFromResource(this,
                R.array.hour_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHour.setAdapter(adapterHour);
        spinnerHourEnd.setAdapter(adapterHour);
        //
        spinnerMinute = (Spinner) findViewById(R.id.spinnerMinute);
        spinnerMinuteEnd = (Spinner) findViewById(R.id.spinnerMinuteEnd);
        ArrayAdapter<CharSequence> adapterMinute = ArrayAdapter.createFromResource(this,
                R.array.min_sec_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMinute.setAdapter(adapterMinute);
        spinnerMinuteEnd.setAdapter(adapterMinute);
        //


    }

    public void setCustomDate() {
        String startTime = "";
        startTime += spinnerYear.getSelectedItem().toString();
        startTime += "-";
        startTime += spinnerMonth.getSelectedItem().toString();
        startTime += "-";
        startTime += spinnerDate.getSelectedItem().toString();
        startTime += "%20";
        startTime += spinnerHour.getSelectedItem().toString();
        startTime += ":";
        startTime += spinnerMinute.getSelectedItem().toString();
        startTime += ":";
        startTime += "00";

        String endTime = "";

        endTime += spinnerYearEnd.getSelectedItem().toString();
        endTime += "-";
        endTime += spinnerMonthEnd.getSelectedItem().toString();
        endTime += "-";
        endTime += spinnerDateEnd.getSelectedItem().toString();
        endTime += "%20";
        endTime += spinnerHourEnd.getSelectedItem().toString();
        endTime += ":";
        endTime += spinnerMinuteEnd.getSelectedItem().toString();
        endTime += ":";
        endTime += "00";


        SharedPreferences settings = getSharedPreferences("CustomTime", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("StartTime", startTime);
        editor.putString("EndTime", endTime);
        editor.commit();
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}


