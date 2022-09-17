package com.sprd.validationtools.modules;

import android.content.Context;

import com.sprd.validationtools.itemstest.ai.AITest;
import com.sprd.validationtools.itemstest.audio.HeadSetTest;
import com.sprd.validationtools.itemstest.audio.MelodyTest;
import com.sprd.validationtools.itemstest.audio.PhoneLoopBackTest;
import com.sprd.validationtools.itemstest.audio.SmartPATest;
import com.sprd.validationtools.itemstest.audio.SoundTriggerTestActivity;
import com.sprd.validationtools.itemstest.backlight.BackLightTest;
import com.sprd.validationtools.itemstest.bt.BluetoothTestActivity;
import com.sprd.validationtools.itemstest.camera.CameraTestActivity;
import com.sprd.validationtools.itemstest.camera.ColorTemperatureTestActivty;
import com.sprd.validationtools.itemstest.camera.FrontCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.FrontSecondaryCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.MacroLensCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.SecondaryCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.SpwCameraTestActivity;
import com.sprd.validationtools.itemstest.camera.TofCalibrationTest;
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
import com.sprd.validationtools.itemstest.storage.SDCardTest;
import com.sprd.validationtools.itemstest.sysinfo.RFCALITest;
import com.sprd.validationtools.itemstest.sysinfo.SystemVersionTest;
import com.sprd.validationtools.itemstest.telephony.PhoneCallTestActivity;
import com.sprd.validationtools.itemstest.telephony.SIMCardTestActivity;
import com.sprd.validationtools.itemstest.tp.MutiTouchTest;
import com.sprd.validationtools.itemstest.tp.MutiTouchDoubleScreenTest;
import com.sprd.validationtools.itemstest.tp.SingleTouchPointTest;
import com.sprd.validationtools.itemstest.wifi.WifiTestActivity;

public class UnitTestItemList extends TestItemList {
    private static final String TAG = "UnitTestItemList";

    /**
     * This array define the order of test items.
     */
    private static final String[] FILTER_CLASS_NAMES = {
            TofCalibrationTest.class.getName(),
            ColorTemperatureTestActivty.class.getName(),
            SystemVersionTest.class.getName(), RFCALITest.class.getName(),
            RTCTest.class.getName(), BackLightTest.class.getName(),
            SmartPATest.class.getName(),
            AITest.class.getName(),
            ScreenColorTest.class.getName(),
            SingleTouchPointTest.class.getName(),
            MutiTouchTest.class.getName(), MutiTouchDoubleScreenTest.class.getName(),
            MelodyTest.class.getName(),
            PhoneLoopBackTest.class.getName(),
            PhoneCallTestActivity.class.getName(),
            GsensorTestActivity.class.getName(),
            CompassTestActivity.class.getName(),
            PsensorTestActivity.class.getName(),
            LsensorNoiseTestActivity.class.getName(),
            MagneticTestActivity.class.getName(),
            GyroscopeTestActivity.class.getName(),
            PressureTestActivity.class.getName(),
            ASensorCalibrationActivity.class.getName(),
            GSensorCalibrationActivity.class.getName(),
            MSensorCalibrationActivity.class.getName(),
            ProxSensorCalibrationActivity.class.getName(),
            NFCTestActivity.class.getName(),
            FrontSecondaryCameraTestActivity.class.getName(),
            SecondaryCameraTestActivity.class.getName(),
            FrontCameraTestActivity.class.getName(),
            CameraTestActivity.class.getName(),
            SpwCameraTestActivity.class.getName(),
            MacroLensCameraTestActivity.class.getName(),
//            FingerprintTestActivity.class.getName(),
            KeyTestActivity.class.getName(), ChargerTest.class.getName(),
            HeadSetTest.class.getName(), FMTest.class.getName(),
            SoundTriggerTestActivity.class.getName(),
            RedLightTest.class.getName(), GreenLightTest.class.getName(),
            BlueLightTest.class.getName(),
            BluetoothTestActivity.class.getName(),
            WifiTestActivity.class.getName(), GpsTestActivity.class.getName(),
            SDCardTest.class.getName(), SIMCardTestActivity.class.getName(),
            OTGTest.class.getName() };

    private static UnitTestItemList mTestItemListInstance = null;

    public static TestItemList getInstance(Context context) {
        if (mTestItemListInstance == null) {
        	mTestItemListInstance = new UnitTestItemList(context);
        }
        return mTestItemListInstance;
    }

    private UnitTestItemList(Context context) {
        super(context);
    }

    @Override
    public String[] getfilterClassName() {
        return FILTER_CLASS_NAMES;
    }

}
