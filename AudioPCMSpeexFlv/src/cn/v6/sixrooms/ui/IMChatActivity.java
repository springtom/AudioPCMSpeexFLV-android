package cn.v6.sixrooms.ui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.v6.sixroom.audio.demo.R;
import cn.v6.sixrooms.adapter.IMChatAdapter;
import cn.v6.sixrooms.bean.ImMessageChatBean;
import cn.v6.sixrooms.im.audio.handler.AudioPlayHandler;
import cn.v6.sixrooms.im.audio.handler.AudioRecordHandler;
import cn.v6.sixrooms.im.audio.handler.AudioPlayHandler.PlayFactoryCallBack;
import cn.v6.sixrooms.im.audio.handler.AudioRecordHandler.RecordFactoryCallBack;
import cn.v6.sixrooms.utils.FileManager;
import cn.v6.sixrooms.utils.LogUtils;

/**
 * 聊天对话窗口
 * 
 * @author xingchun
 * @2015-2-11 下午6:26:16
 */
@SuppressLint("NewApi")
public class IMChatActivity extends Activity implements OnClickListener {
	protected static final String TAG = IMChatActivity.class.getSimpleName();

	private String audioSavePath;
	private Dialog soundVolumeDialog = null;
	private ImageView soundVolumeImg;
	private LinearLayout soundVolumeLayout;
	private TextView tvRecordOvertimeWarning;
	private AudioRecordHandler recordInstance;
	private AudioPlayHandler playInstance;
	private Timer mTimer;
	private int time;
	private boolean isCancelRecord = false;
	private final int MSG_AUDIO_RECORD_CONTROL_STOP = 2;
	private final int MSG_AUDIO_RECORD_STOP = 3;
	private final int MSG_AUDIO_RECEIVE_VOLUME = 4;
	private final int MSG_AUDIO_RECORD_OVERTIME_WARNING = 5;
	private final int MSG_AUDIO_RECORD_OVERTIME_WARNING_TIME = 6;
	private final int MSG_AUDIO_RECORD_OVERTIME = 7;
	private final int MSG_AUDIO_RECORD_TIME_IS_TOO_SHORT = 8;
	private ListView listView;
	private IMChatAdapter imChatAdapter;
	private TextView tv_userStatus;
	private TextView tv_userRid;
	private TextView tv_userName;
	private float recordTime;
	private String myUid = "123";
	private int i;
	private TextView back;

	private ArrayList<ImMessageChatBean> chatMsgList;

	private Button bt_chatInputBar;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_AUDIO_RECORD_CONTROL_STOP:
				stopRecod();
				break;
			case MSG_AUDIO_RECORD_STOP:
				addImMessage();
				break;
			case MSG_AUDIO_RECEIVE_VOLUME:
				int maxVolume = msg.getData().getInt("MaxVolume");
				onReceiveMaxVolume(maxVolume);
				break;
			case MSG_AUDIO_RECORD_OVERTIME_WARNING:
				tvRecordOvertimeWarning.setVisibility(View.VISIBLE);
				break;
			case MSG_AUDIO_RECORD_OVERTIME_WARNING_TIME:
				int time = msg.getData().getInt("time");
				tvRecordOvertimeWarning.setText("您还能说：" + time + "s");
				break;
			case MSG_AUDIO_RECORD_OVERTIME:
				stopRecod();
				break;
			case MSG_AUDIO_RECORD_TIME_IS_TOO_SHORT:
				showToast("录音时间太短！");
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.phone_activity_im_chat);
		initView();
		initSoundVolumeDlg();
		initData();
		initListener();
	}

	protected void initListener() {
		back.setOnClickListener(this);
		bt_chatInputBar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int y_DOWN = 0;
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					y_DOWN = (int) event.getY();
					LogUtils.i(TAG, "y_DOWN = " + y_DOWN);
					bt_chatInputBar.setBackground(getResources().getDrawable(R.drawable.voice_bt_pressed));
					record();
					break;
				case MotionEvent.ACTION_MOVE:
					int y_MOVE = (int) event.getY();
					LogUtils.i(TAG, "y_MOVE = " + y_MOVE);
					if (isCancelRecord) {
						break;
					}
					synchronized (IMChatActivity.class) {
						if (!isCancelRecord && y_MOVE - y_DOWN < -100) {
							cancelRecord();
							isCancelRecord = true;
						}
					}

					break;
				case MotionEvent.ACTION_UP:
					bt_chatInputBar.setBackground(getResources().getDrawable(R.drawable.voice_bt_normal));
					int y_UP = (int) event.getY();
					LogUtils.i(TAG, "y_UP = " + y_UP);
					if (isCancelRecord) {
						dismissRecordDialog();
						isCancelRecord = false;
					} else {
						handler.sendEmptyMessageDelayed(
								MSG_AUDIO_RECORD_CONTROL_STOP, 100);// 延时100毫秒
					}

					break;

				default:
					break;
				}
				return true;
			}
		});
	}

	protected void initView() {
		back = (TextView) findViewById(R.id.back);
		tv_userName = (TextView) findViewById(R.id.tv_userName);
		tv_userRid = (TextView) findViewById(R.id.tv_userRid);
		tv_userStatus = (TextView) findViewById(R.id.tv_userStatus);
		listView = (ListView) findViewById(R.id.listView);
		bt_chatInputBar = (Button) findViewById(R.id.bt_chatInputBar);
	}

	protected void initData() {

		tv_userName.setText("小明");
		tv_userRid.setText("(" + 123456 + ")");
		tv_userStatus.setText("[" + "在线" + "]");
		chatMsgList = new ArrayList<ImMessageChatBean>();
		imChatAdapter = new IMChatAdapter(this, chatMsgList, myUid);
		listView.setAdapter(imChatAdapter);
	}

	private void addImMessage() {
		i++;
		ImMessageChatBean imMessageChatBean = new ImMessageChatBean();
		imMessageChatBean.setVoicePath(audioSavePath);
		imMessageChatBean.setRecodeTime(recordTime);
		imMessageChatBean.setTimetamp(System.currentTimeMillis());
		if (i % 2 == 0) {
			imMessageChatBean.setAlias("小明");
			imMessageChatBean.setUid(myUid);
		} else {
			imMessageChatBean.setAlias("我");
			imMessageChatBean.setUid("321");
		}
		chatMsgList.add(imMessageChatBean);
		imChatAdapter.notifyDataSetChanged();
		listView.setSelection(imChatAdapter.getCount() - 1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}

	private void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * @Description 录音
	 * @author xingchun
	 */
	private void record() {
		if (playInstance != null) {
			playInstance.close();
		}
		audioSavePath = FileManager.getAudioRecoderPath()
				+ System.currentTimeMillis() + ".flv";
		recordInstance = AudioRecordHandler.getInstance();
		recordInstance.setFilePath(audioSavePath);
		recordInstance.setAudioSource(MediaRecorder.AudioSource.MIC);
		recordInstance.setChannels(AudioFormat.CHANNEL_IN_MONO);
		recordInstance.setNframes(AudioFormat.ENCODING_PCM_16BIT);
		recordInstance.setSampleRate(8000);
		recordInstance.setRecording(true);
		recordInstance.setCancel(false);
		recordInstance.setRecordFactoryCallBack(new RecordFactoryCallBack() {

			@Override
			public void recordVolume(int volume) {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = MSG_AUDIO_RECEIVE_VOLUME;
				Bundle bundle = new Bundle();
				bundle.putInt("MaxVolume", volume);
				message.setData(bundle);
				handler.sendMessage(message);
			}

			@Override
			public void recordStop(float time) {
				if (time < 0.2) {
					handler.sendEmptyMessage(MSG_AUDIO_RECORD_TIME_IS_TOO_SHORT);
					return;
				}
				recordTime = time;
				handler.sendEmptyMessage(MSG_AUDIO_RECORD_STOP);
			}

			@Override
			public void recordOvertimeWarning() {
				// TODO Auto-generated method stub
				if (handler != null) {
					handler.sendEmptyMessage(MSG_AUDIO_RECORD_OVERTIME_WARNING);
					recordOvertimeWarningUI();
				}
			}
		});
		recordInstance.start();
		soundVolumeLayout.setBackground(getResources().getDrawable(
				R.drawable.phone_im_sound_volume_default_bk));
		soundVolumeDialog.show();
		tvRecordOvertimeWarning.setVisibility(View.INVISIBLE);
	}

	/**
	 * @Description 初始化音量对话框
	 * @author xingchun
	 */
	private void initSoundVolumeDlg() {
		soundVolumeDialog = new Dialog(this, R.style.SoundVolumeStyle);
		soundVolumeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		soundVolumeDialog.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		soundVolumeDialog.setContentView(R.layout.phone_dialog_im_voice_volume);
		soundVolumeDialog.setCanceledOnTouchOutside(true);
		soundVolumeImg = (ImageView) soundVolumeDialog
				.findViewById(R.id.sound_volume_img);
		tvRecordOvertimeWarning = (TextView) soundVolumeDialog
				.findViewById(R.id.tv_record_over_time_warning);
		soundVolumeLayout = (LinearLayout) soundVolumeDialog
				.findViewById(R.id.sound_volume_bk);
	}

	/**
	 * @Description 根据分贝值设置录音时的音量动画
	 * @param voiceValue
	 */
	private void onReceiveMaxVolume(int voiceValue) {
		if (voiceValue < 1200.0) {
			soundVolumeImg
					.setImageResource(R.drawable.phone_im_sound_volume_01);
		} else if (voiceValue > 1200.0 && voiceValue < 2400) {
			soundVolumeImg
					.setImageResource(R.drawable.phone_im_sound_volume_02);
		} else if (voiceValue > 2400.0 && voiceValue < 4800) {
			soundVolumeImg
					.setImageResource(R.drawable.phone_im_sound_volume_03);
		} else if (voiceValue > 4800.0 && voiceValue < 9600) {
			soundVolumeImg
					.setImageResource(R.drawable.phone_im_sound_volume_04);
		} else if (voiceValue > 9600.0 && voiceValue < 19200) {
			soundVolumeImg
					.setImageResource(R.drawable.phone_im_sound_volume_05);
		} else if (voiceValue > 19200.0 && voiceValue < 38400.0) {
			soundVolumeImg
					.setImageResource(R.drawable.phone_im_sound_volume_06);
		} else if (voiceValue > 38400.0) {
			soundVolumeImg
					.setImageResource(R.drawable.phone_im_sound_volume_07);
		}
	}

	public void playNative(String url) {
		if (playInstance != null && playInstance.isPlaying()) {
			playInstance.close();
			return;
		}
		playInstance = AudioPlayHandler.getInstance();
		playInstance.setFilePath(url);
		playInstance.setChannels(AudioFormat.CHANNEL_OUT_MONO);
		playInstance.setCreationMode(AudioTrack.MODE_STREAM);
		playInstance.setNframes(AudioFormat.ENCODING_PCM_16BIT);
		playInstance.setSampleRate(8000);
		playInstance.setStreamType(AudioManager.STREAM_MUSIC);
		playInstance.setPlaying(true);
		playInstance.setPlayFactoryCallBack(new PlayFactoryCallBack() {

			@Override
			public void callbackProgressed(int time, int totalTime) {
				// TODO Auto-generated method stub

			}
		});
		playInstance.start();
	}

	/**
	 * @Description 停止录音
	 * @author xingchun
	 */
	private void stopRecod() {
		if (recordInstance.isRecording()) {
			recordInstance.setRecording(false);
			recordInstance.stop();
			if (soundVolumeDialog.isShowing()) {
				soundVolumeDialog.dismiss();
			}
		}
	}

	/**
	 * @Description 录音时间快到1分钟。
	 * @author xingchun
	 */
	private void recordOvertimeWarningUI() {
		time = 9;
		if (mTimer != null) {
			mTimer = null;
		}
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (time > 0) {
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putInt("time", time);
					message.setData(bundle);
					message.what = MSG_AUDIO_RECORD_OVERTIME_WARNING_TIME;
					handler.sendMessage(message);
					time--;
				}
				if (time == 0) {
					handler.sendEmptyMessage(MSG_AUDIO_RECORD_OVERTIME);
				}
			}
		}, 2000, 1000);

	}

	/**
	 * @Description 去掉录音dialog
	 * @author xingchun
	 */
	private void dismissRecordDialog() {
		// TODO Auto-generated method stub
		if (soundVolumeDialog.isShowing()) {
			soundVolumeDialog.dismiss();
		}
	}

	/**
	 * @Description 取消录音
	 * @author xingchun
	 */
	private void cancelRecord() {
		// TODO Auto-generated method stub
		soundVolumeLayout.setBackground(getResources().getDrawable(
				R.drawable.phone_im_sound_volume_cancel_bk));
		if (recordInstance.isRecording()) {
			recordInstance.cancel();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (recordInstance != null) {
			recordInstance.cancel();
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		super.onPause();
	}
}
