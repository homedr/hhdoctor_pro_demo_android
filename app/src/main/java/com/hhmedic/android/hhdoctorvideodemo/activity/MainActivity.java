package com.hhmedic.android.hhdoctorvideodemo.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.hhmedic.android.hhdoctorvideodemo.R;
import com.hhmedic.android.sdk.listener.HHLoginListener;
import com.hhmedic.android.sdk.pro.HHProDoctor;
import com.yanzhenjie.permission.AndPermission;

public class MainActivity extends BaseActivity {

    private Switch mIsDevelopSwitch;
    private EditText mUserTokenEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int contentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initUI() {
        super.initUI();
        mIsDevelopSwitch = findViewById(R.id.developSwitch);
        mIsDevelopSwitch.setChecked(LocalConfig.isDevelop(this));
        mIsDevelopSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            LocalConfig.setDevelop(this, isChecked);
            Toast.makeText(MainActivity.this, "切换完环境后需要重启打开APP才会生效", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> System.exit(0), 1000);
        });
        findViewById(R.id.login_button).setOnClickListener(v -> callDoctor());
        mUserTokenEdit = findViewById(R.id.userToken);

        findViewById(R.id.is_in_develop).setVisibility(LocalConfig.isDevelop(this) ? View.VISIBLE : View.GONE);
        findViewById(R.id.use_default_toke).setOnClickListener(v -> {
            mUserTokenEdit.setText(LocalConfig.DefaultUserToken);
        });
    }

    private void callDoctor() {

        AndPermission.with(this).permission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        )
                .onGranted(permissions -> doLogin())

                .onDenied(permissions -> {


                    if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, permissions)) {
                        // 这些权限被用户总是拒绝。
//                        alwaysTips(permissionTips());
                    } else {

                    }

                })
                .start();
    }

    private void doLogin() {
        String userToken = mUserTokenEdit.getText().toString(); //这个ID是和和缓对接之后得到的和缓的UserToken
        if (userToken.isEmpty()) {
            Toast.makeText(this, "请输入需要登录的userToken", Toast.LENGTH_SHORT).show();
            return;
        }
        LocalConfig.setLoginedToken(this, userToken);
        HHProDoctor.callDoctor(this, userToken, new HHLoginListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String s) {

            }
        });
    }
}
