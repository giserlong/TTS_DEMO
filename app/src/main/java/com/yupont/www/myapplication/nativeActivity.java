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

import java.util.Locale;

public class nativeActivity extends AppCompatActivity implements OnInitListener {
    private Button speechButton;
    private EditText speechText;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        //初始化TTS
        tts = new TextToSpeech(this, this);
        //获取控件
        speechText = (EditText)findViewById(R.id.speechTextView);

        speechButton = (Button)findViewById(R.id.speechButton);
        //为button添加监听
        speechButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO Auto-generated method stub
                tts.speak(speechText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    @Override
    public void onInit(int status){
        // 判断是否转化成功
        if (status == TextToSpeech.SUCCESS){
            //tts.getCurrentEngine();
            //默认设定语言为中文，原生的android貌似不支持中文。
            int result = tts.setLanguage(Locale.CHINA);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                tts.setLanguage(Locale.US);
                Toast.makeText(this,"不支持中文，已自动设置为英文",Toast.LENGTH_SHORT).show();
                Log.d("ss","");
            }else{
                //不支持中文就将语言设置为英文
               // tts.setLanguage(Locale.US);
                Toast.makeText(this,"已自动设置为中文",Toast.LENGTH_SHORT).show();
                Log.d("ss","");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
