package com.qm.gangsdk.ui.view.gangdynamic.dynamic;

import android.content.Intent;
import android.os.Bundle;

import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.base.XLBaseActivity;

/**
 * Created by lijiyuan on 2018/1/3.
 *
 * 更多评论
 */

public class MoreCommentsActivity extends XLBaseActivity{
    public static final String DYANMIC_ID = "DYANMIC_ID";
    public static final String COMMENT_NUM = "COMMENT_NUM";

    @Override
    protected int getContentView() {
        return R.layout.activityxl_more_comments;
    }

    @Override
    protected void initView() {
        MoreCommentsFragment fragment = new MoreCommentsFragment();
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if(intent != null) {
            bundle.putInt(DYANMIC_ID, intent.getIntExtra(DYANMIC_ID, -1));
            bundle.putInt(COMMENT_NUM, intent.getIntExtra(COMMENT_NUM, -1));
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
