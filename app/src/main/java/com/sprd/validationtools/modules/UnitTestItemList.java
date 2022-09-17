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
            SystemVersionTest.class.getName(), RFCALITest.class.getName(),              // AP版本测试
            ScreenColorTest.class.getName(),                                            // LCD测试
//            TofCalibrationTest.class.getName(),
//            ColorTemperatureTestActivty.class.getName(),
            RTCTest.class.getName(), BackLightTest.class.getName(),                     // RTC时钟测试
//            SmartPATest.class.getName(),
//            AITest.class.getName(),

//            SingleTouchPointTest.class.getName(),                                       // 单点触摸测试
//            MutiTouchTest.class.getName(), MutiTouchDoubleScreenTest.class.getName(),   // 多点触摸测试
            MelodyTest.class.getName(),                                                 // 喇叭和马达测试
            PhoneLoopBackTest.class.getName(),                                          // 语音回环测试
            PhoneCallTestActivity.class.getName(),                                      // 电话测试
            GsensorTestActivity.class.getName(),                                        // 重力传感器测试
            CompassTestActivity.class.getName(),                                        // 罗盘测试
            PsensorTestActivity.class.getName(),                                        // 距离和光传感器测试
            LsensorNoiseTestActivity.class.getName(),                                   // 光感底噪测试
            MagneticTestActivity.class.getName(),                                       // 磁力传感器测试
            GyroscopeTestActivity.class.getName(),                                      // 陀螺仪测试
            PressureTestActivity.class.getName(),                                       // 压力传感器测试
            ASensorCalibrationActivity.class.getName(),                                 // 加速度传感器校验
            GSensorCalibrationActivity.class.getName(),                                 // 陀螺仪传感器校验
            MSensorCalibrationActivity.class.getName(),                                 // 磁力传感器校准
            ProxSensorCalibrationActivity.class.getName(),                              // 距离传感器校验
            NFCTestActivity.class.getName(),                                            // NFC测试
            FrontSecondaryCameraTestActivity.class.getName(),                           // 前置辅相机测试
            SecondaryCameraTestActivity.class.getName(),                                // 后置辅相机测试
            FrontCameraTestActivity.class.getName(),                                    // 前置相机测试
            CameraTestActivity.class.getName(),                                         // 后相机测试
            SpwCameraTestActivity.class.getName(),                                      // 后置三摄相机测试
            MacroLensCameraTestActivity.class.getName(),                                // 微距相机测试
//            FingerprintTestActivity.class.getName(),
            KeyTestActivity.class.getName(), ChargerTest.class.getName(),               // 按键测试
            HeadSetTest.class.getName(), FMTest.class.getName(),                        // 耳机测试
            SoundTriggerTestActivity.class.getName(),                                   // 语音唤醒
            RedLightTest.class.getName(), GreenLightTest.class.getName(),               // 红灯
            BlueLightTest.class.getName(),                                              // 蓝灯
            BluetoothTestActivity.class.getName(),                                      // 蓝牙测试
            WifiTestActivity.class.getName(), GpsTestActivity.class.getName(),          // WiFi测试
            SDCardTest.class.getName(), SIMCardTestActivity.class.getName(),            // SD卡测试
            OTGTest.class.getName(),                                                    // OTG测试
    };

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
