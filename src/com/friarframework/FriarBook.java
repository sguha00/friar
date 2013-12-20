package com.friarframework;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class FriarBook extends FragmentActivity {
	// Requires trailing slash!
	final String BASE_URL = "file:///android_asset/book/"; 
	final String MIME_TYPE = "text/html";
	final String ENCODING = "utf-8";

	WebView webView;
	BookData bData;
	int currentPage = 0;

	ViewPager pager;
    FragmentStatePagerAdapter adapter; 
  
    /* Just some random URLs
    * 
    * Each page of our pager will display one URL from this array
    * Swiping, to the right will take you to the next page having
    * the next URL.
    */
//     String[] toVisit={
//        "http://www.jdepths.com",
//        "http://www.google.com",
//        "http://www.reddit.com/.compact",
//        "http://www.dribbble.com",
//    };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bData = BookData.getInstance(getBaseContext());
        setContentView(R.layout.main);
        pager=(ViewPager)findViewById(R.id.my_pager);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
         
        adapter=new FragmentStatePagerAdapter(
            getSupportFragmentManager()
            ){
    
            @Override
            public int getCount() {
                // This makes sure getItem doesn't use a position
               // that is out of bounds of our array of URLs
               return bData.numPages();
            }
    
            @Override
            public Fragment getItem(int position) {
                // Here is where all the magic of the adapter happens
                // As you can see, this is really simple.
                return BrowserFragment.newInstance(bData.getPage(position));
            }
        };
   
        //Let the pager know which adapter it is supposed to use
        pager.setAdapter(adapter);  
	}

	// Handle Android physical back button.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("currentPage", currentPage);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		currentPage = savedInstanceState.getInt("currentPage");
	}
	
	public void pageTo(String filename) {
		pager.setCurrentItem(bData.getIndex(filename), true);
	}

//	private void showUrl(int pageNum) {
//		assert pageNum >= 0 && pageNum < totalPages;
//
//		String filename = htmlFiles.get(pageNum);
//		String url = BASE_URL + filename;
//		webView.loadUrl(url);
//		showToast(currentPage + "");
//	}

	private void showToast(final String text) {
		Toast t = Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT);
		t.show();
		System.out.println(text);
	}

	class FriarWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			try {
				URI uri = new URI(url);
				String[] segments = uri.getPath().split("/");
				String filename = segments[segments.length - 1];
//				currentPage = htmlMap.get(filename);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			System.out.println(currentPage + " " + url);
		}

        @Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        if (url != null && url.startsWith("http://")) {
	            view.getContext().startActivity(
	                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	            return true;
	        } else {
	            return false;
	        }
	    }
	}
}
