package com.xunqinli.verifiterm.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xunqinli.verifiterm.R;

import java.util.List;

public class WifiListAdapter extends BaseAdapter {
    private List<ScanResult> wifiInfo;
    private Context context;

    public WifiListAdapter(List<ScanResult> wifiInfo, Context context){
        this.wifiInfo = wifiInfo;
        this.context = context;

    }

    public void setWifiInfo(List<ScanResult> wifiInfo) {
        this.wifiInfo = wifiInfo;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return wifiInfo == null ? 0 : wifiInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_wifi_name_view, parent, false);
        ((TextView)itemView.findViewById(R.id.item_wifiname)).setText(wifiInfo.get(position).SSID);
        return itemView;
    }
}
