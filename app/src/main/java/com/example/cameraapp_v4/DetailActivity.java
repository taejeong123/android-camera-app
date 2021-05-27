package com.example.cameraapp_v4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView name, rGroup1_text, rGroup2_text, rGroup3_text, rGroup4_text, rGroup5_text;

    private RadioGroup rGroup1_degree, rGroup1_degree1_state, rGroup1_degree2_state, rGroup1_degree3_state, rGroup1_degree4_state;
    private RadioGroup rGroup2_mask, rGroup3_distance, rGroup4_yaw, rGroup5_pitch;

    private RadioButton degree1, degree2, degree3, degree4;
    private RadioButton degree1_10, degree1_09, degree1_08, degree2_07, degree2_06, degree2_05, degree3_04, degree3_03, degree3_02, degree4_01, degree4_00;
    private RadioButton mask_white, mask_multi;
    private RadioButton distance1, distance2, distance3, distance4;
    private RadioButton yaw_90, yaw_45, yaw_30, yaw_0;
    private RadioButton pitch_front, pitch_under, pitch_over;

    private Button btn_picture, btn_video;
    private Button btn_check, btn_camera;

    // ------------------------------------------------------------------------------

    private int MODE = 0; // 0: picture, 1: video

    private String id;
    private String front_name;
    private boolean distance;
    private boolean isDegree4;

    private File photoFile = null;
    private File videoFile = null;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    static final int CAPTURE_VIDEO_REQUEST = 2;
    private String AUTHORITY_FILEPROVIDER = "com.example.cameraapp_v4.fileprovider";
    private String mCurrentPicturePath;
    private String mCurrentVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_detail_portrait);
        } else {
            setContentView(R.layout.activity_detail_landscape);
        }

        // set text
        name = (TextView) findViewById(R.id.name);
        rGroup1_text = (TextView) findViewById(R.id.rGroup1_text);
        rGroup2_text = (TextView) findViewById(R.id.rGroup2_text);
        rGroup3_text = (TextView) findViewById(R.id.rGroup3_text);
        rGroup4_text = (TextView) findViewById(R.id.rGroup4_text);
        rGroup5_text = (TextView) findViewById(R.id.rGroup5_text);

        // set radio group
        rGroup1_degree = (RadioGroup) findViewById(R.id.rGroup1_degree);
        rGroup1_degree1_state = (RadioGroup) findViewById(R.id.rGroup1_degree1_state);
        rGroup1_degree2_state = (RadioGroup) findViewById(R.id.rGroup1_degree2_state);
        rGroup1_degree3_state = (RadioGroup) findViewById(R.id.rGroup1_degree3_state);
        rGroup1_degree4_state = (RadioGroup) findViewById(R.id.rGroup1_degree4_state);
        rGroup2_mask = (RadioGroup) findViewById(R.id.rGroup2_mask);
        rGroup3_distance = (RadioGroup) findViewById(R.id.rGroup3_distance);
        rGroup4_yaw = (RadioGroup) findViewById(R.id.rGroup4_yaw);
        rGroup5_pitch = (RadioGroup) findViewById(R.id.rGroup5_pitch);

        setRadioVisiblility(View.GONE);
        rGroup1_degree.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.degree1:
                        setRadioVisiblility(View.GONE);
                        rGroup1_degree1_state.setVisibility(View.VISIBLE);
                        break;
                    case R.id.degree2:
                        setRadioVisiblility(View.GONE);
                        rGroup1_degree2_state.setVisibility(View.VISIBLE);
                        break;
                    case R.id.degree3:
                        setRadioVisiblility(View.GONE);
                        rGroup1_degree3_state.setVisibility(View.VISIBLE);
                        break;
                    case R.id.degree4:
                        setRadioVisiblility(View.GONE);
                        rGroup1_degree4_state.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        // set radio button
        degree1 = (RadioButton) findViewById(R.id.degree1);
        degree2 = (RadioButton) findViewById(R.id.degree2);
        degree3 = (RadioButton) findViewById(R.id.degree3);
        degree4 = (RadioButton) findViewById(R.id.degree4);
        distance1 = (RadioButton) findViewById(R.id.distance1);
        distance2 = (RadioButton) findViewById(R.id.distance2);
        distance3 = (RadioButton) findViewById(R.id.distance3);
        distance4 = (RadioButton) findViewById(R.id.distance4);

        // set button
        btn_picture = (Button) findViewById(R.id.btn_picture);
        btn_video = (Button) findViewById(R.id.btn_video);
        btn_check = (Button) findViewById(R.id.btn_check);
        btn_camera = (Button) findViewById(R.id.btn_camera);

        btn_picture.setOnClickListener(this);
        btn_video.setOnClickListener(this);
        btn_check.setOnClickListener(this);
        btn_camera.setOnClickListener(this);

        btn_video.setVisibility(View.GONE);

        // get intent from MainActivity
        Intent detailIntent = getIntent();
        id = detailIntent.getStringExtra("id");
        front_name = detailIntent.getStringExtra("name");
        distance = detailIntent.getBooleanExtra("distance", false);
        isDegree4 = detailIntent.getBooleanExtra("isDegree4", false);
        name.setText(front_name);

        if (distance) {
            // true - 2m Over
            distance4.setVisibility(View.VISIBLE);
            distance3.setVisibility(View.GONE);
            distance3.setChecked(false);
        } else {
            // false - 2m Under
            distance3.setVisibility(View.VISIBLE);
            distance4.setVisibility(View.GONE);
            distance4.setChecked(false);
        }

        if (isDegree4) {
            // true - degree4
            degree4.setVisibility(View.VISIBLE);
        } else {
            // false - not degree4
            degree4.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_picture) {
            // picture -> video
            name.setText(id);

            MODE = 1;
            btn_video.setVisibility(View.VISIBLE);
            btn_picture.setVisibility(View.GONE);

            setVideoRadioButton(View.GONE);
        } else if (v == btn_video) {
            // video -> picture
            name.setText(front_name);

            MODE = 0;
            btn_video.setVisibility(View.GONE);
            btn_picture.setVisibility(View.VISIBLE);

            setVideoRadioButton(View.VISIBLE);
        } else if (v == btn_check) {
            // open check activity
            Intent checkIntent = new Intent(getApplicationContext(), CheckActivity.class);
            checkIntent.putExtra("front_name", front_name);
            checkIntent.putExtra("id", id);
            checkIntent.putExtra("mode", MODE);
            checkIntent.putExtra("distance", distance);
            checkIntent.putExtra("isDegree4", isDegree4);
            startActivity(checkIntent);
        } else if (v == btn_camera) {
            // open camera
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String filename = getFileName();
                if (filename.equals("")) {
                    Functions.displayMessage(getBaseContext(), "empty value");
                    return;
                }

                if (MODE == 0) { captureImage(filename); }
                else if (MODE == 1) { recordVideo(filename); }
            } else {
                Functions.displayMessage(getBaseContext(), "not allowed version");
            }
        }
    }

    public void setVideoRadioButton(int v) {
        if (v == View.GONE) {
            rGroup1_degree.clearCheck();
            rGroup1_degree1_state.setVisibility(v);
            rGroup1_degree2_state.setVisibility(v);
            rGroup1_degree3_state.setVisibility(v);
            rGroup1_degree4_state.setVisibility(v);
        }

        rGroup1_text.setVisibility(v);
        rGroup1_degree.setVisibility(v);
        rGroup2_text.setVisibility(v);
        rGroup2_mask.setVisibility(v);
        rGroup4_text.setVisibility(v);
        rGroup4_yaw.setVisibility(v);
        rGroup5_text.setVisibility(v);
        rGroup5_pitch.setVisibility(v);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage(getFileName());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            mCurrentPicturePath = renameFile(mCurrentPicturePath, ".jpg");
            if (checkIfExists(Environment.DIRECTORY_PICTURES + "/" + id)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("중복된 파일명이 있습니다.");
                builder.setCancelable(false);
                builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { moveFile(mCurrentPicturePath, getExternalFilesDir(Environment.DIRECTORY_PICTURES)); }
                }).setNegativeButton("저장안함", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { deleteTempFile(); }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else { moveFile(mCurrentPicturePath, getExternalFilesDir(Environment.DIRECTORY_PICTURES)); }
        } else if (requestCode == CAPTURE_VIDEO_REQUEST && resultCode == RESULT_OK) {
            mCurrentVideoPath = renameFile(mCurrentVideoPath, ".mp4");
            if (checkIfExists(Environment.DIRECTORY_MOVIES + "/" + id)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("중복된 파일명이 있습니다.");
                builder.setCancelable(false);
                builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { moveFile(mCurrentVideoPath, getExternalFilesDir(Environment.DIRECTORY_MOVIES)); }
                }).setNegativeButton("저장안함", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { deleteTempFile(); }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else { moveFile(mCurrentVideoPath, getExternalFilesDir(Environment.DIRECTORY_MOVIES)); }
        } else {
            Functions.displayMessage(getBaseContext(), "Request cancelled or something went wrong.");
            deleteTempFile();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void deleteTempFile() {
        Path path;
        if (MODE == 0) { path = Paths.get(mCurrentPicturePath); }
        else { path = Paths.get(mCurrentVideoPath); }

        try { Files.deleteIfExists(path); }
        catch (IOException e) { e.printStackTrace(); }
    }

    private boolean checkIfExists(String root) {
        String path = getExternalFilesDir(root).toString();
        File directory = new File(path);
        File[] files = directory.listFiles();

        boolean flag = false;
        for (int i = 0; i < files.length; i++) {
            String str = files[i].getName();
            if (str.contains(getFileName())) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String renameFile(String root, String ext) {
        Log.e("", root + "================================== root ================================");
        String fileStr = root.replace(root.substring(root.lastIndexOf("_"), root.length()), "");
        File filePre = new File(root);
        File fileNow = new File(fileStr + ext);

        filePre.renameTo(fileNow);
        return fileNow.getAbsolutePath();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void moveFile(String root, File dest) {
        File storageDir = new File(dest + "/" + id);
        if (!storageDir.exists()) { storageDir.mkdirs(); }

        Path source = Paths.get(root);
        Path target = Paths.get(storageDir.getAbsolutePath() + "/" + source.getFileName());

        try {
            Files.move(source, target);
            Functions.displayMessage(getBaseContext(), "Saved Success");
        } catch (IOException e) {
            e.printStackTrace();
            Functions.displayMessage(getBaseContext(), "Saved Failed. Please try again");
        }
    }

    private void captureImage(String filename) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    photoFile = createFile(filename);
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this, AUTHORITY_FILEPROVIDER, photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Functions.displayMessage(getBaseContext(),"Null");
            }
        }
    }

    private void recordVideo(String filename) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        } else {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    videoFile = createFile(filename);
                    if (videoFile != null) {
                        Uri videoURI = FileProvider.getUriForFile(this, AUTHORITY_FILEPROVIDER, videoFile);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                        startActivityForResult(takeVideoIntent, CAPTURE_VIDEO_REQUEST);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Functions.displayMessage(getBaseContext(),"Null");
            }
        }
    }

    private File createFile(String filename) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String new_fileName = filename + "_" + timeStamp + "_";

        File storageDir;
        File new_file;
        String ext = "";

        if (MODE == 0) {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            ext = ".jpg";
        } else {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            ext = ".mp4";
        }

        new_file = File.createTempFile(new_fileName, ext, storageDir);
        if (ext.equals(".jpg")) {
            mCurrentPicturePath = new_file.getAbsolutePath();
        } else if (ext.equals(".mp4")) {
            mCurrentVideoPath = new_file.getAbsolutePath();
        }
        return new_file;
    }

    private void setRadioVisiblility(int v) {
        rGroup1_degree1_state.setVisibility(v);
        rGroup1_degree2_state.setVisibility(v);
        rGroup1_degree3_state.setVisibility(v);
        rGroup1_degree4_state.setVisibility(v);

        rGroup1_degree1_state.clearCheck();
        rGroup1_degree2_state.clearCheck();
        rGroup1_degree3_state.clearCheck();
        rGroup1_degree4_state.clearCheck();
    }

    private String getFileName() {
        String state = "", mask = "", distance = "", yaw = "", pitch = "";

        switch (rGroup3_distance.getCheckedRadioButtonId()) {
            case R.id.distance1:
                distance = "0.2m0.5m";
                break;
            case R.id.distance2:
                distance = "0.5m1m";
                break;
            case R.id.distance3:
                distance = "1m2m";
                break;
            case R.id.distance4:
                distance = "2m4m";
                break;
        }

        if (MODE == 0) {
            switch (rGroup1_degree.getCheckedRadioButtonId()) {
                case R.id.degree1:
                    switch (rGroup1_degree1_state.getCheckedRadioButtonId()) {
                        case R.id.degree1_10:
                            state = "Degree1_1.0";
                            break;
                        case R.id.degree1_09:
                            state = "Degree1_0.9";
                            break;
                        case R.id.degree1_08:
                            state = "Degree1_0.8";
                            break;
                    }
                    break;
                case R.id.degree2:
                    switch (rGroup1_degree2_state.getCheckedRadioButtonId()) {
                        case R.id.degree2_07:
                            state = "Degree2_0.7";
                            break;
                        case R.id.degree2_06:
                            state = "Degree2_0.6";
                            break;
                        case R.id.degree2_05:
                            state = "Degree2_0.5";
                            break;
                    }
                    break;
                case R.id.degree3:
                    switch (rGroup1_degree3_state.getCheckedRadioButtonId()) {
                        case R.id.degree3_04:
                            state = "Degree3_0.4";
                            break;
                        case R.id.degree3_03:
                            state = "Degree3_0.3";
                            break;
                        case R.id.degree3_02:
                            state = "Degree3_0.2";
                            break;
                    }
                    break;
                case R.id.degree4:
                    switch (rGroup1_degree4_state.getCheckedRadioButtonId()) {
                        case R.id.degree4_01:
                            state = "Degree4_0.1";
                            break;
                        case R.id.degree4_00:
                            state = "Degree4_0.0";
                            break;
                    }
                    break;
            }

            switch (rGroup2_mask.getCheckedRadioButtonId()) {
                case R.id.mask_white:
                    mask = "White";
                    break;
                case R.id.mask_multi:
                    mask = "Multi";
                    break;
            }

            switch (rGroup4_yaw.getCheckedRadioButtonId()) {
                case R.id.yaw_90:
                    yaw = "90";
                    break;
                case R.id.yaw_45:
                    yaw = "45";
                    break;
                case R.id.yaw_30:
                    yaw = "30";
                    break;
                case R.id.yaw_0:
                    yaw = "0";
                    break;
            }

            switch (rGroup5_pitch.getCheckedRadioButtonId()) {
                case R.id.pitch_under:
                    pitch = "Down";
                    break;
                case R.id.pitch_front:
                    pitch = "Front";
                    break;
                case R.id.pitch_over:
                    pitch = "Up";
                    break;
            }
            if (state.equals("") || mask.equals("") || distance.equals("") || yaw.equals("") || pitch.equals("")) { return ""; }
            return front_name + "_" + state + "_" + mask + "_" + distance + "_" + yaw + "_" + pitch;
        }
        if (distance.equals("")) { return ""; }
        return id + "_" + distance;
    }
}