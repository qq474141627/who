package com.qm.gangsdk.ui.view.gangdynamic.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qm.gangsdk.core.outer.common.callback.DataCallBack;
import com.qm.gangsdk.core.outer.common.entity.XLDynamicCommentBean;
import com.qm.gangsdk.core.outer.common.utils.StringUtils;
import com.qm.gangsdk.ui.GangSDK;
import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.base.XLBaseFragment;
import com.qm.gangsdk.ui.custom.headerfooter.EndlessRecyclerOnScrollListener;
import com.qm.gangsdk.ui.custom.loadingfooter.LoadingFooter;
import com.qm.gangsdk.ui.custom.loadingfooter.RecyclerViewStateUtils;
import com.qm.gangsdk.ui.utils.XLKeyBoardUtil;
import com.qm.gangsdk.ui.utils.XLToastUtil;
import com.qm.gangsdk.ui.view.gangdynamic.dynamic.helperclass.DynamicHelper;
import com.xl.views.ptr.PtrClassicFrameLayout;
import com.xl.views.ptr.PtrDefaultHandler;
import com.xl.views.ptr.PtrFrameLayout;
import com.xl.views.ptr.PtrHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijiyuan on 2018/1/3.
 *
 * 更多评论
 */

public class MoreCommentsFragment extends XLBaseFragment {
    public static final int PAGE_SIZE = 20;
    public static final int COMMENT_SUCCESS = 0X001;        //评论成功
    public static final String DYANMIC_ID = "DYANMIC_ID";
    public static final String COMMENT_NUM = "COMMENT_NUM";

    private ImageButton btnMenuLeft;
    private TextView tvTitle;
    private RecyclerView recyclerViewComment;
    private MoreCommentAdapter adapter;
    private PtrClassicFrameLayout ptrFrameLayout;
    private Button btnDynamicComment;
    private EditText editCommentContent;

    private String endtime = "";        //查询时间
    private int dynamicid = -1;         //动态id
    private int commentnum = 0;         //评论总数
    private int refusedid = 0;          //回复评论id
    private List<XLDynamicCommentBean> commentList = new ArrayList<>();

    @Override
    protected int getContentView() {
        return R.layout.fragmentxl_more_comments;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView(View view) {
        btnMenuLeft = (ImageButton) view.findViewById(R.id.btnMenuLeft);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        recyclerViewComment = (RecyclerView) view.findViewById(R.id.recyclerViewComment);
        ptrFrameLayout = (PtrClassicFrameLayout) view.findViewById(R.id.ptrFrameLayout);
        editCommentContent = (EditText) view.findViewById(R.id.editCommentContent);
        btnDynamicComment = (Button) view.findViewById(R.id.btnDynamicComment);
        bindRecyclerView();

        Bundle bundle = getArguments();
        if(bundle != null){
            dynamicid = bundle.getInt(DYANMIC_ID, -1);
            commentnum = bundle.getInt(COMMENT_NUM, 0);
            tvTitle.setText("评论（" + commentnum + "）");
        }
    }

    private void bindRecyclerView() {
        recyclerViewComment.setHasFixedSize(false);
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(aContext));
        adapter = new MoreCommentAdapter();
        recyclerViewComment.setAdapter(adapter);
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


        btnDynamicComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editCommentContent.getText().toString().trim();
                publishComment(content);
            }
        });

        recyclerViewComment.addOnScrollListener(new EndlessRecyclerOnScrollListener() {

            @Override
            public void onLoadNextPage(View view) {
                super.onLoadNextPage(view);
                LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(recyclerViewComment);
                if (state == LoadingFooter.State.Loading) {
                    return;
                }
                // loading more
                RecyclerViewStateUtils.setFooterViewState(aContext, recyclerViewComment, PAGE_SIZE, LoadingFooter.State.Loading, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecyclerViewStateUtils.setFooterViewState(aContext, recyclerViewComment, LoadingFooter.State.Loading, null);
                    }
                });
                getCommentData(PAGE_SIZE, endtime);

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
                getCommentData(PAGE_SIZE, endtime);
            }
        });

        ptrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrameLayout.autoRefresh(true);
            }
        }, 150);

    }

    /**
     * 发表评论
     * @param content       评论内容
     */
    private void publishComment(String content) {
        if(StringUtils.isEmpty(content)){
            XLToastUtil.showToastShort("内容不能为空");
            return;
        }

        if(StringUtils.getChineseLength(content) > 300){
            XLToastUtil.showToastShort("内容不能超过150个汉字");
            return;
        }

        if(refusedid <= 0){
            refusedid = 0;
        }
        loading.show();
        GangSDK.getInstance().dynamicManager().commentDynamic(dynamicid, content, refusedid, new DataCallBack<XLDynamicCommentBean>() {
            @Override
            public void onSuccess(int status, String message, XLDynamicCommentBean data) {
                XLToastUtil.showToastShort(message);
                loading.dismiss();
                refusedid = 0;
                commentnum++;
                tvTitle.setText("评论（" + commentnum + "）");
                editCommentContent.setText("");
                editCommentContent.setHint("请输入评论内容");
                if(data != null){
                    commentList.add(data);
                    adapter.notifyDataSetChanged();
                }
                Intent intent = new Intent();
                intent.putExtra(DYANMIC_ID, dynamicid);
                aContext.setResult(555, intent);
            }

            @Override
            public void onFail(String message) {
                loading.dismiss();
                XLToastUtil.showToastShort(message);
            }
        });
    }

    /**
     * 获取评论数据
     * @param pageSize      每页大小
     * @param time          截止时间
     */
    private void getCommentData(int pageSize, final String time) {
        GangSDK.getInstance().dynamicManager().getDynamicCommentList(dynamicid, pageSize, time, new DataCallBack<List<XLDynamicCommentBean>>() {
            @Override
            public void onSuccess(int status, String message, List<XLDynamicCommentBean> data) {
                ptrFrameLayout.refreshComplete();
                if (StringUtils.isEmpty(time)) {
                    commentList.clear();
                }

                if(data != null && !data.isEmpty()) {
                    endtime = String.valueOf(data.get(data.size() -1).getCreatetime());
                }else {
                    RecyclerViewStateUtils.setFooterViewState(recyclerViewComment, LoadingFooter.State.Normal);
                    return;
                }
                commentList.addAll(data);
                adapter.notifyDataSetChanged();
                RecyclerViewStateUtils.setFooterViewState(recyclerViewComment, LoadingFooter.State.Normal);
            }

            @Override
            public void onFail(String message) {
                ptrFrameLayout.refreshComplete();
                RecyclerViewStateUtils.setFooterViewState(recyclerViewComment, LoadingFooter.State.TheEnd);
                XLToastUtil.showToastShort(message);
            }
        });
    }

    /**
     * 评论adapter
     */
    class MoreCommentAdapter extends RecyclerView.Adapter<MoreCommentViewHolder>{

        @Override
        public MoreCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MoreCommentViewHolder(LayoutInflater.from(aContext).inflate(R.layout.item_recyclerview_more_comment, parent, false));
        }

        @Override
        public void onBindViewHolder(MoreCommentViewHolder holder, int position) {
            final XLDynamicCommentBean commentBean = commentList.get(position);
            DynamicHelper.setCommentContent(aContext, holder.textCommentContent, commentBean, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(commentBean.getUserid() != null){
                        refusedid = commentBean.getUserid().intValue();
                        if(GangSDK.getInstance().getUserid() == refusedid){
                            return;
                        }
                        XLKeyBoardUtil.showKeyBoard(aContext, editCommentContent);
                        editCommentContent.setHint("回复  " + commentBean.getNickname());
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }
    }

    /**
     * 评论viewHolder
     */
    class MoreCommentViewHolder extends RecyclerView.ViewHolder{

        private TextView textCommentContent;

        public MoreCommentViewHolder(View itemView) {
            super(itemView);
            textCommentContent = (TextView) itemView.findViewById(R.id.textCommentContent);
        }
    }
}
