package com.huongtt.fluxrssdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.huongtt.fluxrssdemo.model.FluxRSSItem;

@SuppressLint("NewApi") public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
	 
    //private String TAG = MainActivity.class.getSimpleName();
    private String URL_RSS = "http://www.courrierinternational.com/feed/category/6271/rss.xml";
 
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private FluxListAdapter adapter;
    private String[] bgColors;
    List<FluxRSSItem> allFlux = new ArrayList<FluxRSSItem>();
	List<FluxRSSItem> list = new ArrayList<FluxRSSItem>();
    private File file;
    private int limit = 3;
    private int skip = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WifiManager wifiManager = (WifiManager)MainActivity.this.getSystemService(Context.WIFI_SERVICE);
    	boolean enable = wifiManager.isWifiEnabled();
        file = new File(this.getExternalFilesDir(null)+"/"+"rss.xml");
        if(!file.exists()&&!enable){
        	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				 switch (which){
    			        case DialogInterface.BUTTON_POSITIVE:
    			        	wifiManager.setWifiEnabled(true);
    			        	
    			        	break;
    			        case DialogInterface.BUTTON_NEGATIVE:
    			        	finish();
    			        	break;
    				
    				 }
    			
    			}
    		};
    		
        	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        	builder.setMessage("Active wifi for the first time");
        	builder.setPositiveButton(getResources().getString(R.string.Dialog_OK), dialogClickListener)
	    	.setNegativeButton(getResources().getString(R.string.Dialog_Cancel), dialogClickListener);
			final AlertDialog alertDialogEnd = builder.create();
			alertDialogEnd.show();
        }
        CheckServerStatus status = new CheckServerStatus(this, new Date());
		status.execute();
		fillList(file);
		bgColors = this.getResources().getStringArray(R.array.rss_bg);
		
        listView = (ListView) findViewById(R.id.listView);
        
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        Log.e("test","list.size()"+list.size());
        adapter = new FluxListAdapter(this, list);
        listView.setAdapter(adapter);
        
        swipeRefreshLayout.setOnRefreshListener(this);
        
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      swipeRefreshLayout.setRefreshing(true);
                                      fetchFluxRSS();
                                  }
                              }
      );

    }
 
    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
    	if(list.size()==0){
    		fillList(file);
    		adapter.notifyDataSetChanged();
    	}
        fetchFluxRSS();
    }

    private void fetchFluxRSS() {

        swipeRefreshLayout.setRefreshing(true);
        int j = 0;
        int i;
        int start = ((skip-limit)>0)?(skip-limit):0;
        for(i=start;i<skip; i++){
        	list.add(j,allFlux.get(i));
        	j++;
        }
        skip = start;
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String color = bgColors[position % bgColors.length];
				Intent intent = new Intent(MainActivity.this, FluxItemActivity.class);
				intent.putExtra("TITLE",list.get(position).getTitle());
				intent.putExtra("DESCRIPTION",list.get(position).getDescription());
				intent.putExtra("PATH", list.get(position).getFilePath());
				intent.putExtra("DATE", list.get(position).getPubDate());
				intent.putExtra("PUBLISHER", list.get(position).getPublisher());
				intent.putExtra("COLOR", color);
				startActivity(intent);
				
			}
        	
        });
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        
    }
    @SuppressWarnings("unused")
    private class CheckServerStatus extends AsyncTask<String, Integer, Integer> {
		private Context mContext;
		private Date date;

		
		public CheckServerStatus(Context context, Date date) {
			this.mContext = context;
			this.date = date;
	    }

		@Override
		protected Integer doInBackground(String... params) {
			URL url;
			int status=0;
			InputStream in = null;
			OutputStream outputStream = null;
			
			try {
				url = new URL(URL_RSS);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");

				status = connection.getResponseCode();
				File file = new File(mContext.getExternalFilesDir(null)+"/"+"rss.xml");
				if(status == 200){
					Log.i("test","connection status "+status);
					in = connection.getInputStream();		
					outputStream =  new FileOutputStream(file);

		            byte[] buffer = new byte[1024];
		            for (int count; (count = in.read(buffer)) != -1; ) {
		               outputStream.write(buffer, 0, count);

		            }
		        }
				
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
    private void fillList(File file){
    	InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			InputSource is = new InputSource(inputStream);
	        XMLParser parser=new XMLParser();
	        SAXParserFactory factory=SAXParserFactory.newInstance();
	        SAXParser sp=factory.newSAXParser();
	        XMLReader reader=sp.getXMLReader();
	        reader.setContentHandler(parser);
	        reader.parse(is);
	        allFlux = parser.getList();
	        skip = allFlux.size();
	        int j = 0;
	        int i;
	        int start = ((skip-limit)>0)?(skip-limit):0;
	        for(i=start;i<skip; i++){
	        	list.add(j,allFlux.get(i));
	        	j++;
	        }
	        skip = start;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
   
 
}
