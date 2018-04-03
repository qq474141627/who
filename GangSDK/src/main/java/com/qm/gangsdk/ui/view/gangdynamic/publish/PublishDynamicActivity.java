package com.qm.gangsdk.ui.view.gangdynamic.publish;

import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.base.XLBaseActivity;

/**
 * Created by lijiyuan on 2018/1/3.
 *
 * 用户发布动态
 */

public class PublishDynamicActivity extends XLBaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activityxl_publish_dynamic;
    }

    @Override
    protected void initView() {
        PublishDynamicFragment fragment = new PublishDynamicFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentParent, fragment)
                .show(fragment)
                .commitAllowingStateLoss();
    }

    @Override
    protected void initData() {

    }
}
