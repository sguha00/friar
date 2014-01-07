package com.friarframework;

import java.net.URI;
import java.net.URISyntaxException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class BrowserFragment extends Fragment {

	private WebView mWebView;
	private boolean mIsWebViewAvailable;
	private String mUrl = null;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_browser, container,
				false);

		if (mWebView != null) {
			mWebView.destroy();
		}

		mWebView = (WebView) view.findViewById(R.id.my_browser);
		mWebView.setWebViewClient(new FriarWebViewClient());
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    WebView.setWebContentsDebuggingEnabled(true);
		}
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.addJavascriptInterface(new VideoManager(getActivity()), "VideoManager");
		mWebView.loadUrl(mUrl);
		
		mIsWebViewAvailable = true;
		// Just load whatever URL this fragment is
		// created with.
		return view;
	}

	// This is the method the pager adapter will use
	// to create a new fragment
	public static Fragment newInstance(String url) {
		BrowserFragment f = new BrowserFragment();
		f.mUrl = "file:///android_asset/" + BookData.ASSET_DIR + "/" + url;
		
		return f;
	}
	
	/**
	 * Called when the fragment is visible to the user and actively running.
	 * Resumes the WebView.
	 */
	@Override
	public void onPause() {
		super.onPause();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mWebView.onPause();
		}
	}

	/**
	 * Called when the fragment is no longer resumed. Pauses the WebView.
	 */
	@Override
	public void onResume() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mWebView.onResume();
		}
		super.onResume();
	}

	/**
	 * Called when the WebView has been detached from the fragment. The WebView
	 * is no longer available after this time.
	 */
	@Override
	public void onDestroyView() {
		mIsWebViewAvailable = false;
		super.onDestroyView();
	}

	/**
	 * Called when the fragment is no longer in use. Destroys the internal state
	 * of the WebView.
	 */
	@Override
	public void onDestroy() {
		if (mWebView != null) {
			mWebView.destroy();
			mWebView = null;
		}
		super.onDestroy();
	}

	/**
	 * Gets the WebView.
	 */
	public WebView getWebView() {
		return mIsWebViewAvailable ? mWebView : null;
	}


	class FriarWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url != null && url.startsWith("http://")) {
				view.getContext().startActivity(
						new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				return true;
			} else {
				URI uri;
				try {
					uri = new URI(url);
					String[] segments = uri.getPath().split("/");
					String filename = segments[segments.length - 1];
					((FriarBook) getActivity()).pageTo(filename);
					return true;
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
		}
	}

}