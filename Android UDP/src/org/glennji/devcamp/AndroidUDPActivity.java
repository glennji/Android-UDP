package org.glennji.devcamp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.main)
public class AndroidUDPActivity extends Activity {

	@ViewById
	TextView hello;

	int PORT = 12345;
	String data = "Wake up, you!";
	String host = "10.0.1.129";

	@Click(R.id.button1)
	void startForkableComputation() {
		doSomeStuffInBackground();
	}

	@Background
	void doSomeStuffInBackground() {
		try {
			InetAddress address = InetAddress.getByName(host);
			DatagramSocket socket = new DatagramSocket(PORT);
			DatagramPacket packet = new DatagramPacket(data.getBytes(),
					data.length(), address, PORT);
			socket.send(packet);
			Log.d("AndroidUDP", "Sent: " + packet);
			publishProgress("Packet sent");
			byte[] buf = new byte[1024];
			DatagramPacket inputPacket = new DatagramPacket(buf, buf.length);
			socket.receive(inputPacket);
			String s = new String(packet.getData(), 0, packet.getLength());
			Log.d("AndroidUDP", "Received: " + s);
			publishProgress(s);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@UiThread
	void publishProgress(String update) {
		hello.setText(update);
	}

}