import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

	public static void main(String[] args) throws Exception {
		List<String> list = getListFromFile("targetList");
		PrintWriter pw = new PrintWriter(new FileWriter("pictures.md", true));
		for (String str : list) {
			String name = str.split("\t")[0];
			System.out.println("Start "+ name);
			String url = str.split("\t")[1];
			pw.println("# " + name);
			pw.println(getImg2String(url));
			pw.flush();
		}
		pw.close();
	}

	public static List<String> getListFromFile(String fileName) {
		List<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			for (String str = br.readLine(); str != null; str = br.readLine()) {
				list.add(str);
			}
			br.close();
			Collections.sort(list, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {

					return Integer.parseInt(RegExp.exec("/t/(\\d+)", o2)[0])
							- Integer.parseInt(RegExp.exec("/t/(\\d+)", o1)[0]);
				}
			});
			PrintWriter pw = new PrintWriter("new");
			for (String str : list)
				pw.append(str + "\n");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void getAllPage() throws Exception {
		String str = "https://v2ex.com/go/afterdark?p=";
		Document d;
		d = Jsoup.connect(str + String.format("%d", 1)).get();
		String a = d.select("div#TopicsNode").first().nextElementSibling()
				.select("span.fade").html();
		int totalPages = Integer.parseInt(RegExp.exec("\\d+/(\\d+)", a)[0]);
		Thread.sleep(1000 + r.nextInt(1000));
		for (int i = 1; i <= totalPages; i++) {
			getPage2(String.format("%s%d", str, i));
		}
	}

	public static void getAllImg() throws Exception {
		String str = "https://v2ex.com/go/afterdark?p=";
		Document d;
		d = Jsoup.connect(str + String.format("%d", 1)).get();
		String a = d.select("div#TopicsNode").first().nextElementSibling()
				.select("span.fade").html();
		int totalPages = Integer.parseInt(RegExp.exec("\\d+/(\\d+)", a)[0]);
		Thread.sleep(1000 + r.nextInt(1000));
		for (int i = 1; i <= totalPages; i++) {
			getPage(String.format("%s%d", str, i));
		}
	}

	public static String getImg2String(String url) throws Exception {
		Document d;
		StringBuffer sb = new StringBuffer();
		try {
			d = Jsoup.connect(url).get();
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
		Thread.sleep(20000 + r.nextInt(12345));
		return sb.toString();
	}

	public static void getImg(String url) throws Exception {
		Document d;
		try {
			d = Jsoup.connect(url).get();
			Elements imgs = d.select(".cell img[class!=avatar]");
			for (Element e : imgs) {
				String hrefString = e.attr("src");
				if (hrefString.startsWith("//"))
					System.out.println("![](https:" + hrefString + ")");
				else
					System.out.println("![](" + hrefString + ")");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		Thread.sleep(30000 + r.nextInt(12345));
	}

	public static void getPage(String url) throws Exception {
		Document d;
		try {
			d = Jsoup.connect(url).get();
			Elements imgs = d.select("div#TopicsNode span.item_title a");
			for (Element e : imgs) {
				System.out.println("# " + e.text());
				String hrefString = e.attr("href");
				if (hrefString.startsWith("/t/"))
					getImg("https://v2ex.com" + hrefString);
				else
					getImg(hrefString);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void getPage2(String url) throws Exception {
		Document d;
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("output", true));
			d = Jsoup.connect(url).get();
			Elements imgs = d.select("div#TopicsNode span.item_title a");
			for (Element e : imgs) {
				System.out.println("# " + e.text());
				pw.append(e.text() + "\t");
				String hrefString = e.attr("href");
				if (hrefString.startsWith("/t/")) {
					System.out.println("https://v2ex.com" + hrefString);
					pw.println("https://v2ex.com" + hrefString);
				} else {
					System.out.println(hrefString);
					pw.println(hrefString);
				}
			}
			pw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		Thread.sleep(30000 + r.nextInt(12345));
	}
}
