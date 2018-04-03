package com.qm.gangsdk.ui.view.gangdynamic.dynamic.helperclass;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.qm.gangsdk.core.outer.common.entity.XLDynamicCommentBean;
import com.qm.gangsdk.core.outer.common.entity.XLDynamicSupportBean;
import com.qm.gangsdk.core.outer.common.utils.StringUtils;
import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.view.common.GangModuleManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lijiyuan on 2018/1/10.
 *
 * 动态辅助类
 */

public class DynamicHelper {

    /**
     * 设置评论文本
     * @param context           上下文对象
     * @param commentText       显示文字控件
     * @param commentBean       评论bean
     * @param commentClick      点击事件
     */
    public static void setCommentContent(final Context context, TextView commentText, final XLDynamicCommentBean commentBean, View.OnClickListener commentClick){
        if(context == null) {
            return;
        }
        if(commentText == null) {
            return;
        }
        if(commentBean == null) {
            return;
        }
        if(StringUtils.isEmpty(commentBean.getNickname())){
            return;
        }
        String commentStr;
        if(!StringUtils.isEmpty(commentBean.getRefnickname())){
            commentStr = commentBean.getNickname() + "回复"
                    + commentBean.getRefnickname() + "：" + commentBean.getComment();
        }else {
            commentStr = commentBean.getNickname() + "：" + commentBean.getComment();
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(commentStr);
        int nickIndex = -1;
        int nickIndexend = -1;
        int refnickIndex = -1;
        nickIndex = commentStr.indexOf(commentBean.getNickname());
        if (!StringUtils.isEmpty(commentBean.getRefnickname())) {
            if (commentBean.getRefnickname().equals(commentBean.getNickname())) {
                refnickIndex = nickIndex + commentBean.getNickname().length() + 2;
            } else {
                refnickIndex = commentStr.indexOf(commentBean.getRefnickname());
            }
        }

        if(nickIndex >= 0) {
            if(refnickIndex >= 0){
                nickIndexend = nickIndex + commentBean.getNickname().length();
            }else {
                nickIndexend = nickIndex + commentBean.getNickname().length() + 1;
            }
            builder.setSpan(new Clickable(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(commentBean.getUserid() != null) {
                                GangModuleManage.toMemberInfoActivity(context, commentBean.getUserid().intValue());
                            }
                        }
                    }, context, true), nickIndex,
                    nickIndexend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if(refnickIndex >= 0) {
            builder.setSpan(new Clickable(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(commentBean.getRefuserid() != null) {
                                GangModuleManage.toMemberInfoActivity(context, commentBean.getRefuserid().intValue());
                            }
                        }
                    }, context, true), refnickIndex,
                    refnickIndex + commentBean.getRefnickname().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        commentText.setText(builder);
        if(commentClick != null) {
            commentText.setOnClickListener(commentClick);
        }
        commentText.setMovementMethod(LinkMovementMethod.getInstance());
    }


    /**
     * 设置点赞文本
     * @param context           上下文对象
     * @param textSupport       显示控件
     * @param list              点赞集合
     */
    public static void setSupprotContent(final Context context, TextView textSupport, List<XLDynamicSupportBean> list){
        if(context == null) {
            return;
        }
        if(textSupport == null) {
            return;
        }
        if(list == null || list.isEmpty()) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        final Map<String, Integer> params = new HashMap();
        for(int i = 0; i < list.size(); i++){
            if(i == list.size() -1) {
                stringBuffer.append(list.get(i).getNickname());
                params.put(list.get(i).getNickname(), list.get(i).getUserid());
            }else {
                stringBuffer.append(list.get(i).getNickname() + ",  ");
                params.put(list.get(i).getNickname(), list.get(i).getUserid());
            }
        }
        SpannableString builder = new SpannableString(stringBuffer);
        for(final String key : params.keySet()) {
            builder.setSpan(new Clickable(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GangModuleManage.toMemberInfoActivity(context, params.get(key));
                }
            }, context, false), stringBuffer.indexOf(key), stringBuffer.indexOf(key) + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textSupport.setText(builder);
        textSupport.setMovementMethod(LinkMovementMethod.getInstance());
    }


    /**
     * 文本区域点击事件
     */
    static class Clickable extends ClickableSpan {
        private final View.OnClickListener mListener;
        private Context context;
        private boolean isSetColor;
        public Clickable(View.OnClickListener click, Context context, boolean isSetColor) {
            this.mListener = click;
            this.context = context;
            this.isSetColor = isSetColor;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            if(isSetColor) {
                ds.setColor(ContextCompat.getColor(context, R.color.xldynamic_item_text_comment_nickname_color));//文本颜色
            }
            ds.setUnderlineText(false);//是否有下划线
        }

        @Override
        public void onClick(View v) {
            if (null != mListener) {
                mListener.onClick(v);
            }
        }
    }
}
