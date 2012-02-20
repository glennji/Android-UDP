package org.glennji.devcamp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.main)
public class AndroidUDPActivity extends Activity {

  @ViewById
  TextView output;

  @ViewById
  EditText hostname;

  @ViewById
  EditText port;

  String data = "Wake up, you!";

  @Click(R.id.sendPacketButton)
  void clickSendPacketButton() {
    DatagramSocket socket;
    try {
      final int portInt = Integer.parseInt(port.getText().toString());
      if (portInt < 1024) {
        printOutput("Can't send to a port below 1024");
        port.setText("1024");
      }
      socket = openSocket(portInt);
      sendPacket(socket, portInt);
      receiveResponse(socket);
    } catch (final NumberFormatException e) {
      printOutput("Port must be a number!");
    }
  }

  DatagramSocket openSocket(final int port) {
    try {
      return new DatagramSocket(port);
    } catch (final SocketException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Background
  void sendPacket(final DatagramSocket socket, final int port) {
    try {
      final InetAddress address = InetAddress.getByName(hostname.getText().toString());

      final DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), address,
          port);
      socket.send(packet);
      Log.d("AndroidUDP", "Sent: " + packet);
      printOutput("Packet sent, waiting for response");

    } catch (final UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final SocketException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Background
  void receiveResponse(final DatagramSocket socket) {
    try {
      final byte[] buf = new byte[1024];
      final DatagramPacket packet = new DatagramPacket(buf, buf.length);
      socket.setSoTimeout(3);
      socket.receive(packet);
      if (packet.getData().length < 1) {
        printOutput("Timed out with no response");
      } else {
        final String s = new String(packet.getData(), 0, packet.getLength());
        Log.d("AndroidUDP", "Received: " + s);
        printOutput(s);
      }
      socket.close();
    } catch (final SocketException sex) {
      printOutput("Socket exception! " + sex.getMessage());
    } catch (final IOException ioex) {
      printOutput("IOException! " + ioex.getMessage());
    }
  }

  @UiThread
  void printOutput(final String update) {
    output.append("\n" + update);
  }

}