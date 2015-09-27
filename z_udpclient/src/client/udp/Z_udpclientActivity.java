package client.udp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.R.string;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Z_udpclientActivity extends Activity {

	public Button send;
	
	public int SAMPLE_RATE = 8000;
	public int BUF_SIZE = 1024;
	public int buffersize;
	public byte[] buffer1;
	public byte[] encbyte; 
	public AudioRecord m_record ;
	public MediaPlayer m_play;
	public AudioTrack m_track;
	public File outfile ;
	public FileOutputStream fos;
	public FileInputStream fis;
	public Thread m_thread ;
	public boolean recording = false;
	public boolean playing = false;
	public boolean thread = true;
	public boolean thread1 = false;
	public boolean thread2 = false;
	public int read;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		buffersize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
		buffersize = Math.max(buffersize, AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT));
		
		buffersize = Math.max(buffersize, BUF_SIZE);
		
		buffer1 = new byte[buffersize];
		encbyte = new byte[buffersize];
		m_record = new AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT ,buffersize);
		
		///////////////////////////////////////////
        
		send = (Button) findViewById(R.id.send);
        send.setOnClickListener(handlesend);
        
        
    }
	
	private final OnClickListener handlesend = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			recording = true;
			m_record.startRecording();
			Runnable r1 = new Runnable() {
	                public void run() {
						while (thread) 
						{
	                       read = m_record.read(buffer1, 0, buffersize);						   					
	                       try {
							udpsend();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                      
						}
	                }
	        };
	        
	        m_thread = new Thread(r1);
			m_thread.start();
			
		}
	};
	
	private void udpsend() throws Exception
	{
		System.out.println("0");
		
		for(int i=0 ; i<read ; i++)
		{
			encbyte[i] = (byte) (buffer1[i]^ 0xAA);
		}
		
		//String str1 = new String(buffer1, "UTF-8");
		//String str2	 = StringCryptor.encrypt(new String(PASSWORD) , str1);
		//encbyte = str2.getBytes();
		System.out.println("1");
		DatagramSocket ds = null;
    	ds = new DatagramSocket();
		InetAddress serverAddr = InetAddress.getByName("172.24.195.110");
		DatagramPacket dp = null;
		System.out.println("2");
		dp = new DatagramPacket(encbyte, encbyte.length, serverAddr, 11111);
		//dp = new DatagramPacket(buffer1, buffer1.length, serverAddr, 11111);
		System.out.println("3");
		ds.send(dp);
		System.out.println("4");
	}
}