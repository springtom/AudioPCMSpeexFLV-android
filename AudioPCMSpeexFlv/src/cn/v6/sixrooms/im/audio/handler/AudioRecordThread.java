package cn.v6.sixrooms.im.audio.handler;

import cn.v6.sixrooms.utils.GlobleValue;
import cn.v6.sixrooms.utils.LogUtils;
import android.media.AudioRecord;

/**
 * 录音线程
 * 
 * @author xingchun
 * 
 */
public class AudioRecordThread extends Thread {
	private static final String TAG = AudioRecordThread.class.getSimpleName();
	private AudioRecordHandler factory;
	private long maxVolumeStart;
	private long maxVolumeEnd;
	private float recordTime;
	private boolean isOvertimeWarning;

	public AudioRecordThread(AudioRecordHandler factory) {
		this.factory = factory;
	}

	@Override
	public void run() {
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		AudioRecord recordInstance = null;
		try {
			int bufferSize = AudioRecord.getMinBufferSize(
					factory.getSampleRate(), factory.getChannels(),
					factory.getNframes());
			LogUtils.i(TAG, "bufferSize ="+bufferSize);
			int packagesize = 160;
			short[] tempBuffer = new short[packagesize];
			recordInstance = new AudioRecord(factory.getAudioSource(),
					factory.getSampleRate(), factory.getChannels(),
					factory.getNframes(), bufferSize);
			recordInstance.startRecording();
			recordTime = 0;
			LogUtils.i(TAG, "recordTime = "+recordTime);
			isOvertimeWarning = false;
			long startTime = System.currentTimeMillis();
			long endTime = startTime;
			maxVolumeStart = System.currentTimeMillis();
			while (factory.isRecording()) {
				int bufferRead = recordInstance
						.read(tempBuffer, 0, packagesize);
				LogUtils.i(TAG, "bufferRead="+bufferRead);
				if (recordTime >= GlobleValue.WARNING_MAX_SOUND_RECORD_TIME && !isOvertimeWarning) {
					isOvertimeWarning = true;
					factory.getRecordFactoryCallBack().recordOvertimeWarning();
				}
				if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
					throw new IllegalStateException(
							"read() returned AudioRecord.ERROR_INVALID_OPERATION");
				} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
					throw new IllegalStateException(
							"read() returned AudioRecord.ERROR_BAD_VALUE");
				} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
					throw new IllegalStateException(
							"read() returned AudioRecord.ERROR_INVALID_OPERATION");
				}
				PCMData pcmData = new PCMData();
				pcmData.size = bufferRead;
				System.arraycopy(tempBuffer, 0, pcmData.pcmData, 0, bufferRead);
				pcmData.time = (int) (endTime - startTime);
				endTime = System.currentTimeMillis();
				recordTime = (float) ((endTime - startTime) / 1000.0f);
				factory.setPCMData(pcmData);
				maxVolumeEnd = System.currentTimeMillis();
				getVolume(tempBuffer, bufferRead);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			recordTime = 0;
			isOvertimeWarning = false;
			if (recordInstance != null) {
				recordInstance.stop();
				recordInstance.release();
				recordInstance = null;
			}
		}
	}


	private void getVolume(short[] buffer, int readLen) {
		try {
			if (maxVolumeEnd - maxVolumeStart < 100) {
				return;
			}
			maxVolumeStart = maxVolumeEnd;
			int max = 0;
			for (int i = 0; i < readLen; i++) {
				if (Math.abs(buffer[i]) > max) {
					max = Math.abs(buffer[i]);
				}
			}
			factory.getRecordFactoryCallBack().recordVolume(max);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public float getRecordTime() {
		return recordTime;
	}
}
