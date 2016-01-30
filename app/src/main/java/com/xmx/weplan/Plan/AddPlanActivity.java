package com.xmx.weplan.Plan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.xmx.weplan.ActivityBase.BaseTempActivity;
import com.xmx.weplan.Database.SQLManager;
import com.xmx.weplan.R;

import java.util.Date;

public class AddPlanActivity extends BaseTempActivity {
    SQLManager sqlManager = new SQLManager();

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_plan);
    }

    @Override
    protected void setListener() {
        Button ok = getViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText hourText = getViewById(R.id.hour);
                EditText minuteText = getViewById(R.id.min);
                EditText titleText = getViewById(R.id.title);
                if (titleText.getText().toString().equals("")) {
                    showToast("请输入标题");
                    return;
                } else if (minuteText.getText().toString().equals("")) {
                    showToast("请输入时间");
                    return;
                } else if (hourText.getText().toString().equals("")) {
                    showToast("请输入时间");
                    return;
                }

                Date plan = new Date(System.currentTimeMillis());
                int hour =  Integer.valueOf(hourText.getText().toString());
                int minute =  Integer.valueOf(minuteText.getText().toString());
                plan.setHours(hour);
                plan.setMinutes(minute);
                plan.setSeconds(0);

                String title = titleText.getText().toString();
                if (sqlManager.insertPlan(title, "", plan)) {
                    showToast("添加成功");
                    finish();
                } else {
                    showToast("添加失败");
                }
            }
        });

        Button cancel = getViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
