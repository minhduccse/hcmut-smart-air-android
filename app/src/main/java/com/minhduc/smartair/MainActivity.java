package com.minhduc.smartair;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int total;
    private int remoteNow, indoorNow, outdoorNow;
    private int prevRemote, prevIndoor, prevOutdoor;
    private int remoteIdx;

    private TextView mTotalValue, mRemoteValue, mIndoorTempValue, mOutdoorTempValue, mStatusValue;
    private TextView mTempPercentUp, mTempPercentDown, mPercentUp, mPercentDown;
    private TextView mIndoorDeltaUp, mIndoorDeltaDown, mIndoorPercentUp, mIndoorPercentDown;
    private TextView mOutdoorDeltaUp, mOutdoorDeltaDown, mOutdoorPercentUp, mOutdoorPercentDown;

    private ImageView mTempDown, mTempUp, mIndoorImgUp, mIndoorImgDown, mOutdoorImgUp, mOutdoorImgDown;

    public ImageButton mBtnUp, mBtnDown;

    private DatabaseReference totalRef, indoorNowRef, outdoorNowRef, remoteNowRef, statusRef;
    private DatabaseReference remoteIdxRef, remoteRef, indoorRef, outdoorRef;

    LineDataSet dataSetIndoor, dataSetOutdoor;
    LineChart lineChart;

    BarDataSet barDataSetIndoor, barDataSetOutdoor;
    BarChart indoorBarChart, outdoorBarChart;

    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    List<Entry> indoorEntries = new ArrayList<Entry>();
    List<Entry> outdoorEntries = new ArrayList<Entry>();

    List<BarEntry> indoorBarEntries = new ArrayList<BarEntry>();
    List<BarEntry> outdoorBarEntries = new ArrayList<BarEntry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRemoteValue = (TextView) findViewById(R.id.remote_value);
        mIndoorTempValue = (TextView) findViewById(R.id.indoor_temp_value);
        mOutdoorTempValue = (TextView) findViewById(R.id.outdoor_temp_value);
        mStatusValue = (TextView) findViewById(R.id.status_value);
        mTotalValue = (TextView) findViewById(R.id.total_value);
        mBtnDown = (ImageButton) findViewById(R.id.button_down);
        mBtnUp = (ImageButton) findViewById(R.id.button_up);

        //------------------------chart--------------------------
        indoorBarChart = (BarChart) findViewById(R.id.IndoorChart);
        outdoorBarChart = (BarChart) findViewById(R.id.OutdoorChart);
        lineChart = (LineChart) findViewById(R.id.lineChart);

        //--------------------air condition----------------------
        mTempPercentUp = (TextView) findViewById(R.id.temp_percent_up);
        mTempPercentDown = (TextView) findViewById(R.id.temp_percent_down);
        mPercentDown = (TextView) findViewById(R.id.percent_down);
        mPercentUp = (TextView) findViewById(R.id.percent_up);
        mTempDown = (ImageView) findViewById(R.id.imageTempDown);
        mTempUp = (ImageView) findViewById(R.id.imageTempUp);

        //------------------------indoor-------------------------
        mIndoorDeltaUp = (TextView) findViewById(R.id.in_percent_up);
        mIndoorDeltaDown = (TextView) findViewById(R.id.in_percent_down);
        mIndoorPercentUp = (TextView) findViewById(R.id.syl_in_up);
        mIndoorPercentDown = (TextView) findViewById(R.id.syl_in_down);
        mIndoorImgDown = (ImageView) findViewById(R.id.imageInDown);
        mIndoorImgUp = (ImageView) findViewById(R.id.imageInUp);

        //------------------------outdoor------------------------
        mOutdoorDeltaUp = (TextView) findViewById(R.id.out_percent_up);
        mOutdoorDeltaDown = (TextView) findViewById(R.id.out_percent_down);
        mOutdoorPercentUp = (TextView) findViewById(R.id.syl_out_up);
        mOutdoorPercentDown = (TextView) findViewById(R.id.syl_out_down);
        mOutdoorImgDown = (ImageView) findViewById(R.id.imageOutDown);
        mOutdoorImgUp = (ImageView) findViewById(R.id.imageOutUp);

        //-------------------------------------------------------
        mTempDown.setVisibility(View.INVISIBLE);
        mTempUp.setVisibility(View.INVISIBLE);
        mIndoorImgDown.setVisibility(View.INVISIBLE);
        mIndoorImgUp.setVisibility(View.INVISIBLE);
        mOutdoorImgDown.setVisibility(View.INVISIBLE);
        mOutdoorImgUp.setVisibility(View.INVISIBLE);

        //-------------------------------------------------------
        indoorBarChart.setNoDataText("");
        outdoorBarChart.setNoDataText("");
        lineChart.setScaleEnabled(false);
        indoorBarChart.setScaleEnabled(false);
        outdoorBarChart.setScaleEnabled(false);
        lineChart.setNoDataText("");

        //-------------------------------------------------------
        totalRef = FirebaseDatabase.getInstance().getReference().child("current").child("total");
        remoteIdxRef = FirebaseDatabase.getInstance().getReference().child("current").child("remoteIdx");

        indoorNowRef = FirebaseDatabase.getInstance().getReference().child("current").child("indoorNow");
        outdoorNowRef = FirebaseDatabase.getInstance().getReference().child("current").child("outdoorNow");
        remoteNowRef = FirebaseDatabase.getInstance().getReference().child("current").child("remoteNow");
        statusRef = FirebaseDatabase.getInstance().getReference().child("current").child("status");

        remoteRef = FirebaseDatabase.getInstance().getReference().child("remote");
        indoorRef = FirebaseDatabase.getInstance().getReference().child("indoor");
        outdoorRef = FirebaseDatabase.getInstance().getReference().child("outdoor");



        //---------------------------------------------------------
        indoorNowRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                prevIndoor = indoorNow;
                indoorNow = Integer.parseInt(dataSnapshot.getValue().toString());
                mIndoorTempValue.setText(dataSnapshot.getValue().toString());

                if(prevIndoor < indoorNow && prevIndoor != 0){
                    float result = ((float)(indoorNow - prevIndoor) / (float)prevIndoor)*100;
                    String str = String.format("%.2f", result);
                    mIndoorDeltaDown.setText("");
                    mIndoorPercentDown.setText("");
                    mIndoorImgDown.setVisibility(View.INVISIBLE);
                    mIndoorImgUp.setVisibility(View.VISIBLE);
                    mIndoorDeltaUp.setText(str);
                    mIndoorPercentUp.setText("%");
                }
                else if(prevIndoor > indoorNow && prevIndoor != 0){
                    float result = ((float)(prevIndoor - indoorNow) / (float)prevIndoor)*100;
                    String str = String.format("%.2f", result);
                    mIndoorDeltaUp.setText("");
                    mIndoorPercentUp.setText("");
                    mIndoorImgUp.setVisibility(View.INVISIBLE);
                    mIndoorImgDown.setVisibility(View.VISIBLE);
                    mIndoorDeltaDown.setText(str);
                    mIndoorPercentDown.setText("%");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError){}
        });

        outdoorNowRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                prevOutdoor = outdoorNow;
                outdoorNow = Integer.parseInt(dataSnapshot.getValue().toString());
                mOutdoorTempValue.setText(dataSnapshot.getValue().toString());

                if(prevOutdoor < outdoorNow && prevOutdoor != 0){
                    float result = ((float)(outdoorNow - prevOutdoor) / (float)prevOutdoor)*100;
                    String str = String.format("%.2f", result);
                    mOutdoorDeltaDown.setText("");
                    mOutdoorPercentDown.setText("");
                    mOutdoorImgDown.setVisibility(View.INVISIBLE);
                    mOutdoorImgUp.setVisibility(View.VISIBLE);
                    mOutdoorDeltaUp.setText(str);
                    mOutdoorPercentUp.setText("%");
                }
                else if(prevOutdoor > outdoorNow && prevOutdoor != 0){
                    float result = ((float)(prevOutdoor - outdoorNow) / (float)prevOutdoor)*100;
                    String str = String.format("%.2f", result);
                    mOutdoorDeltaUp.setText("");
                    mOutdoorPercentUp.setText("");
                    mOutdoorImgUp.setVisibility(View.INVISIBLE);
                    mOutdoorImgDown.setVisibility(View.VISIBLE);
                    mOutdoorDeltaDown.setText(str);
                    mOutdoorPercentDown.setText("%");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        remoteNowRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                prevRemote = remoteNow;
                remoteNow = Integer.parseInt(dataSnapshot.getValue().toString());
                mRemoteValue.setText(dataSnapshot.getValue().toString());

                if(prevRemote < remoteNow && prevRemote != 0){
                    float result = ((float)(remoteNow - prevRemote) / (float)prevRemote)*100;
                    String str = String.format("%.2f", result);
                    mTempPercentDown.setText("");
                    mPercentDown.setText("");
                    mTempDown.setVisibility(View.INVISIBLE);
                    mTempUp.setVisibility(View.VISIBLE);
                    mTempPercentUp.setText(str);
                    mPercentUp.setText("%");
                }
                else if(prevRemote > remoteNow && prevRemote != 0){
                    float result = ((float)(prevRemote - remoteNow) / (float)prevRemote)*100;
                    String str = String.format("%.2f", result);
                    mTempPercentUp.setText("");
                    mPercentUp.setText("");
                    mTempUp.setVisibility(View.INVISIBLE);
                    mTempDown.setVisibility(View.VISIBLE);
                    mTempPercentDown.setText(str);
                    mPercentDown.setText("%");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mStatusValue.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        remoteIdxRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                remoteIdx = Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        totalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total = Integer.parseInt(dataSnapshot.getValue().toString());
                mTotalValue.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        indoorRef.orderByChild("index").limitToLast(5).addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                indoorEntries.add(new Entry(Float.parseFloat(dataSnapshot.child("index").getValue().toString()),
                        Float.parseFloat(dataSnapshot.child("value").getValue().toString())));

                indoorBarEntries.add(new BarEntry(Float.parseFloat(dataSnapshot.child("index").getValue().toString()),
                        Float.parseFloat(dataSnapshot.child("value").getValue().toString())));

                dataSetIndoor = new LineDataSet(indoorEntries, "Indoor Temperature");
                dataSetIndoor.setColor(Color.rgb(0,107,230));
                dataSetIndoor.setCircleColor(Color.rgb(0,107,230));
                dataSetIndoor.setLineWidth(2f);
                dataSetIndoor.setCircleRadius(3f);
                dataSetIndoor.setFillAlpha(65);
                dataSetIndoor.setFillColor(Color.rgb(0,107,230));
                dataSetIndoor.setValueTextColor(Color.BLACK);

                dataSetOutdoor = new LineDataSet(outdoorEntries, "Outdoor Temperature");
                dataSetOutdoor.setColor(Color.rgb(255,153,0));
                dataSetOutdoor.setCircleColor(Color.rgb(255,153,0));
                dataSetOutdoor.setLineWidth(2f);
                dataSetOutdoor.setCircleRadius(3f);
                dataSetOutdoor.setFillAlpha(65);
                dataSetOutdoor.setFillColor(Color.rgb(255,153,0));
                dataSetOutdoor.setValueTextColor(Color.BLACK);


                lineChart.getData().notifyDataChanged();
                lineChart.notifyDataSetChanged();
                LineData lineData = new LineData(dataSetIndoor, dataSetOutdoor);
                lineChart.setData(lineData);
                lineChart.invalidate(); // refresh

                barDataSetIndoor = new BarDataSet(indoorBarEntries,"");
                barDataSetIndoor.setColor(Color.rgb(0,107,230));

                indoorBarChart.getData().notifyDataChanged();
                indoorBarChart.notifyDataSetChanged();
                BarData theData = new BarData(barDataSetIndoor);
                theData.setDrawValues(false);
                indoorBarChart.setData(theData);
                indoorBarChart.invalidate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



        outdoorRef.orderByChild("index").limitToLast(5).addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                outdoorEntries.add(new Entry(Float.parseFloat(dataSnapshot.child("index").getValue().toString()),
                        Float.parseFloat(dataSnapshot.child("value").getValue().toString())));

                outdoorBarEntries.add(new BarEntry(Float.parseFloat(dataSnapshot.child("index").getValue().toString()),
                        Float.parseFloat(dataSnapshot.child("value").getValue().toString())));

                dataSetIndoor = new LineDataSet(indoorEntries, "Indoor Temperature");
                dataSetIndoor.setColor(Color.rgb(0,107,230));
                dataSetIndoor.setCircleColor(Color.rgb(0,107,230));
                dataSetIndoor.setLineWidth(2f);
                dataSetIndoor.setCircleRadius(3f);
                dataSetIndoor.setFillAlpha(65);
                dataSetIndoor.setFillColor(Color.rgb(0,107,230));
                dataSetIndoor.setValueTextColor(Color.BLACK);

                dataSetOutdoor = new LineDataSet(outdoorEntries, "Outdoor Temperature");
                dataSetOutdoor.setColor(Color.rgb(255,153,0));
                dataSetOutdoor.setCircleColor(Color.rgb(255,153,0));
                dataSetOutdoor.setLineWidth(2f);
                dataSetOutdoor.setCircleRadius(3f);
                dataSetOutdoor.setFillAlpha(65);
                dataSetOutdoor.setFillColor(Color.rgb(255,153,0));
                dataSetOutdoor.setValueTextColor(Color.BLACK);


                lineChart.getData().notifyDataChanged();
                lineChart.notifyDataSetChanged();
                LineData lineData = new LineData(dataSetIndoor, dataSetOutdoor);
                lineChart.setData(lineData);
                lineChart.invalidate(); // refresh

                barDataSetOutdoor = new BarDataSet(outdoorBarEntries,"");
                barDataSetOutdoor.setColor(Color.rgb(255,153,0));

                outdoorBarChart.getData().notifyDataChanged();
                outdoorBarChart.notifyDataSetChanged();
                BarData theData = new BarData(barDataSetOutdoor);
                theData.setDrawValues(false);
                outdoorBarChart.setData(theData);
                outdoorBarChart.invalidate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        //----------------------------------------------------------------
        mBtnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempRemote;
                if(remoteNow < 37){
                    tempRemote = remoteNow+1;
                }
                else{
                    tempRemote = remoteNow;
                }

                Map<String, Object> childUpdates = new HashMap<>();
                String currentTime = dateFormat.format(Calendar.getInstance().getTime());

                childUpdates.put("index", remoteIdx+1);
                childUpdates.put("time", currentTime);
                childUpdates.put("value", String.valueOf(tempRemote));

                totalRef.setValue(total+1);
                remoteIdxRef.setValue(remoteIdx+1);
                remoteNowRef.setValue(String.valueOf(tempRemote));
                remoteRef.push().setValue(childUpdates);
            }
        });



        mBtnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempRemote;
                if(remoteNow > 17){
                    tempRemote = remoteNow-1;
                }
                else{
                    tempRemote = remoteNow;
                }

                Map<String, Object> childUpdates = new HashMap<>();
                String currentTime = dateFormat.format(Calendar.getInstance().getTime());

                childUpdates.put("index", remoteIdx+1);
                childUpdates.put("time", currentTime);
                childUpdates.put("value", String.valueOf(tempRemote));

                totalRef.setValue(total+1);
                remoteIdxRef.setValue(remoteIdx+1);
                remoteNowRef.setValue(String.valueOf(tempRemote));
                remoteRef.push().setValue(childUpdates);
            }
        });


        if (indoorBarChart.getData() != null && indoorBarChart.getData().getDataSetCount() > 0) {
            barDataSetIndoor = (BarDataSet) indoorBarChart.getData().getDataSetByIndex(0);
            barDataSetIndoor.setValues(indoorBarEntries);
            outdoorBarChart.getData().notifyDataChanged();
            outdoorBarChart.notifyDataSetChanged();

        } else {
            barDataSetIndoor = new BarDataSet(indoorBarEntries,"");
            barDataSetIndoor.setColor(Color.rgb(0,107,230));

            BarData theData = new BarData(barDataSetIndoor);
            theData.setDrawValues(false);
            indoorBarChart.setData(theData);
            indoorBarChart.invalidate();
        }
        XAxis xAxisIndoor = indoorBarChart.getXAxis();
        xAxisIndoor.setEnabled(false);
        indoorBarChart.getAxisLeft().setEnabled(false);
        indoorBarChart.getAxisRight().setEnabled(false);
        indoorBarChart.getDescription().setEnabled(false);
        indoorBarChart.getLegend().setEnabled(false);
        indoorBarChart.setTouchEnabled(false);
        indoorBarChart.getDescription().setEnabled(false);


        if (outdoorBarChart.getData() != null && outdoorBarChart.getData().getDataSetCount() > 0) {
            barDataSetOutdoor = (BarDataSet) outdoorBarChart.getData().getDataSetByIndex(0);
            barDataSetOutdoor.setValues(outdoorBarEntries);
            outdoorBarChart.getData().notifyDataChanged();
            outdoorBarChart.notifyDataSetChanged();

        } else {
            barDataSetOutdoor = new BarDataSet(outdoorBarEntries,"");
            barDataSetOutdoor.setColor(Color.rgb(255,153,0));

            BarData theData = new BarData(barDataSetOutdoor);
            theData.setDrawValues(false);
            outdoorBarChart.setData(theData);
            outdoorBarChart.invalidate();
        }
        XAxis xAxisOutdoor = outdoorBarChart.getXAxis();
        xAxisOutdoor.setEnabled(false);
        outdoorBarChart.getAxisLeft().setEnabled(false);
        outdoorBarChart.getAxisRight().setEnabled(false);
        outdoorBarChart.getDescription().setEnabled(false);
        outdoorBarChart.getLegend().setEnabled(false);
        outdoorBarChart.setTouchEnabled(false);
        outdoorBarChart.getDescription().setEnabled(false);


        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            dataSetIndoor = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            dataSetOutdoor = (LineDataSet) lineChart.getData().getDataSetByIndex(1);

            dataSetIndoor.setValues(indoorEntries);
            dataSetOutdoor.setValues(outdoorEntries);

            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            dataSetIndoor = new LineDataSet(indoorEntries, "Indoor Temperature");
            dataSetIndoor.setColor(Color.rgb(0,107,230));
            dataSetIndoor.setCircleColor(Color.rgb(0,107,230));
            dataSetIndoor.setLineWidth(2f);
            dataSetIndoor.setCircleRadius(3f);
            dataSetIndoor.setFillAlpha(65);
            dataSetIndoor.setFillColor(Color.rgb(0,107,230));
            dataSetIndoor.setValueTextColor(Color.BLACK);

            dataSetOutdoor = new LineDataSet(outdoorEntries, "Outdoor Temperature");
            dataSetOutdoor.setColor(Color.rgb(255,153,0));
            dataSetOutdoor.setCircleColor(Color.rgb(255,153,0));
            dataSetOutdoor.setLineWidth(2f);
            dataSetOutdoor.setCircleRadius(3f);
            dataSetOutdoor.setFillAlpha(65);
            dataSetOutdoor.setFillColor(Color.rgb(255,153,0));
            dataSetOutdoor.setValueTextColor(Color.BLACK);
            LineData data = new LineData(dataSetIndoor, dataSetOutdoor);
            lineChart.setData(data);
            lineChart.invalidate();
        }
//        lineChart.setTouchEnabled(false);
        lineChart.getDescription().setEnabled(false);
    }
}

