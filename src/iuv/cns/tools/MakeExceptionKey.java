package iuv.cns.tools;
import iuv.cns.wechat.weutils.WeSign;


public class MakeExceptionKey {

	public static void main(String[] args) {
		String exceptionKey=WeSign.timestamp();
		System.out.println(exceptionKey);
		System.out.println(WeSign.md5("1462433392"));
	}

}
