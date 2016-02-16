package com.xmx.weplan.Plan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xmx.weplan.ActivityBase.BaseTempActivity;
import com.xmx.weplan.Database.CloudManager;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

public class InformationActivity extends BaseTempActivity {
    int id;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_information);

        String title = getIntent().getStringExtra("title");
        setTitle(title);
    }

    @Override
    protected void setListener() {
        id = getIntent().getIntExtra("id", -1);

        Button complete = getViewById(R.id.complete);
        if (id > 0) {
            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SQLManager.getInstance().completePlan(id)) {
                        CloudManager.getInstance().completePlan(id);
                    }
                    finish();
                }
            });
        } else {
            showToast("加载出错");
        }

        Button cancel = getViewById(R.id.cancel);
        if (id > 0) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLManager.getInstance().cancelPlan(id);
                    CloudManager.getInstance().cancelPlan(id);
                    finish();
                }
            });
        } else {
            showToast("加载出错");
        }
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }
}
