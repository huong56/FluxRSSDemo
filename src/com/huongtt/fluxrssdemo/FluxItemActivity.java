package com.huongtt.fluxrssdemo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint({ "NewApi", "SimpleDateFormat", "SetJavaScriptEnabled" }) 
public class FluxItemActivity extends ActionBarActivity {
	
	private String title;
	private String description;
	private String filePath;
	private String date;
	private String color;
	private String publisher;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_fluxitem);
	        
	    if (savedInstanceState == null) {
	       Bundle  extras = getIntent().getExtras();
	       if(extras != null)
	    	   	title = extras.getString("TITLE");
	       		description = extras.getString("DESCRIPTION");
	       		filePath = extras.getString("PATH");
	       		date = extras.getString("DATE");
	       		color = extras.getString("COLOR");
	       		publisher = extras.getString("PUBLISHER");
	    } else {
	        title = savedInstanceState.getString("TITLE");
	        description = savedInstanceState.getString("DESCRIPTION");
	        filePath = savedInstanceState.getString("PATH");
	        date = savedInstanceState.getString("DATE");
	        color = savedInstanceState.getString("COLOR");
	        publisher = savedInstanceState.getString("PUBLISHER");	
	    }
	    
	    TextView titleView = (TextView) findViewById(R.id.title);
	    TextView dateView = (TextView) findViewById(R.id.date);
	    TextView publisherView = (TextView) findViewById(R.id.publisher);
        WebView descriptionWebView = (WebView) findViewById(R.id.description);
        ImageView image = (ImageView) findViewById(R.id.image);
        String pubDate = convertDateString(date);
        titleView.setText(title);
        titleView.setTextColor(Color.parseColor(color));
        dateView.setText(pubDate);
        publisherView.setText("Publié par "+publisher);
        Bitmap bm = createBitmapFromFilePath(filePath);
 		image.setImageBitmap(bm);
        
        descriptionWebView.setWebViewClient(new WebViewClient());
    	descriptionWebView.getSettings().setJavaScriptEnabled(true);
        descriptionWebView.getSettings().setBuiltInZoomControls(false);
        descriptionWebView.getSettings().setLoadWithOverviewMode(true);
        
        StringBuffer HTML = new StringBuffer();
     	HTML.append("<html>"+
     			"<head>"+
     				"<title>WEBVIEW</title>"+
     			"</head>"+
     			"<body>");
     	HTML.append("<div style=\"text-align:justify; color:"+color+"; font-weight: bold; font-size:16px;\">"+description+"</div>");
     	HTML.append("</body>");
     	HTML.append("</html>");

 		descriptionWebView.loadDataWithBaseURL("file:///android_asset/", HTML.toString(), "text/html", "utf-8", null);
 		if(description==null)
 			descriptionWebView.setVisibility(View.INVISIBLE);
 		
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	private String convertDateString(String date){
		String pubDate="";
		Locale localeUS = new Locale("en", "US");
		DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", localeUS);
		Locale localeFR = Locale.FRANCE;
		DateFormat df1 = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss", localeFR);
		try {
			Date d = df.parse(date);
			pubDate = df1.format(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return pubDate;
	}
	
	protected Bitmap createBitmapFromFilePath(String filePath){
		Long startProcess = System.currentTimeMillis();
		
		if(filePath != null){   
			
			int sampleSize = 1;
			
			// get width of image
			Bitmap bitmap = null;
    		BitmapFactory.Options opts = new BitmapFactory.Options();
	    	opts.inSampleSize = sampleSize;
	    	opts.inPurgeable = true;
	    	opts.inJustDecodeBounds = true;
	    	bitmap = BitmapFactory.decodeFile(filePath, opts); 		

			// take lower resolution for bullshit device
	        int density= (FluxRSSApplication.getInstance()).getResources().getDisplayMetrics().densityDpi;
	        switch(density)
	        {
	            case DisplayMetrics.DENSITY_HIGH:
	            	sampleSize = sampleSize++;
	            	// more lower if huge image
	            	if(opts.outWidth > 2000){
	    	    		//sampleSize++;
	            		sampleSize++;
	    	    	}
	                break;
	        }
			
			
	    	int count = 0;
	    	// until res divided by 4, still hope
	    	while(count < 4){
		    	opts.inSampleSize = sampleSize;
		    	opts.inPurgeable = true;
		    	opts.inJustDecodeBounds = false;
	    		count++;
	        	try {	        		
	        		bitmap = BitmapFactory.decodeFile(filePath, opts); 	
	        		Log.e("DEBUG", "PROCESS TIME createBitmapFromAssetPaht : "+(System.currentTimeMillis() - startProcess)+"ms");
	        		return bitmap;
				}
				catch(OutOfMemoryError e) {
					if(bitmap != null){
						bitmap.recycle();
						bitmap = null;
	            	}
					Log.e("DEBUG", "Failed to load image: "+filePath+" (out of mem) with sample size = "+opts.inSampleSize);
					sampleSize++;
					Log.e("DEBUG", "Try with sample size = "+sampleSize);				
				}
	        	catch(Error e){
	        		e.printStackTrace();
				}
	    	}	
		}else{
			Log.e("DEBUG", "assetImagePath is null !");
		}
		Log.e("DEBUG", "PROCESS TIME createBitmapFromAssetPaht (returned null): "+(System.currentTimeMillis() - startProcess)+"ms");
    	return null;
	}
	
	
}
