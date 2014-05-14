package com.hilifes.record;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.hilifes.R;
import com.hilifes.Utils;

public class HiRecordFileActivity extends Activity {
	private List<String> items;
	private ListView fileView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.title_file_list));
		
		setContentView(R.layout.filelist);
		fileView = (ListView) findViewById(R.id.listView1);

		updateFileList();
		fileView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				playFile(Utils.getOutputMediaFilePath()+File.separator+items.get(arg2));
			}
		});
		registerForContextMenu(fileView);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu,View v,ContextMenuInfo menuInfo)
	{
		if(v.getId()==R.id.listView1)
		{
			super.onCreateContextMenu(menu, v, menuInfo);
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.mediamenu, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info =(AdapterContextMenuInfo)item.getMenuInfo();
		String fileName = Utils.getOutputMediaFilePath()+File.separator+items.get(info.position);
		
		//Cannot use switch case here....Because we are using library
		if (item.getItemId() == R.id.send) {
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(new File(fileName).toURI().toString()));
			sendIntent.setType("audio/mpeg");
			startActivity(Intent.createChooser(sendIntent, getString(R.string.context_share_title)));
			return true;
		} else if (item.getItemId() == R.id.delete) {
			File f1 = new File(fileName);
			f1.delete();
			updateFileList();
			return true;
		} else if (item.getItemId() == R.id.append) {
			Intent intent = new Intent(this, GlobalParameters.serviceClass);
			intent.setAction(HiRecordService.ACTION_RECORD);
			intent.putExtra(HiRecordService.PARAM_FILENAME, fileName);
			startService(intent);
			finish();
			return true;
		} else if (item.getItemId() == R.id.rename) {
			final File file = new File(fileName);
			String name = file.getName().substring(0, file.getName().length()-4);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setTitle(getString(R.string.context_dialog_rename_title));
			
			final EditText input = new EditText(this);
			input.setText(name);
			builder.setView(input);
			builder.setPositiveButton(getString(R.string.context_rename), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String new_name = file.getParent()+File.separator+input.getText()+".mp3";
					file.renameTo(new File(new_name));
					updateFileList();
				}
			});
			builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//do nothing
				}
			});
			builder.show();
			updateFileList();
			return true;
		} else if (item.getItemId() == R.id.play) {
			playFile(fileName);
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}
	
	private void updateFileList()
	{
		File dir = new File(Utils.getOutputMediaFilePath());
		items = new ArrayList<String>();

		for(String file :dir.list()){

			if(file.endsWith(".3GP") || file.endsWith(".mp3") || file.endsWith(".raw")){
				items.add(file);
			}
		}
		fileView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items));
	}
	
	public void playFile(String fileName)
	{
		Intent intent = new Intent();  
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		File file = new File(fileName);  
		intent.setDataAndType(Uri.fromFile(file), "audio/*");  
		startActivity(intent);
	}
}
