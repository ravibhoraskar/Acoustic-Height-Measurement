package micro.phone;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MicrophoneThingActivity extends Activity {

	MediaRecorder recorder;
	File audiofile = null;
	private static final String TAG = "SoundRecordingActivity";
	private ToggleButton toggleButton;
	private TextView tv,tv2;
	private EditText threshBox;
	private Timer timer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		toggleButton= (ToggleButton)findViewById(R.id.toggleButton);
		tv=(TextView)findViewById(R.id.tv);
		tv2=(TextView)findViewById(R.id.tv2);
		threshBox=(EditText)findViewById(R.id.editText1);
	}

	public void toggle(View v) throws IllegalStateException, IOException
	{
		if(toggleButton.isChecked())
		{
			File sampleDir = Environment.getExternalStorageDirectory();
			try {
				audiofile = File.createTempFile("sound", ".3gp", sampleDir);
			} catch (IOException e) {
				Log.e(TAG, "sdcard access error");
				return;
			}
			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setOutputFile(audiofile.getAbsolutePath());
			recorder.prepare();
			recorder.start();
			timer = new Timer();
			timer.scheduleAtFixedRate(new RecorderTask(handler), 0, 1000);
			

		}
		else
		{
			timer.cancel();
			recorder.stop();
			recorder.release();
			audiofile.delete();
			//addRecordingToMediaLibrary();
			
		}
	}
	
	final Handler handler = new Handler();
//	{
//		public void HandleMssg(Message msg)
//		{
//			int amplitude = msg.arg1;
//			tv.setText("MicInfoService amplitude = "+amplitude);
//		}
//	};
	

    private class RecorderTask extends TimerTask {
    	Handler myhandler;
    	RecorderTask(Handler h)
    	{
    		myhandler=h;
    	}
    	
    	int i = 0;
    	public void run() {
    		final int amplitude = recorder.getMaxAmplitude();
            Log.e("haha","MicInfoService amplitude: " + amplitude);
            handler.post(new Runnable() {
				@Override
				public void run() {
					tv.setText("Amplitude = "+amplitude);
				}
			});
        }
    }
    
    int thresh = 10000;
    int samplingTime=250;
    public void setThresh(View v)
    {
    	thresh=Integer.parseInt(threshBox.getText().toString());
    }
    
    public void throwStone(View v) throws IllegalStateException, IOException
    {
		File sampleDir = Environment.getExternalStorageDirectory();
		try {
			audiofile = File.createTempFile("sound", ".3gp", sampleDir);
		} catch (IOException e) {
			Log.e(TAG, "sdcard access error");
			return;
		}
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(audiofile.getAbsolutePath());
		recorder.prepare();
		recorder.start();
		timer = new Timer();
		timer.scheduleAtFixedRate(new RecorderTask2(handler), 0, samplingTime);
    }
    
    private class RecorderTask2 extends TimerTask {
    	Handler myhandler;
    	RecorderTask2(Handler h)
    	{
    		myhandler=h;
    	}
    	
    	int timePassed = 0;
    	public void run() {
    		final int amplitude = recorder.getMaxAmplitude();
    		if(amplitude>thresh)
    		{
    			
    			final double time = timePassed*(samplingTime/1000.0);
    			final double height = 0.5*9.8*time*time;
    			handler.post(new Runnable() {
    				@Override
    				public void run() {
    					tv2.setText("Amp = "+amplitude+"\nTime = "+time+"\n" +
    							"Height = "+height);
    				}
    			});
    			timer.cancel();
    			recorder.stop();
    			recorder.release();
    			audiofile.delete();

    		}
    		else
    		{
    			timePassed++;
       			handler.post(new Runnable() {
    				@Override
    				public void run() {
    					tv.setText("Amplitude = "+amplitude);
    				}
    			});

    		}
    		
    		
            Log.e("haha","MicInfoService amplitude: " + amplitude);
            handler.post(new Runnable() {
				@Override
				public void run() {
					tv.setText("Amplitude = "+amplitude);
				}
			});
        }
    }
    
}
		