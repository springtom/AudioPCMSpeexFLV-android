package cn.v6.sixrooms.im.audio.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.media.AudioFormat;

import cn.v6.sixrooms.utils.LogUtils;

/**
 * 写flv文件线程
 * 
 * @author xingchun
 * 
 */
public class AudioFlvWriterThread extends Thread {
	private static final String TAG = AudioFlvWriterThread.class
			.getSimpleName();
	private FileOutputStream out;
	private int currentTagDataSize = 0;
	private int dataType;
	private int audioDataType;
	private int SPEEX = 11;
	private int AUDIO = 8;
	private int currentTime = 0;
	private AudioRecordHandler factory;
	private int nframes;// 1--snd16Bit 0 -- snd8Bit
	private int channels; // 1 -- sndStereo 0 -- sndMomo
	private int sampleRate;// 0 -- 5.5KHz 1 -- 11kHz 2 -- 22kHz 3 -- 44kHz

	// private String filePath;
	// private boolean isRecording;
	// private int sampleRate;// 0 -- 5.5KHz 1 -- 11kHz 2 -- 22kHz 3 -- 44kHz
	// private int nframes;// 1--snd16Bit 0 -- snd8Bit
	// private int channels;// 1 -- sndStereo 0 -- sndMomo

	public AudioFlvWriterThread(AudioRecordHandler factory) {
		this.factory = factory;
		dataType = AUDIO;
		audioDataType = SPEEX;
		if (factory.getChannels() == AudioFormat.CHANNEL_IN_MONO) {
			channels = 0;
		} else {
			channels = 1;
		}
		if (factory.getNframes() == AudioFormat.ENCODING_PCM_16BIT) {
			nframes = 1;
		} else {
			nframes = 0;
		}
		switch (factory.getSampleRate()) {
		case 44100:
			this.sampleRate = 3;
			break;
		case 22050:
			this.sampleRate = 2;
			break;
		case 11025:
			this.sampleRate = 1;
			break;
		case 5500:
			this.sampleRate = 0;
			break;
		default:
			this.sampleRate = 0;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		try {
			openFile();
			writeHeader();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		try {
			while (factory.isRecording()) {
				EncodedData encodedData;
				if ((encodedData = factory.getEncoded()) != null) {
					try {
						writeTag(encodedData.encoded, encodedData.size);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
					}
				} else {
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
			if (factory.isCancel()) {
				cancel();
			}
		}

	}

	private void openFile() throws FileNotFoundException {
		// TODO Auto-generated method stub
		File file = new File(factory.getFilePath());
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		file.delete();
		out = new FileOutputStream(file);
		if (out == null) {
			LogUtils.i(TAG, "文件打开失败，输出流为空！");
		}
	}

	/**
	 * 写flv文件头
	 * 
	 * @throws IOException
	 */
	private void writeHeader() throws IOException {
		if (out == null) {
			return;
		}
		byte headers[] = new byte[13];
		writeString(headers, 0, "FLV");
		write1Int(headers, 3, 1);
		write1Int(headers, 4, 4);
		write4Int(headers, 5, 9);
		write4Int(headers, 9, 0);
		out.write(headers);
	}

	/**
	 * 写flv文件tag（tagHeader+tagData+tagSize）
	 * 
	 * @param data
	 * @param datasize
	 * @throws IOException
	 */
	private void writeTag(byte[] data, int datasize) throws IOException {
		if (out == null) {
			return;
		}
		currentTagDataSize = datasize + 1;// 每个data区都会在data的前面添加一个关于data信息的字节
		byte tagHeaderbuf[] = new byte[11];
		byte tagDatabuf[] = new byte[currentTagDataSize];
		byte TagSizebuf[] = new byte[4];// 当前tag的大小，flvHeader后的4个0已在flvHeader中写入
		write1Int(tagHeaderbuf, 0, dataType);// 类型(1)
		write3Int(tagHeaderbuf, 1, currentTagDataSize);// 数据区大小(3)
		writeTime4Int(tagHeaderbuf, 4, currentTime);// 时间戳(4)
		currentTime = currentTime + 20;
		write3Int(tagHeaderbuf, 8, 0);// streamid-总是0(3)
		write1Int(tagDatabuf, 0, audioDataType, sampleRate, nframes, channels);// 数据类型(1)
		System.arraycopy(data, 0, tagDatabuf, 1, datasize);// 数据
		write4Int(TagSizebuf, 0, currentTagDataSize + 11);// tag大小

		out.write(tagHeaderbuf);
		out.write(tagDatabuf);
		out.write(TagSizebuf);
	}

	private void cancel() {
		// TODO Auto-generated method stub
		File file = new File(factory.getFilePath());
		if (file.isFile()) {
			file.delete();
			file = null;
		}
	}

	private void close() {
		if (out != null) {
			try {
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (out != null)
					try {
						out.close();
						out = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	}

	public static void write3Int(byte[] data, int offset, int v) {
		data[offset] = (byte) (0xff & (v >>> 16));
		data[offset + 1] = (byte) (0xff & (v >>> 8));
		data[offset + 2] = (byte) (0xff & v);
	}

	public static void write4Int(byte[] data, int offset, int v) {
		data[offset] = (byte) (0xff & (v >>> 24));
		data[offset + 1] = (byte) (0xff & (v >>> 16));
		data[offset + 2] = (byte) (0xff & (v >>> 8));
		data[offset + 3] = (byte) (0xff & v);
	}

	public static void writeTime4Int(byte[] data, int offset, int v) {
		data[offset] = (byte) (0xff & (v >>> 16));
		data[offset + 1] = (byte) (0xff & (v >>> 8));
		data[offset + 2] = (byte) (0xff & v);
		data[offset + 3] = (byte) (0xff & (v >>> 24));
	}

	private void write1Int(byte[] data, int offset, int audioDataType,
			int samplerate, int samplerateLen, int audioType) {
		// TODO Auto-generated method stub
		data[offset] = (byte) (0xff & (audioDataType << 4));
		data[offset] = (byte) (data[offset] | (0xff & (samplerate << 2)));
		data[offset] = (byte) (data[offset] | (0xff & (samplerateLen << 1)));
		data[offset] = (byte) (data[offset] | (0xff & audioType));
	}

	private void write1Int(byte[] data, int offset, int v) {
		// TODO Auto-generated method stub
		data[offset] = (byte) (0xff & v);
	}

	private void writeString(byte[] data, int offset, String v) {
		byte[] str = v.getBytes();
		System.arraycopy(str, 0, data, offset, str.length);
	}
}
