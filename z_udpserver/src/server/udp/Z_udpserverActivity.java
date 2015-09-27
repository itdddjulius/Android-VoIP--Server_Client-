package server.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Z_udpserverActivity extends Activity {

	public Button start1,start2  ;
	public AudioTrack m_track = null;
	public Thread m_thread ;
	public boolean thread = true;
	public int SAMPLE_RATE = 8000;
	public int BUF_SIZE = 1024;
	public int buffersize = 1024;
	public byte[] buffer;
	public Button gdid,go,stop;
	DatagramPacket dp ;
	DatagramSocket ds = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        start1 = (Button) findViewById(R.id.dec);
        start1.setOnClickListener(handlestart);
        
        start2 = (Button) findViewById(R.id.nodec);
        start2.setOnClickListener(handles);
    }
	
	private final OnClickListener handlestart = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			try {
				
				startconn();
				
			} catch (SocketException e) {}
		}
	};
	
	private void startconn() throws SocketException
	{
		byte[] input = new byte[1024];
		byte[] decbyte = new byte[1024];
		dp = new DatagramPacket(input, input.length);
		ds = new DatagramSocket(11111);
		m_track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT, 8000,AudioTrack.MODE_STREAM);
		m_track.setPlaybackRate(SAMPLE_RATE);
		
		while(thread)
		{
			try {
				ds.receive(dp);
			} catch (IOException e) {		 
				Toast.makeText(getBaseContext(),"error", Toast.LENGTH_SHORT).show();
			}
			
			for(int i=0 ; i< input.length ; i++)
			{
				decbyte[i] = (byte) (input[i]^ 0xAA);
			}
			
			int wrote = m_track.write(decbyte, 0, decbyte.length);
			m_track.play();
		}
	
	}
	
	private final OnClickListener handles = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			byte[] input = new byte[1024];
			byte[] decbyte = new byte[1024];
			dp = new DatagramPacket(input, input.length);
			try {
				ds = new DatagramSocket(11111);
			} catch (SocketException e1) {}
			m_track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT, 8000,AudioTrack.MODE_STREAM);
			m_track.setPlaybackRate(SAMPLE_RATE);
			
			while(thread)
			{
				try {
					ds.receive(dp);
				} catch (IOException e) {		 
					Toast.makeText(getBaseContext(),"error", Toast.LENGTH_SHORT).show();
				}
				/*
				for(int i=0 ; i< input.length ; i++)
				{
					decbyte[i] = (byte) (input[i]^ 0xAA);
				}*/
				
				int wrote = m_track.write(input, 0, input.length);
				m_track.play();
			}
		}
	};
	
}