package com.app.webdroidx;

public class Config {

    //generate your Server Key from the admin panel or check the documentation for more detailed instructions
    public static final String SERVER_KEY = "MkzVBvyZ2aU2ESAd2QaHR0cHM6Ly9hcHBzbGlnby5jbG91ZC93ZWJkcm9pZF9hcHBsaWNhdGlvbklkX2NvbS5hcHAud2ViZHJvaWR4";

    //RTL Direction for Arabic Language
    public static final boolean ENABLE_RTL_MODE =  false;

    //show linear progress indicator when load web page
    public static final boolean ENABLE_LINEAR_PROGRESS_INDICATOR = true;

    //GDPR EU consent for users in EEA and UK countries
    public static final boolean ENABLE_GDPR_EU_CONSENT = true;

    //force to open link in the web page using external web browser
    public static final boolean FORCE_TO_OPEN_LINK_IN_EXTERNAL_BROWSER = false;

    //Show exit dialog when user want to close the app
    public static final boolean SHOW_EXIT_DIALOG = true;

    //Enable it with true value if want to the app will force to display open ads first before start the main menu
    //Longer duration to start the app may occur depending on internet connection or open ad response time itself
    public static final boolean FORCE_TO_SHOW_APP_OPEN_AD_ON_START = false;

    //delay splash when remote config finish loading
    public static final int DELAY_SPLASH = 1500;

}