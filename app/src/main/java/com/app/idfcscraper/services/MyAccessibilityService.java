package com.app.idfcscraper.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.app.idfcscraper.utils.AccessibilityMethod;
import com.app.idfcscraper.utils.Const;
import com.app.idfcscraper.utils.DataFilter;

import java.util.ArrayList;
import java.util.List;


public class MyAccessibilityService extends AccessibilityService {
    private final Handler handler = new Handler();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        CharSequence packageNameCharSeq = event.getPackageName();
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (packageNameCharSeq != null) {
            String packageName = packageNameCharSeq.toString();
            Log.d("MyAccessibilityService", "Package Name: " + packageName);
            if (packageNameCharSeq.equals(Const.packageName)) {
                if (rootNode != null) {
                    removeSmsPopup(rootNode);
                    internetConnection(rootNode);
                    logout(rootNode);
                    if(!Const.isLoading)
                    {
                        loginUser(rootNode);
                        openDrawer(rootNode);
                        accessBusinessProfile(rootNode);
                        clickMahadevEnterprisesNode(rootNode);
                        viewTransactions(rootNode);
                        scrollToReachTransactionList(rootNode);
                        performAutoScroll(rootNode);
                        clickViewFullHistoryText(rootNode);
                        moreTransactionScrollView(rootNode);
                        getTransactionDetails(rootNode);
                        rootNode.recycle();
                    }
                    rootNode.recycle();
                }
            }

        } else {
            Log.e("MyAccessibilityService", "Package Name is null");
        }
    }


    @Override
    public void onInterrupt() {
        Log.d(Const.TAG, "onInterrupt Something went wrong");
    }


    private void openDrawer(AccessibilityNodeInfo root) {
        if (Const.isDrawerOpen) {
            return;
        }
        AccessibilityNodeInfo menuNode = AccessibilityMethod.findNodeByResourceId(root, "hamburger-menu-icon");
        if (menuNode != null) {
            boolean isClicked = menuNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            menuNode.recycle();
            if (isClicked) {
                Const.isDrawerOpen = true;
            }
        }
    }

    private void accessBusinessProfile(AccessibilityNodeInfo rootNode) {
        List<String> allText = AccessibilityMethod.getAllTextInNode(rootNode);
        for (String text : allText) {
            if (text.contains("Access Business Profile")) {
                AccessibilityNodeInfo accessBusinessProfileText = AccessibilityMethod.findNodeByResourceId(rootNode, "access-business-profile");
                if (accessBusinessProfileText != null) {
                    Rect boundsInScreen = new Rect();
                    accessBusinessProfileText.getBoundsInScreen(boundsInScreen);
                    int clickX = boundsInScreen.centerX();
                    int clickY = boundsInScreen.centerY();
                    boolean isClicked = performTap(clickX, clickY, 1100);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (isClicked) {
                        accessBusinessProfileText.recycle();
                        // Const.showBottomSheetDialog = true;
                    } else {
                        Log.d(Const.TAG, "Unable to click");
                    }
                }
            }
        }
    }


    private void clickMahadevEnterprisesNode(AccessibilityNodeInfo rootNode) {
//        if (Const.showBottomSheetDialog) {


//        if (Const.mahaDevEnterPrisesClick) {
//            return;
//        }
//        AccessibilityNodeInfo mahadevEnterprisesNode = AccessibilityMethod.findNodeWithTextRecursive(rootNode, "Mahadev Enterprises");
//        if (mahadevEnterprisesNode != null) {
//            AccessibilityNodeInfo parentNode = mahadevEnterprisesNode.getParent();
//            mahadevEnterprisesNode.recycle();
//            if (parentNode != null) {
//                Log.d(Const.TAG, "Parent Node Properties: " +
//                        "Clickable: " + parentNode.isClickable() +
//                        ", Focusable: " + parentNode.isFocusable() +
//                        ", Enabled: " + parentNode.isEnabled());
//
//                Rect bounds = new Rect();
//                parentNode.getBoundsInScreen(bounds);
//                Log.d(Const.TAG, "Parent Node Bounds: " + bounds.toString());
//                try {
//                    Thread.sleep(1000); // 1-second delay
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (parentNode.isClickable()) {
//                    boolean isClicked = parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    Log.d(Const.TAG, "Parent Node Clicked: " + isClicked);
//                    Const.showTransaction = true;
//                    Const.mahaDevEnterPrisesClick = true;
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                } else {
//                    Log.e(Const.TAG, "Parent Node is not clickable");
//                }
//                parentNode.recycle();
//            } else {
//                Log.e(Const.TAG, "Parent Node not found");
//            }
//        }
//
        String targetResourceId = "initialText";
        List<AccessibilityNodeInfo> initialTextNode = getAllNodesRecursively(rootNode);
        AccessibilityNodeInfo lastNodeToClick = null;

        for (AccessibilityNodeInfo node : initialTextNode) {
            if (node.getViewIdResourceName() != null && node.getViewIdResourceName().equals(targetResourceId)) {
                lastNodeToClick = node;
            }
        }
        if (lastNodeToClick != null) {
            Rect boundsInScreen = new Rect();
            lastNodeToClick.getBoundsInScreen(boundsInScreen);
            int clickX = boundsInScreen.centerX();
            int clickY = boundsInScreen.centerY();
            boolean isClicked = performTap(clickX, clickY, 500);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (isClicked) {
                Log.d("IsClicked", String.valueOf(isClicked));
                Const.showTransaction = true;
                Const.mahaDevEnterPrisesClick = true;
            }
            lastNodeToClick.recycle();
        }

    }

    private List<AccessibilityNodeInfo> getAllNodesRecursively(AccessibilityNodeInfo node) {
        List<AccessibilityNodeInfo> nodes = new ArrayList<>();
        if (node == null) {
            return nodes;
        }
        nodes.add(node);
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo childNode = node.getChild(i);
            List<AccessibilityNodeInfo> childNodes = getAllNodesRecursively(childNode);
            nodes.addAll(childNodes);
        }
        return nodes;
    }


    private void viewTransactions(AccessibilityNodeInfo rootNode) {
        if (Const.showTransaction) {
            List<String> allText = AccessibilityMethod.getAllTextInNode(rootNode);
            //View
            AccessibilityNodeInfo viewTransaction = AccessibilityMethod.findNodeWithTextRecursive(rootNode, "View");
            if (viewTransaction != null) {
                AccessibilityNodeInfo parentNode = viewTransaction.getParent();
                viewTransaction.recycle();
                if (parentNode != null) {
                    Log.d(Const.TAG, "Transactions Node Properties: " +
                            "Transactions Clickable: " + parentNode.isClickable() +
                            ",Transactions  Focusable: " + parentNode.isFocusable() +
                            ",Transactions  Enabled: " + parentNode.isEnabled());

                    Rect bounds = new Rect();
                    parentNode.getBoundsInScreen(bounds);
                    Log.d(Const.TAG, "Transactions Parent Node Bounds: " + bounds.toString());
                    try {
                        Thread.sleep(500); // 1-second delay
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (parentNode.isClickable()) {
                        boolean isClicked = parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.d(Const.TAG, "Parent Node Clicked: " + isClicked);
                        if (isClicked) {
                            Const.showTransaction = false;
                            Const.isScroll = true;
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.e(Const.TAG, "Transactions  Node is not clickable");
                    }
                    parentNode.recycle();
                } else {
                    Log.e(Const.TAG, "Transactions Node not found");
                }
            } else {
                Log.e(Const.TAG, "Transactions Text  Node not found");
            }
        }
    }


    private void scrollToReachTransactionList(AccessibilityNodeInfo rootNode) {
        if (Const.isScroll) {
            if (rootNode != null) {
                AccessibilityNodeInfo scrollableNode = findScrollableNode(rootNode);
                if (scrollableNode != null) {
                    boolean scrolled = scrollableNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    if (scrolled) {
                        Log.d(Const.TAG, "Scrolled foreword done ! ");
                        Const.parentScroll = true;
                        Const.isScroll = false;
                    } else {
                        Log.e(Const.TAG, "Failed to scroll  first ");
                    }
                    scrollableNode.recycle();
                } else {
                    Log.e(Const.TAG, "No scrollable node found ");
                }
            }
        }
    }


    private void performAutoScroll(AccessibilityNodeInfo rootNode) {
        if (Const.parentScroll) {
            if (rootNode != null) {
                AccessibilityNodeInfo statementWrapper = AccessibilityMethod.findNodeByResourceId(rootNode, "statementwrapper");
                if (statementWrapper != null) {
                    boolean scrolled = statementWrapper.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    if (scrolled) {
                        Log.d(Const.TAG, "Scrolled forward successfully");
                    } else {
                        Log.e(Const.TAG, "Failed to scroll forward");
                    }
                    statementWrapper.recycle();
                } else {
                    Log.e(Const.TAG, "transactionListNode not found");
                }
            }
        }
    }

    private void clickViewFullHistoryText(AccessibilityNodeInfo rootNode) {
        AccessibilityNodeInfo viewFullHistoryNode = AccessibilityMethod.findNodeByResourceId(rootNode, "view-full-history");
        if (viewFullHistoryNode != null) {
            boolean isClicked = viewFullHistoryNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            if (isClicked) {
                Const.parentScroll = false;
            }

        }
    }


    private AccessibilityNodeInfo findScrollableNode(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return null;
        }

        if (rootNode.isScrollable()) {
            return rootNode;
        }
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = rootNode.getChild(i);
            AccessibilityNodeInfo scrollableChild = findScrollableNode(childNode);

            if (scrollableChild != null) {
                return scrollableChild;
            }
        }

        return null;
    }



    private void moreTransactionScrollView(AccessibilityNodeInfo rootNode) {
        AccessibilityNodeInfo transactionSectionList = AccessibilityMethod.findNodeByResourceId(rootNode, "transaction-section-list");
        if (transactionSectionList != null) {
            while (transactionSectionList.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                    getTransactionDetails(rootNode);
            }
        }
        }

    private void  getTransactionDetails(AccessibilityNodeInfo rootNode) {
        List<String> allText = AccessibilityMethod.getAllTextInNode(rootNode);
        for (String text : allText) {
            if (text.contains("No more transactions to load.")) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                DataFilter.convertToJson(rootNode);
                AccessibilityNodeInfo start = AccessibilityMethod.findNodeByResourceId(rootNode, "start");
                if (start != null) {
                    boolean isClicked = start.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    if (isClicked) {
                        Const.showTransaction = true;
                        Const.isScroll = false;
                        Const.parentScroll = false;
                        Const.isLoading = false;
                    }
                    start.recycle();
                }
                break;
            }
        }
    }




//        AccessibilityNodeInfo transactionSectionList = AccessibilityMethod.findNodeByResourceId(rootNode, "transaction-section-list");
//        if (transactionSectionList != null && scrollCount < 2) {
//            while (transactionSectionList.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
//                scrollCount++;
//                if (scrollCount >= 2) {
//                    DataFilter.convertToJson(rootNode);
//                    AccessibilityNodeInfo Start = AccessibilityMethod.findNodeByResourceId(rootNode, "start");
//                    if (Start != null) {
//                        boolean isClicked = Start.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        if (isClicked) {
//                            scrollCount = 0;
//                            Const.showTransaction = true;
//                            Const.isScroll = false;
//                            Const.parentScroll = false;
//                        }
//                    }
//                    break;
//                }
//            }
//        }





    private void logout(AccessibilityNodeInfo rootNode) {
        List<String> data = AccessibilityMethod.getAllTextInNode(rootNode);
        for (String text : data) {
            if (text.contains("Timeout")) {
                Const.isLoginProcess = false;
                Const.isDrawerOpen = false;
                Const.showBottomSheetDialog = false;
                Const.showTransaction = false;
                Const.isScroll = false;
                Const.parentScroll = false;
                Const.mahaDevEnterPrisesClick = false;
                Log.d("Logout", "Logout Successfully");
            }
        }
    }

    private void internetConnection(AccessibilityNodeInfo rootNode) {
        List<String> data = AccessibilityMethod.getAllTextInNode(rootNode);
        for (String text : data) {
            if (text.contains("No internet connection") || text.contains("Please check your internet connection and try again.")) {
                Const.isLoginProcess = false;
                Const.isDrawerOpen = false;
                Const.showBottomSheetDialog = false;
                Const.showTransaction = false;
                Const.isScroll = false;
                Const.parentScroll = false;
                Const.mahaDevEnterPrisesClick = false;
                Log.d("Internet Connection", "Internet Connection Found");
            }
        }
    }


    private void loginUser(AccessibilityNodeInfo rootNode) {

        if (Const.isLoginProcess) {
            return;
        }
        AccessibilityNodeInfo inputTextField = AccessibilityMethod.findNodeByResourceId(rootNode, "mpin-input");
        if (inputTextField != null) {
            inputTextField.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle.EMPTY);
//                String pinValue = "8898";
            String pinValue = Const.pinText;
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, pinValue);
            inputTextField.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            inputTextField.recycle();
            Const.isLoginProcess = true;
        } else {
            Const.isLoginProcess = false;
        }

    }


    private void removeSmsPopup(AccessibilityNodeInfo node) {
        List<String> data = AccessibilityMethod.getAllTextInNode(node);
        for (String text : data) {
            if (text.contains("Flash SMS message")) {
                AccessibilityNodeInfo cancel = AccessibilityMethod.findNodeWithTextRecursive(node, "Cancel");
                cancel.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d(Const.TAG, "Message Pop Closed");
            }
        }
    }

    private void continueTransactionDialog(AccessibilityNodeInfo rootNode) {
        List<String> allText = AccessibilityMethod.getAllTextInNode(rootNode);
        for (String text : allText) {
            Log.d("continueTransactionDialog ", text);
            if (text.contains("Session about to expire. Would you like to continue?")) {
                AccessibilityMethod.findAndClickNodeByText(rootNode, "Continue");
                Const.isLoginProcess = false;
                Const.isDrawerOpen = false;
                Const.showBottomSheetDialog = false;
                Const.showTransaction = false;
                Const.isScroll = false;
                Const.parentScroll = false;
                Const.mahaDevEnterPrisesClick = false;
                Const.isLoading  = false;
            }
        }

    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(Const.TAG, "onServiceConnected");
    }

    public boolean performTap(float x, float y, long duration) {
        Log.d("Accessibility", "Tapping " + x + " and " + y);
        Path p = new Path();
        p.moveTo(x, y);
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(p, 0, duration));

        GestureDescription gestureDescription = gestureBuilder.build();

        boolean dispatchResult = false;
        dispatchResult = dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }
        }, null);
        Log.d("Dispatch Result", String.valueOf(dispatchResult));
        return dispatchResult;
    }

}
