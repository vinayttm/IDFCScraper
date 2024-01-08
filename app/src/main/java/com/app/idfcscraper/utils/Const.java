package com.app.idfcscraper.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Const {


    public static  final String TAG = "RESULT";

    public  static  String baseUrl = "https://91.playludo.app/api/CommonAPI/";

    public  static  String SaveMobileBankTransactionUrl =   baseUrl + "SaveMobilebankTransaction";

    public  static  String updateDateBasedOnUpi = baseUrl + "UpdateDateBasedOnUpi?upiId=";
    public  static  String getUpiStatusUrl  = baseUrl + "GetUpiStatus?upiId=";

    public static boolean isLoginProcess = false;

    public static boolean isDrawerOpen = false;

    public  static Context context;
    public static boolean showBottomSheetDialog = false;
    public  static boolean showTransaction = false;
    public static boolean isScroll = false;
    public static boolean parentScroll = false;
    public  static boolean mahaDevEnterPrisesClick = false;
    public  static  boolean isLoading = false;

    public static String pinText = "";
    public static String BankLoginId = "";
    public static String upiId = "";

    public  static  String packageName = "com.idfcfirstbank.optimus";

    public static void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Const.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

}
