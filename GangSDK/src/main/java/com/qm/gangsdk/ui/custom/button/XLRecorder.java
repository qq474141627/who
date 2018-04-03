package com.qm.gangsdk.ui.custom.button;

import android.os.Environment;

import com.xl.undercover.mp3recorder.MP3Recorder;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lijiyuan on 2017/8/24.
 * 录音管理类
 */
public class XLRecorder {

	private MP3Recorder mp3Recorder;
	private File voiceFile = null;
	private boolean canRecord =true;


	public XLRecorder() {
		voiceFile = new File(Environment.getExternalStorageDirectory(),"gangsdk_temp.mp3");
	}

	public MP3Recorder getRecorder(){
		if(mp3Recorder == null){
			mp3Recorder = new MP3Recorder(voiceFile);
		}else {
			return mp3Recorder;
		}
		return mp3Recorder;
	}

	public boolean startRecord(){
		if(!isRecording() && canRecord){
			try {
				getRecorder().start();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean isRecording(){
		return	getRecorder().isRecording();
	}

	public void stopRecord(){
		if(isRecording()){
			canRecord = false;
			getRecorder().stop();
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					canRecord = true;
				}
			},500);
		}

	}

	public String getVoicePath(){
		return voiceFile.getAbsolutePath();
	}

	public boolean existsFile(){
		return (voiceFile != null && voiceFile.exists());
	}
	public void deleteFile(){
		if(voiceFile != null && voiceFile.exists()){
			voiceFile.delete();
		}
	}

}
