package com.QingLong.Interact;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

public class Networker extends AsyncTask<String, Void, Void> {
	Socket sock;
	PrintStream PS;
	String IP;
	int PORT;
	public Networker (String ip ,int port )
	{
		IP = ip;
		PORT = port;
	}
	@Override
	protected Void doInBackground(String... arg0) {

		//InputStreamReader IR = new InputStreamReader(sock.getInputStream());
		//BufferedReader BR = new BufferedReader(IR);

		//String Message = BR.readLine();

		/*while(Message != null){
					System.out.println(Message);
					Message = BR.readLine();
				}*/

		try {
			if(sock == null)
			{
				sock = new Socket(IP, PORT);
				PS  = new PrintStream(sock.getOutputStream());
			}

			PS.println(arg0[0]+","+arg0[1]);
			Log.d("Network",arg0[0]+","+arg0[1]);

		}
		catch (Exception e)
		{
			//Log.d("Exception thrown",e.getMessage());
		}

		return null;

	}

	@Override
	protected void onPostExecute(Void result) {
		//textResponse.setText(response);
		super.onPostExecute(result);
	}
	@Override
	protected void onCancelled() {
		try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}