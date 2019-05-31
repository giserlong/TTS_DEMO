package com.yupont.www.myapplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;

public class yzsTTSOfflineActivity extends Activity {

	private static boolean TTS_PLAY_FLAGE = false;

	private EditText mTTSText;
	private TextView mTextViewTip;
	private TextView mTextViewStatus;
	private Button mTTSPlayBtn;
	private SpeechSynthesizer mTTSPlayer;
	private final String mFrontendModel= Environment.getExternalStorageDirectory().toString()+"/Yupont/UAV/OfflineTTSModels/frontend_model";
	private final String mBackendModel =   Environment.getExternalStorageDirectory().toString()+"/Yupont/UAV/OfflineTTSModels/backend_lzl";
//	private final String mFrontendModel= getClass().getClassLoader().getResource("assets/OfflineTTSModels/frontend_model").getPath().substring(5);
//	private final String mBackendModel =   getClass().getClassLoader().getResource("assets/OfflineTTSModels/backend_lzl").getPath();
//
	@Override
	public void onCreate(Bundle savedInstanceState) {
	//	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yzs_offline_tts);
		copyFilesFassets(this,"OfflineTTSModels", Environment.getExternalStorageDirectory().toString()+"/Yupont/UAV/OfflineTTSModels");

		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.status_bar_main);

		mTTSText = (EditText) findViewById(R.id.textViewResult);
		//mTextViewStatus = (TextView) findViewById(R.id.textViewStatus);
		//mTextViewTip = (TextView) findViewById(R.id.textViewTip);
		mTTSPlayBtn = (Button) findViewById(R.id.recognizer_btn);
		mTTSPlayBtn.setEnabled(false);
		mTTSPlayBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				TTSPlay();
			}
		});

		// 初始化本地TTS播报
		initTts();
	}

	/**
	 *  从assets目录中复制整个文件夹内容
	 *  @param  context  Context 使用CopyFiles类的Activity
	 *  @param  oldPath  String  原文件路径  如：/aa
	 *  @param  newPath  String  复制后路径  如：xx:/bb/cc
	 */
	public void copyFilesFassets(Context context, String oldPath, String newPath) {
		try {
			String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
			if (fileNames.length > 0) {//如果是目录
				File file = new File(newPath);
				file.mkdirs();//如果文件夹不存在，则递归
				for (String fileName : fileNames) {
					copyFilesFassets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
				}
			} else {//如果是文件
				if(new File(newPath).exists()){
					return;
				}
				InputStream is = context.getAssets().open(oldPath);
				FileOutputStream fos = new FileOutputStream(new File(newPath));
				byte[] buffer = new byte[1024];
				int byteCount=0;
				while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
					fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
				}
				fos.flush();//刷新缓冲区
				is.close();
				fos.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//如果捕捉到错误则通知UI线程
			//MainActivity.handler.sendEmptyMessage(COPY_FALSE);
		}
	}


	/**
	 * 初始化本地离线TTS
	 */
	private void initTts() {

		// 初始化语音合成对象
		try {
			mTTSPlayer = new SpeechSynthesizer(this, Config.appKey, Config.secret);


		// 设置本地合成
		mTTSPlayer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_LOCAL);
		File _FrontendModelFile = new File(mFrontendModel);
		if (!_FrontendModelFile.exists()) {
			toastMessage("文件：" + mFrontendModel + "不存在，请将assets下相关文件拷贝到SD卡指定目录！");
		}
		File _BackendModelFile = new File(mBackendModel);
		if (!_BackendModelFile.exists()) {
			toastMessage("文件：" + mBackendModel + "不存在，请将assets下相关文件拷贝到SD卡指定目录！");
		}
		// 设置前端模型
		mTTSPlayer.setOption(SpeechConstants.TTS_KEY_FRONTEND_MODEL_PATH, mFrontendModel);
		// 设置后端模型
		mTTSPlayer.setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, mBackendModel);
		// 设置回调监听
		mTTSPlayer.setTTSListener(new SpeechSynthesizerListener() {

			@Override
			public void onEvent(int type) {
				switch (type) {
					case SpeechConstants.TTS_EVENT_INIT:
						// 初始化成功回调
						log_i("onInitFinish");
						mTTSPlayBtn.setEnabled(true);
						break;
					case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
						// 开始合成回调
						log_i("beginSynthesizer");
						break;
					case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
						// 合成结束回调
						log_i("endSynthesizer");
						break;
					case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
						// 开始缓存回调
						log_i("beginBuffer");
						break;
					case SpeechConstants.TTS_EVENT_BUFFER_READY:
						// 缓存完毕回调
						log_i("bufferReady");
						break;
					case SpeechConstants.TTS_EVENT_PLAYING_START:
						// 开始播放回调
						log_i("onPlayBegin");
						break;
					case SpeechConstants.TTS_EVENT_PLAYING_END:
						// 播放完成回调
						log_i("onPlayEnd");
						setTTSButtonReady();
						break;
					case SpeechConstants.TTS_EVENT_PAUSE:
						// 暂停回调
						log_i("pause");
						break;
					case SpeechConstants.TTS_EVENT_RESUME:
						// 恢复回调
						log_i("resume");
						break;
					case SpeechConstants.TTS_EVENT_STOP:
						// 停止回调
						log_i("stop");
						break;
					case SpeechConstants.TTS_EVENT_RELEASE:
						// 释放资源回调
						log_i("release");
						break;
					default:
						break;
				}

			}

			@Override
			public void onError(int type, String errorMSG) {
				// 语音合成错误回调
				log_i("onError");
				toastMessage(errorMSG);
				setTTSButtonReady();
			}
		});
		// 初始化合成引擎
		mTTSPlayer.init("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void TTSPlay() {
		if (!TTS_PLAY_FLAGE) {
			mTTSPlayer.playText(mTTSText.getText().toString());
			setTTSButtonStop();
		} else {
			mTTSPlayer.stop();
			setTTSButtonReady();
		}

	}

	private void setTTSButtonStop() {
		TTS_PLAY_FLAGE = true;
		mTTSPlayBtn.setText(R.string.stop_tts);
	}

	private void setTTSButtonReady() {
		mTTSPlayBtn.setText(R.string.start_tts);
		TTS_PLAY_FLAGE = false;
	}

	protected void setTipText(String tip) {

		mTextViewTip.setText(tip);
	}

	protected void setStatusText(String status) {

		mTextViewStatus.setText(getString(R.string.lable_status) + "(" + status + ")");
	}

	@Override
	public void onPause() {
		super.onPause();
		// 主动停止识别
		if (mTTSPlayer != null) {
			mTTSPlayer.stop();
		}
	}

	private void log_i(String log) {
		Log.i("demo", log);
	}

	@Override
	protected void onDestroy() {
		// 主动释放离线引擎
		if (mTTSPlayer != null) {
			mTTSPlayer.release(SpeechConstants.TTS_RELEASE_ENGINE, null);
		}
		super.onDestroy();
	}

	private void toastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}
