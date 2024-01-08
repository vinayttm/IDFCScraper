package com.app.idfcscraper.utils;

import android.os.Environment;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.app.idfcscraper.api.ApiCaller;
import com.app.idfcscraper.client.RetrofitClient;
import com.app.idfcscraper.localstorage.SharedPreferencesManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataFilter {
    static ApiCaller apiCaller = new ApiCaller();

    public static void convertToJson(AccessibilityNodeInfo rootNode) {
        List<String> allTextList = AccessibilityMethod.getAllTextInNode(rootNode);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < allTextList.size(); i++) {
            if (allTextList.contains("Balance")) {
                if (i <= 5) {
                    jsonArray.put(allTextList.get(i));
                }
            } else {
                jsonArray.put(allTextList.get(i));
            }
        }
        String[] stringsToRemove = {"Can't find your recent transactions?", "Payments", "Transactions", "Upcoming",
                "Recurring", "Money Manager", "Can't find your recent transactions? ", "Click here",
                "No more transactions to load.", "Start", "Accounts", "Pay", "Loans", "More", "Is it ", "?", "Del",
                "Is it", "Del", "Search", "Filter", "Try “Mutual Funds”"};
        for (String stringToRemove : stringsToRemove) {
            AccessibilityMethod.removeStringFromJsonArray(jsonArray, stringToRemove);
        }
        List<String> currentGroup = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            String jsonString = null;
            try {
                jsonString = jsonArray.getString(i);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            currentGroup.add(jsonString);
        }
        if (!currentGroup.isEmpty()) {
            List<String> newList = new ArrayList<>();
            for (String str : currentGroup) {
                if (str.contains("|") || str.contains(" | ")) {
                    String[] parts = str.split("\\|");
                    if (parts.length >= 2) {
                        newList.add(parts[0].trim());
                        newList.add(parts[1].trim());
                    }
                } else {
                    newList.add(str);
                }
            }
            Log.d("JSON Array", new JSONArray(newList).toString());
            List<List<String>> groupedData = groupData(newList);
            Log.d("Grouped Data", groupedData.toString());
            List<JSONObject> main = new ArrayList<>();
            String CreatedDate = "", AccountBalance = "", time = "";
            String modelNumber = DeviceInfo.getModelNumber();
            String secureId = DeviceInfo.generateSecureId(Const.context);
            Log.d("DATA SIZE LENGTH", String.valueOf(newList.size()));
            for (int i = 0; i < newList.size(); ) {
                JSONObject jsonObject = new JSONObject();
                JSONObject entry = new JSONObject();
                String descriptionOrMaybeDate = newList.get(i);
                if (descriptionOrMaybeDate.contains("Yesterday")) {
                    descriptionOrMaybeDate = convertYesterDayToDate(descriptionOrMaybeDate, "dd MMM, yyyy");
                }
                if (descriptionOrMaybeDate.contains("Today")) {
                    descriptionOrMaybeDate = convertTodayToDate(descriptionOrMaybeDate, "dd MMM, yyyy");
                }
                System.out.println("Original Date =>" + descriptionOrMaybeDate);

                if (isDate(descriptionOrMaybeDate)) {
                    CreatedDate = newList.get(i);
                    AccountBalance = newList.get(i + 1);
                    String Description = newList.get(i + 2);
                    // String Description = newList.get(i + 3);
                    String Amount = newList.get(i + 4);
                    time = newList.get(i + 5) + " " + newList.get(i + 6);
                    try {
                        Description = Description.replaceAll("\\s+", "").replaceAll("Del", "");
                        Amount = Amount.replace("Balance ₹", "");
                        entry.put("Description", extractUTRFromDesc(Description));
                        entry.put("UPIId", getUPIId(Description));
                        if (CreatedDate.contains("Today")) {
                            CreatedDate = convertTodayToDate(CreatedDate, "dd MMM, yyyy") + " " + time;
                        }
                        if (CreatedDate.contains("Yesterday")) {
                            CreatedDate = convertYesterDayToDate(CreatedDate, "dd MMM, yyyy") + " " + time;
                        }
                        String formattedDate = convertDateFormat(CreatedDate) + " " + time;
                        entry.put("CreatedDate", formattedDate);
                        if (Amount.contains("+")) {
                            Amount = Amount.replace("+", "");
                        } else {
                            Amount = "-" + Amount;
                        }
                        if (AccountBalance.contains("Balance ₹")) {
                            AccountBalance = AccountBalance.replace("Balance ₹", "");
                        }
                        if (Amount.contains(" ₹")) {
                            Amount = Amount.replace(" ₹", "");
                        }
                        if (Amount.contains("₹")) {
                            Amount = Amount.replace("₹", "");
                        }
                        entry.put("Amount", Amount.toString());
                        entry.put("RefNumber", extractUTRFromDesc(Description));
                        entry.put("BankName", "IDFC Bank-" + Const.BankLoginId);
                        entry.put("BankLoginId", Const.BankLoginId);
                        entry.put("DeviceInfo", modelNumber + " " + secureId);
                        entry.put("AccountBalance", AccountBalance.toString());
                        jsonObject.put("entries", entry);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    i = i + 7;
                } else {
                    String UPIId = "";
                    String Description = "";
                    String Amount = "";
                    Description = newList.get(i);
                    // Description = newList.get(i + 1);
                    Amount = newList.get(i + 2);
                    time = newList.get(i + 3) + " " + newList.get(i + 4);
                    try {
                        Description = Description.replaceAll("\\s+", "").replaceAll("Del", "");
                        Amount = Amount.replace("Balance ₹", "");
                        entry.put("Description", extractUTRFromDesc(Description));
                        entry.put("UPIId", getUPIId(Description));
                        if (CreatedDate.contains("Today")) {
                            CreatedDate = convertTodayToDate(CreatedDate, "dd MMM, yyyy") + " " + time;
                        }
                        if (CreatedDate.contains("Yesterday")) {
                            CreatedDate = convertYesterDayToDate(CreatedDate, "dd MMM, yyyy") + " " + time;
                        }
                        String formattedDate = convertDateFormat(CreatedDate) + " " + time;
                        entry.put("CreatedDate", formattedDate);
                        if (Amount.contains("+")) {
                            Amount = Amount.replace("+", "");
                        } else {
                            Amount = "-" + Amount;
                        }
                        if (AccountBalance.contains("Balance ₹")) {
                            AccountBalance = AccountBalance.replace("Balance ₹", "");
                        }
                        if (Amount.contains(" ₹")) {
                            Amount = Amount.replace(" ₹", "");
                        }
                        if (Amount.contains("₹")) {
                            Amount = Amount.replace("₹", "");
                        }
                        entry.put("Amount", Amount);
                        entry.put("RefNumber", extractUTRFromDesc(Description));
                        entry.put("BankName", "IDFC Bank-" + Const.BankLoginId);
                        entry.put("BankLoginId", Const.BankLoginId);
                        entry.put("DeviceInfo", modelNumber + " " + secureId);
                        entry.put("AccountBalance", AccountBalance.toString());
                        jsonObject.put("entries", entry);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    i = i + 5;
                }
                main.add(jsonObject);
            }
            List<JSONObject> allData = parseEntries(main);
            JSONObject finalJson = new JSONObject();

            System.out.println("allData final " + allData);

//            try {
//                finalJson.put("Result", allData);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            try {
                finalJson.put("Result", AES.encrypt(allData.toString()));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            System.out.println("finalJson File= " + finalJson.toString());
            sendTransactionData(finalJson.toString());


        }
    }

    public static List<List<String>> groupData(List<String> data) {
        List<List<String>> groupedData = new ArrayList<>();
        List<String> currentGroup = new ArrayList<>();

        for (String item : data) {
            currentGroup.add(item);

            if ((item.endsWith("PM") || item.endsWith("AM")) && !currentGroup.isEmpty()) {
                groupedData.add(new ArrayList<>(currentGroup));
                currentGroup.clear();
            }
        }

        if (!currentGroup.isEmpty()) {
            groupedData.add(new ArrayList<>(currentGroup));
        }

        return groupedData;
    }

    public static boolean isDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        try {
            Date date = dateFormat.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static List<JSONObject> parseEntries(List<JSONObject> jsonList) {
        List<JSONObject> entryList = new ArrayList<>();
        try {
            for (JSONObject jsonObject : jsonList) {
                JSONObject entriesObject = jsonObject.getJSONObject("entries");
                entryList.add(entriesObject);
            }
        } catch (JSONException e) {
        }
        return entryList;
    }

    public static String extractUTRFromDesc(String description) {
        try {
            String[] split = description.split("/");
            String value = null;
            value = Arrays.stream(split).filter(x -> x.length() == 12).findFirst().orElse(null);
            if (value != null) {
                return value + " " + description;
            }
            return description;
        } catch (Exception e) {
            return description;
        }
    }

    public static String getUPIId(String description) {
        try {
            if (!description.contains("@"))
                return "";
            String[] split = description.split("/");
            String value = null;
            value = Arrays.stream(split).filter(x -> x.contains("@")).findFirst().orElse(null);
            return value != null ? value : "";
        } catch (Exception ex) {
            Log.d("Exception", ex.getMessage());
            return "";
        }
    }

    public static String convertDateFormat(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String convertTodayToDateFormat(String dateString) {
        if ("Today".equals(dateString)) {
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
            Date currentDate = new Date();
            return outputFormat.format(currentDate);
        } else {
            return dateString;
        }
    }

    private static String convertTodayToDate(String dateString, String pattern) {
        if ("Today".equals(dateString)) {
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(currentDate);
        } else {
            return dateString;
        }
    }


    private static String convertYesterDayToDate(String dateString, String pattern) {
        if ("Yesterday".equals(dateString)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            Date yesterday = calendar.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(yesterday);
        } else {
            return dateString;
        }
    }


    private static void sendTransactionData(String data) {
        Const.isLoading = true;
        System.out.println("sendTransactionData  upiId" + Const.upiId);
        if (apiCaller.getUpiStatus(Const.getUpiStatusUrl + Const.upiId)) {
            apiCaller.postData(Const.SaveMobileBankTransactionUrl, data);
            updateDateBasedOnUpi();
            Const.isLoading = false;
        } else {
            Log.d("Failed to called api because of upi status off", "in Active status");
            Const.isLoading = false;
        }
    }

    private static void updateDateBasedOnUpi() {
        System.out.println("updateDateBasedOnUpi  upiId" + Const.upiId);
        ApiCaller apiCaller = new ApiCaller();
        apiCaller.fetchData(Const.updateDateBasedOnUpi + Const.upiId);
    }




    public static void flitterList(AccessibilityNodeInfo rootNode) {
        if(Const.totalBalance.equals(""))
        {
            return;
        }
        List<String> allText = AccessibilityMethod.getAllTextInNode(rootNode);
        JSONArray jsonArray = new JSONArray();
        String modelNumber = DeviceInfo.getModelNumber();
        String secureId = DeviceInfo.generateSecureId(Const.context);
        System.out.println("transactionSectionList" + allText.toString());
        allText.removeIf(e -> e.contains("Recent Transactions") || e.contains("For transactions with categorization and more") || e.contains("kindly refer to the History tab in the Pay menu.") || e.contains("Recent up to 15 transaction(s)") || e.contains("Close") || e.contains("Recent upto 15 transaction(s)"));
        for (int i = 0; i < allText.size(); i += 3) {
            JSONObject entry = new JSONObject();
            String date = allText.get(i);
            String amount = allText.get(i + 2);
            String formattedDate = "";
            System.out.println("Today With Vinay" + date);
            if (date.contains("Today")) {
                formattedDate = convertTodayToDateFormat(date);
            } else if (date.contains("Yesterday")) {
                formattedDate = convertYesterDayToDate(date, "dd MMM, yyyy");
            } else {
                formattedDate = convertDateFormat(date);
            }
            try {
                if(amount.contains("₹"))
                {
                    amount = amount.replace("₹", "");
                }
                if (amount.contains("+")) {
                    amount = amount.replace("+", "");
                } else {
                    amount = "-" + amount;
                }


                entry.put("Description", allText.get(i + 1));
                entry.put("UPIId", getUPIId(allText.get(i + 1)));
                entry.put("CreatedDate", formattedDate);
                entry.put("Amount", amount);
                entry.put("RefNumber", extractUTRFromDesc(allText.get(i + 1)));
                entry.put("BankName", "IDFC Bank-" + Const.BankLoginId);
                entry.put("BankLoginId", Const.BankLoginId);
                entry.put("DeviceInfo", modelNumber + " " + secureId);
                entry.put("AccountBalance", Const.totalBalance);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            jsonArray.put(entry);
        }

        Log.d("filterList = ", jsonArray.toString());
        JSONObject finalJson = new JSONObject();


//            try {
//                finalJson.put("Result", allData);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

        try {
            finalJson.put("Result", AES.encrypt(jsonArray.toString()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        System.out.println("finalJson File= " + finalJson.toString());
       sendTransactionData(finalJson.toString());


    }

    //            String fileName = "data.json";
//            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//
//            if (!downloadFolder.exists()) {
//                downloadFolder.mkdirs();
//            }
//
//            File jsonFile = new File(downloadFolder, fileName);
//
//            try (FileWriter fileWriter = new FileWriter(jsonFile)) {
//                fileWriter.write(new Gson().toJson(finalJson));
//                fileWriter.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            }
}
