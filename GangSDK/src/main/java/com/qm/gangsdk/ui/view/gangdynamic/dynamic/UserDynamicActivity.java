package com.qm.gangsdk.ui.view.gangdynamic.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.qm.gangsdk.ui.GangSDK;
import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.base.XLBaseActivity;


/**
 * Created by lijiyuan on 2018/1/3.
 *
 * 用户动态
 */

public class UserDynamicActivity extends XLBaseActivity {
    public static final String USER_ID = "USER_ID";
    public static final String USER_NICKNAME = "USER_NICKNAME";
    private DynamicFragment fragment;

    @Override
    protected int getContentView() {
        return R.layout.activityxl_userdynamic;
    }

    @Override
    protected void initView() {
        fragment = new DynamicFragment();
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if(intent != null) {
            int userid = intent.getIntExtra(USER_ID, -1);
            if (userid == GangSDK.getInstance().getUserid()) {
                bundle.putSerializable(DynamicFragment.DYNAMIC_TYPE, DynamicFragment.DYNAMIC_TYPE_PERSONAL);
            } else {
                bundle.putSerializable(DynamicFragment.DYNAMIC_TYPE, DynamicFragment.DYNAMIC_TYPE_MEMBERS);
            }
            bundle.putInt(USER_ID, userid);
            bundle.putString(USER_NICKNAME, intent.getStringExtra(USER_NICKNAME));
        }
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContent, fragment)
                .show(fragment)
                .commitAllowingStateLoss();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(fragment != null){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
