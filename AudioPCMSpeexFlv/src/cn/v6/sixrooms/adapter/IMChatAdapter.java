package cn.v6.sixrooms.adapter;

import java.util.ArrayList;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.v6.sixroom.audio.demo.R;
import cn.v6.sixrooms.bean.ImMessageChatBean;
import cn.v6.sixrooms.ui.IMChatActivity;
import cn.v6.sixrooms.utils.DateUtil;

public class IMChatAdapter extends BaseAdapter {
	private ArrayList<ImMessageChatBean> chatMsgList;
	private IMChatActivity context;
	private String myUid;

	public IMChatAdapter(IMChatActivity context,
			ArrayList<ImMessageChatBean> chatMsgList, String myUid) {
		this.myUid = myUid;
		this.context = context;
		this.chatMsgList = chatMsgList;
	}

	@Override
	public int getCount() {
		return chatMsgList.size();
	}

	@Override
	public Object getItem(int position) {
		return chatMsgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context,
					R.layout.phone_activity_im_chat_item, null);
			holder = new ViewHolder();
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_otherName = (TextView) convertView
					.findViewById(R.id.tv_otherName);
			holder.iv_otherPic = (ImageView) convertView
					.findViewById(R.id.iv_otherPic);
			holder.iv_myPic = (ImageView) convertView
					.findViewById(R.id.iv_myPic);
			holder.tv_msg_text = (TextView) convertView
					.findViewById(R.id.tv_msg_text);
			holder.tv_msg_pic = (ImageView) convertView
					.findViewById(R.id.tv_msg_pic);
			holder.tv_msg_voice = (TextView) convertView
					.findViewById(R.id.tv_msg_voice);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ImMessageChatBean chatMsgBean = chatMsgList.get(position);

		// 自己还是别人
		String uid = chatMsgBean.getUid();
		if (myUid != null && myUid.equals(uid)) { // 自己
			holder.iv_otherPic.setVisibility(View.GONE);
			holder.iv_myPic.setVisibility(View.VISIBLE);
			holder.tv_otherName.setGravity(Gravity.CENTER_VERTICAL
					| Gravity.RIGHT);

			LayoutParams layoutParamsMsgText = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParamsMsgText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			holder.tv_msg_text.setLayoutParams(layoutParamsMsgText);

			LayoutParams layoutParamsMsgPic = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParamsMsgPic.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			holder.tv_msg_pic.setLayoutParams(layoutParamsMsgPic);

			LayoutParams layoutParamsMsgVoice = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParamsMsgVoice.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			holder.tv_msg_voice.setLayoutParams(layoutParamsMsgVoice);
		} else { // 别人
			holder.iv_otherPic.setVisibility(View.VISIBLE);
			holder.iv_myPic.setVisibility(View.GONE);
			holder.tv_otherName.setGravity(Gravity.CENTER_VERTICAL
					| Gravity.LEFT);

			// LayoutParams layoutParamsMsgText = (LayoutParams)
			// holder.tv_msg_text.getLayoutParams();
			LayoutParams layoutParamsMsgText = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParamsMsgText.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			holder.tv_msg_text.setLayoutParams(layoutParamsMsgText);

			LayoutParams layoutParamsMsgPic = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParamsMsgPic.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			holder.tv_msg_pic.setLayoutParams(layoutParamsMsgPic);

			LayoutParams layoutParamsMsgVoice = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParamsMsgVoice.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			holder.tv_msg_voice.setLayoutParams(layoutParamsMsgVoice);
		}

		// 显示头像

		// 显示昵称
		String alias = chatMsgBean.getAlias();
		holder.tv_otherName.setText(alias);
		final String voice = chatMsgBean.getVoicePath();

		if (!TextUtils.isEmpty(voice)) { // 语音也为空, 那就显示空文本消息吧
			holder.tv_msg_pic.setVisibility(View.GONE);
			holder.tv_msg_pic.setOnClickListener(null);
			// TODO
			// 加载语音
			holder.tv_msg_voice.setVisibility(View.VISIBLE);
			holder.tv_msg_voice.setText(" " + chatMsgBean.getRecodeTime()
					+ "  ");
			holder.tv_msg_text.setVisibility(View.GONE);

			holder.tv_msg_voice.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 播放语音
					context.playNative(voice);
				}
			});
		}

		// 显示时间
		long tm = chatMsgBean.getTimetamp();
		holder.tv_time.setText(DateUtil.getTimeInfo(tm));

		return convertView;
	}

	static class ViewHolder {
		public TextView tv_time;
		public TextView tv_otherName;
		public ImageView iv_otherPic;
		public ImageView iv_myPic;
		public TextView tv_msg_text;
		public ImageView tv_msg_pic;
		public TextView tv_msg_voice;
	}

}
