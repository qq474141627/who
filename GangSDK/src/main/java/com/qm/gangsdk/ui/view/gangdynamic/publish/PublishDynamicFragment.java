package com.qm.gangsdk.ui.view.gangdynamic.publish;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qm.gangsdk.core.outer.common.callback.DataCallBack;
import com.qm.gangsdk.core.outer.common.utils.StringUtils;
import com.qm.gangsdk.core.outer.common.utils.logger.Logger;
import com.qm.gangsdk.core.outer.receiver.GangPosterReceiver;
import com.qm.gangsdk.ui.GangSDK;
import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.base.XLBaseFragment;
import com.qm.gangsdk.ui.custom.button.XLRecorder;
import com.qm.gangsdk.ui.custom.button.XLRecorderButton;
import com.qm.gangsdk.ui.custom.dialog.ViewTools;
import com.qm.gangsdk.ui.custom.ninegrid.ImageInfo;
import com.qm.gangsdk.ui.event.XLCompletePublishDynamicEvent;
import com.qm.gangsdk.ui.utils.DensityUtil;
import com.qm.gangsdk.ui.utils.ImageLoadUtil;
import com.qm.gangsdk.ui.utils.XLToastUtil;
import com.qm.gangsdk.ui.view.common.DialogHintFragment;
import com.qm.gangsdk.ui.view.common.GangModuleManage;
import com.qm.gangsdk.ui.view.gangdynamic.dynamic.ImageBrowseFragment;
import com.xl.xlaudio.XLAudioClient;
import com.xl.xlaudio.XLAudioPlayerListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import xl.com.jph.takephoto.app.TakePhoto;
import xl.com.jph.takephoto.app.TakePhotoImpl;
import xl.com.jph.takephoto.compress.CompressConfig;
import xl.com.jph.takephoto.model.InvokeParam;
import xl.com.jph.takephoto.model.TContextWrap;
import xl.com.jph.takephoto.model.TImage;
import xl.com.jph.takephoto.model.TResult;
import xl.com.jph.takephoto.model.TakePhotoOptions;
import xl.com.jph.takephoto.permission.InvokeListener;
import xl.com.jph.takephoto.permission.PermissionManager;
import xl.com.jph.takephoto.permission.TakePhotoInvocationHandler;


/**
 * Created by lijiyuan on 2018/1/3.
 *
 * 发布动态
 */

public class PublishDynamicFragment extends XLBaseFragment implements TakePhoto.TakeResultListener,InvokeListener {

    private static final int MAX_PICTURE_SIZE = 9;

    private ImageButton btnMenuLeft;
    private TextView tvTitle;
    private View viewDynamicPublish;

    private RecyclerView recyclerView;
    private LinearLayout LlVoice;
    private LinearLayout areaVoice;
    private View imageVoice;
    private TextView textVoiceTime;
    private EditText editContent;
    private ImageButton btnVoiceDelete;
    private ImageButton btnTakePhoto;
    private ImageButton btnSelectPicture;
    private TextView textRecordTime;
    private TextView textRecordState;
    private XLRecorderButton btnVoiceRecord;

    private ImageAdapter mAdapter;
    private List<TImage> mImageList = new ArrayList<>();
    private String mVoiceFilePath = null;  //声音文件路径
    private int mVoiceFileTime = 0;

    private XLRecorder recorder;
    private TakePhoto mTakePhoto;
    private InvokeParam mInvokeParam;

    @Override
    protected int getContentView() {
        return R.layout.fragmentxl_publish_dynamic;
    }

    @Override
    protected void initData() {
        mVoiceFilePath = null;
        mVoiceFileTime = 0;
    }

    @Override
    protected void initView(View view) {
        btnMenuLeft = (ImageButton) view.findViewById(R.id.btnMenuLeft);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        viewDynamicPublish = view.findViewById(R.id.viewDynamicPublish);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LlVoice = (LinearLayout) view.findViewById(R.id.LlVoice);
        areaVoice = (LinearLayout) view.findViewById(R.id.areaVoice);
        imageVoice = view.findViewById(R.id.imageVoice);
        textVoiceTime = (TextView) view.findViewById(R.id.textVoiceTime);
        editContent = (EditText) view.findViewById(R.id.editContent);
        btnVoiceDelete = (ImageButton) view.findViewById(R.id.btnVoiceDelete);
        btnTakePhoto = (ImageButton) view.findViewById(R.id.btnTakePhoto);
        btnSelectPicture = (ImageButton) view.findViewById(R.id.btnSelectPicture);
        textRecordTime = (TextView) view.findViewById(R.id.textRecordTime);
        textRecordState = (TextView) view.findViewById(R.id.textRecordState);
        btnVoiceRecord = (XLRecorderButton) view.findViewById(R.id.btnVoiceRecord);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvTitle.setText(aContext.getResources().getString(R.string.dynamic_publish));
        btnMenuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aContext.finish();
            }
        });

        ImageBrowseFragment.mDeleteClickListener = new ImageBrowseFragment.DeleteImageClickListener() {
            @Override
            public void delete(int position) {
                if(position <mImageList.size() && position >= 0){
                    mImageList.remove(position);
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
        viewDynamicPublish.setVisibility(View.VISIBLE);
        //发布动态
        viewDynamicPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtils.isEmpty(editContent.getText().toString())
                        && StringUtils.isEmpty(mVoiceFilePath)
                        && (mImageList == null || mImageList.isEmpty())){
                    XLToastUtil.showToastShort("请写下您的心路历程吧~");
                    return;
                }
                final Dialog loading = ViewTools.createLoadingDialog(aContext,"正在发布...", false);
                loading.show();
                List<String> imageFilePaths = new ArrayList<>();
                if(mImageList != null) {
                    for (TImage image : mImageList) {
                        imageFilePaths.add(image.getCompressPath());
                    }
                }

                //上传动态
                GangSDK.getInstance().dynamicManager().publishDynamic(editContent.getText().toString(), mVoiceFilePath,
                        mVoiceFileTime, imageFilePaths , new DataCallBack() {
                    @Override
                    public void onSuccess(int status, String message, Object data) {
                        XLToastUtil.showToastShort("发布成功");
                        loading.dismiss();
                        aContext.finish();
                        GangPosterReceiver.post(new XLCompletePublishDynamicEvent());
                    }

                    @Override
                    public void onFail(String message) {
                        XLToastUtil.showToastShort(message);
                        loading.dismiss();
                    }
                });
            }
        });

        //播放录音
        areaVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageVoice.setBackgroundResource(R.drawable.qm_play_receiver_voice_anim);
                AnimationDrawable drawable = (AnimationDrawable) imageVoice.getBackground();
                drawable.start();
                XLAudioClient.sharedInstance().stopAll();
                XLAudioClient.sharedInstance().play(mVoiceFilePath, new XLAudioPlayerListener() {
                    @Override
                    public void onFinished(String url) {
                        imageVoice.setBackgroundResource(R.mipmap.qm_record_volume_left3);
                    }
                });
            }
        });

        //删除录音
        btnVoiceDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogHintFragment()
                        .setMessage("确定要删除这段录音吗？")
                        .setOnclickCallBack(new DialogHintFragment.CallbackOnclick() {
                            @Override
                            public void confirm() {
                                LlVoice.setVisibility(View.GONE);
                                XLAudioClient.sharedInstance().stopAll();
                                removeVoiceFile();
                                btnVoiceRecord.setRecordable(true);
                            }

                            @Override
                            public void cancel() {

                            }
                        })
                        .show(aContext.getFragmentManager());
            }
        });

        //拍照片
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageList.size() >= MAX_PICTURE_SIZE){
                    XLToastUtil.showToastShort("图片最多选择9张");
                    return;
                }

                File tempFile=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
                if (!tempFile.getParentFile().exists()) tempFile.getParentFile().mkdirs();
                Uri tempImageUri = Uri.fromFile(tempFile);
                configCompress(mTakePhoto, false);
                mTakePhoto.onPickFromCapture(tempImageUri);
            }
        });

        //选照片
        btnSelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageList.size() >= MAX_PICTURE_SIZE){
                    XLToastUtil.showToastShort("图片最多选择9张");
                    return;
                }
                configCompress(mTakePhoto, true);
                mTakePhoto.onPickMultiple(MAX_PICTURE_SIZE - mImageList.size());
            }
        });

        //录音
        recorder = btnVoiceRecord.getRecorder();
        btnVoiceRecord.setRecordable(true);
        btnVoiceRecord.setOnRecorderStateListener(new XLRecorderButton.OnRecorderStateListener() {
            @Override
            public void onUnrecord() {
                textRecordTime.setVisibility(View.INVISIBLE);
                textRecordState.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStartRecord() {
                if(!existVoiceFile()){
                    textRecordTime.setVisibility(View.VISIBLE);
                    textRecordState.setVisibility(View.VISIBLE);
                    textRecordState.setText("说话中");
                    textRecordTime.setText("");
                }else{
                    XLToastUtil.showToastShort("只能录制一条");
                    onUnrecord();
                }
            }

            @Override
            public void onRecording(float seconds) {
                textRecordTime.setText((int) Math.ceil(seconds) + "秒");
            }

            @Override
            public void onWantRecord() {
                textRecordState.setText("说话中");
            }

            @Override
            public void onWantCancel() {
                textRecordState.setText("手指松开，取消录音");
            }

            @Override
            public void onFinish(boolean success, float seconds) {
                textRecordTime.setVisibility(View.INVISIBLE);
                textRecordState.setVisibility(View.INVISIBLE);
                if(success){
                    if(!existVoiceFile()){
                        mVoiceFilePath = recorder.getVoicePath();
                        mVoiceFileTime = (int) Math.ceil(seconds);
                    }
                    LlVoice.setVisibility(View.VISIBLE);
                    textVoiceTime.setText(mVoiceFileTime + "″");
                    btnVoiceRecord.setRecordable(false);
                }
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),3));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = DensityUtil.dip2px(aContext, 5);
                outRect.right = DensityUtil.dip2px(aContext, 5);
                outRect.bottom = DensityUtil.dip2px(aContext, 5);
                outRect.top = DensityUtil.dip2px(aContext, 5);
            }
        });
        mAdapter = new ImageAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 需要上传的图片Adapter
     */
    class ImageAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageHolder holder = new ImageHolder(View.inflate(aContext, R.layout.item_publish_dynamic_picture, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ImageHolder mHolder = (ImageHolder) holder;
            final TImage tImage = mImageList.get(position);
            ImageLoadUtil.loadNormalImage(mHolder.ivPicture, tImage.getCompressPath());
            mHolder.ivPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
                    for(TImage image : mImageList){
                        ImageInfo imageInfo = new ImageInfo();
                        imageInfo.setBigImageUrl(image.getOriginalPath());
                        imageInfo.setBigImageUrl(image.getCompressPath());
                        imageInfoList.add(imageInfo);
                    }
                    GangModuleManage.toImageBrowseActivity(aContext, imageInfoList, position, true);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mImageList.size();
        }
    }

    class ImageHolder extends RecyclerView.ViewHolder{

        private ImageView ivPicture;
        private View view;

        public ImageHolder(View view) {
            super(view);
            this.view = view;
            ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
        }
    }

    /**
     *  获取TakePhoto实例
     * @return
     */
    public TakePhoto getTakePhoto(){
        if (mTakePhoto == null){
            mTakePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        return mTakePhoto;
    }

    private static void configCompress(TakePhoto takePhoto, boolean keepRawPhoto) {
        int maxSize = 1024 * 200;  //200kb
        int width = 1000;
        int height = 1000;
        boolean showProgressBar = true;
        boolean enableRawFile = false;
        CompressConfig config = new CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(keepRawPhoto)
                    .create();
        takePhoto.onEnableCompress(config, showProgressBar);
        TakePhotoOptions options = new TakePhotoOptions.Builder()
                .setCorrectImage(true)
                .create();
        takePhoto.setTakePhotoOptions(options);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type=PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PermissionManager.handlePermissionsResult(this.getActivity(), type, mInvokeParam, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void takeSuccess(TResult tResult) {
        for(TImage image : tResult.getImages()){
            Logger.d("result.getimge == " + image.getCompressPath());
        }
        mImageList.addAll(tResult.getImages());
        if(mImageList.size() > MAX_PICTURE_SIZE){
            for(int i = mImageList.size(); i > MAX_PICTURE_SIZE; i--){
                mImageList.remove(i-1);
            }
        }

        mHandler.sendEmptyMessage(0);
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Logger.d("result.getimge == handleMessage");
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void takeFail(TResult tResult, String s) {
        Logger.d("操作失败");
    }

    @Override
    public void takeCancel() {
        Logger.d("操作取消");
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
            this.mInvokeParam = invokeParam;
        }
        return type;
    }

    public void removeVoiceFile(){
        mVoiceFilePath = null;
        mVoiceFileTime = 0;
    }

    public boolean existVoiceFile(){
        return (mVoiceFilePath != null);
    }
}
