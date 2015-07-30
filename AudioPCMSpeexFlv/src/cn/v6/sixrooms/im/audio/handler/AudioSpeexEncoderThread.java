package cn.v6.sixrooms.im.audio.handler;

import cn.v6.sixrooms.im.audio.encode.Speex;
import cn.v6.sixrooms.utils.LogUtils;

/**
 * speex编码线程
 * 
 * @author xingchun
 * 
 */
public class AudioSpeexEncoderThread extends Thread {
	private static final String TAG = AudioSpeexEncoderThread.class
			.getSimpleName();
	private AudioRecordHandler factory;
	private Speex speex;

	public AudioSpeexEncoderThread(AudioRecordHandler factory) {
		this.factory = factory;
		speex = new Speex();
		speex.init();
	}

	@Override
	public void run() {
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		try {
			while (factory.isRecording()) {
				PCMData pcmData;
				if ((pcmData = factory.getPCMData())!=null) {
					byte[] encode = new byte[1024];
					int encodeSize = speex.encode(pcmData.pcmData, 0, encode,
							pcmData.size);
					if (encodeSize > 0) {
						EncodedData encodeData = new EncodedData();
						encodeData.size = encodeSize;
						encodeData.time = pcmData.time;
						encodeData.encoded = encode;
						factory.setEncoded(encodeData);
					} else {
						LogUtils.e(TAG, "encodeSize=" + encodeSize);
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
			speex.close();
		}
		// TODO Auto-generated method stub
	}
}
