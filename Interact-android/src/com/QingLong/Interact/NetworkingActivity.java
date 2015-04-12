package com.QingLong.Interact;

import android.app.Activity;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NetworkingActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    // TODO Auto-generated method stub
	    setContentView(R.layout.activity_networking);
	    findViewById(R.id.btn_save).setOnClickListener(myhandler);
	   
	}
	View.OnClickListener myhandler = new View.OnClickListener() {
	    public void onClick(View v) {
	    	Intent i = new Intent();
	    	i.putExtra("ip", ((EditText)findViewById(R.id.txt_ip)).getText());
	    	i.putExtra("port", ((EditText)findViewById(R.id.txt_port)).getText());
	    	setResult(100,i);
	    	finish();
	    }
	  };
}
