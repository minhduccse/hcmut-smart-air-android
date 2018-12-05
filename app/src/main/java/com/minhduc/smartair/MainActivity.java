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
    private int indoorNow;
    private int outdoorNow;
    private int remoteNow;
    private int remoteIdx;

    private TextView mTotalValue, mRemoteValue, mIndoorTempValue, mOutdoorTempValue;
    private TextView mTempPercentUp,mTempPercentDown,mPercentUp,mPercentDown;
    private TextView mInPercentUp,mInPercentDown,mPercentInUp,mPercentInDown;
    private TextView mOutPercentUp,mOutPercentDown,mPercentOutUp,mPercentOutDown;

    private ImageView mTempDown, mTempUp, mInUp, mInDown, mOutUp, mOutDown;

    public ImageButton mBtnUp, mBtnDown;

    private DatabaseReference totalRef, indoorNowRef, outdoorNowRef, remoteNowRef;
    private DatabaseReference remoteIdxRef, remoteRef, indoorRef, outdoorRef;

    LineDataSet dataSetIndoor, dataSetOutdoor;
    BarChart indoorBarChart, outdoorBarChart;
    LineChart lineChart;

    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    List<Entry> indoorEntries = new ArrayList<Entry>();
    List<Entry> outdoorEntries = new ArrayList<Entry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRemoteValue = (TextView) findViewById(R.id.remote_value);
        mIndoorTempValue = (TextView) findViewById(R.id.indoor_temp_value);
        mOutdoorTempValue = (TextView) findViewById(R.id.outdoor_temp_value);
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
        mInPercentUp = (TextView) findViewById(R.id.in_percent_up);
        mInPercentDown = (TextView) findViewById(R.id.in_percent_down);
        mPercentInUp = (TextView) findViewById(R.id.syl_in_up);
        mPercentInDown = (TextView) findViewById(R.id.syl_in_down);
        mInDown = (ImageView) findViewById(R.id.imageInDown);
        mInUp = (ImageView) findViewById(R.id.imageInUp);

        //------------------------outdoor------------------------
        mOutPercentUp = (TextView) findViewById(R.id.out_percent_up);
        mOutPercentDown = (TextView) findViewById(R.id.out_percent_down);
        mPercentOutUp = (TextView) findViewById(R.id.syl_out_up);
        mPercentOutDown = (TextView) findViewById(R.id.syl_out_down);
        mOutDown = (ImageView) findViewById(R.id.imageOutDown);
        mOutUp = (ImageView) findViewById(R.id.imageOutUp);

        //-------------------------------------------------------
        mTempDown.setVisibility(View.INVISIBLE);
        mTempUp.setVisibility(View.INVISIBLE);
        mInDown.setVisibility(View.INVISIBLE);
        mInUp.setVisibility(View.INVISIBLE);
        mOutDown.setVisibility(View.INVISIBLE);
        mOutUp.setVisibility(View.INVISIBLE);

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

        remoteRef = FirebaseDatabase.getInstance().getReference().child("remote");
        indoorRef = FirebaseDatabase.getInstance().getReference().child("indoor");
        outdoorRef = FirebaseDatabase.getInstance().getReference().child("outdoor");




        //---------------------------------------------------------
        indoorNowRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                indoorNow = Integer.parseInt(dataSnapshot.getValue().toString());
                mIndoorTempValue.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError){}
        });

        outdoorNowRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                outdoorNow = Integer.parseInt(dataSnapshot.getValue().toString());
                mOutdoorTempValue.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        remoteNowRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                remoteNow = Integer.parseInt(dataSnapshot.getValue().toString());
                mRemoteValue.setText(dataSnapshot.getValue().toString());
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

