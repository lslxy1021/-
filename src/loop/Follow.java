package loop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import resolve.ParseHtml;
import resolve.Seed;

public class Follow {

	private static Seed seed;
	private static String cookie;
	private static List<String> urlList;
	private static int count = 0;
	private static int followingNumber = 0;
	private RequestConfig globalConfig;
	private CloseableHttpClient httpClient;
	private static String bFollowingURL = "https://www.zhihu.com/api/v4/members/";
	private static String eFollowingURL = "/followees?include=data%5B%2A%5D.answer_count%2Carticles_count%2Cgender%2Cfollower_count%2Cis_followed%2Cis_following%2Cbadge%5B%3F%28type%3Dbest_answerer%29%5D.topics&";
	private List<Object> personInfo;
	private Map<Object,List<Object>> person;
	
	public List<Object> getPersonInfo() {
		return personInfo;
	}

	public Map<Object, List<Object>> getPerson() {
		return person;
	}

	public Follow(Seed seed) {
		globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(5000)
				.setConnectionRequestTimeout(1000).setSocketTimeout(5000).build();
		httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
		Follow.seed = seed;
	}

	/**
	 * 
	 * @param url Ҫ�������û�����ַ���õ����û���ע�������˵���Ϣ
	 */
	public void parseURL(String url) {
		person = new LinkedHashMap<>();
		//ֻ�е�һ����������֤��
		if (count == 0) {
			cookie = seed.getCookie("https://www.zhihu.com/login/phone_num");
			count++;
		} 
		//��ַ�б�ͨ����Щ��ַ���Ի���û����й�ע���˵���Ϣ
		List<String> urlList = handleURL(url,cookie);
		for (int i = 0; i < urlList.size(); i++) {
			HttpGet getMethod = new HttpGet(urlList.get(i));
			getMethod.setHeader("Cookie", cookie);
			HttpResponse response = null;
			String jsonContent = null;
			try {
				response = httpClient.execute(getMethod);
				if (response.getStatusLine().getStatusCode() == 200) {
					if (response.getEntity() != null) {
						jsonContent = EntityUtils.toString(response.getEntity());
					} 
				}
			} 
			catch (ParseException | IOException e) {} 
			for(int j = 0;j < 20 && j < followingNumber - 20 * i; j++) {
				personInfo = new ArrayList<>();
				JSONObject jsonObject = JSON.parseObject(jsonContent);
				JSONArray object = jsonObject.getJSONArray("data");
				JSONObject preName =  object.getJSONObject(j);
				if(preName != null) {
					Object name = preName.get("name");
					Object gender = preName.get("gender");
					Object url_token = preName.get("url_token");
					String headline = (String) preName.get("headline");
					headline = headline.replaceAll("<a.*</a>", "(����)");
					Object follower_count = preName.get("follower_count");
					Object answer_count = preName.get("answer_count");
					personInfo.add(gender);
					personInfo.add(url_token);
					personInfo.add(headline);
					personInfo.add(follower_count);
					personInfo.add(answer_count);
					person.put(name, personInfo);
				}
			}
		}

	}

	/**
	 * 
	 * @param url �û���עҳ�����ַ
	 * @param cookie �û�cookie(��Щ�û�������ֻ�е�¼�û����ܲ鿴������Ϣ)
	 * @return ����һ����ַ�б�����Щ��ַ���Ի���û���ע�������˵���Ϣ
	 */
	public static List<String> handleURL(String url,String cookie) {
		String html = Seed.visit(url,cookie);
		List<String> content = ParseHtml.basicInfo(html);
		String name = content.get(0);
		String followingURL;
		followingNumber = Integer.parseInt(content.get(1));
		int n = 0;
		urlList = new ArrayList<>();
		if (followingNumber > 20) {
			while (n < followingNumber) {
				followingURL = bFollowingURL + name + eFollowingURL + "limit=20&offset=" + n;
				urlList.add(followingURL);
				n += 20;
			}

		} else {
			followingURL = bFollowingURL + name + eFollowingURL + "limit=20&offset=0";
			urlList.add(followingURL);
		}
		return urlList;
	}
}
