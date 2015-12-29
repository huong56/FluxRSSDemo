package com.huongtt.fluxrssdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.huongtt.fluxrssdemo.model.FluxRSSItem;

public class XMLParser extends DefaultHandler{
       
           
	private List<FluxRSSItem> list=null;
           
    // string builder acts as a buffer
    private StringBuilder builder;
       
    private FluxRSSItem fluxItem = new FluxRSSItem();
    // Initialize the arraylist
    // @throws SAXException
    
    public List<FluxRSSItem> getList(){
    	return this.list;
    }
    
    @Override
    public void startDocument() throws SAXException {
    	
	    /******* Create ArrayList To Store FluxRSSItem object ******/
	    list = new ArrayList<FluxRSSItem>();
	    
	}
    @Override
    public void startElement(String uri, String localName, String qName,
    	Attributes attributes) throws SAXException {
               
        /****  When New XML Node initiating to parse this function called *****/
               
        // Create StringBuilder object to store xml node value
        builder=new StringBuilder();
        if(localName.equals("item")){
        	fluxItem = new FluxRSSItem();
        	//Log.e("test","fluxItem created ");
        }else if(localName.equalsIgnoreCase("enclosure")){
            String strURL = attributes.getValue("url");
            int slashIndex = strURL.lastIndexOf("/");
            int endIndex = strURL.length();
            if(strURL.contains("?")){
            	endIndex = strURL.indexOf("?");
            }
           	String fileName = strURL.substring(slashIndex+1,endIndex);
           	try {
           		URL url = new URL(strURL);
				DownloadImage download = new DownloadImage(FluxRSSApplication.getInstance(), new Date());
				download.setURL(url);
				download.setFileName(fileName);
				download.execute();
				fluxItem.setFilePath((FluxRSSApplication.getInstance()).getExternalFilesDir(null)+"/"+fileName);

           	}catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
           	}  
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
                  throws SAXException {
   
    	if(localName.equalsIgnoreCase("title")){
    		fluxItem.setTitle(builder.toString());
        } else if(localName.equalsIgnoreCase("description")){
        	fluxItem.setDescription(builder.toString());
        }else if(localName.equalsIgnoreCase("link")){
        	fluxItem.setLink(builder.toString());
        }else if(localName.equalsIgnoreCase("pubDate")){
        	fluxItem.setPubDate(builder.toString());  
        }else if(localName.equalsIgnoreCase("publisher")){	  
            fluxItem.setPublisher(builder.toString());
        }else if(localName.equals("item")){ 
            /** finished reading a job xml node, add it to the arraylist **/
            list.add( fluxItem );
        }
             
    }
       
    @Override
    public void characters(char[] ch, int start, int length)
    		throws SAXException {
                   
    	/******  Read the characters and append them to the buffer  ******/
        String tempString=new String(ch, start, length);
        	builder.append(tempString);
        }
    @SuppressWarnings("unused") 
    private class DownloadImage extends AsyncTask<String, Integer, Integer> {
      	
		private Context mContext;
      	private Date date;
      	private URL url;
      	private String fileName;
      	private String filePath;

      		
      	public DownloadImage(Context context, Date date) {
      		this.mContext = context;
      		this.date = date;
      	}
      		
      	public URL getURL(){
      		return this.url;
      	}
      		
      	public void setURL(URL url){
      		this.url = url;
      	}
      	
      	public void setFileName(String fileName){
      		this.fileName = fileName;
      	}
      		
      	public String getFilePath(){
      		return this.filePath;
      	}
      		
      	public void setFilePath(String filePath){
      		this.filePath = filePath;
      	}

      	@Override
      	protected Integer doInBackground(String... params) {
      		URL url;
      		int status=0;
      		InputStream in = null;
      		OutputStream outputStream = null;
      		try {
      			url = getURL();
      			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      			connection.setRequestMethod("GET");

      			status = connection.getResponseCode();
      				
      			File file = new File((FluxRSSApplication.getInstance()).getExternalFilesDir(null)+"/"+fileName);
      				
      			if(status == 200 && !file.exists()){
      				in = connection.getInputStream();		
      				outputStream =  new FileOutputStream(file);

      		        byte[] buffer = new byte[1024];
      		        for (int count; (count = in.read(buffer)) != -1; ) {
      		        	outputStream.write(buffer, 0, count);

      		        }
      		        Log.e("test","download filepath = "+file.getAbsolutePath());
      		    }
      				
      			setFilePath(file.getAbsolutePath());
      			fluxItem.setFilePath(getFilePath());
      		} catch (MalformedURLException e) {
      			Log.e("test"," "+e.getMessage());
      		} catch (IOException e) {
      			Log.e("test"," "+e.getMessage());
      		}finally{
      			if (in != null) {
                  	try {
    	           		in.close();
                  	} catch (IOException e) {
      	                e.printStackTrace();
      	            }
      			}
      			if (outputStream != null) {
      	    		try {
      	    			// outputStream.flush();
      	    			outputStream.close();
      	    		} catch (IOException e) {
      	    			e.printStackTrace();
      	    		}

      	    	}
      		}
      			
      		return status;
      	}

      }
}
