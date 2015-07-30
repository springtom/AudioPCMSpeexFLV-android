package cn.v6.sixrooms.utils;

public class FileManager {
	public static String path = "cn.v6/files/";
	
	private static String audioRecoderPath = "6rooms/audio/recoder/";
	private static String audioPlayPath = "6rooms/audio/receive/";

	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			// return CommonUtil.getRootFilePath() + "com.geniuseoe2012/files/";
			return CommonUtil.getRootFilePath() + path;
		} else {
			// return CommonUtil.getRootFilePath() + "com.geniuseoe2012/files";
			return CommonUtil.getRootFilePath() + path;
		}
	}
	public static String getAudioRecoderPath() {
		if (CommonUtil.hasSDCard()) {
			// return CommonUtil.getRootFilePath() + "com.geniuseoe2012/files/";
			return CommonUtil.getRootFilePath() + audioRecoderPath;
		} else {
			// return CommonUtil.getRootFilePath() + "com.geniuseoe2012/files";
			return CommonUtil.getRootFilePath() + audioRecoderPath;
		}
	}
	public static String getAudioPlayPath() {
		if (CommonUtil.hasSDCard()) {
			// return CommonUtil.getRootFilePath() + "com.geniuseoe2012/files/";
			return CommonUtil.getRootFilePath() + audioPlayPath;
		} else {
			// return CommonUtil.getRootFilePath() + "com.geniuseoe2012/files";
			return CommonUtil.getRootFilePath() + audioPlayPath;
		}
	}
}
