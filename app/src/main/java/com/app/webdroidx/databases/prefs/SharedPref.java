package com.app.webdroidx.databases.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("blog_setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //blog credentials
    public void saveBlogCredentials(String bloggerId, String apiKey) {
        editor.putString("blogger_id", bloggerId);
        editor.putString("api_key", apiKey);
        editor.apply();
    }

    public Boolean getIsDarkTheme() {
        return sharedPreferences.getBoolean("theme", false);
    }

    public void setIsDarkTheme(Boolean isDarkTheme) {
        editor.putBoolean("theme", isDarkTheme);
        editor.apply();
    }

    public Integer getLastItemPosition() {
        return sharedPreferences.getInt("item_position", 0);
    }

    public void setLastItemPosition(int position) {
        editor.putInt("item_position", position);
        editor.apply();
    }

    public String getBaseUrl() {
        return sharedPreferences.getString("base_url", "");
    }

    public void setBaseUrl(String baseUrl) {
        editor.putString("base_url", baseUrl);
        editor.apply();
    }

    public void saveSettings(boolean toolbar, boolean navigationDrawer, boolean geolocation, boolean cache, boolean zoom_controls, String userAgent, String privacyPolicy, String moreAppsUrl) {
        editor.putBoolean("toolbar", toolbar);
        editor.putBoolean("navigation_drawer", navigationDrawer);
        editor.putBoolean("geolocation", geolocation);
        editor.putBoolean("cache", cache);
        editor.putBoolean("zoom_controls", zoom_controls);
        editor.putString("user_agent", userAgent);
        editor.putString("privacy_policy", privacyPolicy);
        editor.putString("more_apps_url", moreAppsUrl);
        editor.apply();
    }

    public boolean getToolbar() {
        return sharedPreferences.getBoolean("toolbar", true);
    }

    public boolean getNavigationDrawer() {
        return sharedPreferences.getBoolean("navigation_drawer", true);
    }

    public boolean getGeolocation() {
        return sharedPreferences.getBoolean("geolocation", true);
    }

    public boolean getCache() {
        return sharedPreferences.getBoolean("cache", true);
    }

//    public boolean getOpenLinkInExternalBrowser() {
//        return sharedPreferences.getBoolean("open_link_in_external_browser", true);
//    }

    public boolean getBuiltInZoomControls() {
        return sharedPreferences.getBoolean("zoom_controls", false);
    }

    public String getUserAgent() {
        return sharedPreferences.getString("user_agent", "");
    }

    public String getPrivacyPolicy() {
        return sharedPreferences.getString("privacy_policy", "");
    }

    public String getMoreAppsUrl() {
        return sharedPreferences.getString("more_apps_url", "");
    }

    public void setRedirectUrl(String redirectUrl) {
        editor.putString("redirect_url", redirectUrl);
        editor.apply();
    }

    public String getRedirectUrl() {
        return sharedPreferences.getString("redirect_url", "");
    }

//    public void resetPageToken() {
//        sharedPreferences.edit().remove("page_token").apply();
//    }
//
//    public Integer getFontSize() {
//        return sharedPreferences.getInt("font_size", 2);
//    }
//
//    public void updateFontSize(int font_size) {
//        editor.putInt("font_size", font_size);
//        editor.apply();
//    }

    public Integer getInAppReviewToken() {
        return sharedPreferences.getInt("in_app_review_token", 0);
    }

    public void updateInAppReviewToken(int value) {
        editor.putInt("in_app_review_token", value);
        editor.apply();
    }

}
