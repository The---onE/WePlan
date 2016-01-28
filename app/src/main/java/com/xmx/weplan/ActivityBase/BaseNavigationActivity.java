package com.xmx.weplan.ActivityBase;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.AVObject;
import com.xmx.weplan.R;
import com.xmx.weplan.User.Callback.AutoLoginCallback;
import com.xmx.weplan.User.LoginActivity;
import com.xmx.weplan.User.UserManager;

/**
 * Created by The_onE on 2015/12/28.
 */
public abstract class BaseNavigationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean loggedinFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDrawerNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoggedIn();
    }

    protected void initDrawerNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            showToast("Press Camera");
        } else if (id == R.id.nav_gallery) {
            showToast("Press Gallery");
        } else if (id == R.id.nav_slideshow) {
            showToast("Press Nav_slideshow");
        } else if (id == R.id.nav_manage) {
            login();
        } else if (id == R.id.nav_share) {
            showToast("Press Nav_share");
        } else if (id == R.id.nav_send) {
            showToast("Press Nav_send");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void login() {
        if (!loggedinFlag) {
            startActivity(LoginActivity.class);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("退出登录吗？");
            builder.setTitle("注销");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private void logout() {
        UserManager.getInstance().logout();
        NavigationView navigation = getViewById(R.id.nav_view);
        Menu menu = navigation.getMenu();
        MenuItem login = menu.findItem(R.id.nav_manage);
        login.setTitle("登录");
        loggedinFlag = false;
    }

    protected void checkLoggedIn() {
        NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigation.getMenu();
        final MenuItem login = menu.findItem(R.id.nav_manage);

        UserManager.getInstance().setContext(this);

        UserManager.getInstance().checkLogin(new AutoLoginCallback() {
            @Override
            public void success(AVObject user) {
                login.setTitle(user.getString("nickname"));

                loggedinFlag = true;
            }

            @Override
            public void notLoggedIn() {
                login.setTitle("登录");
                loggedinFlag = false;
            }

            @Override
            public void errorNetwork() {
                showToast("网络连接失败");
                login.setTitle("登录");
                loggedinFlag = false;
            }

            @Override
            public void errorUsername() {
                login.setTitle("登录");
                loggedinFlag = false;
            }

            @Override
            public void errorChecksum() {
                showToast("请重新登录");
                login.setTitle("登录");
                loggedinFlag = false;
            }
        });
    }
}
