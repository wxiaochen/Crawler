/**
 * 4.1 在本实验的基础上，进行相关的设置：
 *（1）爬取的深度；设置开启的线程数；
 *（2）设置爬取url的上限；
 *（3）通过正则表达式设置爬取哪些网页，不爬取哪些网页等
 *
 *姓名：王冀琛
 *学号：2014011708
 *班级：4班
 *日期：2016/6/15
*/
package test1;

import java.io.FileNotFoundException;
import java.io.IOException;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;

public class Crawler1 extends BreadthCrawler {

	public Crawler1(String crawlPath, boolean autoParse) {
		super(crawlPath, autoParse);
		
	}

	@Override
	public void visit(Page page, CrawlDatums arg1) {
		System.out.println("正在提取"+page.getUrl());
		String title = page.getDoc().title();
		System.out.println("标题"+title);

		//将爬取的内容写到*.html页
		try {
			FileUtils.writeFileWithParent("downloads/"+title+".html",page.getContent());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static void main(String [] args){
		//实例化爬虫类对象
		Crawler1 crawler = new Crawler1("html_crawler", true);
		//通过爬虫对象，调用addSeed方法
		crawler.addSeed("http://software.hebtu.edu.cn/");
		//定义规则，爬取完种子后 还要爬取那些规则(/.*表示 软件学院下的所有网页都要爬取)
		crawler.addRegex("http://software.hebtu.edu.cn/.*\\.(jpg||png||gif).*");	//自定义不提取网页中的.jpg .png .gif 文件
		
		try {
			//自定义的爬取时开启的线程数为5个
			crawler.setThreads(5);
			///自定义的爬取最大网页数为22个
			crawler.setTopN(22);
			//启动,参数表示爬取层数为2层
			crawler.start(2);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
