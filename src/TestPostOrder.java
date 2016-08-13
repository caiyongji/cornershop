import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;


public class TestPostOrder {

	public static void main(String[] args) throws Exception{
		Map<String, String> map= new HashMap<>();
		map.put("order_id", "12927088079432000730");
		String json=JSONObject.fromObject(map).toString();
		System.out.println(json);
		String token="*********-LQZt18td_t9_p2rM8JuobtrNhs3VIxJXeB6sGhPMPgJ0qK9QoTznyESv8vlxK5LzlKquMxgZ8a4tpvnAY04Jf5hFFPXA61Mx-**********";
		String httpUrl="https://api.weixin.qq.com/merchant/order/getbyid?access_token=%s";
		httpUrl=String.format(httpUrl, token);
		System.out.println(httpUrl);
		URL url=new URL(httpUrl);
		HttpURLConnection connection=(HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(3600000);
		connection.setReadTimeout(3600000);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		PrintWriter out=new PrintWriter(connection.getOutputStream());
		out.print(json);
		out.flush();
		connection.connect();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String lines;
		String result="";
		while ((lines = reader.readLine()) != null) {
			result+=lines;
		}
		System.out.println(result);
		out.close();
		reader.close();
		connection.disconnect();
	}

}
