package iuv.cns.utils;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringUtil {
	private static final Log LOG = LogFactory.getLog(StringUtil.class);
	public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";

	public static boolean isValid(String str) {
		if (str != null && !"".equals(str.trim()) && !"null".equalsIgnoreCase(str.trim())) {
			return true;
		}
		return false;
	}

	public static String toKo(String str) {
		str = encode(str, "8859_1", "EUC-KR");
		return str;
	}

	public static String toDB(String str) {
		str = encode(str, "EUC-KR", "8859_1");
		return str;
	}

	public static String encode(String str, String enc1, String enc2) {
		try {
			str = new String(str.getBytes(enc1), enc2);
		} catch (UnsupportedEncodingException e) {
			LOG.error("StringUtil encode error!", e);
		}

		return str;
	}

	public static List<String> numberList(int start, int end) {
		List<String> result = new ArrayList<String>();

		String string = null;
		int length = String.valueOf(end).length();

		for (int i = 0; i <= end - start; i++) {
			string = fulfill(String.valueOf(start + i), length, "0");
			result.add(string);
		}

		return result;
	}

	public static String fulfill(String str, int dimension, String fulfillStr) {
		int length = str.length();
		int replaceLength = fulfillStr.length();

		if (length < dimension) {
			for (int i = 0; i < (dimension - length) / replaceLength; i++) {
				str = fulfillStr + str;
			}
		}

		return str;
	}

	public static String numberFormat(String str) {
		if (str == null || str.equals("")) {
			str = "0";
		}
		return numberFormat(Double.parseDouble(str));
	}

	public static String numberFormat(double str) {
		NumberFormat nf = NumberFormat.getInstance();
		return nf.format(Math.round(str));
	}

	public static String quote(String str) {
		if (str == null) {
			return null;
		} else {
			str = str.replaceAll("&", "&amp;");
			str = str.replaceAll("\"", "&quot;");
			str = str.replaceAll("<", "&lt;");
			str = str.replaceAll(">", "&gt;");
			return str;
		}
	}

	public static String uniToHex(String str) {
		StringBuffer uniS = new StringBuffer();

		String tempS = null;

		for (int i = 0; i < str.length(); i++) {
			tempS = Integer.toHexString(str.charAt(i));
			uniS.append(tempS);
		}

		return uniS.toString();
	}

	public static String hexToUni(String hex) {
		StringBuffer str = new StringBuffer();

		for (int i = 0; i < hex.length(); i = i + 2) {
			str.append(String.valueOf((char) Integer.parseInt(hex.substring(i, i + 2), 16)));
		}

		return str.toString();
	}

	public String makeString(String... tokens) {
		StringBuffer buffer = new StringBuffer(256);
		for (String temp : tokens) {
			buffer.append(temp);
		}
		return buffer.toString();
	}

	public static String nullToEmptyString(String inputString) {
		return inputString != null ? inputString : "";
	}

	public static int toNumber(Object obj) {
		return toNumber(obj, 0);
	}

	public static int toNumber(Object obj, int defaultValue) {
		int result = defaultValue;
		try {
			result = Integer.parseInt(String.valueOf(obj));
		} catch (Exception e) {
			LOG.error("StringUtil toNumber error!", e);
		}

		return result;
	}

	public static String getFmtDateStr(String dateStr) {

		int length = 14; // YYYYMMDDHH24MISS

		if (StringUtils.isBlank(dateStr) || dateStr.length() != length) {
			return dateStr;
		}

		try {
			double date = Double.parseDouble(dateStr);
			int hh24miss = (int) (date % 1000000);

			if (hh24miss > 235959) {
				dateStr = dateStr.substring(0, 8) + "235959";
			}

		} catch (NumberFormatException ne) {
			LOG.error("StringUtil getFmtDateStr error!", ne);
		}

		String formatedDate = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6, 8)
				+ " " + dateStr.substring(8, 10) + ":" + dateStr.substring(10, 12) + ":" + dateStr.substring(12, 14);

		return formatedDate;
	}

	public static List<String> splitOrgId(String orgId, String splitCd) {
		List<String> tmpList = new ArrayList<String>();
		if (StringUtils.isNotBlank(orgId)) {
			String[] tmpOrgId = orgId.split(splitCd);
			for (String id : tmpOrgId) {
				tmpList.add(id);
			}
		}
		return tmpList;
	}
}