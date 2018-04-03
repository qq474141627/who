package com.qm.gangsdk.ui.view.gangdynamic.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qm.gangsdk.core.outer.common.callback.DataCallBack;
import com.qm.gangsdk.core.outer.common.entity.XLDynamicBean;
import com.qm.gangsdk.core.outer.common.utils.StringUtils;
import com.qm.gangsdk.core.outer.receiver.GangPosterReceiver;
import com.qm.gangsdk.core.outer.receiver.base.OnGangReceiverListener;
import com.qm.gangsdk.core.outer.receiver.listener.GangReceiverListener;
import com.qm.gangsdk.ui.GangSDK;
import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.base.XLBaseFragment;
import com.qm.gangsdk.ui.custom.headerfooter.EndlessRecyclerOnScrollListener;
import com.qm.gangsdk.ui.custom.loadingfooter.LoadingFooter;
import com.qm.gangsdk.ui.custom.loadingfooter.RecyclerViewStateUtils;
import com.qm.gangsdk.ui.event.XLCompletePublishDynamicEvent;
import com.qm.gangsdk.ui.utils.XLToastUtil;
import com.qm.gangsdk.ui.view.common.GangConfigureUtils;
import com.qm.gangsdk.ui.view.common.GangModuleManage;
import com.qm.gangsdk.ui.view.gangdynamic.dynamic.helperclass.DynamicAdapter;
import com.xl.views.ptr.PtrClassicFrameLayout;
import com.xl.views.ptr.PtrDefaultHandler;
import com.xl.views.ptr.PtrFrameLayout;
import com.xl.views.ptr.PtrHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijiyuan on 2018/1/3.
 *
 * 动态
 */

public class DynamicFragment extends XLBaseFragment{

    public static final String DYNAMIC_TYPE = "DYNAMIC_TYPE";
    public static final String USER_NICKNAME = "USER_NICKNAME";
    public static final String USER_ID = "USER_ID";
    public static final int PAGE_SIZE = 10;

    public static final int DYNAMIC_TYPE_PERSONAL = 1;      //个人动态
    public static final int DYNAMIC_TYPE_MEMBERS = 2;       //成员动态
    public static final int DYNAMIC_TYPE_GANG = 3;          //社群动态

    private ImageButton btnMenuLeft;
    private ImageButton btnMenuRight;
    private TextView tvTitle;
    private RecyclerView recyclerDynamic;
    private DynamicAdapter adapter;
    private PtrClassicFrameLayout ptrFrameLayout;
    private int mDynamicType = DYNAMIC_TYPE_PERSONAL;       //动态类型
    private int userid = -1;                                //用户id
    private String endtime = "";                            //查询截止时间
    private List<XLDynamicBean> dynamicLsit = new ArrayList<>();
    private GangReceiverListener publishCompleteListener;

    @Override
    protected int getContentView() {
        return R.layout.fragmentxl_dynamic;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView(View view) {
        btnMenuLeft = (ImageButton) view.findViewById(R.id.btnMenuLeft);
        btnMenuRight = (ImageButton) view.findViewById(R.id.btnMenuRight);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        recyclerDynamic = (RecyclerView) view.findViewById(R.id.recyclerDynamic);
        ptrFrameLayout = (PtrClassicFrameLayout) view.findViewById(R.id.ptrFrameLayout);

        tvTitle.setText("个人动态");
        btnMenuRight.setImageResource(R.mipmap.qm_btn_gangdynamic_publish);

        initTitleBar();

        bindRecyclerView();
    }

    private void bindRecyclerView() {
        recyclerDynamic.setHasFixedSize(false);
        recyclerDynamic.setLayoutManager(new LinearLayoutManager(aContext));
        adapter = new DynamicAdapter(aContext, dynamicLsit, mDynamicType);
        recyclerDynamic.setAdapter(adapter);
    }

    private void initTitleBar() {
        Bundle bundle = getArguments();
        if(bundle != null){
            mDynamicType = bundle.getInt(DYNAMIC_TYPE, -1);
            if(mDynamicType == DYNAMIC_TYPE_GANG){
                tvTitle.setText(GangConfigureUtils.getGangName() + "圈");
                btnMenuRight.setVisibility(View.VISIBLE);
            }else {
                userid = bundle.getInt(USER_ID, -1);
                String nickname = bundle.getString(USER_NICKNAME);
                if(!StringUtils.isEmpty(nickname)){
                    tvTitle.setText(nickname);
                }
                if( mDynamicType == DYNAMIC_TYPE_PERSONAL){
                    btnMenuRight.setVisibility(View.VISIBLE);
                }else {
                    btnMenuRight.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnMenuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aContext.finish();
            }
        });

        btnMenuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GangModuleManage.toPublishDynamicActivity(aContext);
            }
        });

        publishCompleteListener = GangPosterReceiver.addReceiverListener(this, XLCompletePublishDynamicEvent.class, new OnGangReceiverListener() {
            @Override
            public void onReceived(Object data) {
                getDynamicList(PAGE_SIZE, "");
            }
        });

        recyclerDynamic.addOnScrollListener(new EndlessRecyclerOnScrollListener() {

            @Override
            public void onLoadNextPage(View view) {
                super.onLoadNextPage(view);
                LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(recyclerDynamic);
                if (state == LoadingFooter.State.Loading) {
                    return;
                }
                // loading more
                RecyclerViewStateUtils.setFooterViewState(aContext, recyclerDynamic, PAGE_SIZE, LoadingFooter.State.Loading, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecyclerViewStateUtils.setFooterViewState(aContext, recyclerDynamic, LoadingFooter.State.Loading, null);
                    }
                });
                getDynamicList(PAGE_SIZE, endtime);

            }
        });

        ptrFrameLayout.setLastUpdateTimeRelateObject(this);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                endtime = "";
                getDynamicList(PAGE_SIZE, endtime);
            }
        });

        ptrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrameLayout.autoRefresh(true);
            }
        }, 150);

    }

    private void getDynamicList(int pageSize, final String time) {
        switch (mDynamicType){
            case DYNAMIC_TYPE_GANG:
                GangSDK.getInstance().dynamicManager().getGangDynamicList(pageSize, time, new DataCallBack<List<XLDynamicBean>>() {
                    @Override
                    public void onSuccess(int status, String message, List<XLDynamicBean> data) {
                        ptrFrameLayout.refreshComplete();
                        if (StringUtils.isEmpty(time)) {
                            dynamicLsit.clear();
                        }

                        if(data != null && !data.isEmpty()) {
                            endtime = String.valueOf(data.get(data.size() -1).getCreatetime());
                        }else {
                            RecyclerViewStateUtils.setFooterViewState(recyclerDynamic, LoadingFooter.State.Normal);
                            return;
                        }
                        dynamicLsit.addAll(data);
                        adapter.notifyDataSetChanged();
                        RecyclerViewStateUtils.setFooterViewState(recyclerDynamic, LoadingFooter.State.Normal);
                    }

                    @Override
                    public void onFail(String message) {
                        ptrFrameLayout.refreshComplete();
                        XLToastUtil.showToastShort(message);
                        RecyclerViewStateUtils.setFooterViewState(recyclerDynamic, LoadingFooter.State.TheEnd);
                    }
                });
                break;
            case DYNAMIC_TYPE_MEMBERS:
            case DYNAMIC_TYPE_PERSONAL:
                GangSDK.getInstance().dynamicManager().getUserDynamicList(userid, pageSize, time, new DataCallBack<List<XLDynamicBean>>() {
                    @Override
                    public void onSuccess(int status, String message, List<XLDynamicBean> data) {
                        ptrFrameLayout.refreshComplete();
                        if (StringUtils.isEmpty(time)) {
                            dynamicLsit.clear();
                        }

                        if(data != null && !data.isEmpty()) {
                            endtime = String.valueOf(data.get(data.size() -1).getCreatetime());
                        }else {
                            RecyclerViewStateUtils.setFooterViewState(recyclerDynamic, LoadingFooter.State.Normal);
                            return;
                        }
                        dynamicLsit.addAll(data);
                        adapter.notifyDataSetChanged();
                        RecyclerViewStateUtils.setFooterViewState(recyclerDynamic, LoadingFooter.State.Normal);
                    }

                    @Override
                    public void onFail(String message) {
                        ptrFrameLayout.refreshComplete();
                        XLToastUtil.showToastShort(message);
                        RecyclerViewStateUtils.setFooterViewState(recyclerDynamic, LoadingFooter.State.TheEnd);
                    }
                });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null || dynamicLsit == null || dynamicLsit.isEmpty()){
            return;
        }
        for(XLDynamicBean dynamicBean : dynamicLsit) {
            if (dynamicBean.getActionid() != null) {
                if (dynamicBean.getActionid().intValue() == data.getIntExtra(MoreCommentsFragment.DYANMIC_ID, -1)) {
                    if(dynamicBean.getCommentnum() != null) {
                        dynamicBean.setCommentnum(dynamicBean.getCommentnum().intValue() + 1);
                    }else {
                        dynamicBean.setCommentnum(1);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GangPosterReceiver.removeReceiverListener(publishCompleteListener);
    }
}
