package com.sprd.validationtools.modules;

import android.content.Context;

import com.sprd.validationtools.itemstest.audio.HeadSetTest;
import com.sprd.validationtools.itemstest.audio.RingtoneTestActivity;
import com.sprd.validationtools.itemstest.audio.VibratorTestActivity;
import com.sprd.validationtools.itemstest.backlight.BackLightTest;
import com.sprd.validationtools.itemstest.bt.BluetoothTestActivity;
import com.sprd.validationtools.itemstest.camera.CameraTestActivity;
import com.sprd.validationtools.itemstest.charger.ChargerTest;
//import com.sprd.validationtools.itemstest.fingerprint.FingerprintTestActivity;
import com.sprd.validationtools.itemstest.tp.ScreenTestActivity;
import com.sprd.validationtools.itemstest.gps.GpsTestActivity;
import com.sprd.validationtools.itemstest.keypad.KeyTestActivity;
import com.sprd.validationtools.itemstest.lcd.ScreenColorTest;
import com.sprd.validationtools.itemstest.otg.OTGTest;
import com.sprd.validationtools.itemstest.rtc.RTCTest;
import com.sprd.validationtools.itemstest.sensor.PsensorTestActivity;
import com.sprd.validationtools.itemstest.sptest.ICCardTestActivity;
import com.sprd.validationtools.itemstest.sptest.MCRTestActivity;
import com.sprd.validationtools.itemstest.sptest.MyNFCTestActivity;
import com.sprd.validationtools.itemstest.sptest.PosIDTestActivity;
import com.sprd.validationtools.itemstest.sptest.VirtualLedTestActivity;
import com.sprd.validationtools.itemstest.sysinfo.SystemVersionTest;
import com.sprd.validationtools.itemstest.telephony.SIMCardTestActivity;
import com.sprd.validationtools.itemstest.wholetest.BarcodeTestActivity;
import com.sprd.validationtools.itemstest.wholetest.BuzzerTestActivity;
import com.sprd.validationtools.itemstest.wholetest.FlashTestActivity;
import com.sprd.validationtools.itemstest.wholetest.PrintTestActivity;
import com.sprd.validationtools.itemstest.wholetest.QRCodeTestActivity;
import com.sprd.validationtools.itemstest.wifi.WifiTestActivity;

public class UnitTestItemList extends TestItemList {
    private static final String TAG = "UnitTestItemList";

    /**
     * This array define the order of test items.
     */
    private static final String[] FILTER_CLASS_NAMES = {
            SystemVersionTest.class.getName(),                                          // AP版本测试
            PosIDTestActivity.class.getName(),                                          // todo:SP版本测试
            ScreenColorTest.class.getName(),                                            // LCD测试
            BackLightTest.class.getName(),                                              // 背景光测试
            ScreenTestActivity.class.getName(),                                  // todo:触摸屏
            KeyTestActivity.class.getName(),                                            // 按键测试 todo need to modify
            RingtoneTestActivity.class.getName(),                                       // 铃声测试
            VibratorTestActivity.class.getName(),                                       // 震动测试
            HeadSetTest.class.getName(),                                                // 耳机测试
            FlashTestActivity.class.getName(),                                          // todo:闪光灯
            OTGTest.class.getName(),                                                    // OTG测试
            ChargerTest.class.getName(),                                                // 充电测试
            SIMCardTestActivity.class.getName(),                                        // SIM卡测试
            WifiTestActivity.class.getName(),                                           // wifi测试
            BluetoothTestActivity.class.getName(),                                      // 蓝牙测试
            GpsTestActivity.class.getName(),                                            // GPS测试
            MyNFCTestActivity.class.getName(),                                          // todo:NFC测试   ok
            ICCardTestActivity.class.getName(),                                         // todo:IC卡测试   ok
            MCRTestActivity.class.getName(),                                            // todo:磁卡测试    ok
            VirtualLedTestActivity.class.getName(),                                     // todo:LED灯及蜂鸣器    ok
            BuzzerTestActivity.class.getName(),
            PrintTestActivity.class.getName(),                                          // todo:打印机     ok
            CameraTestActivity.class.getName(),                                         // 后相机测试
            QRCodeTestActivity.class.getName(),                                         // todo:二维码     ok
            BarcodeTestActivity.class.getName(),                                        // todo:一维码     ok
            PsensorTestActivity.class.getName(),                                        // 距离和光传感器测试
            RTCTest.class.getName(),                                                    // RTC时钟测试
                                                                                        // todo:POS TP识别测试      ok
                                                                                        // todo:POS Sensor测试
//            MelodyTest.class.getName(),                                                 // 喇叭和马达测试
//            RFCALITest.class.getName(),
//            TofCalibrationTest.class.getName(),
//            ColorTemperatureTestActivty.class.getName(),
//            FMTest.class.getName(),

//            SmartPATest.class.getName(),
//            AITest.class.getName(),
//            SingleTouchPointTest.class.getName(),                                       // 单点触摸测试
//            MutiTouchTest.class.getName(), MutiTouchDoubleScreenTest.class.getName(),   // 多点触摸测试

//            PhoneLoopBackTest.class.getName(),                                          // 语音回环测试
//            PhoneCallTestActivity.class.getName(),                                      // 电话测试
//            GsensorTestActivity.class.getName(),                                        // 重力传感器测试
//            CompassTestActivity.class.getName(),                                        // 罗盘测试
//            LsensorNoiseTestActivity.class.getName(),                                   // 光感底噪测试
//            MagneticTestActivity.class.getName(),                                       // 磁力传感器测试
//            GyroscopeTestActivity.class.getName(),                                      // 陀螺仪测试
//            PressureTestActivity.class.getName(),                                       // 压力传感器测试
//            ASensorCalibrationActivity.class.getName(),                                 // 加速度传感器校验
//            GSensorCalibrationActivity.class.getName(),                                 // 陀螺仪传感器校验
//            MSensorCalibrationActivity.class.getName(),                                 // 磁力传感器校准
//            ProxSensorCalibrationActivity.class.getName(),                              // 距离传感器校验
//            NFCTestActivity.class.getName(),                                            // NFC测试
//            FrontSecondaryCameraTestActivity.class.getName(),                           // 前置辅相机测试
//            SecondaryCameraTestActivity.class.getName(),                                // 后置辅相机测试
//            FrontCameraTestActivity.class.getName(),                                    // 前置相机测试
//            SpwCameraTestActivity.class.getName(),                                      // 后置三摄相机测试
//            MacroLensCameraTestActivity.class.getName(),                                // 微距相机测试
////            FingerprintTestActivity.class.getName(),
//            SoundTriggerTestActivity.class.getName(),                                   // 语音唤醒
//            RedLightTest.class.getName(), GreenLightTest.class.getName(),               // 红灯
//            BlueLightTest.class.getName(),                                              // 蓝灯
//            SDCardTest.class.getName(),                                                 // SD卡测试
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
