package com.xunqinli.verifiterm.view;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xunqinli.verifiterm.R;
import com.xunqinli.verifiterm.adapter.HistroyAdapter;
import com.xunqinli.verifiterm.databinding.ActivityHistoryBinding;
import com.xunqinli.verifiterm.interf.HistroyInterf;
import com.xunqinli.verifiterm.model.VerificationModeBean;
import com.xunqinli.verifiterm.model.VerificationNotifyBean;
import com.xunqinli.verifiterm.sql.MySQLiteHelper;
import com.xunqinli.verifiterm.viewmodel.HistroyVM;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity implements HistroyInterf.MainView {
    private ActivityHistoryBinding mBinding;
    private HistroyVM mHistroyVM;
    private HistroyAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        initAdapter();
        initSql();
    }

    private void initSql() {
        MySQLiteHelper helper = new MySQLiteHelper(this);
        List<VerificationNotifyBean> list = helper.searchRecord();
       }

    private void initAdapter() {
        adapter = new HistroyAdapter(this, null);
        mBinding.historyRecyclerView.setAdapter(adapter);
        mBinding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_history);
        mHistroyVM = new HistroyVM(this, mBinding);
    }

    @Override
    public BaseActivity getActivity() {
        return HistoryActivity.this;
    }

    @Override
    public void refreshData(List<VerificationNotifyBean> list) {
        adapter.setData(list);
        mBinding.orderCountTv.setText(list == null || list.size() == 0 ? "暂无已核销订单" : "共"+list.size()+"笔已核销订单");
    }
}
