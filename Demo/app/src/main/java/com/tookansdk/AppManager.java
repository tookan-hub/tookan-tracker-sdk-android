package com.tookansdk;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Developer: Rishabh
 * Dated: 24/06/15.
 */
public class AppManager
{

    // Tag for App Manager
    private static final String TAG = "AppManager";

    //  The singleton instance of AppManager
    private static AppManager appManager;

    //  Activity that runs currently on front
    private Activity activity;



    /**
     * Method to capture the instance of AppManager
     *
     * @return
     */
    public static AppManager getInstance() {

        if (appManager == null)
            appManager = new AppManager();

        return appManager;
    }



    /**
     * Method to return the instance of Currently
     * running Activity with which the AppManager
     * was initialized
     *
     * @return
     */
    public Activity getActivity() {
        return activity;
    }



    /**
     * Method to check whether the Permission is Granted by the User
     * <p/>
     * permission type: DANGEROUS
     *
     * @param activity
     * @param permission
     * @return
     */
    public boolean isPermissionGranted(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Method to check whether the Permission is Granted by the User
     * <p/>
     * permission type: DANGEROUS
     *
     * @param activity
     * @param permission
     * @return
     */
    public boolean askUserToGrantPermission(Activity activity, String permission, String explanation, int code) {

        return askUserToGrantPermission(activity, new String[]{permission}, explanation, code);
    }

    /**
     * Method to check whether the Permission is Granted by the User
     * <p/>
     * permission type: DANGEROUS
     *
     * @param activity
     * @param permissions
     * @param explanation
     * @param requestCode
     * @return
     */
    public boolean askUserToGrantPermission(Activity activity, String[] permissions, String explanation, int requestCode) {

        Log.e(TAG, "askUserToGrantPermission");

        String permissionRequired = null;

        for (String permission : permissions)
            if (!isPermissionGranted(activity, permission)) {
                permissionRequired = permission;
                break;
            }

        // Check if the Permission is ALREADY GRANTED
        if (permissionRequired == null) return true;

        // Check if there is a need to show the PERMISSION DIALOG
        boolean explanationRequired = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionRequired);
        Log.e(TAG, "askUserToGrantPermission: explanationRequired(" + explanationRequired + "): " + permissionRequired);

        // Convey the EXPLANATION if required
        if (explanationRequired) {

            if (explanation == null) explanation = "Please grant permission";
            Toast.makeText(activity, explanation, Toast.LENGTH_SHORT).show();
        } else {

            // We can request the permission, if no EXPLANATIONS required
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }

        return false;
    }






//    public void sendOnlineStatus(final Context context)
//    {
//        JSONArray locationDataArr = null;
//        try {
//            locationDataArr = new JSONArray();
//            JSONObject locationData = new JSONObject();
//            locationData.put("lat", Double.toString(LocationUtils.LATITUDE));
//            locationData.put("lng", Double.toString(LocationUtils.LONGITUDE));
//            locationData.put("bat_lvl", Utils.getBatteryLevel(context));
//            locationData.put("d_acc", 1);
//            locationDataArr.put(locationData);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.i(TAG,"locationDataArr"+locationDataArr.toString());
//        ApiClient.executeRestApi(context).updateFleetLocation(
//                Dependencies.getAccessToken(context),
//                locationDataArr, new Callback<String>() {
//
//                    @Override
//                    public void success(String stringJson, Response response) {
//
//                        try {
//
//                            JSONObject json = new JSONObject(stringJson);
//
//                            int status = json.getInt("status");
//
//                            if (status == 200) {
//
//
//                            } else if (status == Codes.StatusCode.INVALID_ACCESS_TOKEN.getStatusCode()) {
//
//                                AppManager appManager = AppManager.getInstance();
//
//                                if (appManager != null) {
//
//                                    appManager.stopFleetTracker(context);
//
//                                    Activity activity = appManager.getActivity();
//                                    if (activity != null)
//                                        appManager.invalidateSession(activity, true);
//                                }
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//
//                    }
//                }
//        );
//    }


}
