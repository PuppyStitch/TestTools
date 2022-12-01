package com.sprd.validationtools.modules;

import com.sprd.validationtools.itemstest.audio.HeadSetTest;
import com.sprd.validationtools.itemstest.audio.RingtoneTestActivity;
import com.sprd.validationtools.itemstest.audio.VibratorTestActivity;
import com.sprd.validationtools.itemstest.backlight.BackLightTest;
import com.sprd.validationtools.itemstest.bt.BluetoothTestActivity;
import com.sprd.validationtools.itemstest.camera.CameraTestActivity;
import com.sprd.validationtools.itemstest.charger.ChargerTest;
import com.sprd.validationtools.itemstest.gps.GpsTestActivity;
import com.sprd.validationtools.itemstest.keypad.KeyTestActivity;
import com.sprd.validationtools.itemstest.lcd.ScreenColorTest;
import com.sprd.validationtools.itemstest.otg.OTGTest;
import com.sprd.validationtools.itemstest.sensor.PsensorTestActivity;
import com.sprd.validationtools.itemstest.sptest.POSSensorTestActivity;
import com.sprd.validationtools.itemstest.sptest.TimerTestActivity;
import com.sprd.validationtools.itemstest.sysinfo.SystemVersionTest;
import com.sprd.validationtools.itemstest.telephony.SIMCardTestActivity;
import com.sprd.validationtools.itemstest.tp.MutiTouchTest;
import com.sprd.validationtools.itemstest.tp.ScreenTestActivity;
import com.sprd.validationtools.itemstest.wholetest.BarcodeTestActivity;
import com.sprd.validationtools.itemstest.wholetest.FlashTestActivity;
import com.sprd.validationtools.itemstest.wholetest.PrintTestActivity;
import com.sprd.validationtools.itemstest.wholetest.QRCodeTestActivity;
import com.sprd.validationtools.itemstest.wholetest.WiFiBluetoothAddressTest;
import com.sprd.validationtools.itemstest.wifi.WifiTestActivity;

public class MMI1TestItems {

    public static final String[] FILTER_CLASS_NAMES = {
            SystemVersionTest.class.getName(),                                          // AP版本测试
            ScreenColorTest.class.getName(),                                            // LCD测试
            BackLightTest.class.getName(),                                              // 背景光测试
            ScreenTestActivity.class.getName(),                                         // todo:触摸屏
            KeyTestActivity.class.getName(),                                            // 按键测试 todo need to modify
            RingtoneTestActivity.class.getName(),                                       // 铃声测试
            VibratorTestActivity.class.getName(),                                       // 震动测试
            HeadSetTest.class.getName(),                                                // 耳机测试
            MutiTouchTest.class.getName(),
            FlashTestActivity.class.getName(),                                          // todo:闪光灯
            OTGTest.class.getName(),                                                    // OTG测试
            ChargerTest.class.getName(),                                                // 充电测试
            SIMCardTestActivity.class.getName(),                                        // SIM卡测试
            WiFiBluetoothAddressTest.class.getName(),                                   // 蓝牙wifi地址测试
            WifiTestActivity.class.getName(),                                           // wifi测试
            BluetoothTestActivity.class.getName(),                                      // 蓝牙测试
            GpsTestActivity.class.getName(),                                            // GPS测试
//            BuzzerTestActivity.class.getName(),                                         // 蜂鸣器测试
            PrintTestActivity.class.getName(),                                          // todo:打印机     ok
            CameraTestActivity.class.getName(),                                         // 后相机测试
            QRCodeTestActivity.class.getName(),                                         // todo:二维码     ok
            BarcodeTestActivity.class.getName(),                                        // todo:一维码     ok
            PsensorTestActivity.class.getName(),                                        // 距离和光传感器测试
            TimerTestActivity.class.getName(),                                          // RTC时钟测试
            POSSensorTestActivity.class.getName(),                                      // todo:POS Sensor测试
    };
}
