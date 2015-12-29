package com.huongtt.fluxrssdemo;

import java.util.List;
import com.huongtt.fluxrssdemo.model.FluxRSSItem;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class FluxListAdapter extends ArrayAdapter<FluxRSSItem> {
    private LayoutInflater inflater;
    private List<FluxRSSItem> fluxList;
    private String[] bgColors;
    private Context mContext;
 
    public FluxListAdapter(Context context, List<FluxRSSItem> fluxList) {
    	super(context,0,0,fluxList);
    	this.mContext = context;
        this.fluxList = fluxList;
        inflater = LayoutInflater.from(mContext);
                
        bgColors = mContext.getResources().getStringArray(R.array.rss_bg);
    }
 
    @Override
    public int getCount() {
        return fluxList.size();
    }
 
    @Override
    public FluxRSSItem getItem(int location) {
        return fluxList.get(location);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @SuppressLint("InflateParams")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	LinearLayout ll;
    	TextView title;
    	ImageView image;
            
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        ll = (LinearLayout) convertView.findViewById(R.id.list_row);
        title = (TextView) convertView.findViewById(R.id.title);
        image = (ImageView) convertView.findViewById(R.id.image);
        
        ll.setTag(R.id.title,title);
        
        title.setText(fluxList.get(position).getTitle());
        String color = bgColors[position % bgColors.length];
        ll.setBackgroundColor(Color.parseColor(color));
        Bitmap bm = createBitmapFromFilePath(fluxList.get(position).getFilePath());
        image.setImageBitmap(bm);
        
        return convertView;
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