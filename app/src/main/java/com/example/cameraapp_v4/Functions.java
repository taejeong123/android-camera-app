package com.example.cameraapp_v4;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Functions {

    public static String getId(LinearLayout id_layout) {
        String n = "";
        for (int i = 0; i < id_layout.getChildCount(); i++) {
            if (id_layout.getChildAt(i) instanceof EditText) {
                String id = ((EditText) id_layout.getChildAt(i)).getText().toString();
                if (id.equals("")) {
                    return "";
                }
                n += id + "_";
            }
        }
        return n.replace("_", " ").trim().replace(" ", "_");
    }

    public static void displayMessage(Context context, String message) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}