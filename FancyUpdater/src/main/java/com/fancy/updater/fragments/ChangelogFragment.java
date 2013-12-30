/*
 * Copyright 2013 Fancy Updater (Boy Petersen & Parthipan Ramesh)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fancy.updater.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.fancy.updater.R;

public class ChangelogFragment extends Fragment {

    private static final String JELLY_BEAN_MR2_CHANGELOG =
            "https://github.com/boype/kernel_tuna_jb43/commits/android-omap-tuna-3.0-jb-mr2";
    private static final String KITKAT_CHANGELOG = "https://github.com/boype/kernel_tuna_kk44/commits/android-omap-tuna-3.0-jb-mr2";

    public WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setProgressBarVisibility(true);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_changelog,
                container, false);
        mWebView = ((WebView) rootView.findViewById(R.id.changelog));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                getActivity().setProgressBarVisibility(false);
            }

            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                getActivity().setProgressBarVisibility(false);
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.changelog_error), Toast.LENGTH_SHORT).show();
            }
        });
        if (savedInstanceState == null) {
            if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2)
                mWebView.loadUrl(JELLY_BEAN_MR2_CHANGELOG);
            else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
                mWebView.loadUrl(KITKAT_CHANGELOG);
        }
        return rootView;
    }

    public void refresh() {
        getActivity().setProgressBarVisibility(true);
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2)
            mWebView.loadUrl(JELLY_BEAN_MR2_CHANGELOG);
        else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            mWebView.loadUrl(KITKAT_CHANGELOG);
    }
}
