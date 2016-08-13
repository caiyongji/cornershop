package iuv.cns.wechat.weutils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class MapUtil {
	/**
	 * map转换成xml
	 * 【排序】：按照map中key的字典序排序生成
	 * @param map
	 * @return
	 */
	public static String asXml(Map<String, String> map){
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("xml");
		Set<String> set=map.keySet();
		if (set.isEmpty()) {
			return "";
		}
		String[] keys=set.toArray(new String[set.size()]);
		Arrays.sort(keys);
		for (String key : keys) {
			Element element=root.addElement(key);
			element.setText(map.get(key));
		}
		return document.asXML();
		
	}
}
