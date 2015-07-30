package cn.v6.sixrooms.im.audio.handler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.v6.sixrooms.utils.GlobleValue;


/**
 * 录音工厂
 * 
 * @author xingchun
 * 
 */
public class AudioRecordHandler {
	private int audioSource;
	private int nframes;
	private int channels;
	private int sampleRate;
	private String filePath;
	private boolean isRecording = false;
	private boolean isCancel = false;
	private List<PCMData> pcmList;
	private List<EncodedData> encodedlist;
	private RecordFactoryCallBack recordFactoryCallBack;

	private static AudioRecordHandler instance;
	private AudioRecordThread audioRecordThread;
	private AudioFlvWriterThread flvWriterThread;
	private AudioSpeexEncoderThread audioSpeexEncoderThread;

	private AudioRecordHandler() {
	}

	public static AudioRecordHandler getInstance() {
		if (instance == null) {
			synchronized (AudioRecordHandler.class) {
				if (instance == null) {
					instance = new AudioRecordHandler();
				}
			}
		}
		return instance;
	}

	public void start() {
		// TODO Auto-generated method stub
		pcmList = Collections.synchronizedList(new LinkedList<PCMData>());
		encodedlist = Collections
				.synchronizedList(new LinkedList<EncodedData>());
		audioRecordThread = new AudioRecordThread(this);
		audioSpeexEncoderThread = new AudioSpeexEncoderThread(this);
		flvWriterThread = new AudioFlvWriterThread(this);
		audioRecordThread.start();
		audioSpeexEncoderThread.start();
		flvWriterThread.start();
	}

	private void close() {
		isRecording = false;
		if (audioRecordThread != null) {
			audioRecordThread.interrupt();
		}
		if (audioSpeexEncoderThread != null) {
			audioSpeexEncoderThread.interrupt();
		}
		if (flvWriterThread != null) {
			flvWriterThread.interrupt();
		}
	}

	public void stop() {
		close();
		float time;
		if (audioRecordThread.getRecordTime() > GlobleValue.MAX_SOUND_RECORD_TIME) {
			time = GlobleValue.MAX_SOUND_RECORD_TIME;
		} else {
			time = audioRecordThread.getRecordTime();
		}
		recordFactoryCallBack.recordStop(time);
	}

	public void cancel() {
		isCancel = true;
		close();
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
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

	public String getFilePath() {
		return filePath;
	}

	public PCMData getPCMData() {
		if (pcmList != null && pcmList.size() > 0)
			return pcmList.remove(0);
		return null;
	}

	public void setPCMData(PCMData pCMData) {
		if (pcmList != null) {
			pcmList.add(pCMData);
		}
	}

	public EncodedData getEncoded() {
		if (encodedlist != null && encodedlist.size() > 0)
			return encodedlist.remove(0);
		return null;
	}

	public void setEncoded(EncodedData encodedData) {
		if (encodedlist != null)
			encodedlist.add(encodedData);
	}

	public boolean isRecording() {
		return isRecording;
	}

	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}

	public RecordFactoryCallBack getRecordFactoryCallBack() {
		return recordFactoryCallBack;
	}

	public void setRecordFactoryCallBack(
			RecordFactoryCallBack recordFactoryCallBack) {
		this.recordFactoryCallBack = recordFactoryCallBack;
	}

	public int getAudioSource() {
		return audioSource;
	}

	public void setAudioSource(int audioSource) {
		this.audioSource = audioSource;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	public interface RecordFactoryCallBack {
		void recordOvertimeWarning();

		void recordVolume(int volume);

		void recordStop(float time);
	}

}
