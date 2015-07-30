package cn.v6.sixrooms.im.audio.handler;

import android.media.AudioTrack;

/**音频播放线程
 * @author xingchun
 *
 */
public class AudioPlayThread extends Thread {
	private AudioPlayHandler audioPlayFoctoy;
	private AudioTrack track;
	public AudioPlayThread(AudioPlayHandler audioPlayFoctoy) {
		this.audioPlayFoctoy = audioPlayFoctoy;
	}

	@Override
	public void run() {
		android.os.Process
		.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		// TODO Auto-generated method stub
		try {
			initializeAndroidAudio();
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (audioPlayFoctoy.isPlaying()) {
				PCMData pcmData;
				if((pcmData = audioPlayFoctoy.getPCMData())!=null){
					track.write(pcmData.pcmData, 0, pcmData.size);
					track.setStereoVolume(0.7f, 0.7f);
					track.play();
					callBackPlayProgress(pcmData.time);	
				}else{
					try {
						Thread.sleep(20);
					} catch (Exception e) {
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
			if (track != null) {
				track.stop();
				track.release(); 
				track = null;
			}
			audioPlayFoctoy.close();
		}

	}

	private void callBackPlayProgress(int time) {
		// TODO Auto-generated method stub
		int callnum = time % 100;
		if(0<=callnum && callnum<20){
			audioPlayFoctoy.getPlayFactoryCallBack().callbackProgressed(time,
					audioPlayFoctoy.getTotalTime());
		}
	}

	private void initializeAndroidAudio() throws Exception {
		int minBufferSize = AudioTrack.getMinBufferSize(
				audioPlayFoctoy.getSampleRate(), audioPlayFoctoy.getChannels(),
				audioPlayFoctoy.getNframes());
		if (minBufferSize < 0) {
			throw new Exception("Failed to get minimum buffer size: "
					+ Integer.toString(minBufferSize));
		}

		track = new AudioTrack(audioPlayFoctoy.getStreamType(),
				audioPlayFoctoy.getSampleRate(), audioPlayFoctoy.getChannels(),
				audioPlayFoctoy.getNframes(), minBufferSize,
				audioPlayFoctoy.getCreationMode());

	}
}
