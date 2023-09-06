package com.app.webdroidx.callbacks;

import com.app.webdroidx.models.Ads;
import com.app.webdroidx.models.App;
import com.app.webdroidx.models.Navigation;
import com.app.webdroidx.models.Placement;
import com.app.webdroidx.models.Settings;

import java.util.ArrayList;
import java.util.List;

public class CallbackConfig {

    public String status = "";
    public App app = null;
    public List<Navigation> menus = new ArrayList<>();
    public Ads ads = null;
    public Placement ads_placement = null;
    public Settings settings = null;

}