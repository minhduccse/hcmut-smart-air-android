package com.minhduc.smartair;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTempValue,mIndoorTempValue,mOutdoorTempValue,mIndex;
    private ImageButton mUp,mDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTempValue = (TextView) findViewById(R.id.temp_value);
        mIndoorTempValue = (TextView) findViewById(R.id.indoor_temp_value);
        mOutdoorTempValue = (TextView) findViewById(R.id.outdoor_temp_value);
        mIndex = (TextView) findViewById(R.id.record_value);
        mDown = (ImageButton) findViewById(R.id.but_down);
        mUp = (ImageButton) findViewById(R.id.but_up);
    }
}
