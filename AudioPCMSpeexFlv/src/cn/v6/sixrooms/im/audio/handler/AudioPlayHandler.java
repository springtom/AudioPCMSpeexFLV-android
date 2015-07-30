package cn.v6.sixrooms.im.audio.handler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 语音播放
 * 
 * @author xingchun
 * 
 */
public class AudioPlayHandler {
	private static AudioPlayHandler playInstance;

	private String filePath;
	private boolean isPause = false;
	private boolean isStop = false;
	private List<PCMData> pcmList;
	private List<EncodedData> encodedlist;
	private int nframes;
	private int channels;
	private int sampleRate;
	private int streamType;
	private int creationMode;
	private int totalTime;
	private PlayFactoryCallBack playFactoryCallBack;
	private AudioPlayThread audioPlayThread;

	private AudioSpeexDecoderThread audioSpeexDecoderThread;

	private AudioFlvReaderThread audioFlvReaderThread;

	private AudioPlayHandler() {

	}

	public static AudioPlayHandler getInstance() {
		if (playInstance == null) {
			synchronized (AudioPlayHandler.class) {
				if (playInstance == null) {
					playInstance = new AudioPlayHandler();
				}
			}
		}
		return playInstance;
	}

	public void start() {
		// TODO Auto-generated method stub
		pcmList = Collections.synchronizedList(new LinkedList<PCMData>());
		encodedlist = Collections
				.synchronizedList(new LinkedList<EncodedData>());
		audioFlvReaderThread = new AudioFlvReaderThread(this);
		audioSpeexDecoderThread = new AudioSpeexDecoderThread(this);
		audioPlayThread = new AudioPlayThread(this);
		audioFlvReaderThread.start();
		audioSpeexDecoderThread.start();
		audioPlayThread.start();
	}

	public void close() {
		setPlaying(false);
		if (audioFlvReaderThread != null) {
			audioFlvReaderThread.interrupt();
		}
		if (audioSpeexDecoderThread != null) {
			audioSpeexDecoderThread.interrupt();
		}
		if (audioPlayThread != null) {
			audioPlayThread.interrupt();
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}

	public EncodedData getEncoded() {
		if (encodedlist != null && encodedlist.size() > 0)
			return encodedlist.remove(0);
		return null;
	}

	public void setEncodedData(EncodedData encodedData) {
		if (encodedlist != null)
			encodedlist.add(encodedData);
	}

	public PCMData getPCMData() {
		if (pcmList != null && pcmList.size() > 0)
			return pcmList.remove(0);
		return null;
	}

	public void setPCMData(PCMData pCMData) {
		if (pcmList != null)
			pcmList.add(pCMData);
	}

	public boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

	public int getStreamType() {
		return streamType;
	}

	public void setStreamType(int streamType) {
		this.streamType = streamType;
	}

	public int getNframes() {
		return nframes;
	}

	public void setNframes(int nframes) {
		this.nframes = nframes;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getCreationMode() {
		return creationMode;
	}

	public void setCreationMode(int creationMode) {
		this.creationMode = creationMode;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	public PlayFactoryCallBack getPlayFactoryCallBack() {
		return playFactoryCallBack;
	}

	public void setPlayFactoryCallBack(PlayFactoryCallBack playFactoryCallBack) {
		this.playFactoryCallBack = playFactoryCallBack;
	}

	public boolean isPlaying() {
		return !(isPause | isPause);
	}

	public void setPlaying(boolean isPlaying) {
		this.isPause = !isPlaying;
		this.isStop = !isPlaying;
	}

	public interface PlayFactoryCallBack {
		void callbackProgressed(int time, int totalTime);
	}
}
