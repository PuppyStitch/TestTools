package com.sprd.validationtools;

import android.util.Log;
import android.os.SystemProperties;

/**
 * Created by SPREADTRUM\zhengxu.zhang on 9/25/15.
 */
public class TelephonyManagerSprd {
    private static final String TAG = "TelephonyManagerSprd";
    // modem type
    public static final int MODEM_TYPE_GSM = 0;
    public static final int MODEM_TYPE_TDSCDMA = 1;
    public static final int MODEM_TYPE_WCDMA = 2;
    public static final int MODEM_TYPE_LTE = 3;
    public static final int MODEM_TYPE_NR = 4;
    public static final String MODEM_TYPE = "ro.vendor.radio.modemtype";
    private static String PROP_SSDA_MODE = "persist.vendor.radio.modem.config";

    // ssda mode
    private static String MODE_SVLTE = "svlte";
    private static String MODE_TDD_CSFB = "TL_TD_G,G";
    private static String MODE_FDD_CSFB = "TL_LF_W_G,G";
    private static String MODE_CSFB = "TL_LF_TD_W_G,G";
    private static String MODE_LW = "TL_LF_TD_W_G,W_G";
    /* SPRD bug 773421:Supprt WCDMA */
    private static String MODE_WG = "W_G,G";
    private static String MODE_WG_WG = "W_G,W_G";
    /* @} */
    /* SPRD bug 814255:Supprt WCDMA */
    private static String MODE_LW_LW = "TL_LF_TD_W_G,TL_LF_TD_W_G";
    /*@}*/
    //LTE FLAG
    private static String LTE_FLAG_TL = "TL";
    private static String LTE_FLAG_TD = "TD";
    private static String LTE_FLAG_LF = "LF";
    /*SPRd bug 827099:Add for support WCDMA*/
    private static String MODE_LWG_WG = "TL_LF_W_G,W_G";
    private static String MODE_LWG_LWG = "TL_LF_W_G,TL_LF_W_G";
    private static String[] MODE_SUPPORT_WCDMA = { MODE_LWG_WG , MODE_LWG_LWG };
    private static String[] MODE_SUPPORT_LTE = { LTE_FLAG_TL,LTE_FLAG_TD,LTE_FLAG_LF };
    /*@}*/
    /*SPRd bug 1025873:Add for support WCDMA*/
    private static String CDMA_FLAG = "C";
    private static String WCDMA_FLAG = "W";
    //Support NR
    private static String NR_FLAG = "NR";
    private static String[] MODE_SUPPORT_NR = { NR_FLAG };

    /**
     * Returns the type of modem for 0:GSM;1:TDSCDMA;2:WCDMA;3:LTE
     */
    public static int getModemType() {
        String modeType = SystemProperties.get(MODEM_TYPE, "");
        Log.d(TAG, "getModemType: modemType=" + modeType);
        if ("t".equals(modeType)) {
            return MODEM_TYPE_TDSCDMA;
        } else if ("w".equals(modeType)) {
            return MODEM_TYPE_WCDMA;
        } else if ("tl".equals(modeType) || "lf".equals(modeType)
                || "l".equals(modeType)) {
            return MODEM_TYPE_LTE;
        } else if ("nr".equals(modeType)) {
            return MODEM_TYPE_NR;
        }
        else {
            return MODEM_TYPE_GSM;
        }
    }

    public static enum RadioCapbility {
        NONE, TDD_SVLTE, FDD_CSFB, TDD_CSFB, CSFB, LW, WG, LWLW
    };

    public static RadioCapbility getRadioCapbility() {
        String ssdaMode = SystemProperties.get(PROP_SSDA_MODE);
        Log.d(TAG, "getRadioCapbility: ssdaMode=" + ssdaMode);
        if (ssdaMode.equals(MODE_SVLTE)) {
            return RadioCapbility.TDD_SVLTE;
        } else if (ssdaMode.equals(MODE_TDD_CSFB)) {
            return RadioCapbility.TDD_CSFB;
        } else if (ssdaMode.equals(MODE_FDD_CSFB)) {
            return RadioCapbility.FDD_CSFB;
        } else if (ssdaMode.equals(MODE_CSFB)) {
            return RadioCapbility.CSFB;
        } else if (ssdaMode.contains(MODE_LW)) {
            return RadioCapbility.CSFB;
        }
        /* SPRD bug 773421:Supprt WCDMA */
        else if (ssdaMode.equals(MODE_WG) || ssdaMode.equals(MODE_WG_WG)) {
            return RadioCapbility.WG;
        }
        /* @} */
        /* SPRD bug 814255:Supprt WCDMA */
        else if (ssdaMode.equals(MODE_LW_LW)) {
            return RadioCapbility.LWLW;
        }
        /* @} */
        return RadioCapbility.NONE;
    }

    /* SPRd bug 830737:Add for support WCDMA */
    public static boolean IsSupportWCDMA() {
        String ssdaMode = SystemProperties.get(PROP_SSDA_MODE);
        Log.d(TAG, "IsSupportWCDMA: ssdaMode=" + ssdaMode);
        for (String support : MODE_SUPPORT_WCDMA) {
            Log.d(TAG, "IsSupportWCDMA: support=" + support);
            if (support.equals(ssdaMode)) {
                return true;
            }
        }
        boolean support = ssdaMode != null && ssdaMode.contains(WCDMA_FLAG);
        Log.d(TAG, "IsSupportWCDMA: support=" + support);
        return support;
    }

    /* @} */
    /* SPRd bug 1025873:Add for support CDMA */
    public static boolean IsSupportCDMA() {
        String ssdaMode = SystemProperties.get(PROP_SSDA_MODE);
        Log.d(TAG, "IsSupportCDMA: ssdaMode=" + ssdaMode);
        boolean support = ssdaMode != null && ssdaMode.contains(CDMA_FLAG);
        Log.d(TAG, "IsSupportCDMA: support=" + support);
        return support;
    }
    /*@}*/
    public static boolean IsSupportLTE() {
        String ssdaMode = SystemProperties.get(PROP_SSDA_MODE);
        Log.d(TAG, "IsSupportLTE: ssdaMode=" + ssdaMode);
        for (String support : MODE_SUPPORT_LTE) {
            Log.d(TAG, "IsSupportLTE: support=" + support);
            if (ssdaMode.contains(support)) {
                return true;
            }
        }
        return false;
    }
    public static boolean IsSupportNR() {
        String ssdaMode = SystemProperties.get(PROP_SSDA_MODE);
        Log.d(TAG, "IsSupportNR: ssdaMode=" + ssdaMode);
        for (String support : MODE_SUPPORT_NR) {
            Log.d(TAG, "IsSupportNR: support=" + support);
            if (ssdaMode.contains(support)) {
                return true;
            }
        }
        return false;
    }
}
