package cn.v6.sixrooms.im.audio.handler;

import cn.v6.sixrooms.im.audio.encode.Speex;

public class AudioSpeexDecoderThread extends Thread {
	private AudioPlayHandler audioPlayFoctoy;
	private Speex speex;

	public AudioSpeexDecoderThread(AudioPlayHandler audioPlayFoctoy) {
		this.audioPlayFoctoy = audioPlayFoctoy;
		speex = new Speex();
		speex.init();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		try {
			while (audioPlayFoctoy.isPlaying()) {
				EncodedData encodedData;
				if ((encodedData = audioPlayFoctoy.getEncoded()) != null) {
					PCMData pcmData = new PCMData();
					int decode = speex.decode(encodedData.encoded,
							pcmData.pcmData, 160);
					if (decode > 0) {
						pcmData.size = decode;
						pcmData.time = encodedData.time;
						audioPlayFoctoy.setPCMData(pcmData);
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
			if (speex != null) {
				speex.close();
				speex = null;
			}
		}
	}
}
