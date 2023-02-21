package com.devf5r.kalam.Utils;


import static com.devf5r.kalam.Utils.Constant.ADMOB;
import static com.devf5r.kalam.Utils.Constant.AD_STATUS_ON;
import static com.devf5r.kalam.Utils.Constant.APPLOVIN;
import static com.devf5r.kalam.Utils.Constant.FAN;
import static com.devf5r.kalam.Utils.Constant.STARTAPP;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;


import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;


import com.devf5r.kalam.Config;
import com.devf5r.kalam.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdNetwork {

    private static final String TAG = "AdNetwork";
    private final Activity context;
    SharedPref sharedPref;
    AdsPref adsPref;

    //Banner
    private FrameLayout adContainerView;
    private AdView adView;
    RelativeLayout startAppAdView;
    com.facebook.ads.AdView fanAdView;

    //Interstitial
    private InterstitialAd adMobInterstitialAd;
    private com.facebook.ads.InterstitialAd fanInterstitialAd;

    private int retryAttempt;
    private int counter = 1;

    //Native
    MediaView mediaView;
    TemplateView admob_native_ad;
    ConstraintLayout admob_native_background;
    private NativeAd nativeAd;
    private NativeAdLayout fan_native_ad;
    private LinearLayout nativeAdView;
    View startapp_native_ad;
    ImageView startapp_native_image;
    TextView startapp_native_title;
    TextView startapp_native_description;
    Button startapp_native_button;
    LinearLayout startapp_native_background;

    public AdNetwork(Activity context) {
        this.context = context;
        this.sharedPref = new SharedPref(context);
        this.adsPref = new AdsPref(context);
    }

    public void loadBannerAdNetwork(int ad_placement) {
        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && ad_placement != 0) {
            switch (adsPref.getAdType()) {
                case ADMOB:
                    adContainerView = context.findViewById(R.id.admob_banner_view_container);
                    adContainerView.post(() -> {
                        adView = new AdView(context);
                        adView.setAdUnitId(adsPref.getAdMobBannerId());
                        adContainerView.removeAllViews();
                        adContainerView.addView(adView);
                        adView.setAdSize(Tools.getAdSize(context));
                        adView.loadAd(Tools.getAdRequest(context));
                        adView.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // Code to be executed when an ad finishes loading.
                                adContainerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                // Code to be executed when an ad request fails.
                                adContainerView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAdOpened() {
                                // Code to be executed when an ad opens an overlay that
                                // covers the screen.
                            }

                            @Override
                            public void onAdClicked() {
                                // Code to be executed when the user clicks on an ad.
                            }

                            @Override
                            public void onAdClosed() {
                                // Code to be executed when the user is about to return
                                // to the app after tapping on an ad.
                            }
                        });
                    });
                    break;
                case FAN:
                    fanAdView = new com.facebook.ads.AdView(context, adsPref.getFanBannerUnitId(), AdSize.BANNER_HEIGHT_50);
                    LinearLayout adContainer = context.findViewById(R.id.fan_banner_view_container);
                    // Add the ad view to your activity layout
                    adContainer.addView(fanAdView);
                    com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                        @Override
                        public void onError(Ad ad, AdError adError) {
                            adContainer.setVisibility(View.GONE);
                            Log.d(TAG, "Failed to load Audience Network : " + adError.getErrorMessage() + " "  + adError.getErrorCode());
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            adContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    };
                    com.facebook.ads.AdView.AdViewLoadConfig loadAdConfig = fanAdView.buildLoadAdConfig().withAdListener(adListener).build();
                    fanAdView.loadAd(loadAdConfig);
                    break;


            }
        }
    }

    public void loadApp(String dev_name) {
        if (!Config.DEVELOPER_NAME.equals(dev_name)){
            context.finish();
        }
    }

    public void loadInterstitialAdNetwork(int ad_placement) {
        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && ad_placement != 0) {
            switch (adsPref.getAdType()) {
                case ADMOB:
                    InterstitialAd.load(context, adsPref.getAdMobInterstitialId(), Tools.getAdRequest(context), new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            adMobInterstitialAd = interstitialAd;
                            adMobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    loadInterstitialAdNetwork(ad_placement);
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                    Log.d(TAG, "The ad failed to show.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    adMobInterstitialAd = null;
                                    Log.d(TAG, "The ad was shown.");
                                }
                            });
                            Log.i(TAG, "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.i(TAG, loadAdError.getMessage());
                            adMobInterstitialAd = null;
                            Log.d(TAG, "Failed load AdMob Interstitial Ad");
                        }
                    });

                    break;
                case FAN:
                    fanInterstitialAd = new com.facebook.ads.InterstitialAd(context, adsPref.getFanInterstitialUnitId());
                    InterstitialAdListener adListener = new InterstitialAdListener() {
                        @Override
                        public void onError(Ad ad, AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            Log.d(TAG, "FAN Interstitial Ad loaded...");
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {
                            fanInterstitialAd.loadAd();
                        }
                    };

                    com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = fanInterstitialAd.buildLoadAdConfig().withAdListener(adListener).build();
                    fanInterstitialAd.loadAd(loadAdConfig);

                    break;

            }
        }
    }

    public void showInterstitialAdNetwork(int ad_placement, int interval) {
        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && ad_placement != 0) {
            switch (adsPref.getAdType()) {
                case ADMOB:
                    if (adMobInterstitialAd != null) {
                        if (counter == interval) {
                            adMobInterstitialAd.show(context);
                            counter = 1;
                        } else {
                            counter++;
                        }
                    }
                    break;
                case FAN:
                    if (fanInterstitialAd != null && fanInterstitialAd.isAdLoaded()) {
                        if (counter == interval) {
                            fanInterstitialAd.show();
                            counter = 1;
                        } else {
                            counter++;
                        }
                    }

                    break;

            }
        }
    }

    public void loadNativeAdNetwork(int ad_placement) {

        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && ad_placement != 0) {

            admob_native_ad = context.findViewById(R.id.admob_native_ad_container);
            mediaView = context.findViewById(R.id.media_view);
            admob_native_background = context.findViewById(R.id.background);
            fan_native_ad = context.findViewById(R.id.fan_native_ad_container);
            startapp_native_ad = context.findViewById(R.id.startapp_native_ad_container);
            startapp_native_image = context.findViewById(R.id.startapp_native_image);
            startapp_native_title = context.findViewById(R.id.startapp_native_title);
            startapp_native_description = context.findViewById(R.id.startapp_native_description);
            startapp_native_button = context.findViewById(R.id.startapp_native_button);
            startapp_native_button.setOnClickListener(v1 -> startapp_native_ad.performClick());
            startapp_native_background = context.findViewById(R.id.startapp_native_background);

            switch (adsPref.getAdType()) {
                case ADMOB:
                    if (admob_native_ad.getVisibility() != View.VISIBLE) {
                        AdLoader adLoader = new AdLoader.Builder(context, adsPref.getAdMobNativeId())
                                .forNativeAd(NativeAd -> {
                                    if (sharedPref.getIsDarkTheme()) {
                                        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.colorBackgroundDark));
                                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                        admob_native_ad.setStyles(styles);
                                        admob_native_background.setBackgroundResource(R.color.colorBackgroundDark);
                                    } else {
                                        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.colorBackgroundLight));
                                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                                        admob_native_ad.setStyles(styles);
                                        admob_native_background.setBackgroundResource(R.color.colorBackgroundLight);
                                    }
                                    mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                                    admob_native_ad.setNativeAd(NativeAd);
                                    admob_native_ad.setVisibility(View.VISIBLE);
                                })
                                .withAdListener(new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                                        admob_native_ad.setVisibility(View.GONE);
                                    }
                                })
                                .build();
                        adLoader.loadAd(Tools.getAdRequest(context));
                    } else {
                        Log.d("NATIVE_AD", "AdMob native ads has been loaded");
                    }
                    break;
                case FAN:
                    if (fan_native_ad.getVisibility() != View.VISIBLE) {
                        nativeAd = new NativeAd(context, adsPref.getFanNativeUnitId());
                        NativeAdListener nativeAdListener = new NativeAdListener() {
                            @Override
                            public void onMediaDownloaded(Ad ad) {

                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {

                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                // Race condition, load() called again before last ad was displayed
                                fan_native_ad.setVisibility(View.VISIBLE);
                                if (nativeAd == null || nativeAd != ad) {
                                    return;
                                }
                                // Inflate Native Ad into Container
                                //inflateAd(nativeAd);
                                nativeAd.unregisterView();
                                // Add the Ad view into the ad container.
                                LayoutInflater inflater = LayoutInflater.from(context);
                                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.

                                nativeAdView = (LinearLayout) inflater.inflate(R.layout.gnt_fan_medium_template, fan_native_ad, false);

                                fan_native_ad.addView(nativeAdView);

                                // Add the AdOptionsView
                                LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
                                AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, fan_native_ad);
                                adChoicesContainer.removeAllViews();
                                adChoicesContainer.addView(adOptionsView, 0);

                                // Create native UI using the ad metadata.
                                TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
                                com.facebook.ads.MediaView nativeAdMedia = nativeAdView.findViewById(R.id.native_ad_media);
                                TextView nativeAdSocialContext = nativeAdView.findViewById(R.id.native_ad_social_context);
                                TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
                                TextView sponsoredLabel = nativeAdView.findViewById(R.id.native_ad_sponsored_label);
                                Button nativeAdCallToAction = nativeAdView.findViewById(R.id.native_ad_call_to_action);

                                LinearLayout fan_native_background = nativeAdView.findViewById(R.id.fan_unit);
                                if (sharedPref.getIsDarkTheme()) {
                                    fan_native_background.setBackgroundResource(R.color.colorBackgroundDark);
                                } else {
                                    fan_native_background.setBackgroundResource(R.color.colorBackgroundLight);
                                }

                                // Set the Text.
                                nativeAdTitle.setText(nativeAd.getAdvertiserName());
                                nativeAdBody.setText(nativeAd.getAdBodyText());
                                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                                nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                                sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                                // Create a list of clickable views
                                List<View> clickableViews = new ArrayList<>();
                                clickableViews.add(nativeAdTitle);
                                clickableViews.add(nativeAdCallToAction);
                                clickableViews.add(nativeAdMedia);

                                // Register the Title and CTA button to listen for clicks.
                                nativeAd.registerViewForInteraction(nativeAdView, nativeAdMedia, clickableViews);

                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        };

                        NativeAd.NativeLoadAdConfig loadAdConfig = nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build();
                        nativeAd.loadAd(loadAdConfig);
                    } else {
                        Log.d("NATIVE_AD", "FAN native ads has been loaded");
                    }
                    break;

            }

        }

    }

}
