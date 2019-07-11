package gmedia.net.id.semargres2019merchant.util;

/**
 * Created by Bayu on 29/12/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import gmedia.net.id.semargres2019merchant.HomeActivity;
import gmedia.net.id.semargres2019merchant.LoginActivity;

public class SessionManager {
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_UID = "uid";

    // Shared pref mode
    public static final String KEY_USERNAME = "username";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_CHECK = "check";
    public static final String KEY_FLAG = "flag";
    public static final String KEY_FLAG_JUAL_TIKET = "flag_jual_tiket";
    public static final String KEY_FLAG_PARADE = "flag_parade";

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";
    private static final String CHECK_NAME = "check";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_CHECK = "IsCheckIn";
    // Shared Preferences
    SharedPreferences pref, pref2;
    // Editor for Shared preferences
    Editor editor, editor2;
    // Context
    Context _context;
    int PRIVATE_MODE = 0;
    int CHECK_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        pref2 = context.getSharedPreferences(CHECK_NAME, CHECK_MODE);
        editor = pref.edit();
        editor2 = pref2.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, String email, String token, String uid, String username, String flag, String flagTiket, String flagParade) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        // Storing token in pref
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FLAG, flag);

        editor.putString(KEY_FLAG_JUAL_TIKET, flagTiket);
        editor.putString(KEY_FLAG_PARADE, flagParade);


        // commit changes
        editor.commit();
    }


    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (this.isLoggedIn()) {
            // user is not logged in redirect him to LoginActivity Activity
            Intent i = new Intent(_context, HomeActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring LoginActivity Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public String getUid() {
        return pref.getString(KEY_UID, "");
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, "");
    }

    public String getFlag() {
        return pref.getString(KEY_FLAG, "");
    }

    public String getFlagTiket(){
        return pref.getString(KEY_FLAG_JUAL_TIKET, "");
    }

    public String getFlagParade(){
        return pref.getString(KEY_FLAG_PARADE, "");
    }

    /**
     * Clear session details
     */

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring LoginActivity Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get LoginActivity State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isCheckIn() {
        return pref2.getBoolean(IS_CHECK, false);
    }
}