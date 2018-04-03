package com.opar.mobile.who;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.qm.gangsdk.ui.GangSDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GangSDK.getInstance().init(getApplication(),"eVJs3YdseiT9+30NuCnC9g==").startUI(MainActivity.this);
    }
}
