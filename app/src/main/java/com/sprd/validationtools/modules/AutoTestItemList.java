package com.sprd.validationtools.modules;

import android.content.Context;

import com.sprd.validationtools.Const;
import com.sprd.validationtools.itemstest.audio.HeadSetTest;
import com.sprd.validationtools.itemstest.audio.MelodyTest;
import com.sprd.validationtools.itemstest.audio.PhoneLoopBackTest;
import com.sprd.validationtools.itemstest.audio.RingtoneTestActivity;
import com.sprd.validationtools.itemstest.audio.SoundTriggerTestActivity;
import com.sprd.validationtools.itemstest.audio.VibratorTestActivity;
import com.sprd.validationtools.itemstest.backlight.BackLightTest;
import com.sprd.validationtools.itemstest.bt.BluetoothTestActivity;
import com.sprd.validationtools.itemstest.camera.CameraTestActivity;
import com.sprd.validationtools.itemstest.camera.FrontCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.FrontSecondaryCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.MacroLensCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.SecondaryCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.SpwCameraTestActivity;
import com.sprd.validationtools.itemstest.charger.ChargerTest;
//import com.sprd.validationtools.itemstest.fingerprint.FingerprintTestActivity;
import com.sprd.validationtools.itemstest.fm.FMTest;
import com.sprd.validationtools.itemstest.gps.GpsTestActivity;
import com.sprd.validationtools.itemstest.keypad.KeyTestActivity;
import com.sprd.validationtools.itemstest.lcd.ScreenColorTest;
import com.sprd.validationtools.itemstest.led.BlueLightTest;
import com.sprd.validationtools.itemstest.led.GreenLightTest;
import com.sprd.validationtools.itemstest.led.RedLightTest;
import com.sprd.validationtools.itemstest.nfc.NFCTestActivity;
import com.sprd.validationtools.itemstest.otg.OTGTest;
import com.sprd.validationtools.itemstest.rtc.RTCTest;
import com.sprd.validationtools.itemstest.sensor.ASensorCalibrationActivity;
import com.sprd.validationtools.itemstest.sensor.CompassTestActivity;
import com.sprd.validationtools.itemstest.sensor.GSensorCalibrationActivity;
import com.sprd.validationtools.itemstest.sensor.GsensorTestActivity;
import com.sprd.validationtools.itemstest.sensor.GyroscopeTestActivity;
import com.sprd.validationtools.itemstest.sensor.LsensorNoiseTestActivity;
import com.sprd.validationtools.itemstest.sensor.MSensorCalibrationActivity;
import com.sprd.validationtools.itemstest.sensor.MagneticTestActivity;
import com.sprd.validationtools.itemstest.sensor.PressureTestActivity;
import com.sprd.validationtools.itemstest.sensor.ProxSensorCalibrationActivity;
import com.sprd.validationtools.itemstest.sensor.PsensorTestActivity;
import com.sprd.validationtools.itemstest.sptest.ICCardTestActivity;
import com.sprd.validationtools.itemstest.sptest.MCRTestActivity;
import com.sprd.validationtools.itemstest.sptest.MyNFCTestActivity;
import com.sprd.validationtools.itemstest.sptest.POSSensorTestActivity;
import com.sprd.validationtools.itemstest.sptest.PosIDTestActivity;
import com.sprd.validationtools.itemstest.sptest.SPVersionTestActivity;
import com.sprd.validationtools.itemstest.sptest.TimerTestActivity;
import com.sprd.validationtools.itemstest.sptest.VirtualLedTestActivity;
import com.sprd.validationtools.itemstest.sysinfo.SystemVersionTest;
import com.sprd.validationtools.itemstest.telephony.PhoneCallTestActivity;
import com.sprd.validationtools.itemstest.telephony.SIMCardTestActivity;
import com.sprd.validationtools.itemstest.tp.MutiTouchTest;
import com.sprd.validationtools.itemstest.tp.MutiTouchDoubleScreenTest;
import com.sprd.validationtools.itemstest.tp.ScreenTestActivity;
import com.sprd.validationtools.itemstest.tp.SingleTouchPointTest;
import com.sprd.validationtools.itemstest.wholetest.BarcodeTestActivity;
import com.sprd.validationtools.itemstest.wholetest.BuzzerTestActivity;
import com.sprd.validationtools.itemstest.wholetest.FlashTestActivity;
import com.sprd.validationtools.itemstest.wholetest.PrintTestActivity;
import com.sprd.validationtools.itemstest.wholetest.QRCodeTestActivity;
import com.sprd.validationtools.itemstest.wifi.WifiTestActivity;

public class AutoTestItemList extends TestItemList {
    private static final String TAG = "AutoTestItemList";

    /** This array of auto test items. */
    private static final String[] FILTER_CLASS_NAMES = {

            SystemVersionTest.class.getName(),                                          // AP????????????
            SPVersionTestActivity.class.getName(),                                      // SP????????????
            ScreenColorTest.class.getName(),                                            // LCD??????
            BackLightTest.class.getName(),                                              // ???????????????
            ScreenTestActivity.class.getName(),                                         // todo:?????????
            KeyTestActivity.class.getName(),                                            // ???????????? todo need to modify
            RingtoneTestActivity.class.getName(),                                       // ????????????
            VibratorTestActivity.class.getName(),                                       // ????????????
            HeadSetTest.class.getName(),                                                // ????????????
            FlashTestActivity.class.getName(),                                          // todo:?????????
            OTGTest.class.getName(),                                                    // OTG??????
            ChargerTest.class.getName(),                                                // ????????????
            SIMCardTestActivity.class.getName(),                                        // SIM?????????
            WifiTestActivity.class.getName(),                                           // wifi??????
            BluetoothTestActivity.class.getName(),                                      // ????????????
            GpsTestActivity.class.getName(),                                            // GPS??????
            MyNFCTestActivity.class.getName(),                                          // todo:NFC??????   ok
            ICCardTestActivity.class.getName(),                                         // todo:IC?????????   ok
            MCRTestActivity.class.getName(),                                            // todo:????????????    ok
            VirtualLedTestActivity.class.getName(),                                     // todo:LED???????????????    ok
            BuzzerTestActivity.class.getName(),                                         // ???????????????
            PrintTestActivity.class.getName(),                                          // todo:?????????     ok
            CameraTestActivity.class.getName(),                                         // ???????????????
            QRCodeTestActivity.class.getName(),                                         // todo:?????????     ok
            BarcodeTestActivity.class.getName(),                                        // todo:?????????     ok
            PsensorTestActivity.class.getName(),                                        // ???????????????????????????
            TimerTestActivity.class.getName(),                                          // RTC????????????
            PosIDTestActivity.class.getName(),                                          // todo:SP????????????
            POSSensorTestActivity.class.getName(),                                      // todo:POS Sensor??????

//            BackLightTest.class.getName(), ScreenColorTest.class.getName(),
//            SingleTouchPointTest.class.getName(),
//            MutiTouchTest.class.getName(), MutiTouchDoubleScreenTest.class.getName(),
//            MelodyTest.class.getName(),
//            PhoneLoopBackTest.class.getName(),
//            PhoneCallTestActivity.class.getName(),
//            GsensorTestActivity.class.getName(),
//            CompassTestActivity.class.getName(),
//            PsensorTestActivity.class.getName(),
//            LsensorNoiseTestActivity.class.getName(),
//            MagneticTestActivity.class.getName(),
//            GyroscopeTestActivity.class.getName(),
//            PressureTestActivity.class.getName(),
//            ASensorCalibrationActivity.class.getName(),
//            GSensorCalibrationActivity.class.getName(),
//            MSensorCalibrationActivity.class.getName(),
//            ProxSensorCalibrationActivity.class.getName(),
//            NFCTestActivity.class.getName(),
//            FrontSecondaryCameraTestActivity.class.getName(),
//            SecondaryCameraTestActivity.class.getName(),
//            FrontCameraTestActivity.class.getName(),
//            CameraTestActivity.class.getName(),
//            SpwCameraTestActivity.class.getName(),
//            MacroLensCameraTestActivity.class.getName(),
//            FingerprintTestActivity.class.getName(),
//            KeyTestActivity.class.getName(), ChargerTest.class.getName(),
//            HeadSetTest.class.getName(), FMTest.class.getName(),
//            SoundTriggerTestActivity.class.getName(),
//            RedLightTest.class.getName(), GreenLightTest.class.getName(),
//            BlueLightTest.class.getName(),
//            OTGTest.class.getName() };
    };

    private static AutoTestItemList mTestItemListInstance = null;

    public static TestItemList getInstance(Context context) {
        if (mTestItemListInstance == null) {
        	mTestItemListInstance = new AutoTestItemList(context);
        }
        return mTestItemListInstance;
    }

    private AutoTestItemList(Context context) {
        super(context);
    }

    @Override
    public String[] getFilterClassName() {
        if (Const.TEST_VALUE == Const.SMT_VALUE) {
            return SMTTestItems.FILTER_CLASS_NAMES;
        }
        if (Const.TEST_VALUE == Const.MMI2_VALUE) {
            return MMI2TestItems.FILTER_CLASS_NAMES;
        }
        return MMI1TestItems.FILTER_CLASS_NAMES;
//        return FILTER_CLASS_NAMES;
    }

}
