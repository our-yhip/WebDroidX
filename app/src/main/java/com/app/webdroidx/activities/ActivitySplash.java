package com.app.webdroidx.activities;

import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroid.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.solodroid.ads.sdk.util.Constant.WORTISE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.webdroidx.BuildConfig;
import com.app.webdroidx.Config;
import com.app.webdroidx.R;
import com.app.webdroidx.callbacks.CallbackConfig;
import com.app.webdroidx.databases.prefs.AdsPref;
import com.app.webdroidx.databases.prefs.SharedPref;
import com.app.webdroidx.databases.sqlite.DbNavigation;
import com.app.webdroidx.models.Ads;
import com.app.webdroidx.models.App;
import com.app.webdroidx.models.Navigation;
import com.app.webdroidx.models.Placement;
import com.app.webdroidx.models.Settings;
import com.app.webdroidx.rests.RestAdapter;
import com.app.webdroidx.utils.AdsManager;
import com.app.webdroidx.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    public static final String TAG = "SplashActivity";
    Call<CallbackConfig> callbackConfigCall = null;
    ProgressBar progressBar;
    AdsManager adsManager;
    SharedPref sharedPref;
    AdsPref adsPref;
    List<Navigation> navigationList = new ArrayList<>();
    DbNavigation dbNavigation;
    ImageView imgSplash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.getTheme(this);
        setContentView(R.layout.activity_splash);
        Tools.setNavigation(this);

        dbNavigation = new DbNavigation(this);
        adsManager = new AdsManager(this);
        adsManager.initializeAd();

        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);

        imgSplash = findViewById(R.id.imgSplash);
        if (sharedPref.getIsDarkTheme()) {
            imgSplash.setImageResource(R.drawable.splash_dark);
        } else {
            imgSplash.setImageResource(R.drawable.splash_default);
        }

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        requestConfig();

    }

    private void requestConfig() {
        if (adsPref.getAdStatus() && adsPref.getIsAppOpenAdOnStart()) {
            if (!Config.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
                Tools.postDelayed(() -> {
                    switch (adsPref.getMainAds()) {
                        case ADMOB:
                            if (!adsPref.getAdMobAppOpenId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::validateAccessKey);
                            } else {
                                validateAccessKey();
                            }
                            break;
                        case GOOGLE_AD_MANAGER:
                            if (!adsPref.getAdManagerAppOpenId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::validateAccessKey);
                            } else {
                                validateAccessKey();
                            }
                            break;
                        case APPLOVIN:
                        case APPLOVIN_MAX:
                            if (!adsPref.getApplovinMaxAppOpenUnitId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::validateAccessKey);
                            } else {
                                validateAccessKey();
                            }
                            break;
                        case WORTISE:
                            if (!adsPref.getWortiseAppOpenUnitId().equals("0")) {
                                ((MyApplication) getApplication()).showAdIfAvailable(ActivitySplash.this, this::validateAccessKey);
                            } else {
                                validateAccessKey();
                            }
                            break;
                        default:
                            validateAccessKey();
                            break;
                    }
                }, Config.DELAY_SPLASH);
            } else {
                validateAccessKey();
            }
        } else {
            validateAccessKey();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void validateAccessKey() {

            String remoteUrl = "https://appsligo.cloud//webdroid_applicationId_com.app.webdroidx";
            
            requestAPI(remoteUrl);

    }



    private void requestAPI(String remoteUrl) {
        callbackConfigCall = RestAdapter.createApi(remoteUrl).getConfig(BuildConfig.APPLICATION_ID);
        callbackConfigCall.enqueue(new Callback<CallbackConfig>() {
            public void onResponse(@NonNull Call<CallbackConfig> call, @NonNull Response<CallbackConfig> response) {
                CallbackConfig resp = response.body();
                displayApiResults(resp);
            }

            public void onFailure(@NonNull Call<CallbackConfig> call, @NonNull Throwable th) {
                Log.e(TAG, "initialize failed");
                showAppOpenAdIfAvailable(false);
            }
        });
    }

    private void displayApiResults(CallbackConfig resp) {

        if (resp != null) {
            App app = resp.app;
            navigationList = resp.menus;
            Ads ads = resp.ads;
            Placement placement = resp.ads_placement;
            Settings settings = resp.settings;

            if (app.status == 1) {
                adsManager.saveSettings(sharedPref, settings);
                adsManager.saveAds(adsPref, ads);
                adsManager.saveAdsPlacement(adsPref, placement);
                sharedPref.setRedirectUrl(app.redirect_url);

                dbNavigation.truncateTableMenu(DbNavigation.TABLE_MENU);
                Tools.postDelayed(() -> {
                    dbNavigation.addListCategory(navigationList, DbNavigation.TABLE_MENU);
                    showAppOpenAdIfAvailable(adsPref.getIsAppOpen());
                }, 100);
                Log.d(TAG, "App status is live");
            } else {
                Intent intent = new Intent(getApplicationContext(), ActivityRedirect.class);
                startActivity(intent);
                finish();
                Log.d(TAG, "App status is suspended");
            }
            Log.d(TAG, "initialize success");
        } else {
            Log.d(TAG, "initialize failed");
            showAppOpenAdIfAvailable(false);
        }

    }

    private void showAppOpenAdIfAvailable(boolean showAd) {
        Tools.postDelayed(() -> {
            if (showAd) {
                if (Config.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
                    adsManager.loadAppOpenAd(adsPref.getIsAppOpenAdOnStart(), this::startMainActivity);
                } else {
                    startMainActivity();
                }
            } else {
                startMainActivity();
            }
        }, 100);
    }

    private void startMainActivity() {
        Tools.postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }, Config.DELAY_SPLASH);
    }

}
