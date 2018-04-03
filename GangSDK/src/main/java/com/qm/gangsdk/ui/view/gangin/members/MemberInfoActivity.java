package com.qm.gangsdk.ui.view.gangin.members;

import android.os.Bundle;

import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.base.XLBaseActivity;

/**
 * Created by lijiyuan on 2017/8/4.
 * 创建社群
 */

public class MemberInfoActivity extends XLBaseActivity {
    public static final String MEMBER_ID = "MEMBER_ID";
    @Override
    protected int getContentView() {
        return R.layout.activityxl_memberinfo;
    }

    @Override
    protected void initView() {

        MemberInfoFragment fragment = new MemberInfoFragment();
        Bundle bundle = new Bundle();
        if(getIntent() != null) {
            bundle.putSerializable(MEMBER_ID, getIntent().getIntExtra(MEMBER_ID, -1));
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

}
