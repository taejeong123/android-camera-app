package com.example.cameraapp_v4;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class CheckActivity extends AppCompatActivity {

    private TextView folder_id, folder_mode, img_cnt, omission_cnt;
    private ListView img_list, omission_list;
    private LinearLayout omission_layout;

    private String front_name;
    private String id;
    private int MODE;
    private boolean distance;
    private boolean isDegree4;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_check_portrait);
        } else {
            setContentView(R.layout.activity_check_landscape);
        }

        folder_id = (TextView) findViewById(R.id.folder_id);
        folder_mode = (TextView) findViewById(R.id.folder_mode);
        img_cnt = (TextView) findViewById(R.id.img_cnt);
        omission_cnt = (TextView) findViewById(R.id.omission_cnt);

        img_list = (ListView) findViewById(R.id.img_list);
        omission_list = (ListView) findViewById(R.id.omission_list);

        omission_layout = (LinearLayout) findViewById(R.id.omission_layout);

        Intent checkIntent = getIntent();
        front_name = checkIntent.getStringExtra("front_name");
        id = checkIntent.getStringExtra("id");
        MODE = checkIntent.getIntExtra("mode", 0);
        distance = checkIntent.getBooleanExtra("distance", false);
        isDegree4 = checkIntent.getBooleanExtra("isDegree4", false);

        folder_id.setText("ID: " + id);
        if (MODE == 0) {
            folder_mode.setText("Mode: Picture");
            omission_layout.setVisibility(View.VISIBLE);
        } else if (MODE == 1) {
            folder_mode.setText("Mode: Video");
            omission_layout.setVisibility(View.GONE);
        }

        getList();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getList() {
        String mode = "";
        String fileStr = "";
        if (MODE == 0) {
            fileStr = getFileList(Environment.DIRECTORY_PICTURES + "/" + id);
            mode = "Picture";
        } else if (MODE == 1) {
            fileStr = getFileList(Environment.DIRECTORY_MOVIES + "/" + id);
            mode = "Video";
        }

        if (fileStr == null) { return; }
        String[] splitFileStr = fileStr.substring(1, fileStr.length()).split("\n");

        // get omission images
        String[] checkFolderList = new String[0];
        if (!distance && !isDegree4) {
            // 2m under, no degree4
            checkFolderList = FoldersList.li1;
        } else if (distance && !isDegree4) {
            // 2m over, no degree4
            checkFolderList = FoldersList.li2;
        } else if (!distance && isDegree4) {
            // 2m under, degree4
            checkFolderList = FoldersList.li3;
        } else if (distance && isDegree4) {
            // 2m over, no degree4
            checkFolderList = FoldersList.li4;
        }

        ArrayList<String> sameNameList = new ArrayList<>();
        ArrayList<String> checkFolderArrayList = new ArrayList<>(Arrays.asList(checkFolderList));
        ArrayList<String> omissionList = new ArrayList<>();

        for (int i = 0; i < checkFolderArrayList.size(); i++) {
            for (int j = 0; j < splitFileStr.length; j++) {
                String front = front_name + "_" + checkFolderArrayList.get(i).split(" ")[0];
                String back = checkFolderArrayList.get(i).split(" ")[1];
                if (splitFileStr[j].contains(front) && splitFileStr[j].contains(back)) { sameNameList.add(checkFolderArrayList.get(i)); }
            }
        }

        for (String item1 : checkFolderArrayList) {
            boolean flag = false;
            for (String item2 : sameNameList) {
                if (item1.equals(item2)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) { omissionList.add(item1); }
        }

        // show image check info
        img_cnt.setText("찍은 이미지 수: " + splitFileStr.length);
        omission_cnt.setText("누락 이미지 수: " + omissionList.size());

        ArrayList<String> imgList = new ArrayList<>(Arrays.asList(splitFileStr));
        CheckAdapter checkImageAdapter = new CheckAdapter(getApplicationContext(), imgList);
        img_list.setAdapter(checkImageAdapter);

        CheckAdapter checkOmissionAdapter = new CheckAdapter(getApplicationContext(), omissionList);
        omission_list.setAdapter(checkOmissionAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getFileList(String dir) {
        String path = getExternalFilesDir(dir).toString();
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (files.length == 0) {
            Functions.displayMessage(getBaseContext(), "Empty folder");
            try {
                Files.deleteIfExists(directory.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        String result = "";
        for (int i = 0; i < files.length; i++) {
            String str = files[i].getName();
            if (!distance && !isDegree4) {
                // 2m under, no degree4
                if (str.contains("2m4m") || str.contains("Degree4")) { continue; }
            } else if (distance && !isDegree4) {
                // 2m over, no degree4
                if (str.contains("1m2m") || str.contains("Degree4")) { continue; }
            } else if (!distance && isDegree4) {
                // 2m under, degree4
                if (str.contains("2m4m")) { continue; }
            } else if (distance && isDegree4) {
                // 2m over, no degree4
                if (str.contains("1m2m")) { continue; }
            }
            result = result + "\n" + str;
        }

        return result;
    }
}