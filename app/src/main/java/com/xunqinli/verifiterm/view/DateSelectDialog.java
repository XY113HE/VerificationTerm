package com.xunqinli.verifiterm.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.databinding.DialogDateselectBinding;
import com.xunqinli.verifiterm.interf.DateSelectListener;
import com.xunqinli.verifiterm.utils.Tools;

public class DateSelectDialog extends Dialog {

    public DateSelectDialog(Context context) {
        super(context);
    }

    public DateSelectDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DateSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder{
        private Context context;
        private DateSelectListener listener;
        private DateSelectDialog dialog;
        private DialogDateselectBinding binding;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setListener(DateSelectListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setDialog(DateSelectDialog dialog) {
            this.dialog = dialog;
            return this;
        }

        public Builder setBinding(){
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_dateselect, null, false);
            return this;
        }

        public void show(){
            dialog = new DateSelectDialog(context, R.style.UpdateDialog);
            initClicks();
            dialog.setContentView(binding.getRoot(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        private void initClicks() {
            for (int i = 0; i < 10; i++) {
                final int finalI = i;
                getDateView(i).setText(Tools.getDay(Tools.getSpecialNowValue()-i));
                getDateView(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.click(Tools.getSpecialNowValue()- finalI);
                        dialog.dismiss();
                    }
                });
            }
            binding.dateAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.clickAll();
                    dialog.dismiss();
                }
            });
        }

        private TextView getDateView(int i) {
            switch (i){
                case 0:
                    return binding.date0;
                case 1:
                    return binding.date1;
                case 2:
                    return binding.date2;
                case 3:
                    return binding.date3;
                case 4:
                    return binding.date4;
                case 5:
                    return binding.date5;
                case 6:
                    return binding.date6;
                case 7:
                    return binding.date7;
                case 8:
                    return binding.date8;
                case 9:
                    return binding.date9;
            }
            return null;
        }

    }
}
