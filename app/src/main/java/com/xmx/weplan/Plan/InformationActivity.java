package com.xmx.weplan.Plan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xmx.weplan.ActivityBase.BaseTempActivity;
import com.xmx.weplan.Constants;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

public class InformationActivity extends BaseTempActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_information);

        setTitle("计划");

        ListView planList = getViewById(R.id.list_plan_card);
        PlanCardAdapter adapter = new PlanCardAdapter(this);
        planList.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }
}
