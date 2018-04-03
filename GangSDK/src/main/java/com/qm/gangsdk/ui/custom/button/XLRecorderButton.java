package com.qm.gangsdk.ui.custom.button;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.qm.gangsdk.ui.custom.dialog.ViewTools;
import com.qm.gangsdk.ui.utils.FileSaveUtil;
import com.qm.gangsdk.ui.utils.XLToastUtil;

import java.io.IOException;

public class XLRecorderButton extends Button {

    private static final int STATE_UNRECORD = 1;
    private static final int STATE_START_RECORD = 2;
    private static final int STATE_RECORDING = 3;
    private static final int STATE_WANT_RECORD = 4;
    private static final int STATE_WANT_CANCEL = 5;
    private static final int STATE_FINISH = 6;
    private static final int STATE_CANCEL = 7;

    private static final int DISTANCE_Y_CANCEL = 50;
    private static final int OVERTIME = 60;
    private int mCurrentState = STATE_UNRECORD;

    private float mTime = 0;

    private XLRecorder recorder;
    private boolean canRecord = true;

    /**
     * 先实现两个参数的构造方法，布局会默认引用这个构造方法， 用一个 构造参数的构造方法来引用这个方法 * @param context
     */
    public XLRecorderButton(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public XLRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        recorder = new XLRecorder();
    }

    public void setRecordable(boolean canRecord){
        this.canRecord = canRecord;
    }

    /**
     * 录音完成后的回调，回调给activiy，可以获得mtime和文件的路径
     */

    public interface OnRecorderStateListener {
        void onUnrecord();

        void onStartRecord();

        void onRecording(float seconds);

        void onWantRecord();

        void onWantCancel();

        void onFinish(boolean success, float seconds);
    }
    private OnRecorderStateListener onRecorderStateListener;

    public void setOnRecorderStateListener(OnRecorderStateListener onRecorderStateListener) {
        this.onRecorderStateListener = onRecorderStateListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setState(STATE_START_RECORD);
                break;
            case MotionEvent.ACTION_MOVE:
                if (recorder.isRecording()) {
                    // 根据x，y来判断用户是否想要取消
                    if (wantToCancel(x, y)) {
                        setState(STATE_WANT_CANCEL);
                    } else {
                        setState(STATE_WANT_RECORD);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // 如果按的时间太短，还没准备好或者时间录制太短，就离开了，则显示这个dialog
                if (!recorder.isRecording() || mTime < 0.6f) {
                    setState(STATE_CANCEL);
                } else if (mCurrentState == STATE_WANT_RECORD) {// 正常录制结束
                    setState(STATE_FINISH);
                } else if (mCurrentState == STATE_WANT_CANCEL) {
                    setState(STATE_CANCEL);
                }
                reset();// 恢复标志位
                break;
            case MotionEvent.ACTION_CANCEL:
                reset();
                break;
        }

        return true;
//		return super.onTouchEvent(event);
    }

    /**
     * 回复标志位以及状态
     */
    private void reset() {
        setState(STATE_UNRECORD);
        mTime = 0;
    }

    private boolean wantToCancel(int x, int y) {
        // TODO Auto-generated method stub
        if (x < 0 || x > getWidth()) {// 判断是否在左边，右边，上边，下边
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    private void setState(int state) {
        // TODO Auto-generated method stub
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (mCurrentState) {
                case STATE_UNRECORD:
                    if(onRecorderStateListener != null){
                        onRecorderStateListener.onUnrecord();
                    }
                    break;
                case STATE_START_RECORD:
                    if(onRecorderStateListener != null){
                        onRecorderStateListener.onStartRecord();
                    }
                    if(canRecord){
                        if(recorder.startRecord()){
                            new Thread(mGetVoiceLevelRunnable).start();
                        }else{
                            XLToastUtil.showToastShort("点击太频繁了");
                            onRecorderStateListener.onUnrecord();
                        }
                    }
                    break;
                case STATE_RECORDING:

                    break;
                case STATE_WANT_RECORD:
                    if(onRecorderStateListener != null){
                        onRecorderStateListener.onWantRecord();
                    }
                    break;
                case STATE_WANT_CANCEL:
                    if(onRecorderStateListener != null){
                        onRecorderStateListener.onWantCancel();
                    }
                    break;
                case STATE_FINISH:
                    recorder.stopRecord();
                    if(onRecorderStateListener != null){
//                        BigDecimal b = new BigDecimal(mTime);
//                        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                        onRecorderStateListener.onFinish(true, mTime);
                    }
                    break;
                case STATE_CANCEL:
                    recorder.stopRecord();
                    if(onRecorderStateListener != null){
                        onRecorderStateListener.onFinish(false, 0);
                    }
                    break;
            }
        }
    }

    public XLRecorder getRecorder(){
        if(recorder == null){
            recorder = new XLRecorder();
        }
        return recorder;
    }

    // 准备三个常量
    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGE = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;
    private static final int MSG_OVERTIME_SEND = 0X113;

    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    break;
                case MSG_VOICE_CHANGE:
                    if(onRecorderStateListener != null){
                        onRecorderStateListener.onRecording(mTime);
                    }
                    break;
                case MSG_DIALOG_DIMISS:
                    break;
                case MSG_OVERTIME_SEND:
                    setState(STATE_FINISH);
                    break;
            }
        }

        ;
    };


    // 获取音量大小的runnable
    private Runnable mGetVoiceLevelRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (recorder.isRecording()) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mhandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                    if (mTime >= OVERTIME) {
                        mTime = OVERTIME;
                        mhandler.sendEmptyMessage(MSG_OVERTIME_SEND);
                        break;
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

}
