package com.qm.gangsdk.ui.view.gangdynamic.dynamic;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qm.gangsdk.core.outer.common.utils.logger.Logger;
import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.base.XLBaseFragment;
import com.qm.gangsdk.ui.custom.ninegrid.ImageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijiyuan on 2018/1/3.
 *
 * 查看图片
 */

public class ImageBrowseFragment extends XLBaseFragment implements ViewTreeObserver.OnPreDrawListener{

    public static final String IMAGE_INFO = "IMAGE_INFO";           //url集合
    public static final String CURRENT_ITEM = "CURRENT_ITEM";       //查看图片下标
    public static final String CAN_DELETE = "CAN_DELETE";           //能否删除
    public static final int ANIMATE_DURATION = 200;                 //动画时间

    private ImageButton btnMenuLeft;
    private ImageButton btnMenuRight;
    private TextView tvTitle;

    private RelativeLayout rootView;

    private ImageBrowseAdapter imageBrowseAdapter;
    private List<ImageInfo> imageInfo = new ArrayList<>();
    private int currentItem = 0;
    private boolean canDelete = false;
    private int imageHeight;
    private int imageWidth;
    private int screenWidth;
    private int screenHeight;
    private ViewPager viewPager;
    private TextView textImageNum;

    @Override
    protected int getContentView() {
        return R.layout.fragmentxl_iamge_browse;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {
        btnMenuLeft = (ImageButton) view.findViewById(R.id.btnMenuLeft);
        btnMenuRight = (ImageButton) view.findViewById(R.id.btnMenuRight);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        textImageNum = (TextView) view.findViewById(R.id.textImageNum);
        rootView = (RelativeLayout) view.findViewById(R.id.rootView);
        tvTitle.setText("查看图片");
        Bundle bundle = getArguments();
        if(bundle != null) {
            imageInfo = (List<ImageInfo>) bundle.getSerializable(IMAGE_INFO);
            currentItem = bundle.getInt(CURRENT_ITEM, 0);
            canDelete = bundle.getBoolean(CAN_DELETE, false);
            if(canDelete){
                btnMenuRight.setVisibility(View.VISIBLE);
                btnMenuRight.setImageResource(R.mipmap.qm_btn_dynamiccomment_delete);
            }else {
                btnMenuRight.setVisibility(View.GONE);
            }
        }

        bindPageAdapter();
        DisplayMetrics metric = new DisplayMetrics();
        aContext.getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;
    }

    private void bindPageAdapter() {
        imageBrowseAdapter = new ImageBrowseAdapter(mContext, imageInfo);
        viewPager.setAdapter(imageBrowseAdapter);
        viewPager.setCurrentItem(currentItem);
        viewPager.getViewTreeObserver().addOnPreDrawListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                textImageNum.setText(currentItem + 1 + "/" + imageInfo.size());
            }
        });
        textImageNum.setText(currentItem + 1+ "/" + imageInfo.size());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnMenuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aContext.finish();
                finishActivityAnim();
            }
        });

        btnMenuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeleteClickListener != null) {
                    mDeleteClickListener.delete(currentItem);
                    if (imageInfo == null || imageInfo.isEmpty()) return;
                    if (imageInfo.size() <= currentItem) return;
                    imageInfo.remove(currentItem);
                    if (imageInfo.size() == 0) {
                        aContext.finish();
                    }
                    if (currentItem >= imageInfo.size()) {
                        currentItem = imageInfo.size() - 1;
                    }
                    textImageNum.setText(currentItem + 1 + "/" + imageInfo.size());
                    viewPager.removeAllViews();
                    viewPager.setAdapter(imageBrowseAdapter);
                    viewPager.setCurrentItem(currentItem);
                }
            }
        });
    }

    public static DeleteImageClickListener mDeleteClickListener;

    public interface DeleteImageClickListener{
        void delete(int position);
    }


    /** 绘制前开始动画 */
    @Override
    public boolean onPreDraw() {
        rootView.getViewTreeObserver().removeOnPreDrawListener(this);
        final View view = imageBrowseAdapter.getPrimaryItem();
        final ImageView imageView = imageBrowseAdapter.getPrimaryImageView();
        computeImageWidthAndHeight(imageView);

        final ImageInfo imageData = imageInfo.get(currentItem);
        final float vx = imageData.imageViewWidth * 1.0f / imageWidth;
        final float vy = imageData.imageViewHeight * 1.0f / imageHeight;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1) fraction = 1;
                view.setTranslationX(evaluateInt(fraction, imageData.imageViewX + imageData.imageViewWidth / 2 - imageView.getWidth() / 2, 0));
                view.setTranslationY(evaluateInt(fraction, imageData.imageViewY + imageData.imageViewHeight / 2 - imageView.getHeight() / 2, 0));
                view.setScaleX(evaluateFloat(fraction, vx, 1));
                view.setScaleY(evaluateFloat(fraction, vy, 1));
                view.setAlpha(fraction);
            }
        });
        addIntoListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();
        return true;
    }

    /** activity的退场动画 */
    public void finishActivityAnim() {
        final View view = imageBrowseAdapter.getPrimaryItem();
        final ImageView imageView = imageBrowseAdapter.getPrimaryImageView();
        if(imageView == null) return;
        computeImageWidthAndHeight(imageView);
        final ImageInfo imageData = imageInfo.get(currentItem);
        final float vx = imageData.imageViewWidth * 1.0f / imageWidth;
        final float vy = imageData.imageViewHeight * 1.0f / imageHeight;
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1) fraction = 1;
                view.setTranslationX(evaluateInt(fraction, 0, imageData.imageViewX + imageData.imageViewWidth / 2 - imageView.getWidth() / 2));
                view.setTranslationY(evaluateInt(fraction, 0, imageData.imageViewY + imageData.imageViewHeight / 2 - imageView.getHeight() / 2));
                view.setScaleX(evaluateFloat(fraction, 1, vx));
                view.setScaleY(evaluateFloat(fraction, 1, vy));
                view.setAlpha(1 - fraction);
            }
        });
        addOutListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();
    }

    /** 计算图片的宽高 */
    private void computeImageWidthAndHeight(ImageView imageView) {
        // 获取真实大小
        Drawable drawable = imageView.getDrawable();
        if(drawable == null) {
            Logger.e("ssssssss");
            return;
        }
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int intrinsicWidth = drawable.getIntrinsicWidth();
        // 计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
        float h = screenHeight * 1.0f / intrinsicHeight;
        float w = screenWidth * 1.0f / intrinsicWidth;
        if (h > w) h = w;
        else w = h;

        // 得出当宽高至少有一个充满的时候图片对应的宽高
        imageHeight = (int) (intrinsicHeight * h);
        imageWidth = (int) (intrinsicWidth * w);
    }

    /** 进场动画过程监听 */
    private void addIntoListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /** 退场动画过程监听 */
    private void addOutListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                aContext.finish();
                aContext.overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /** Integer 估值器 */
    public Integer evaluateInt(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int) (startInt + fraction * (endValue - startInt));
    }

    /** Float 估值器 */
    public Float evaluateFloat(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    public int getCurrentPosition(){
        if(currentItem >= 0) {
            return currentItem;
        }else {
            return 0;
        }
    }
}
