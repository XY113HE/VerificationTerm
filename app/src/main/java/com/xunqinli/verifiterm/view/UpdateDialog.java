package com.xunqinli.verifiterm.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.databinding.DialogUpdateBinding;

public class UpdateDialog extends Dialog {
    public UpdateDialog(Context context) {
        super(context);
    }

    public UpdateDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected UpdateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder{
        private String mode = "0";//默认"0" 确认升级  "1" 更新中
        private Context context;
        private DialogUpdateBinding updateBinding;
        private OnClickListener listener;
        private UpdateDialog dialog;

        private Builder setMode(String mode){
            this.mode = mode;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setUpdateBinding() {
            this.updateBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_update, null, false);
            return this;
        }

        public Builder setListener(OnClickListener listener) {
            this.listener = listener;
            return this;
        }

        public void show(){
            dialog = new UpdateDialog(context, R.style.UpdateDialog);
            updateBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    updateBinding.confirmLayout.setVisibility(View.GONE);
//                    updateBinding.updatingLayout.setVisibility(View.VISIBLE);
                    listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    dialog.dismiss();
                }
            });
            updateBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    listener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    dialog.dismiss();
                }
            });
            updateBinding.updateProgress.setProgress(0);
            dialog.setContentView(updateBinding.getRoot(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        public void setMax(int max){
            updateBinding.updateProgress.setMax(max);
        }

        public void setProgress(int progress){
            updateBinding.updateProgress.setProgress(progress);
        }

    }
}
