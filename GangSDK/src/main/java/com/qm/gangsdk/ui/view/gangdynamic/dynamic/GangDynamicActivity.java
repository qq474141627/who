package com.qm.gangsdk.ui.view.gangdynamic.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.base.XLBaseActivity;

/**
 * Created by lijiyuan on 2018/1/3.
 *
 * 社群动态
 */

public class GangDynamicActivity extends XLBaseActivity{
    private DynamicFragment fragment;
    @Override
    protected int getContentView() {
        return R.layout.activityxl_gangdynamic;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = new DynamicFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DynamicFragment.DYNAMIC_TYPE, DynamicFragment.DYNAMIC_TYPE_GANG);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContent, fragment)
                .show(fragment)
                .commitAllowingStateLoss();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(fragment != null){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
