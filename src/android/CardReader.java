package com.plugin.cardreader;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.corewise.idcardreadersdk.Interface.Conn;
import com.corewise.idcardreadersdk.NFC.NFCConn;
import com.corewise.idcardreadersdk.ReadAPI;
import com.corewise.idcardreadersdk.Bean.IdCardInfo;
import com.corewise.idcardreadersdk.Interface.ReadListener;
import android.content.Intent;
import android.Manifest;
import android.widget.Toast;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Set;
import org.apache.cordova.PermissionHelper;

public class CardReader extends CordovaPlugin{
    private CallbackContext callbackContext;
    private PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        if (action.equals("coolMethod")) {
            // Toast.makeText(cordova.getActivity(), "coolMethod调用成功", Toast.LENGTH_SHORT).show();
            try {
                Intent intent = new Intent(cordova.getActivity(),CardActivity.class);
                // intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("com.plugin.cardreader.isRegister",args.get(0).toString());

                this.checkPermission();

//              cordova.getActivity().startActivity(intent);
                cordova.startActivityForResult(this, intent, 1);

                this.pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);

            } catch (Exception e) {
                callbackContext.error("StartActivityError");
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    // onActivityResult为第二个Activity执行完后的回调接收方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (resultCode) { // resultCode为回传的标记，我在第二个Activity中回传的是RESULT_OK
            case Activity.RESULT_OK:
                Bundle idCardInfo = intent.getExtras(); // data为第二个Activity中回传的Intent
                if(idCardInfo.getBoolean("startFlag")){
                    this.callbackContext.success("{\"start\":true}");
                }else{
                    String userName = intent.getStringExtra("userName");
                    String userIdNo = intent.getStringExtra("userIdNo");
                    String userSex = intent.getStringExtra("userSex");
                    String userNation = intent.getStringExtra("userNation");
                    String userBirthday = intent.getStringExtra("userBirthday");
                    String userAddress = intent.getStringExtra("userAddress");
                    String userEndDate = intent.getStringExtra("userEndDate");
                    String userStartDate = intent.getStringExtra("userStartDate");
                    Set<String> keys = idCardInfo.keySet();
                    String str ="{";
                    for (String key : keys) {
                        str=str+("\""+key+"\":\""+idCardInfo.getString(key)+"\",");
                    }
                    str = str.substring(0,str.length()-1);
                    str+="}";
                    this.callbackContext.success(str);
                    this.pluginResult.setKeepCallback(false);
                    this.callbackContext.sendPluginResult(pluginResult);
                }
                break;
            case  Activity.RESULT_CANCELED:
                Bundle errorInfo = intent.getExtras(); // data为第二个Activity中回传的Intent
                this.callbackContext.error(errorInfo.getString("errorInfo"));
                break;
            default:
                break;
        }
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            Toast.makeText(cordova.getActivity(), message, Toast.LENGTH_SHORT).show();
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }


    public boolean checkPermission(){
        boolean permissionNFC = PermissionHelper.hasPermission(this, Manifest.permission.NFC);
        boolean permissionINTERNET = PermissionHelper.hasPermission(this, Manifest.permission.INTERNET);
        boolean permissionREAD_PHONE_STATE = PermissionHelper.hasPermission(this, Manifest.permission.READ_PHONE_STATE);
        boolean permissionREAD_EXTERNAL_STORAGE = PermissionHelper.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean permissionMOUNT_UNMOUNT_FILESYSTEMS = PermissionHelper.hasPermission(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
        boolean permissionACCESS_WIFI_STATE = PermissionHelper.hasPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        boolean permissionACCESS_NETWORK_STATE = PermissionHelper.hasPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        boolean permissionACCESS_COARSE_LOCATION = PermissionHelper.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean permissionACCESS_FINE_LOCATION = PermissionHelper.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(!permissionNFC){
            PermissionHelper.requestPermission(this, 0, Manifest.permission.NFC);
            return false;
        }
        if(!permissionINTERNET){
            PermissionHelper.requestPermission(this, 0, Manifest.permission.INTERNET);
            return false;
        }
        if(!permissionREAD_PHONE_STATE){
            PermissionHelper.requestPermission(this, 0, Manifest.permission.READ_PHONE_STATE);
            return false;
        }
        if(!permissionREAD_EXTERNAL_STORAGE){
            PermissionHelper.requestPermission(this, 0, Manifest.permission.READ_EXTERNAL_STORAGE);
            return false;
        }
        if(!permissionMOUNT_UNMOUNT_FILESYSTEMS){
            PermissionHelper.requestPermission(this, 0, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
            return false;
        }
        if(!permissionACCESS_WIFI_STATE){
            PermissionHelper.requestPermission(this, 0, Manifest.permission.ACCESS_WIFI_STATE);
            return false;
        }
        if(!permissionACCESS_NETWORK_STATE){
            PermissionHelper.requestPermission(this, 0, Manifest.permission.ACCESS_NETWORK_STATE);
            return false;
        }
        if(!permissionACCESS_COARSE_LOCATION){
            PermissionHelper.requestPermission(this, 0, Manifest.permission.ACCESS_COARSE_LOCATION);
            return false;
        }
        if(!permissionACCESS_FINE_LOCATION){
            PermissionHelper.requestPermission(this, 0, Manifest.permission.ACCESS_FINE_LOCATION);
            return false;
        }
        return true;
    }
}
