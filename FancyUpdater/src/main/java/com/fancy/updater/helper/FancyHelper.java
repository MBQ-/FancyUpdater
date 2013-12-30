package com.fancy.updater.helper;

import org.json.JSONObject;

public class FancyHelper {

    //    Constants
    public static final int NOTIFICATION_ID = 261684;

    //    Update Information
    private static boolean edition_not_found;
    public static int latest_version;
    public static String kernel_url;
    public static JSONObject kernel_information;

    //    Cpu Information
    public static String[] available_frequencies;
    public static String[] available_governors;

    //    Cpu Information
    public static String min_freq;
    public static String max_freq;
    public static String screen_on_min;
    public static String screen_off_max;
    public static String governor;

    public static void reset() {
        edition_not_found = false;
        latest_version = 0;
        kernel_url = null;
        kernel_information = null;
    }

    public static int getNotificationId() {
        return NOTIFICATION_ID;
    }

    public static boolean isEdition_not_found() {
        return edition_not_found;
    }

    public static void setEdition_not_found(boolean edition_not_found) {
        FancyHelper.edition_not_found = edition_not_found;
    }

    public static String getKernel_url() {
        return kernel_url;
    }

    public static void setKernel_url(String kernel_url) {
        FancyHelper.kernel_url = "http://boypetersen.de/" + kernel_url;
    }

    public static JSONObject getKernel_information() {
        return kernel_information;
    }

    public static void setKernel_information(JSONObject kernel_information) {
        FancyHelper.kernel_information = kernel_information;
    }

    public static int getLatest_version() {
        return latest_version;
    }

    public static void setLatest_version(int latest_version) {
        FancyHelper.latest_version = latest_version;
    }

    public static String[] getAvailable_frequencies() {
        return available_frequencies;
    }

    public static void setAvailable_frequencies(String[] available_frequencies) {
        FancyHelper.available_frequencies = available_frequencies;
    }

    public static String[] getAvailable_governors() {
        return available_governors;
    }

    public static void setAvailable_governors(String[] available_governors) {
        FancyHelper.available_governors = available_governors;
    }

    public static String getMin_freq() {
        return min_freq;
    }

    public static void setMin_freq(String min_freq) {
        FancyHelper.min_freq = min_freq;
    }

    public static String getMax_freq() {
        return max_freq;
    }

    public static void setMax_freq(String max_freq) {
        FancyHelper.max_freq = max_freq;
    }

    public static String getScreen_on_min() {
        return screen_on_min;
    }

    public static void setScreen_on_min(String screen_on_min) {
        FancyHelper.screen_on_min = screen_on_min;
    }

    public static String getScreen_off_max() {
        return screen_off_max;
    }

    public static void setScreen_off_max(String screen_off_max) {
        FancyHelper.screen_off_max = screen_off_max;
    }

    public static String getGovernor() {
        return governor;
    }

    public static void setGovernor(String governor) {
        FancyHelper.governor = governor;
    }
}
