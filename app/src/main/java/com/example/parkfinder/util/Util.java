package com.example.parkfinder.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Util {
    public static final String PARK_URL = "https://developer.nps.gov/api/v1/parks?stateCode=wa&api_key=51Y33BY34UdeAoZ4xnnuRR3LSU4IR4MNRT4jxJY3";

    public static String getParkUrl(String stateCode){
        return "https://developer.nps.gov/api/v1/parks?stateCode="+ stateCode +"&api_key=51Y33BY34UdeAoZ4xnnuRR3LSU4IR4MNRT4jxJY3";
    }
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
}
