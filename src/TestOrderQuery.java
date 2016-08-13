import iuv.cns.utils.Constants;
import iuv.cns.utils.NetUtil;
import iuv.cns.wechat.weutils.WeSign;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class TestOrderQuery {

	public static void main(String[] args) throws Exception {
		String url="https://api.mch.weixin.qq.com/pay/orderquery";
		String data="";
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("xml");
		Element element1 = root.addElement("appid");
		element1.setText(Constants.APP_ID);
		Element element2 = root.addElement("mch_id");
		element2.setText(Constants.MCH_ID);
		Element element3 = root.addElement("transaction_id");
		element3.setText("400959200120160503547958***");
		Element element4 = root.addElement("nonce_str");
		String nonce_str=WeSign.md5(String.valueOf(System.currentTimeMillis()));
		element4.setText(nonce_str);
		Map<String, String> map =new HashMap<String, String>();
		map.put("appid", Constants.APP_ID);
		map.put("mch_id", Constants.MCH_ID);
		map.put("nonce_str", nonce_str);
		map.put("transaction_id", "400959200120160503547958***");
		Element element5 = root.addElement("sign");
		element5.setText(WeSign.md5Sign(map, true));
		data=document.asXML();
		String result=NetUtil.connectUrlResponsePostData(url, data);
		System.out.println("result:"+result);

	}

}
