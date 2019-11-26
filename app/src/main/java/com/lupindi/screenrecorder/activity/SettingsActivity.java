package com.lupindi.screenrecorder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.lupindi.screenrecorder.R;


public class SettingsActivity extends Activity {

    private View mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setStatusBar();

        mReturnButton = findViewById(R.id.return_button);
        mReturnButton.setOnClickListener(v->{
            finish();
        });
    }

    private void setStatusBar() {
        StatusBarUtil.setTranslucent(this, 0);
    }
}
