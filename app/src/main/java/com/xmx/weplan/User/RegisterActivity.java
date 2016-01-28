package com.xmx.weplan.User;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.xmx.weplan.ActivityBase.BaseTempActivity;
import com.xmx.weplan.R;
import com.xmx.weplan.User.Callback.RegisterCallback;

public class RegisterActivity extends BaseTempActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.register_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nn = getViewById(R.id.register_nickname);
                final String nickname = nn.getText().toString();
                EditText un = getViewById(R.id.register_username);
                final String username = un.getText().toString();
                EditText pw = getViewById(R.id.register_password);
                final String password = pw.getText().toString();
                EditText pw2 = getViewById(R.id.register_password2);
                String password2 = pw2.getText().toString();

                if (nickname.equals("")) {
                    showToast("请输入昵称");
                    return;
                }
                if (username.equals("")) {
                    showToast("请输入用户名");
                    return;
                }
                if (password.equals("")) {
                    showToast("请输入密码");
                    return;
                }
                if (!password.equals(password2)) {
                    showToast("两次输入密码不一致");
                    return;
                }

                UserManager.getInstance().register(username, password, nickname, new RegisterCallback() {
                    @Override
                    public void success() {
                        showToast("注册成功");
                        finish();
                    }

                    @Override
                    public void usernameExist() {
                        showToast("该用户名已被注册");
                    }

                    @Override
                    public void nicknameExist() {
                        showToast("该昵称已被注册");
                    }

                    @Override
                    public void errorNetwork() {
                        showToast("网络连接失败");
                    }
                });
            }
        });

        getViewById(R.id.register_cancel).setOnClickListener(new View.OnClickListener() {
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
