package com.btw.query.taobao.shop.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.btw.query.taobao.shop.bean.ShopInfo;
import com.btw.query.taobao.shop.utils.Constants;
import com.btw.query.taobao.shop.utils.XlsUtils;

public class QueryShop {

	public final static String filePath = "D:\\temp\\shopquery\\淘宝Z-（钻级）.xls";

	public static void main(String[] args) {
		//读取Excel
		HSSFWorkbook workbook = null;
		try {
			workbook = XlsUtils.readWorkbook(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(workbook!=null){
			//按第一个面签的内容抓数据
			HSSFSheet sheet = workbook.getSheetAt(0);
			for(int rowNum = sheet.getFirstRowNum();rowNum<=sheet.getLastRowNum();rowNum++){
				HSSFRow row = sheet.getRow(rowNum);
				//第1列不为空，第2-7列为空时，抓取该店铺数据
				if((row.getCell(0)!=null&&!isEmpty(row.getCell(0).getStringCellValue())
						&&(row.getCell(1)==null||isEmpty(row.getCell(1).getStringCellValue()))
						&&(row.getCell(2)==null||isEmpty(row.getCell(2).getStringCellValue()))
						&&(row.getCell(3)==null||isEmpty(row.getCell(3).getStringCellValue()))
						&&(row.getCell(4)==null||isEmpty(row.getCell(4).getStringCellValue()))
						&&(row.getCell(5)==null||isEmpty(row.getCell(5).getStringCellValue()))
						&&(row.getCell(6)==null||isEmpty(row.getCell(6).getStringCellValue()))
						)){
					
					String title = row.getCell(0).getStringCellValue();
					ShopInfo shopInfo = null;
					try {
						shopInfo = getShopInfo(title);

						Thread.sleep(getRandomLong(10L,100L));
					} catch (ParserException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(shopInfo!=null){
						//返回值写入：2列：销量、3列：宝贝数、4列：地域、5列：主营、6列：结果数、7列：查询链接
						//销量	宝贝数	地域	主营	结果数	查询链接
						row.createCell(1).setCellValue(shopInfo.getSaleNum());
						row.createCell(2).setCellValue(shopInfo.getItemNum());
						row.createCell(3).setCellValue(shopInfo.getState());
						row.createCell(4).setCellValue(shopInfo.getMainSale());
						row.createCell(5).setCellValue(shopInfo.getQueryNum());
						row.createCell(6).setCellValue(shopInfo.getQueryUrl());
	
						try {
							//及时保存，防止tb屏蔽IP
							XlsUtils.writeWorkbook(workbook, filePath);
							System.out.println(rowNum + " row" + " is done!["+shopInfo.toValueString()+"]");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	public static final Random random = new Random(new Date().getTime());
	
	private static long getRandomLong(long min, long max) {
		long l = random.nextLong();
		return (l>0?l:-l)%(max-min)+min;
	}

	private static boolean isEmpty(String string) {
		return string==null||string.length()==0;
	}

	public final static String left = "http://s.taobao.com/search?q=";
	public final static String right = "&app=shopsearch";
	public final static String charset = "GBK";
	
	public final static String cookie = Constants.getCookiesStr();

	//发送get请求
	public static String httpGet(String url) throws ClientProtocolException, IOException{
		String result = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url); 
        //设置请求超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();
        httpGet.setConfig(requestConfig);
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("Cookie", cookie);
        httpGet.addHeader("DNT", "1");
        httpGet.addHeader("Host", "s.taobao.com");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0");
        
        
        CloseableHttpResponse response = null;
        try {
        	response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = convertStreamToString(entity.getContent(), charset);
        } finally {
			if(response!=null){
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
        }
		return result;
	}
	
	//将响应内容转为字符串
	private static String convertStreamToString(InputStream is, String charset) throws IOException {      
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));      
        StringBuilder sb = new StringBuilder();      
       
        String line = null;      
        try {      
            while ((line = reader.readLine()) != null) {  
                sb.append(line + "\n");      
            }      
        } finally {      
            try {      
                is.close();      
            } catch (IOException e) {      
               e.printStackTrace();      
            }      
        }      
        return sb.toString();      
    }  
	
	private final static NodeFilter shopFilter = new HasAttributeFilter("class", "list-item");
	private final static NodeFilter salefilter = new HasAttributeFilter("class", "info-sale");
	private final static NodeFilter sumFilter = new HasAttributeFilter("class", "info-sum");
	private final static NodeFilter stateFilter = new HasAttributeFilter("class", "shop-address");
	private final static NodeFilter maincatFilter = new HasAttributeFilter("class", "main-cat");
	
	
	//根据名称查询店铺，从响应内容中，抓取销量和宝贝数信息
	private static ShopInfo getShopInfo(String title) throws ParserException, ClientProtocolException, IOException{
		ShopInfo shopInfo = new ShopInfo();
		shopInfo.setTitle(title.trim());
		String encodeTitle = URLEncoder.encode(title, charset);
		//拼接查询地址
		shopInfo.setQueryUrl(left+encodeTitle+right);
		
		//请求并读取html字符串
		Parser parser = Parser.createParser(httpGet(shopInfo.getQueryUrl()), charset);
		
		//过滤销量和宝贝数小节
		NodeList shopList = parser.extractAllNodesThatMatch(shopFilter); 
		shopInfo.setQueryNum(String.valueOf(shopList.size()));
		//数据有多个，只记录第一个，没有不记录
		if(shopList.size() > 0){
			Node shopNode = shopList.elementAt(0);

			NodeList saleList = Parser.createParser(shopNode.toHtml(), charset).extractAllNodesThatMatch(salefilter);
			shopInfo.setSaleNum(saleList.elementAt(0).getChildren().elementAt(2).getText());;
			
			NodeList sumList = Parser.createParser(shopNode.toHtml(), charset).extractAllNodesThatMatch(sumFilter);
			shopInfo.setItemNum(sumList.elementAt(0).getChildren().elementAt(2).getText());
			
			NodeList stateList = Parser.createParser(shopNode.toHtml(), charset).extractAllNodesThatMatch(stateFilter);
			NodeList stateList1 = stateList.elementAt(0).getChildren();
			shopInfo.setState(stateList1==null?"":stateList1.elementAt(0).getText());
			
			NodeList maincatList = Parser.createParser(shopNode.toHtml(), charset).extractAllNodesThatMatch(maincatFilter);
			NodeList maincatList1 = maincatList.elementAt(0).getChildren().elementAt(3).getChildren();
			shopInfo.setMainSale(maincatList1==null?"":maincatList1.elementAt(0).getText());
			
		}
		
		return shopInfo;
	}
	
}
