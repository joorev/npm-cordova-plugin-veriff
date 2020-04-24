

package org.apache.cordova.csantosm;

import android.content.Intent;
import android.util.Log;
import androidx.annotation.Nullable;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONArray;

// Veriff imports
import mobi.lab.veriff.data.Veriff;
import mobi.lab.veriff.data.VeriffConstants;


public class VeriffCordovaPlugin  extends CordovaPlugin {

    // Actions
    private static final String LAUNCH_ACTION = "launchVeriffSDK";

    private static final int REQUEST_CODE = 800;
    private static final String SESSION_URL = "https://alchemy.veriff.com";
    private static CallbackContext callbackContextApp;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContextApp = callbackContext;
        if(action.equals(LAUNCH_ACTION)){
            if(args.isNull(0)){
                this.callbackContextApp.error("Error: Session token is required");
                return false;
            }
            String sessionToken = args.getString(0);
            this.launchVeriffSDK(sessionToken);
            return true;
        }

        this.callbackContextApp.error("Method not implemented");
        return false;
    }

    private void launchVeriffSDK(String sessionToken){
        cordova.setActivityResultCallback(this);
        Veriff.Builder veriffSDK = new Veriff.Builder(SESSION_URL, sessionToken);
        veriffSDK.launch(cordova.getActivity(),REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && data != null) {
            int statusCode = data.getIntExtra(VeriffConstants.INTENT_EXTRA_STATUS, Integer.MIN_VALUE);
            String sessionToken = data.getStringExtra(VeriffConstants.INTENT_EXTRA_SESSION_URL);
            this.handleResult(statusCode, sessionToken); //see below to know how to handle result
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void handleResult(int statusCode, String sessionToken){
        String text = "";
        switch(statusCode) {
            case VeriffConstants.STATUS_DONE:
                // Session is completed from clients perspective.
                text = "STATUS_DONE";
                break;
            case VeriffConstants.STATUS_USER_FINISHED:
                // User finished the session, there might be other callbacks coming after this one. (for example if the images are still being uploaded in the background)
                text = "STATUS_USER_FINISHED";
                break;
            case VeriffConstants.STATUS_SUBMITTED:
                // Photos are successfully uploaded.
                text = "STATUS_SUBMITTED";
                break;
            case VeriffConstants.STATUS_USER_CANCELED:
                // User cancelled the session.
                text = "STATUS_USER_CANCELED";
                break;
            case VeriffConstants.STATUS_ERROR_NO_IDENTIFICATION_METHODS_AVAILABLE:
                // No identifications methods available
                text = "STATUS_ERROR_NO_IDENTIFICATION_METHODS_AVAILABLE";
                break;
            case VeriffConstants.STATUS_ERROR_SETUP:
                // Issue with the provided vendor data
                text = "STATUS_ERROR_SETUP";
                break;
            case VeriffConstants.STATUS_ERROR_UNKNOWN:
                // Unknown error occurred.
                text = "STATUS_ERROR_UNKNOWN";
                break;
            case VeriffConstants.STATUS_ERROR_NETWORK:
                // Network is unavailable.
                text = "STATUS_ERROR_NETWORK";
                break;
            case VeriffConstants.STATUS_UNABLE_TO_ACCESS_CAMERA:
                // Failed to access device's camera. (either access denied or there are no usable cameras)
                text = "STATUS_UNABLE_TO_ACCESS_CAMERA";
                break;
            case VeriffConstants.STATUS_UNABLE_TO_START_CAMERA:
                // Failed to start the device's camera.
                text = "STATUS_UNABLE_TO_START_CAMERA";
                break;
            case VeriffConstants.STATUS_UNABLE_TO_RECORD_AUDIO:
                // Failed to access device's microphone.
                text = "STATUS_UNABLE_TO_RECORD_AUDIO";
                break;
            case VeriffConstants.STATUS_ERROR_SESSION:
                // Invalid sessionToken is passed to the Veriff SDK.
                text = "InvaSTATUS_ERROR_SESSION";
                break;
            case VeriffConstants.STATUS_ERROR_UNSUPPORTED_SDK_VERSION:
                // This version of Veriff SDK is deprecated.
                text = "STATUS_ERROR_UNSUPPORTED_SDK_VERSION";
                break;
            default:
                text = "UNKNOWN";
                break;
        }
        Log.d("Handle VeriffSDK result", text);
        this.callbackContextApp.success(text);
    }

}