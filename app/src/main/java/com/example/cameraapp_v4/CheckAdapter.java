package com.example.cameraapp_v4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CheckAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> array_list;

    private ViewHolder mViewHolder;

    public CheckAdapter(Context mContext, ArrayList<String> array_list) {
        this.mContext = mContext;
        this.array_list = array_list;
    }

    @Override
    public int getCount() {
        return array_list.size();
    }

    @Override
    public Object getItem(int position) {
        return array_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder 패턴
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.check_layout_item, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        // View 에 Data 세팅
        mViewHolder.txt_name.setText(array_list.get(position));

        return convertView;
    }

    public class ViewHolder {
        private TextView txt_name;

        public ViewHolder(View convertView) {
            txt_name = (TextView) convertView.findViewById(R.id.txt_name);
        }
    }
}
