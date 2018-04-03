package com.qm.gangsdk.ui.view.gangdynamic.dynamic;

import android.os.Bundle;

import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.base.XLBaseActivity;

/**
 * Created by lijiyuan on 2018/1/3.
 *
 * 查看图片
 */

public class ImageBrowseActivity extends XLBaseActivity{
    public static final String IMAGE_INFO = "IMAGE_INFO";           //url集合
    public static final String CURRENT_ITEM = "CURRENT_ITEM";       //查看图片下标
    public static final String CAN_DELETE = "CAN_DELETE";           //能否删除

    @Override
    protected int getContentView() {
        return R.layout.activityxl_image_browse;
    }

    @Override
    protected void initView() {
        ImageBrowseFragment fragment = new ImageBrowseFragment();
        Bundle bundle = new Bundle();
        if(getIntent() != null) {
            bundle.putInt(CURRENT_ITEM, getIntent().getIntExtra(CURRENT_ITEM, -1));
            bundle.putBoolean(CAN_DELETE, getIntent().getBooleanExtra(CAN_DELETE, false));
            bundle.putSerializable(IMAGE_INFO, getIntent().getSerializableExtra(IMAGE_INFO));
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
