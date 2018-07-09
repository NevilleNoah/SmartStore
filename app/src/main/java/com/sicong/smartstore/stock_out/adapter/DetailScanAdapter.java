package com.sicong.smartstore.stock_out.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sicong.smartstore.R;
import com.sicong.smartstore.stock_in.data.model.Cargo;
import com.sicong.smartstore.stock_out.view.DetailActivity;

import java.util.List;
import java.util.Map;

public class DetailScanAdapter extends RecyclerView.Adapter {

    private static final String TAG = "StockOutListAdapter";
    private Context mContext;
    private List<String> mList;

    public DetailScanAdapter(@NonNull Context mContext, @NonNull List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_stock_out, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ViewHolder view = (ViewHolder) holder;
        view.id.setText(String.valueOf(position+1));
        view.rfid.setText(mList.get(position));
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    //该适配使用的ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView id;//扫描的顺序编号的视图
        TextView rfid;//rfid码的视图

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.item_stock_out_id);
            rfid = (TextView) itemView.findViewById(R.id.item_stock_out_date);
        }
    }

    public void insert(String str) {
        mList.add(str);
        notifyItemInserted(mList.size() - 1);
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }


}
