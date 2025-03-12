package de.mariusbackes.cordova.plugin;

import android.os.Build;
import android.content.res.Configuration;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ThemeDetection extends CordovaPlugin {
  public enum Action {
    isAvailable,
    isDarkModeEnabled,
    onThemeChanged
  }
  private CallbackContext callback = null;
  private CallbackContext onThemeChangedCallback = null;

  // Android 9 (API 28) is needed for dark theme availability
  private static final int MINIMUM_VERSION = 28;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    boolean result = false;

    switch(Action.valueOf(action)){
      case isAvailable:
        result = isAvailable(callbackContext);
        break;
      case isDarkModeEnabled:
        result = isDarkModeEnabled(callbackContext, false);
        break;
      case onThemeChanged:
        result = onThemeChanged(callbackContext);
        break;
    }
    return result;
  }

  private boolean isAvailable(CallbackContext callbackContext) {
    callback = callbackContext;
    try {
      int systemVersion = Build.VERSION.SDK_INT;
      boolean available = systemVersion >= MINIMUM_VERSION;

      String responseMessage = "Dark mode detection is not available. You need at least Android 9 (API 28), but you have installed API " + systemVersion;
      if(available) {
          responseMessage = "Dark mode detection is available";
      }

      JSONObject obj = createReturnObject(available, responseMessage);
      returnCordovaPluginResult(PluginResult.Status.OK, obj, false);
    } catch (Exception e) {
        JSONObject obj = createReturnObject(false, e.getMessage());
        returnCordovaPluginResult(PluginResult.Status.ERROR, obj, true);
        return false;
    }
    return true;
  }

  private boolean isDarkModeEnabled(CallbackContext callbackContext, boolean setKeepCallback) {
    callback = callbackContext;
    try {
      int uiMode = this.cordova.getActivity().getResources().getConfiguration().uiMode 
                    & Configuration.UI_MODE_NIGHT_MASK;
      boolean enabled = uiMode == Configuration.UI_MODE_NIGHT_YES;

      String responseMessage = "Dark mode is not enabled";
      if(enabled) {
          responseMessage = "Dark mode is enabled";
      }

      JSONObject obj = createReturnObject(enabled, responseMessage);
      returnCordovaPluginResult(PluginResult.Status.OK, obj, setKeepCallback);
    } catch (Exception e) {
        JSONObject obj = createReturnObject(false, e.getMessage());
        returnCordovaPluginResult(PluginResult.Status.ERROR, obj, true);
        return false;
    }
    return true;
  }

  private boolean onThemeChanged(CallbackContext callbackContext) {
    onThemeChangedCallback = callbackContext;

    return true;
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    if ( onThemeChangedCallback != null) {
      isDarkModeEnabled(onThemeChangedCallback, true);
    }
  }

  // creates a return object with all needed information
  private JSONObject createReturnObject(boolean value, String message) {
    try {
      JSONObject obj = new JSONObject();
      obj.put("value", value);
      obj.put("message", message);
      return obj;
    } catch (Exception e) {
        System.out.println("JSONObject error: " + e.getMessage());
    }
    return null;
  }

// returns the plugin result to javascript interface
  private void returnCordovaPluginResult(Status status, JSONObject obj, boolean setKeepCallback) {
    PluginResult result = new PluginResult(status, obj);
    result.setKeepCallback(setKeepCallback);
    callback.sendPluginResult(result);
  }
}
