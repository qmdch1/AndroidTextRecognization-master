package me.shuza.textrecognization;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class getLicense extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        Intent intent = getIntent();
        String data = intent.getStringExtra("license");

        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(data);
    }
}
