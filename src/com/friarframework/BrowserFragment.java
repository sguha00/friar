package com.friarframework;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BrowserFragment extends Fragment {

	WebView browser;
	String url;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_browser, container,
				false);

		browser = (WebView) view.findViewById(R.id.my_browser);
		browser.setWebViewClient(new FriarWebViewClient());
		browser.loadUrl(url);
		// Just load whatever URL this fragment is
		// created with.
		return view;
	}

	// This is the method the pager adapter will use
	// to create a new fragment
	public static Fragment newInstance(String url) {
		BrowserFragment f = new BrowserFragment();
		f.url = "file:///android_asset/book/" + url;
		return f;
	}

	class FriarWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			try {
				URI uri = new URI(url);
				String[] segments = uri.getPath().split("/");
				String filename = segments[segments.length - 1];
				// currentPage = htmlMap.get(filename);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			// System.out.println(currentPage + " " + url);
		}

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