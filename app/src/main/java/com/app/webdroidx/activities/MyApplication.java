package com.app.webdroidx.activities;

import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroid.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.solodroid.ads.sdk.util.Constant.WORTISE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

import com.app.webdroidx.Config;
import com.app.webdroidx.R;
import com.app.webdroidx.databases.prefs.AdsPref;
import com.app.webdroidx.databases.prefs.SharedPref;
import com.app.webdroidx.utils.Tools;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;
import com.solodroid.ads.sdk.format.AppOpenAdAppLovin;
import com.solodroid.ads.sdk.format.AppOpenAdManager;
import com.solodroid.ads.sdk.format.AppOpenAdMob;
import com.solodroid.ads.sdk.format.AppOpenAdWortise;
import com.solodroid.ads.sdk.util.OnShowAdCompleteListener;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    private AppOpenAdMob appOpenAdMob;
    private AppOpenAdManager appOpenAdManager;
    private AppOpenAdAppLovin appOpenAdAppLovin;
    private AppOpenAdWortise appOpenAdWortise;
    Activity currentActivity;
    FirebaseAnalytics mFirebaseAnalytics;
    SharedPref sharedPref;
    AdsPref adsPref;
    String notificationId;
    String notificationTitle = "";
    String notificationMessage = "";
    String notificationBigImage = "";
    String notificationUrl = "";
    String link = "";
    String postId = "";
    String uniqueId = "";

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        initOpenAds();
        initNotification();
    }

    public void initNotification() {
        OneSignal.disablePush(false);
        Log.d(TAG, "OneSignal Notification is enabled");

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.fcm_notification_topic));
        OneSignal.setAppId(getString(R.string.onesignal_app_id));

        OneSignal.setNotificationOpenedHandler(
                result -> {
                    notificationId = result.getNotification().getNotificationId();
                    notificationTitle = result.getNotification().getTitle();
                    notificationMessage = result.getNotification().getBody();
                    notificationBigImage = result.getNotification().getBigPicture();
                    notificationUrl = result.getNotification().getLaunchURL();
                    try {
                        uniqueId = result.getNotification().getAdditionalData().getString("unique_id");
                        postId = result.getNotification().getAdditionalData().getString("post_id");
                        link = result.getNotification().getAdditionalData().getString("link");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("id", notificationId);
                    intent.putExtra("title", notificationTitle);
                    intent.putExtra("message", notificationMessage);
                    intent.putExtra("image", notificationBigImage);
                    intent.putExtra("link", notificationUrl);
                    startActivity(intent);
                });

        OneSignal.unsubscribeWhenNotificationsAreDisabled(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initOpenAds() {
        adsPref = new AdsPref(this);
        if (!Config.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
            registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
            ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);
            appOpenAdMob = new AppOpenAdMob();
            appOpenAdManager = new AppOpenAdManager();
            appOpenAdAppLovin = new AppOpenAdAppLovin();
            appOpenAdWortise = new AppOpenAdWortise();
        }
    }

    LifecycleObserver lifecycleObserver = new DefaultLifecycleObserver() {
        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onStart(owner);
            if (Tools.isAppOpen) {
                if (adsPref.getIsAppOpenAdOnResume()) {
                    if (adsPref.getAdStatus()) {
                        switch (adsPref.getMainAds()) {
                            case ADMOB:
                                if (!adsPref.getAdMobAppOpenId().equals("0")) {
                                    if (!currentActivity.getIntent().hasExtra("unique_id")) {
                                        appOpenAdMob.showAdIfAvailable(currentActivity, adsPref.getAdMobAppOpenId());
                                    }
                                }
                                break;
                            case GOOGLE_AD_MANAGER:
                                if (!adsPref.getAdManagerAppOpenId().equals("0")) {
                                    if (!currentActivity.getIntent().hasExtra("unique_id")) {
                                        appOpenAdManager.showAdIfAvailable(currentActivity, adsPref.getAdManagerAppOpenId());
                                    }
                                }
                                break;
                            case APPLOVIN:
                            case APPLOVIN_MAX:
                                if (!adsPref.getApplovinMaxAppOpenUnitId().equals("0")) {
                                    if (!currentActivity.getIntent().hasExtra("unique_id")) {
                                        appOpenAdAppLovin.showAdIfAvailable(currentActivity, adsPref.getApplovinMaxAppOpenUnitId());
                                    }
                                }
                                break;

                            case WORTISE:
                                if (!adsPref.getWortiseAppOpenUnitId().equals("0")) {
                                    if (!currentActivity.getIntent().hasExtra("unique_id")) {
                                        appOpenAdWortise.showAdIfAvailable(currentActivity, adsPref.getWortiseAppOpenUnitId());
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }
    };

    ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            if (adsPref.getIsAppOpenAdOnStart()) {
                if (adsPref.getAdStatus()) {
                    switch (adsPref.getMainAds()) {
                        case ADMOB:
                            if (!adsPref.getAdMobAppOpenId().equals("0")) {
                                if (!appOpenAdMob.isShowingAd) {
                                    currentActivity = activity;
                                }
                            }
                            break;
                        case GOOGLE_AD_MANAGER:
                            if (!adsPref.getAdManagerAppOpenId().equals("0")) {
                                if (!appOpenAdManager.isShowingAd) {
                                    currentActivity = activity;
                                }
                            }
                            break;
                        case APPLOVIN:
                        case APPLOVIN_MAX:
                            if (!adsPref.getApplovinMaxAppOpenUnitId().equals("0")) {
                                if (!appOpenAdAppLovin.isShowingAd) {
                                    currentActivity = activity;
                                }
                            }
                            break;
                        case WORTISE:
                            if (!adsPref.getWortiseAppOpenUnitId().equals("0")) {
                                if (!appOpenAdWortise.isShowingAd) {
                                    currentActivity = activity;
                                }
                            }
                            break;
                    }
                }
            }
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }
    };

    public void showAdIfAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        if (adsPref.getIsAppOpenAdOnStart()) {
            if (adsPref.getAdStatus()) {
                switch (adsPref.getMainAds()) {
                    case ADMOB:
                        if (!adsPref.getAdMobAppOpenId().equals("0")) {
                            appOpenAdMob.showAdIfAvailable(activity, adsPref.getAdMobAppOpenId(), onShowAdCompleteListener);
                            Tools.isAppOpen = true;
                        }
                        break;
                    case GOOGLE_AD_MANAGER:
                        if (!adsPref.getAdManagerAppOpenId().equals("0")) {
                            appOpenAdManager.showAdIfAvailable(activity, adsPref.getAdManagerAppOpenId(), onShowAdCompleteListener);
                            Tools.isAppOpen = true;
                        }
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                        if (!adsPref.getApplovinMaxAppOpenUnitId().equals("0")) {
                            appOpenAdAppLovin.showAdIfAvailable(activity, adsPref.getApplovinMaxAppOpenUnitId(), onShowAdCompleteListener);
                            Tools.isAppOpen = true;
                        }
                        break;
                    case WORTISE:
                        if (!adsPref.getWortiseAppOpenUnitId().equals("0")) {
                            appOpenAdWortise.showAdIfAvailable(activity, adsPref.getWortiseAppOpenUnitId(), onShowAdCompleteListener);
                            Tools.isAppOpen = true;
                        }
                        break;
                }
            }
        }
    }

}