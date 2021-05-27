package com.example.cameraapp_v4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rGroup1_acc, rGroup2_roll, rGroup3_distance, rGroup4_degree4;
    private RadioButton acc_normal, acc_sunglasses, acc_hat;
    private RadioButton roll_front, roll_tilt;
    private RadioButton distance_under, distance_over;
    private RadioButton isDegree4_y, isDegree4_n;

    private EditText id_cnt;
    private LinearLayout id_layout;

    private Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main_portrait);

        } else {
            setContentView(R.layout.activity_main_landscape);
        }

        // set radio group
        rGroup1_acc = (RadioGroup) findViewById(R.id.rGroup1_acc);
        rGroup2_roll = (RadioGroup) findViewById(R.id.rGroup2_roll);
        rGroup3_distance = (RadioGroup) findViewById(R.id.rGroup3_distance);
        rGroup4_degree4 = (RadioGroup) findViewById(R.id.rGroup4_degree4);

        // set radio button
        acc_normal = (RadioButton) findViewById(R.id.acc_normal);
        acc_sunglasses = (RadioButton) findViewById(R.id.acc_sunglasses);
        acc_hat = (RadioButton) findViewById(R.id.acc_hat);
        roll_front = (RadioButton) findViewById(R.id.roll_front);
        roll_tilt = (RadioButton) findViewById(R.id.roll_tilt);
        distance_under = (RadioButton) findViewById(R.id.distance_under);
        distance_over = (RadioButton) findViewById(R.id.distance_over);
        isDegree4_y = (RadioButton) findViewById(R.id.isDegree4_y);
        isDegree4_n = (RadioButton) findViewById(R.id.isDegree4_n);

        // set id
        id_layout = (LinearLayout) findViewById(R.id.id_layout);
        id_cnt = (EditText) findViewById(R.id.id_cnt);
        id_cnt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void afterTextChanged(Editable s) {
                if (id_cnt.getText().toString().equals("")) {
                    id_layout.removeAllViews();
                    return;
                }
                int num = Integer.parseInt(id_cnt.getText().toString());
                if (num < 1 || num > 4) {
                    id_layout.removeAllViews();
                    return;
                }

                id_layout.removeAllViews();
                for (int i = 0; i < num; i++) {
                    EditText new_id = new EditText(getApplicationContext());
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                    new_id.setLayoutParams(p);
                    new_id.setGravity(Gravity.CENTER);
                    new_id.setInputType(InputType.TYPE_CLASS_NUMBER);
                    new_id.setHint("id " + (i + 1));
                    new_id.setId(i);
                    id_layout.addView(new_id);
                }
            }
        });

        // set next button
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go next
                Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
                String info = getInfo();
                if (info.equals("")) {
                    Functions.displayMessage(getBaseContext(), "empty value");
                    return;
                }
                detailIntent.putExtra("id", Functions.getId(id_layout));
                detailIntent.putExtra("name", info);
                detailIntent.putExtra("distance", getDistanceInfo());
                detailIntent.putExtra("isDegree4", getIsDegree4());

                startActivity(detailIntent);
            }
        });
    }

    private String getInfo() {
        String id = "", acc = "", roll = "";
        id = Functions.getId(id_layout);

        switch (rGroup1_acc.getCheckedRadioButtonId()) {
            case R.id.acc_normal:
                acc = "Normal";
                break;
            case R.id.acc_hat:
                acc = "Hat";
                break;
            case R.id.acc_sunglasses:
                acc = "Sunglasses";
                break;
        }

        switch (rGroup2_roll.getCheckedRadioButtonId()) {
            case R.id.roll_front:
                roll = "F";
                break;
            case R.id.roll_tilt:
                roll = "T";
                break;
        }

        if (id.equals("") || acc.equals("") || roll.equals("")) { return ""; }
        return id + "_" + acc + "_" + roll;
    }

    private boolean getDistanceInfo() {
        switch (rGroup3_distance.getCheckedRadioButtonId()) {
            case R.id.distance_under:
                return false;
            case R.id.distance_over:
                return true;
        }
        return false;
    }

    private boolean getIsDegree4() {
        switch (rGroup4_degree4.getCheckedRadioButtonId()) {
            case R.id.isDegree4_n:
                return false;
            case R.id.isDegree4_y:
                return true;
        }
        return false;
    }
}