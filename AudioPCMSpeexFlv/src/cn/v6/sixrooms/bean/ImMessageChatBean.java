package cn.v6.sixrooms.bean;

public class ImMessageChatBean {

	private String voicePath; // 当该字段有值表明该消息是一条语音消息
	private float recodeTime;
	private long timetamp;
	private String uid;
	private String alias;

	public float getRecodeTime() {
		return recodeTime;
	}

	public void setRecodeTime(float recodeTime) {
		this.recodeTime = recodeTime;
	}

	public long getTimetamp() {
		return timetamp;
	}

	public void setTimetamp(long timetamp) {
		this.timetamp = timetamp;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getVoicePath() {
		return voicePath;
	}

	public void setVoicePath(String voicePath) {
		this.voicePath = voicePath;
	}


	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return "ImMessageChatBean [voicePath=" + voicePath + ", recodeTime="
				+ recodeTime + ", timetamp=" + timetamp + ", uid=" + uid
				+ ", alias=" + alias + "]";
	}
}
