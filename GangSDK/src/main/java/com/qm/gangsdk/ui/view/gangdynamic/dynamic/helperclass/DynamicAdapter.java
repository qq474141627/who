package com.qm.gangsdk.ui.view.gangdynamic.dynamic.helperclass;

/**
 * Created by lijiyuan on 2018/1/12.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qm.gangsdk.core.outer.common.callback.DataCallBack;
import com.qm.gangsdk.core.outer.common.entity.XLDynamicBean;
import com.qm.gangsdk.core.outer.common.entity.XLDynamicCommentBean;
import com.qm.gangsdk.core.outer.common.entity.XLDynamicSupportBean;
import com.qm.gangsdk.core.outer.common.utils.StringUtils;
import com.qm.gangsdk.ui.GangSDK;
import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.custom.ninegrid.ImageInfo;
import com.qm.gangsdk.ui.custom.ninegrid.NineGridView;
import com.qm.gangsdk.ui.utils.DensityUtil;
import com.qm.gangsdk.ui.utils.ImageLoadUtil;
import com.qm.gangsdk.ui.utils.TimeUtils;
import com.qm.gangsdk.ui.utils.XLToastUtil;
import com.qm.gangsdk.ui.view.common.DialogHintFragment;
import com.qm.gangsdk.ui.view.common.GangModuleManage;
import com.xl.xlaudio.XLAudioClient;
import com.xl.xlaudio.XLAudioPlayerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态适配器
 */
public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.DynamicViewHolder> {
    private Context context;
    private List<XLDynamicBean> dynamicLsit = new ArrayList<>();
    private int dynamicType = -1;

    public DynamicAdapter(Context context, List<XLDynamicBean> dynamicLsit, int dynamicType) {
        this.context = context;
        this.dynamicLsit = dynamicLsit;
        this.dynamicType = dynamicType;
    }

    @Override
    public DynamicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DynamicViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recyclerview_dynamic, parent, false));
    }

    @Override
    public void onBindViewHolder(final DynamicViewHolder holder, final int position) {
        final XLDynamicBean dynamicBean = dynamicLsit.get(position);
        ImageLoadUtil.loadRoundImage(holder.imageUserPic, StringUtils.getString(dynamicBean.getIconurl(), ""));
        holder.textUserNickName.setText(StringUtils.getString(dynamicBean.getNickname(), ""));
        holder.textDynamicCreateTime.setText(TimeUtils.dateStringToOnlineTimeFormat(dynamicBean.getCreatetime()));
        if (!StringUtils.isEmpty(dynamicBean.getContent())) {
            holder.textDynamicContent.setVisibility(View.VISIBLE);
            holder.textDynamicContent.setText(StringUtils.getString(dynamicBean.getContent(), ""));
        } else {
            holder.textDynamicContent.setVisibility(View.GONE);
        }
        //图片显示
        if (!StringUtils.isEmpty(dynamicBean.getPicurl())) {
            List<ImageInfo> imageList = new ArrayList<>();
            String[] pictures = dynamicBean.getPicurl().trim().split(",");
            holder.itemDynamicNineGridView.setVisibility(View.VISIBLE);
            for (String picture : pictures) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setThumbnailUrl(picture);
                imageInfo.setBigImageUrl(picture);
                imageList.add(imageInfo);
            }
            holder.itemDynamicNineGridView.setAdapter(new NineGridViewClickAdapter(context, imageList));
        } else {
            holder.itemDynamicNineGridView.setVisibility(View.GONE);
        }
        //是否已经点赞
        if (dynamicBean.getIssupport() != null) {
            if (dynamicBean.getIssupport().intValue() > 0) {
                dynamicBean.setIslike(true);
                holder.btnDynamicSupport.setImageResource(R.mipmap.qm_btn_gangdynamic_support_selected);
            } else {
                dynamicBean.setIslike(false);
                holder.btnDynamicSupport.setImageResource(R.mipmap.qm_btn_gangdynamic_support_normal);
            }
        } else {
            dynamicBean.setIslike(false);
            holder.btnDynamicSupport.setImageResource(R.mipmap.qm_btn_gangdynamic_support_normal);
        }

        //点赞显示
        if (dynamicBean.getSupportlist() != null && !dynamicBean.getSupportlist().isEmpty()) {
            holder.viewSupportArea.setVisibility(View.VISIBLE);
            DynamicHelper.setSupprotContent(context, holder.textDynamicSupport, dynamicBean.getSupportlist());
            ViewTreeObserver vto = holder.textDynamicSupport.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (holder.textDynamicSupport.getLayout() != null) {
                        int ellipsisCount = holder.textDynamicSupport.getLayout().getEllipsisCount(holder.textDynamicSupport.getLineCount() - 1);
                        if (ellipsisCount > 0) {
                            holder.textDynamicSupportNum.setVisibility(View.VISIBLE);
                            holder.textDynamicSupportNum.setText("等" + dynamicBean.getSupportnum() + "人觉得很赞");
                        } else {
                            holder.textDynamicSupportNum.setVisibility(View.GONE);
                        }
                    }
                }
            });
        } else {
            holder.viewSupportArea.setVisibility(View.GONE);
        }

        //评论显示
        if(dynamicBean.getCommentnum() != null && dynamicBean.getCommentnum().intValue() > 0){
            int maxSize;
            holder.viewCommentArea.setVisibility(View.VISIBLE);
            if(dynamicBean.getCommentnum().intValue() > 3){
                maxSize = holder.commentItems.size();
                holder.textDynamicCommentMore.setVisibility(View.VISIBLE);
            }else {
                maxSize = dynamicBean.getCommentlist().size();
                holder.textDynamicCommentMore.setVisibility(View.GONE);
            }

            for(int j= 0; j < holder.commentItems.size(); j++) {
                holder.commentItems.get(j).setVisibility(View.GONE);
                for (int i = 0; i < maxSize; i++) {
                    if(i == j) {
                        holder.commentItems.get(i).setVisibility(View.VISIBLE);
                        final XLDynamicCommentBean dynamicCommentBean = dynamicBean.getCommentlist().get(i);
                        DynamicHelper.setCommentContent(context, holder.commentItems.get(i), dynamicCommentBean, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialogComment(dynamicCommentBean, position);
                            }
                        });
                    }
                }
            }
        }else {
            holder.viewCommentArea.setVisibility(View.GONE);
        }

        //语音显示
        if (!StringUtils.isEmpty(dynamicBean.getSoundurl())) {
            holder.viewVoiceArea.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layoutParams = null;
            if (dynamicBean.getSoundtime() != null) {
                if (dynamicBean.getSoundtime() > 10) {
                    layoutParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 140), DensityUtil.dip2px(context, 20));
                } else if (dynamicBean.getSoundtime() > 5) {
                    layoutParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 120), DensityUtil.dip2px(context, 20));
                } else {
                    layoutParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 100), DensityUtil.dip2px(context, 20));
                }
                holder.linearDynamicVoice.setLayoutParams(layoutParams);
                holder.textDynamicVoiceTime.setText(String.valueOf(dynamicBean.getSoundtime()) + "″");
            }

        } else {
            holder.viewVoiceArea.setVisibility(View.GONE);
        }

        //播放语音
        holder.linearDynamicVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageDynamicVoice.setBackgroundResource(R.drawable.qm_play_receiver_voice_anim);
                AnimationDrawable drawable = (AnimationDrawable) holder.imageDynamicVoice.getBackground();
                drawable.start();
                XLAudioClient.sharedInstance().stopAll();
                XLAudioClient.sharedInstance().play(dynamicBean.getSoundurl(), new XLAudioPlayerListener() {
                    @Override
                    public void onFinished(String url) {
                        holder.imageDynamicVoice.setBackgroundResource(R.mipmap.qm_record_volume_left3);
                    }
                });
            }
        });

        //更多评论
        holder.textDynamicCommentMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dynamicBean.getActionid() != null && dynamicBean.getCommentnum() != null) {
                    GangModuleManage.toMoreCommentsActivity(context, dynamicBean.getActionid().intValue(), dynamicBean.getCommentnum().intValue());
                }
            }
        });

        //点赞
        holder.btnDynamicSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportDynamic(dynamicBean, position);

            }
        });

        //评论
        holder.btnDynamicComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComment(null, position);
            }
        });

        //动态设置
        holder.btnDynamicMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicPopwindow.showDynamicPopWindow(context, holder.btnDynamicMenu, dynamicType, new DynamicPopwindow.ClickCallback() {
                    @Override
                    public void deleteClick() {
                        deleteDynamic(dynamicBean.getActionid(), position);
                    }

                    @Override
                    public void topClick() {
                        topDynamic(dynamicBean.getActionid());
                    }

                    @Override
                    public void hideClick() {
                        hideDynamic(dynamicBean.getActionid(), position);
                    }

                    @Override
                    public void reportClick() {
                        reportDynamic(dynamicBean.getActionid());
                    }
                });
            }
        });

        holder.textUserNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dynamicBean.getUserid() != null) {
                    GangModuleManage.toMemberInfoActivity(context, dynamicBean.getUserid().intValue());
                }
            }
        });

        holder.imageUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GangModuleManage.toMemberInfoActivity(context, dynamicBean.getUserid().intValue());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dynamicLsit.size();
    }

    class DynamicViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageUserPic;
        private TextView textUserNickName;
        private TextView textDynamicCreateTime;
        private ImageButton btnDynamicMenu;
        private TextView textDynamicContent;
        private NineGridView itemDynamicNineGridView;
        private LinearLayout linearDynamicVoice;
        private View imageDynamicVoice;
        private TextView textDynamicVoiceTime;
        private ImageButton btnDynamicSupport;
        private ImageButton btnDynamicComment;
        private TextView textDynamicSupport;
        private TextView textDynamicSupportNum;
        private TextView textDynamicCommentList1;
        private TextView textDynamicCommentList2;
        private TextView textDynamicCommentList3;
        private TextView textDynamicCommentMore;
        private View viewCommentArea;
        private View viewSupportArea;
        private View viewVoiceArea;
        private List<TextView> commentItems = new ArrayList<>();

        public DynamicViewHolder(View itemView) {
            super(itemView);
            imageUserPic = (ImageView) itemView.findViewById(R.id.imageUserPic);
            textUserNickName = (TextView) itemView.findViewById(R.id.textUserNickName);
            textDynamicCreateTime = (TextView) itemView.findViewById(R.id.textDynamicCreateTime);
            btnDynamicMenu = (ImageButton) itemView.findViewById(R.id.btnDynamicMenu);
            textDynamicContent = (TextView) itemView.findViewById(R.id.textDynamicContent);
            itemDynamicNineGridView = (NineGridView) itemView.findViewById(R.id.itemDynamicNineGridView);
            linearDynamicVoice = (LinearLayout) itemView.findViewById(R.id.linearDynamicVoice);
            imageDynamicVoice = itemView.findViewById(R.id.imageDynamicVoice);
            textDynamicVoiceTime = (TextView) itemView.findViewById(R.id.textDynamicVoiceTime);
            btnDynamicSupport = (ImageButton) itemView.findViewById(R.id.btnDynamicSupport);
            btnDynamicComment = (ImageButton) itemView.findViewById(R.id.btnDynamicComment);
            textDynamicSupport = (TextView) itemView.findViewById(R.id.textDynamicSupport);
            textDynamicSupportNum = (TextView) itemView.findViewById(R.id.textDynamicSupportNum);
            textDynamicCommentList1 = (TextView) itemView.findViewById(R.id.textDynamicCommentList1);
            textDynamicCommentList2 = (TextView) itemView.findViewById(R.id.textDynamicCommentList2);
            textDynamicCommentList3 = (TextView) itemView.findViewById(R.id.textDynamicCommentList3);
            textDynamicCommentMore = (TextView) itemView.findViewById(R.id.textDynamicCommentMore);
            viewCommentArea = itemView.findViewById(R.id.viewCommentArea);
            viewSupportArea = itemView.findViewById(R.id.viewSupportArea);
            viewVoiceArea = itemView.findViewById(R.id.viewVoiceArea);
            commentItems.add(textDynamicCommentList1);
            commentItems.add(textDynamicCommentList2);
            commentItems.add(textDynamicCommentList3);
        }
    }

    /**
     * 显示评论弹窗
     * @param dynamicCommentBean
     */
    private void showDialogComment(XLDynamicCommentBean dynamicCommentBean, int index) {
        final DialogDynamicCommentFragment dialog =  new DialogDynamicCommentFragment();
        if(dynamicLsit == null || dynamicLsit.isEmpty()){
            return;
        }
        final XLDynamicBean dynamicBean = dynamicLsit.get(index);

        if(dynamicBean == null || dynamicBean.getActionid() == null){
            return;
        }
        dialog.setDynamicId(dynamicBean.getActionid().intValue());
        if(dynamicCommentBean != null && dynamicCommentBean.getUserid() != null){
            if(GangSDK.getInstance().getUserid() == dynamicCommentBean.getUserid().intValue()){
                return;
            }
            dialog.setRefusedId(dynamicCommentBean.getUserid().intValue()).setRefNickname(dynamicCommentBean.getNickname());
        }
        dialog.setOnCommentResult(new DialogDynamicCommentFragment.CommentResult() {
            @Override
            public void success(XLDynamicCommentBean commentBean) {
                if(commentBean != null) {
                    dynamicBean.getCommentlist().add(commentBean);
                }
                if(dynamicBean.getCommentnum() != null){
                    dynamicBean.setCommentnum(dynamicBean.getCommentnum().intValue() + 1);
                }else {
                    dynamicBean.setCommentnum(1);
                }
                notifyDataSetChanged();
            }
        }).show(((Activity)context).getFragmentManager());
    }


    /**
     * 点赞
     * @param dynamicBean      动态Bean
     * @param position         动态position
     */
    private void supportDynamic(final XLDynamicBean dynamicBean, int position){
        if(position < 0 || dynamicBean == null){
            return;

        }
        if(dynamicBean.islike()){
            return;
        }
        GangSDK.getInstance().dynamicManager().supportDynamic(dynamicBean.getActionid(), !dynamicBean.islike(), new DataCallBack() {
            @Override
            public void onSuccess(int status, String message, Object data) {
                if (!dynamicBean.islike()) {
                    dynamicBean.setIssupport(1);
                    XLDynamicSupportBean supportBean = new XLDynamicSupportBean();
                    supportBean.setNickname(GangSDK.getInstance().userManager().getXlUserBean().getNickname());
                    supportBean.setUserid(GangSDK.getInstance().getUserid());
                    dynamicBean.getSupportlist().add(supportBean);
                    notifyDataSetChanged();
                } else {
                    dynamicBean.setIssupport(0);
                    if(dynamicBean.getSupportlist() != null && !dynamicBean.getSupportlist().isEmpty()){
                        for(int i = 0; i < dynamicBean.getSupportlist().size(); i ++ ){
                            if(dynamicBean.getSupportlist().get(i).getUserid() != null){
                                if(dynamicBean.getSupportlist().get(i).getUserid().intValue() == GangSDK.getInstance().getUserid()){
                                    dynamicBean.getSupportlist().remove(i);
                                    break;
                                }
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(String message) {
                XLToastUtil.showToastShort(message);
            }
        });
    }

    /**
     * 举报动态
     * @param dynamicid     动态id
     */
    private void reportDynamic(final Integer dynamicid) {
        if(dynamicid == null){
            return;
        }
        showDialogHint("确定举报这条内容吗", new DialogHintFragment.CallbackOnclick() {
            @Override
            public void confirm() {
                GangSDK.getInstance().dynamicManager().reportDynamic(dynamicid.intValue(), new DataCallBack() {
                    @Override
                    public void onSuccess(int status, String message, Object data) {
                        DynamicPopwindow.closedPopWindow();
                        XLToastUtil.showToastShort(message);
                    }

                    @Override
                    public void onFail(String message) {
                        XLToastUtil.showToastShort(message);
                    }
                });
            }

            @Override
            public void cancel() {

            }
        });
    }

    /**
     * 置顶动态
     * @param dynamicid     动态id
     */
    private void topDynamic(final Integer dynamicid) {
        if(dynamicid == null){
            return;
        }
        showDialogHint("确定置顶这条内容吗", new DialogHintFragment.CallbackOnclick() {
            @Override
            public void confirm() {
                GangSDK.getInstance().dynamicManager().topDynamic(dynamicid.intValue(), new DataCallBack() {
                    @Override
                    public void onSuccess(int status, String message, Object data) {
                        DynamicPopwindow.closedPopWindow();
                        XLToastUtil.showToastShort(message);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(String message) {
                        XLToastUtil.showToastShort(message);
                    }
                });
            }

            @Override
            public void cancel() {

            }
        });

    }

    /**
     * 删除动态
     * @param dynamicid     动态id
     * @param index         集合中元素下标
     */
    private void deleteDynamic(final Integer dynamicid, final int index) {
        if(dynamicid == null || index < 0){
            return;
        }
        showDialogHint("确定删除这条内容吗？", new DialogHintFragment.CallbackOnclick() {
            @Override
            public void confirm() {
                GangSDK.getInstance().dynamicManager().deleteDynamic(dynamicid.intValue(), new DataCallBack() {
                    @Override
                    public void onSuccess(int status, String message, Object data) {
                        DynamicPopwindow.closedPopWindow();
                        dynamicLsit.remove(index);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(String message) {
                        XLToastUtil.showToastShort(message);
                    }
                });
            }

            @Override
            public void cancel() {

            }
        });
    }

    /**
     * 隐藏动态
     * @param dynamicid     动态id
     * @param index         集合中元素下标
     */
    private void hideDynamic(final Integer dynamicid, final int index) {
        if(dynamicid == null || index < 0){
            return;
        }
        showDialogHint("确定隐藏这条内容吗？", new DialogHintFragment.CallbackOnclick() {
            @Override
            public void confirm() {
                GangSDK.getInstance().dynamicManager().hideDynamic(dynamicid.intValue(), new DataCallBack() {
                    @Override
                    public void onSuccess(int status, String message, Object data) {
                        DynamicPopwindow.closedPopWindow();
                        dynamicLsit.remove(index);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(String message) {
                        XLToastUtil.showToastShort(message);
                    }
                });
            }

            @Override
            public void cancel() {

            }
        });

    }

    /**
     *显示提示框
     */
    public void showDialogHint(String hintstr, DialogHintFragment.CallbackOnclick callbackOnclick){
        if(StringUtils.isEmpty(hintstr)){
            return;
        }
        if(callbackOnclick == null) {
            return;
        }
        new DialogHintFragment()
                .setMessage(hintstr)
                .setOnclickCallBack(callbackOnclick)
                .show(((Activity)context).getFragmentManager());
    }
}
