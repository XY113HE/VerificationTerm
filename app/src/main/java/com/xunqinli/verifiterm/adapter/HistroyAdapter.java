package com.xunqinli.verifiterm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.databinding.ItemHistroyViewBinding;
import com.xunqinli.verifiterm.model.VerificationNotifyBean;

import java.util.List;

public class HistroyAdapter extends RecyclerView.Adapter<HistroyAdapter.ViewHolder> {
    private ItemHistroyViewBinding mItemBinding;
    private Context mContext;
    private List<VerificationNotifyBean> data;

    public HistroyAdapter(Context mContext, List<VerificationNotifyBean> data){
        this.mContext = mContext;
        this.data = data;
    }

    public void setData(List<VerificationNotifyBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mItemBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_histroy_view, parent, false);
        return new ViewHolder(mItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ItemHistroyViewBinding itemHistroyViewBinding;
        private ViewHolder(ItemHistroyViewBinding itemHistroyViewBinding) {
            super(itemHistroyViewBinding.getRoot());
            this.itemHistroyViewBinding = itemHistroyViewBinding;
        }

        private void bindData(int position){
            itemHistroyViewBinding.name.setText(data.get(position).getName());
            itemHistroyViewBinding.car.setText(data.get(position).getCarNum());
            itemHistroyViewBinding.number.setText(data.get(position).getNum()+"");
            itemHistroyViewBinding.phone.setText(data.get(position).getPhone());
            itemHistroyViewBinding.time.setText(data.get(position).getDateTime());
        }
    }
}
