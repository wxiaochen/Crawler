package test1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;

public class Crawler3 extends BreadthCrawler{
	//定义数据库连接的各属性
	private String url = null;
	private Connection conn = null;
	private Statement stmt = null;
	
	public Crawler3(String crawlPath, boolean autoParse) {
		super(crawlPath, autoParse);
		
	}
	
	/**
	 * 每抓取一个页面都会调用一次visit方法
	 * */
	@Override
	public void visit(Page page, CrawlDatums arg1) {
		try {
			System.out.println("正在提取"+page.getUrl());
			//抓取网址
			String sUrl = page.getUrl();
			//抓取标题
			String title = page.getDoc().title();
			//抓取时间
			String time_ = page.getDoc().select("div[class=page-info]").text();
			//抓取内容
			String content = page.getDoc().select("p").text();
			
			Class.forName("com.mysql.jdbc.Driver");
			url = "jdbc:mysql://127.0.0.1:3306/result";
			conn = DriverManager.getConnection(url,"root","");
			stmt = conn.createStatement();
			
			//要执行的插入数据库的sql语句
			String sql = "insert into result.result " + "values('" + sUrl +"','" + title +"','" + time_ + "','" +content +"')";
			//执行插入操作
			int i = stmt.executeUpdate(sql);
			//插入成功后，i的值为1。在控制台打印 1 行受影响
			System.out.println(i + "行受影响");
						
			//在控制台输出网页的网址、标题、时间、内容
			System.out.println("网址："+sUrl);
			System.out.println("标题："+title);
			System.out.println("时间："+time_);
			System.out.println("内容"+content);
						
			//将爬取的内容写到*.html页		
			FileUtils.writeFileWithParent("downloads/"+title.replace("|", "_")+".html",page.getContent()); //把标题中含有的|字符替换为_
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		//实例化爬虫类对象
		Crawler3 crawler = new Crawler3("html_crawler", true);
		//通过爬虫对象，调用addSeed方法
		crawler.addSeed("http://news.sina.com.cn/");
		//定义规则，爬取完种子后 还要爬取那些规则(/.*表示 软件学院下的所有网页都要爬取)
		crawler.addRegex("http://news.sina.com.cn/.*\\.(jpg||png||gif).*");
		
		try {
			//启动,参数表示爬取层数为2层
			crawler.start(2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

/*
 * 
 * CREATE TABLE `result` (
  `url` VARCHAR(255) COLLATE latin1_swedish_ci NOT NULL,
  `title` VARCHAR(255) COLLATE latin1_swedish_ci DEFAULT NULL,
  `time_` VARCHAR(255) COLLATE latin1_swedish_ci DEFAULT NULL,
  `content` TEXT COLLATE latin1_swedish_ci,
  PRIMARY KEY (`url`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'latin1' COLLATE 'latin1_swedish_ci'
;
 * */
