package com.qm.gangsdk.ui.view.gangdynamic.dynamic.helperclass;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.utils.DensityUtil;

import static com.qm.gangsdk.ui.view.gangdynamic.dynamic.DynamicFragment.DYNAMIC_TYPE_GANG;
import static com.qm.gangsdk.ui.view.gangdynamic.dynamic.DynamicFragment.DYNAMIC_TYPE_MEMBERS;
import static com.qm.gangsdk.ui.view.gangdynamic.dynamic.DynamicFragment.DYNAMIC_TYPE_PERSONAL;

/**
 * Created by lijiyuan on 2018/1/12.
 */

public class DynamicPopwindow {
    private static PopupWindow mPopwindow = null;
    /**
     * 动态管理弹窗
     * @param context
     * @param viewparent
     * @param dynamicType
     * @param clickCallback
     */
    public static void showDynamicPopWindow(Context context, View viewparent, int dynamicType, final ClickCallback clickCallback){
        if(context == null || viewparent == null) return;
        int[] location = new int[2];
        viewparent.getLocationOnScreen(location);
        View view = LayoutInflater.from(context).inflate(R.layout.xldynamic_popwindow, null);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        TextView textMenuDelete = (TextView) view.findViewById(R.id.textMenuDelete);
        TextView textMenuReport = (TextView) view.findViewById(R.id.textMenuReport);
        TextView textMenuTop = (TextView) view.findViewById(R.id.textMenuTop);
        TextView textMenuHide = (TextView) view.findViewById(R.id.textMenuHide);
        View viewLine1 = view.findViewById(R.id.viewLine1);
        View viewLine2 = view.findViewById(R.id.viewLine2);
        switch (dynamicType) {
            case DYNAMIC_TYPE_GANG:
                textMenuReport.setVisibility(View.VISIBLE);
                textMenuTop.setVisibility(View.VISIBLE);
                textMenuHide.setVisibility(View.VISIBLE);
                viewLine1.setVisibility(View.VISIBLE);
                viewLine2.setVisibility(View.VISIBLE);
                break;
            case DYNAMIC_TYPE_MEMBERS:
                textMenuReport.setVisibility(View.VISIBLE);
                break;
            case DYNAMIC_TYPE_PERSONAL:
                textMenuDelete.setVisibility(View.VISIBLE);
                break;
        }

        textMenuDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickCallback != null){
                    clickCallback.deleteClick();
                }
            }
        });

        textMenuReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickCallback != null){
                    clickCallback.reportClick();
                }
            }
        });

        textMenuTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickCallback != null){
                    clickCallback.topClick();
                }
            }
        });

        textMenuHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickCallback != null){
                    clickCallback.hideClick();
                }
            }
        });

        mPopwindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(context, 30));
        mPopwindow.setFocusable(true);
        mPopwindow.setOutsideTouchable(true);
        mPopwindow.setBackgroundDrawable(new BitmapDrawable());
        mPopwindow.showAtLocation(viewparent, Gravity.NO_GRAVITY, location[0], location[1] + viewparent.getHeight());
    }


    /**
     * 关闭PopWindow
     */
    public static void closedPopWindow(){
        if(mPopwindow != null || mPopwindow.isShowing()){
            mPopwindow.dismiss();
        }
    }

    public interface ClickCallback{
        void deleteClick();
        void topClick();
        void hideClick();
        void reportClick();
    }
}
