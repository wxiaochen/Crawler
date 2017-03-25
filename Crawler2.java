/**
 * 实现自己的网络爬虫，抓取“新浪新闻”（http://news.sina.com.cn/）网站的部分信息。
 *（1）需要抓取信息包括：网址,标题，时间，网页内容等。
 *（2）将抓取的数据写到文本文件或excel表中。
 *
 *姓名：王冀琛
 *学号：2014011708
 *班级：4班
 *日期：2016/6/20
*/

package test1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;

public class Crawler2 extends BreadthCrawler {
	//定义参数变量、excel、表格、输出流  属性（类加载时执行，且只执行一次，不会出现信息的覆盖）
	private AtomicInteger id = new AtomicInteger(0);
	private Workbook wb = new HSSFWorkbook();
	private Sheet sheet1 = wb.createSheet("数据解析");
	private OutputStream os = null;
	private String [] th = {"url","标题","时间","内容"};
	public Crawler2(String crawlPath, boolean autoParse) {
		super(crawlPath, autoParse);
		try {
			os = new FileOutputStream(new File("E:\\大二第二学期\\Java\\homework\\孙老师网络爬虫\\webspider\\结果.xls"));
			//将表头写入excel表中：
			for (int j = 0; j < th.length; j++) {
				os.write((th[j]+"\t").getBytes());
			}
			os.write(("\n").getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * 每抓取一个页面都会调用一次visit方法
	 * */
	@Override
	public void visit(Page page, CrawlDatums arg1) {		
		try {						
			int i = 0;			
			//创建一行
			Row row = sheet1.createRow((short)0);
			//填充标题
			for (String s : th) {
				Cell cell = row.createCell(i);
			
				cell.setCellValue(s);
				i++;
			}							
			System.out.println("正在提取"+page.getUrl());
			//抓取网址
			String sUrl = page.getUrl();
			//抓取标题
			String title = page.getDoc().title();
			//把标题中含有的|字符替换为_
			if (title.contains("|")) {
				title.replace("|", "_");
			}		
			//抓取时间
			String time = page.getDoc().select("div[class=page-info]").text();
			//抓取内容
			String content = page.getDoc().select("p").text();
			
			Row row_ = sheet1.createRow((short)id.incrementAndGet());
			row_.createCell(0).setCellValue(sUrl);
			row_.createCell(1).setCellValue(title);
			row_.createCell(2).setCellValue(time);
			row_.createCell(3).setCellValue(content);			
			
			//将抓取到的 url 标题  时间  内容 写入到excel表中
			for(int k = 0;k < th.length; k++){
				os.write((row_.getCell(k).toString()+"\t").getBytes());				
			}
			os.write(("\n").getBytes());
			
			System.out.println("网址："+sUrl);
			System.out.println("标题："+title);
			System.out.println("时间："+time);
			System.out.println("内容"+content);
			
			//将爬取的内容写到*.html页
			FileUtils.writeFileWithParent("downloads/"+title+".html",page.getContent());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String [] args){
		//实例化爬虫类对象
		Crawler2 crawler = new Crawler2("html_crawler", true);
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
