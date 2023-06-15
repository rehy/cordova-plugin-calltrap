package name.ratson.cordova.calltrap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;


public class CallTrap extends CordovaPlugin {
    private static Logger log = Logger.getLogger(CallTrap.class.getName());

    CallbackContextOwner listener;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        prepareListener();

        listener.setCallbackContext(callbackContext);

        return true;
    }

    private void prepareListener() {
        if (listener == null) {
            try {
                TelephonyManager telephonyMgr = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    CallTrapCallStateListener callStateListener = new CallTrapCallStateListener();

                    listener = callStateListener;
                    boolean listeningAllowed = ContextCompat.checkSelfPermission(cordova.getActivity().getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;

                    if (listeningAllowed) {
                        telephonyMgr.registerTelephonyCallback(
                                cordova.getActivity().getMainExecutor(),
                                callStateListener
                        );
                    }
                } else {
                    CallTrapPhoneStateListener phoneStateListener = new CallTrapPhoneStateListener();

                    listener = phoneStateListener;
                    telephonyMgr.listen(
                            phoneStateListener,
                            PhoneStateListener.LISTEN_CALL_STATE
                    );
                }
            } catch (SecurityException se) {
                log.log(Level.SEVERE, "Couldn't setup call trap listener due to Security Exception - no calltrap events will be triggered", se);
            }
        }
    }
}

interface CallbackContextOwner {
    void setCallbackContext(CallbackContext context);
}

@RequiresApi(api = Build.VERSION_CODES.S)
class CallTrapCallStateListener extends TelephonyCallback implements TelephonyCallback.CallStateListener, CallbackContextOwner {
    private CallbackContext callbackContext;

    public void setCallbackContext(CallbackContext context) {
        this.callbackContext = context;
    }

    public void onCallStateChanged(int state) {
        if (callbackContext == null) {
            return;
        }

        String msg = "";

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                msg = "IDLE";
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                msg = "OFFHOOK";
                break;

            case TelephonyManager.CALL_STATE_RINGING:
                msg = "RINGING";
                break;
        }

        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("state", msg);
            jsonObj.put("number", null);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }


        PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObj);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
    }
}

class CallTrapPhoneStateListener extends PhoneStateListener implements CallbackContextOwner {

    private CallbackContext callbackContext;

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        if (callbackContext == null) return;

        String msg = "";

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                msg = "IDLE";
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                msg = "OFFHOOK";
                break;

            case TelephonyManager.CALL_STATE_RINGING:
                msg = "RINGING";
                break;
        }

        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("state", msg);
            jsonObj.put("number", incomingNumber);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }


        PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObj);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
    }
}
