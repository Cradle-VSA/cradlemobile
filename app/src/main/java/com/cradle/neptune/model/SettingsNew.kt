package com.cradle.neptune.model

import android.content.SharedPreferences
import javax.inject.Inject

// Implementation note: this class and all it's properties are marked `open`
// so that we can mock them out for testing.

/**
 * Holds app-wide settings which are persisted in Android's shared preference.
 */
open class SettingsNew @Inject constructor(val sharedPreferences: SharedPreferences) {

    /* Network */

    /**
     * The network hostname as configured in Settings > Advanced.
     */
    open val networkHostname: String?
        get() = sharedPreferences.getString("setting_server_hostname", null)

    /**
     * The network port as configured in Settings > Advanced.
     */
    open val networkPort: String?
        get() = sharedPreferences.getString("setting_server_port", null)

    /**
     * Whether to use HTTPS or not, configured in Settings > Advanced.
     */
    open val networkUseHttps: Boolean
        get() = sharedPreferences.getBoolean("setting_server_use_https", true)


    /* VHT Info */

    /**
     * The user's name as configured in Settings.
     */
    open val vhtName: String?
        get() = sharedPreferences.getString("setting_vht_name", null)

    /**
     * The user's region as configured in Settings.
     */
    open val region: String?
        get() = sharedPreferences.getString("setting_region", null)


    /* OCR */

    /**
     * Whether OCR is enabled or not, configured in Settings > Advanced.
     */
    open val isOcrEnabled: Boolean
        get() = sharedPreferences.getBoolean("setting_ocr_enabled", true)

    /**
     * Whether OCR debug options are enabled or not, configured in Settings > Advanced.
     */
    open val isOcrDebugEnabled: Boolean
        get() = sharedPreferences.getBoolean("setting_ocr_debug_enabled", false)
}
