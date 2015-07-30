package cn.v6.sixrooms.im.audio.handler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


public class AudioFlvReaderThread extends Thread {
	private AudioPlayHandler audioPlayFoctoy;
	private RandomAccessFile dis;
	private long fileLength;
	int INIT_SEEK = 14;// flvHeadr(9)+tagSize(4)+tagHeader.dataType(1);to first  data size region
    long seek =INIT_SEEK;

    private boolean isEnd = false;

	public AudioFlvReaderThread(AudioPlayHandler audioPlayFoctoy){
		this.audioPlayFoctoy = audioPlayFoctoy;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		android.os.Process
		.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		try {
			int totalTime = open(audioPlayFoctoy.getFilePath());
			audioPlayFoctoy.setTotalTime(totalTime);
			while (audioPlayFoctoy.isPlaying() && !isEnd) {
				EncodedData readData = readData();
				audioPlayFoctoy.setEncodedData(readData);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			audioPlayFoctoy.setStop(true);
			close();
		}
		
		
	}
	private int open(final File file) throws IOException {
		dis = new RandomAccessFile(file, "r");
		fileLength = file.length();
		dis.seek(fileLength-4);
		byte[] lastTagSizes = new byte[4];
		dis.read(lastTagSizes, 0, 4);
		int lastTagSize = read4byteToint(lastTagSizes, 0);
		dis.seek(fileLength-4-lastTagSize+4);
		byte[] totalTimes = new byte[4];
		dis.read(totalTimes, 0, 4);
		int totalTime = read4byteToIntTm(totalTimes, 0);
		dis.seek(INIT_SEEK);
		return totalTime;
	}



	private int open(final String filename) throws IOException {
	   return	open(new File(filename));
	}

	private void close() {
		if (null != dis) {
			try {
				dis.close();
				dis = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (null != dis) {
					try {
						dis.close();
					} catch (Exception ex) {
					}
				}
			}
		}

	}

	private EncodedData readData() throws Exception {
		EncodedData encodedData2 = new EncodedData();
			byte datasizes[] = new byte[3];
	        byte[] timestamp = new byte[4];
			dis.read(datasizes, 0, 3);// 3 -data size length
			encodedData2.size = read3byteToint(datasizes, 0) - 1;//
			seek = seek+3;
			dis.seek(seek);
			
			dis.read(timestamp, 0, 4);
			encodedData2.time = read4byteToIntTm(timestamp, 0);
			seek =seek+8;//timestamp(4)+streamed(3)+audioDataDescription(1);
			dis.seek(seek);
			dis.read(encodedData2.encoded, 0, encodedData2.size);
			seek = seek + encodedData2.size + 5;// 5 = tagSize(4)+dataTypeOfTagHeader(1)
			if (seek >= fileLength) {
				isEnd = true;
			} else {
				dis.seek(seek);
			}
		return encodedData2;
	}
	
	private int read3byteToint(byte[] src, int offset) {
		// TODO Auto-generated method stub
		int value = (int) (((0 & 0xFF) << 24) | ((src[offset] & 0xFF) << 16)
				| ((src[offset + 1] & 0xFF) << 8) | (src[offset + 2] & 0xFF));
		return value;
	}
	private int read4byteToint(byte[] src, int offset) {
		// TODO Auto-generated method stub
		int value = (int) (((src[offset] & 0xFF) << 24) | ((src[offset+1] & 0xFF) << 16)
				| ((src[offset + 2] & 0xFF) << 8) | (src[offset + 3] & 0xFF));
		return value;
	}
	
	/**
	 * @return tm
	 * 
	 */
	private int read4byteToIntTm(byte[] src ,int offset){
		int value = (int) ( ((src[offset] & 0xFF) << 16)
				| ((src[offset + 1] & 0xFF) << 8) | (src[offset + 2] & 0xFF)|((src[offset +3] & 0xFF) << 24));
		return value;
	}
}
