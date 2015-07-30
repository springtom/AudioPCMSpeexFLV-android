package cn.v6.sixrooms.utils;

import android.util.Log;
/**
 * log工具�?
 * @author wangxg
 *       进入正式版本，LOG_LEVEL=0，那么log都不会输�?
 */
public class LogUtils {
	public static int VERBOSE = 5;
	public static int DEBUG = 4;
	public static int INFO = 3;
	public static int WARN = 2;
	public static int ERROR = 1;
	public final static  int LOG_LEVEL = 6;
	
	public static void v(String tag,String msg){
		if(LOG_LEVEL>VERBOSE){
			Log.v(tag, msg);
		}
	}
	public static void d(String tag,String msg){
		if(LOG_LEVEL>DEBUG){
			Log.d(tag, msg);
		}
	}
	public static void i(String tag,String msg){
		if(LOG_LEVEL>INFO){
			Log.i(tag, msg);
		}
	}
	public static void w(String tag,String msg){
		if(LOG_LEVEL>WARN){
			Log.w(tag, msg);
		}
	}
	public static void e(String tag,String msg){
		if(LOG_LEVEL>ERROR){
			Log.e(tag, msg);
		}
	}
}
