package com.hilifes.record;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.hilifes.R;

public class HiRecordPrefActivity extends Activity {
	private final String TAG = HiRecordPrefActivity.class.toString();

	private final int[] 	Source={MediaRecorder.AudioSource.DEFAULT,MediaRecorder.AudioSource.MIC,MediaRecorder.AudioSource.CAMCORDER,MediaRecorder.AudioSource.VOICE_CALL,MediaRecorder.AudioSource.VOICE_DOWNLINK,MediaRecorder.AudioSource.VOICE_RECOGNITION,MediaRecorder.AudioSource.VOICE_UPLINK};
	private final String[] 	SourceS = {"Default","Mic","Cam Recorder","Voice Call","Voice Downlink","Voice Recognition","Voice Uplink"};

	private final int[] 	Config={AudioFormat.CHANNEL_IN_DEFAULT,AudioFormat.CHANNEL_IN_MONO,AudioFormat.CHANNEL_IN_STEREO};
	private final String[] 	ConfigS={"Default","Mono","Stereo"};

	private final int[] 	Format={AudioFormat.ENCODING_DEFAULT,AudioFormat.ENCODING_PCM_8BIT,AudioFormat.ENCODING_PCM_16BIT};
	private final String[] 	FormatS={"Default","8 bits","16 bits"};


	private final int[] 	Frequency = {8000, 11025, 16000, 22050, 44100};
	private final String[] 	FrequencyS = {"8000", "11025", "16000", "22050", "44100"};

	private final int[] mp3brate={8,16,24,32,40,48,56,64,80,96,112,128};
	private final String[] mp3brateS={"8","16","24","32","40","48","56","64","80","96","112","128"};

	private final int[] mp3quality={0,1,2,3,4,5,6,7,8,9};
	private final String[] mp3qualityS={"0 (best quality)","1","2","3","4","5 (standard)","6","7","8","9 (fastest)"};

	private	Spinner	sourceSpinner;
	private	Spinner	freqSpinner;
	private	Spinner	configSpinner;
	private	Spinner	formatSpinner;
	private Spinner mp3brateSpinner;
	private Spinner mp3qualitySpinner;

	private Button acceptButton;
	private Button cancelButton;

	private boolean getValidSampleRates(int rate,int format,int encoding) {
		int bufferSize = AudioRecord.getMinBufferSize(rate, format,encoding);
		if (bufferSize > 0) {
			return true;
		}
		return false;
	}

	private int iSource;
	private int iFreq;
	private int iConfig;
	private int iFormat;

	private int iMp3Brate;
	private int iMp3Quality;


	private final int spinner_item_id = android.R.layout.simple_spinner_item;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.audio_pref_name));
		setContentView(R.layout.preferencesaudio);

		sourceSpinner = (Spinner) findViewById(R.id.spinner1);
		freqSpinner = (Spinner) findViewById(R.id.spinner2);
		configSpinner = (Spinner) findViewById(R.id.spinner3);
		formatSpinner = (Spinner) findViewById(R.id.spinner4);
		mp3brateSpinner = (Spinner) findViewById(R.id.spinner5);
		mp3qualitySpinner = (Spinner) findViewById(R.id.spinner6);


		acceptButton = (Button)findViewById(R.id.btnOk);
		cancelButton =(Button) findViewById(R.id.btnCancel);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, spinner_item_id,SourceS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sourceSpinner.setAdapter(adapter);
		

		adapter = new ArrayAdapter<String>(this, spinner_item_id,FrequencyS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		freqSpinner.setAdapter(adapter);

		adapter = new ArrayAdapter<String>(this, spinner_item_id,ConfigS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		configSpinner.setAdapter(adapter);

		adapter = new ArrayAdapter<String>(this, spinner_item_id,FormatS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		formatSpinner.setAdapter(adapter);

		adapter = new ArrayAdapter<String>(this,spinner_item_id,mp3qualityS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mp3qualitySpinner.setAdapter(adapter);

		adapter = new ArrayAdapter<String>(this,spinner_item_id,mp3brateS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mp3brateSpinner.setAdapter(adapter);

		GlobalParameters.loadParams(getApplicationContext());


		loadParams();

		sourceSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				iSource = Source[arg2];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		}); 

		freqSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				iFreq = Frequency[arg2];

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		}); 

		configSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				iConfig = Config[arg2];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		}); 

		formatSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				iFormat = Format[arg2];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		}); 


		mp3brateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				iMp3Brate=mp3brate[arg2];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});


		mp3qualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				iMp3Quality=mp3quality[arg2];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});



		acceptButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(CheckFormat())
				{

					saveAndReturn();
				}
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mainActivity();
			}
		});
	}

	private void saveAndReturn()
	{
		saveParams();
		mainActivity();
	}

	private int	getIndex(int value,int[] array)
	{
		int index=0;
		for(int val : array){
			if(val==value)
				return index;
			index++;
		}
		return -1;
	}

	private void loadParams()
	{
		GlobalParameters.loadParams(getApplicationContext());

		iMp3Brate=GlobalParameters.mp3BitRate;
		iMp3Quality = GlobalParameters.mp3Quality;


		iFreq = GlobalParameters.audioFrequency;
		iSource = GlobalParameters.audioSource;	
		iConfig = GlobalParameters.audioChannelConfig;
		iFormat = GlobalParameters.audioFormat;
		setSelector();
	}

	private void setSelector()
	{
		sourceSpinner.setSelection(getIndex(iSource,Source));
		freqSpinner.setSelection(getIndex(iFreq,Frequency));
		configSpinner.setSelection(getIndex(iConfig,Config));
		formatSpinner.setSelection(getIndex(iFormat,Format));

		mp3brateSpinner.setSelection(getIndex(iMp3Brate,mp3brate));
		mp3qualitySpinner.setSelection(getIndex(iMp3Quality,mp3quality));
	}

	private void saveParams()
	{
		//Set GlobalParameters
		GlobalParameters.audioSource=iSource;
		GlobalParameters.audioFrequency=iFreq;
		GlobalParameters.audioChannelConfig=iConfig;
		GlobalParameters.audioFormat=iFormat;

		GlobalParameters.mp3Quality=iMp3Quality;
		GlobalParameters.mp3BitRate=iMp3Brate;
		GlobalParameters.saveParams(getApplicationContext());
	}


	private void mainActivity(){
		finish();
	}

	private boolean CheckFormat()
	{
		if(getValidSampleRates(iFreq,iConfig,iFormat))
		{
			return true;
		}
		else
		{
			//Show dialog to ask the user if he really accepts the parameters
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("The configuration seems invalid, are you sure you want to keep thoose settings?")
				.setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   saveAndReturn();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
			
			builder.show();
			return false;
		}
	}

}
