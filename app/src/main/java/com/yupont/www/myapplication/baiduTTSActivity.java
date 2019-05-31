package com.yupont.www.myapplication;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;

import java.util.Locale;

public class baiduTTSActivity extends AppCompatActivity  {
    private Button speechButton;
    private EditText speechText;
    private BaiDuSpeechUtil mBaiDuSpeechUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_tts);
        //初始化TTS

        //获取控件
        speechText = (EditText)findViewById(R.id.speechTextView);
        speechButton = (Button)findViewById(R.id.speechButton);
        startSpeakText();
        //为button添加监听
        speechButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO Auto-generated method stub
                speakText(speechText.getText().toString());
            }
        });




    }
    //   语音初始化及播报
    private void startSpeakText(){
        try {
            mBaiDuSpeechUtil = BaiDuSpeechUtil.getInstance();
            mBaiDuSpeechUtil.setInitialEnv(this);
            mBaiDuSpeechUtil.setInitialTts(this, new SpeechSynthesizerListener(){
                @Override
                public void onSynthesizeStart(String s) {
                    Log.i("",s);
                }

                @Override
                public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
                    Log.i("",s);
                }

                @Override
                public void onSynthesizeFinish(String s) {
                    Log.i("",s);
                }

                @Override
                public void onSpeechStart(String s) {
                    Log.i("",s);
                }

                @Override
                public void onSpeechProgressChanged(String s, int i) {
                    Log.i("",s);
                }

                @Override
                public void onSpeechFinish(String s) {
                    Log.i("",s);
                }

                @Override
                public void onError(String s, SpeechError speechError) {
Log.i("",s+speechError.toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void speakText(String text) {
        if (mBaiDuSpeechUtil != null) {
            mBaiDuSpeechUtil.speakText(text);
        }
    }


}
