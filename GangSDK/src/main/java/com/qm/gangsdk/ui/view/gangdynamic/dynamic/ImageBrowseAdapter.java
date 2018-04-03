package com.qm.gangsdk.ui.view.gangdynamic.dynamic;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.custom.ninegrid.ImageInfo;
import com.qm.gangsdk.ui.custom.ninegrid.NineGridView;
import com.qm.gangsdk.ui.custom.photoview.OnPhotoTapListener;
import com.qm.gangsdk.ui.custom.photoview.PhotoView;
import com.qm.gangsdk.ui.utils.ImageLoadUtil;
import java.util.ArrayList;
import java.util.List;


/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/3/21
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImageBrowseAdapter extends PagerAdapter implements OnPhotoTapListener {

    private List<ImageInfo> imageInfo = new ArrayList<>();
    private Fragment context;
    private View currentView;

    public ImageBrowseAdapter(Fragment context, @NonNull List<ImageInfo> imageInfo) {
        super();
        this.imageInfo = imageInfo;
        this.context = context;
    }

    @Override
    public int getCount() {
        return imageInfo.size();
    }

    @Override
    public int getItemPosition(Object object) {
        View view = (View) object;
        int currentPage = ((ImageBrowseFragment)context).getCurrentPosition(); // Get current page index
        if(currentPage == (Integer)view.getTag()){
            return POSITION_NONE;
        }else{
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        currentView = (View) object;
    }

    public View getPrimaryItem() {
        return currentView;
    }

    public ImageView getPrimaryImageView() {
        return (ImageView) currentView.findViewById(R.id.pv);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context.getContext()).inflate(R.layout.item_photoview, container, false);
        view.setTag(position);
        final PhotoView imageView = (PhotoView) view.findViewById(R.id.pv);
        ImageInfo info = this.imageInfo.get(position);
        imageView.setOnPhotoTapListener(this);
        showExcessPic(info, imageView);
        ImageLoadUtil.loadNormalImage(imageView, info.bigImageUrl);
//        NineGridView.getImageLoader().onDisplayImage(context.getContext(), imageView, info.getThumbnailUrl());
        container.addView(view);
        return view;
    }

    /** 展示过度图片 */
    private void showExcessPic(ImageInfo imageInfo, PhotoView imageView) {
        //先获取大图的缓存图片
        Bitmap cacheImage = NineGridView.getImageLoader().getCacheImage(imageInfo.bigImageUrl);
        //如果大图的缓存不存在,在获取小图的缓存
        if (cacheImage == null) cacheImage = NineGridView.getImageLoader().getCacheImage(imageInfo.thumbnailUrl);
        //如果没有任何缓存,使用默认图片,否者使用缓存
        if (cacheImage == null) {
        } else {
            imageView.setImageBitmap(cacheImage);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /** 单击屏幕关闭 */
    @Override
    public void onPhotoTap(ImageView view, float x, float y) {
        ((ImageBrowseFragment)context).finishActivityAnim();
    }
}