import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class jsoupTest {

	public static Random r;

	static {
		r = new Random();
	}

	public static final String lastPostIdFileName = "max.prop";
	public static final String firstAfterdarkPageURL = "https://v2ex.com/go/afterdark";
	public static final String pictureFilePath = "/home/royz/v2exAfterDarkPicture";
	public static final String pictureFileName = "pictures.md";
	public static final String logName = "logs.log";

	public static long getLastPostId() throws Exception {
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(lastPostIdFileName));
		String str = br.readLine();
		long result = 0;
		if (str != null && !str.isEmpty())
			result = Long.parseLong(str);
		br.close();
		return result;
	}

	public static void setLastPostId(long id) throws Exception {
		PrintWriter pw = null;
		pw = new PrintWriter(lastPostIdFileName);
		pw.print(id);
		pw.close();
	}

	public static List<String> getNewPosts() throws Exception {
		Document d;
		d = Jsoup.connect(firstAfterdarkPageURL).get();
		Elements posts = d.select("div#TopicsNode span.item_title a");
		List<String> result = new ArrayList<String>();
		for (Element e : posts) {
			String hrefString = e.attr("href");
			if (RegExp.exec("/t/(\\d+)", hrefString) != null
					&& Long.parseLong((RegExp.exec("/t/(\\d+)", hrefString)[0])) > getLastPostId()) {
				result.add("https://v2ex.com" + hrefString);
			}
		}
		Collections.sort(result, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return (int) (Long.parseLong(RegExp.exec("/t/(\\d+)", o2)[0])
						- Long.parseLong(RegExp.exec("/t/(\\d+)", o1)[0]));
			}
		});
		return result;
	}

	public static void addURL2HeadOfFile(String string) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(pictureFilePath + File.separator + pictureFileName));
		StringBuffer sb = new StringBuffer();
		for (String str = br.readLine(); str != null; str = br.readLine()) {
			sb.append(str + "\n");
		}
		br.close();
		PrintWriter pw = new PrintWriter(pictureFilePath + File.separator + pictureFileName);
		pw.print(string);
		pw.print(sb.toString());
		pw.close();
	}

	public static String getImg2String(String url, long delay) throws Exception {
		Document d;
		StringBuffer sb = new StringBuffer();
		try {
			d = Jsoup.connect(url).get();
			String title = d.select(".header h1").text();
			sb.append("# " + title + "\n");
			Elements imgs = d.select(".cell img[class!=avatar]");
			for (Element e : imgs) {
				String hrefString = e.attr("src");
				if (hrefString.startsWith("//"))
					sb.append("![](https:" + hrefString + ")\n");
				else
					sb.append("![](" + hrefString + ")\n");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		Thread.sleep(delay);
		return sb.toString();
	}

	public static void commitGit() throws Exception {
		String cmd;
		cmd = String.format("git commit -m \"%s\" %s", today(), pictureFileName);
		execCMD(cmd);
		cmd = "git pull";
		execCMD(cmd);
		cmd = "git push";
		execCMD(cmd);
	}

	public static String today() {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		return sdf1.format(Calendar.getInstance().getTime());
	}

	public static String now() {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf1.format(Calendar.getInstance().getTime());
	}

	public static void execCMD(String cmd) throws Exception {
		PrintWriter pw = new PrintWriter(new FileWriter(logName, true));
		pw.println(now());
		pw.println(cmd);
		Process p = Runtime.getRuntime().exec(cmd, null, new File(pictureFilePath));
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		for (String str = br.readLine(); str != null; str = br.readLine()) {
			pw.println(str);
		}
		br.close();
		br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		for (String str = br.readLine(); str != null; str = br.readLine()) {
			pw.println(str);
		}
		br.close();
		p.waitFor();
		pw.close();
	}

	public static void main(String[] args) throws Exception {
		StringBuffer sb = new StringBuffer();
		List<String> urls = getNewPosts();
		if (urls != null && !urls.isEmpty()) {
			for (String str : urls) {
				sb.append(getImg2String(str, 0));
			}
			addURL2HeadOfFile(sb.toString());
			setLastPostId(Long.parseLong(RegExp.exec("/t/(\\d+)", urls.get(0))[0]));
			commitGit();
		}
	}
}
