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
import com.sprd.validationtools.itemstest.sptest.ICCardTestActivity;
import com.sprd.validationtools.itemstest.sptest.MCRTestActivity;
import com.sprd.validationtools.itemstest.sptest.MyNFCTestActivity;
import com.sprd.validationtools.itemstest.sptest.POSSensorTestActivity;
import com.sprd.validationtools.itemstest.sptest.PosIDTestActivity;
import com.sprd.validationtools.itemstest.sptest.SPVersionTestActivity;
import com.sprd.validationtools.itemstest.sptest.TimerTestActivity;
import com.sprd.validationtools.itemstest.sptest.VirtualLedTestActivity;
import com.sprd.validationtools.itemstest.sysinfo.SystemVersionTest;
import com.sprd.validationtools.itemstest.telephony.SIMCardTestActivity;
import com.sprd.validationtools.itemstest.tp.ScreenTestActivity;
import com.sprd.validationtools.itemstest.wholetest.BarcodeTestActivity;
import com.sprd.validationtools.itemstest.wholetest.BuzzerTestActivity;
import com.sprd.validationtools.itemstest.wholetest.FlashTestActivity;
import com.sprd.validationtools.itemstest.wholetest.PrintTestActivity;
import com.sprd.validationtools.itemstest.wholetest.QRCodeTestActivity;
import com.sprd.validationtools.itemstest.wholetest.WiFiBluetoothAddressTest;
import com.sprd.validationtools.itemstest.wifi.WifiTestActivity;

public class SMTTestItems {

    public static final String[] FILTER_CLASS_NAMES = {
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
            WiFiBluetoothAddressTest.class.getName(),                                   // ??????wifi????????????
            WifiTestActivity.class.getName(),                                           // wifi??????
            BluetoothTestActivity.class.getName(),                                      // ????????????
            GpsTestActivity.class.getName(),                                            // GPS??????
            MyNFCTestActivity.class.getName(),                                          // todo:NFC??????   ok
            ICCardTestActivity.class.getName(),                                         // todo:IC?????????   ok
            MCRTestActivity.class.getName(),                                            // todo:????????????    ok
            VirtualLedTestActivity.class.getName(),                                     // todo:LED???????????????    ok
//            BuzzerTestActivity.class.getName(),                                         // ???????????????
            PrintTestActivity.class.getName(),                                          // todo:?????????     ok
            CameraTestActivity.class.getName(),                                         // ???????????????
//            QRCodeTestActivity.class.getName(),                                         // todo:?????????     ok
//            BarcodeTestActivity.class.getName(),                                        // todo:?????????     ok
            PsensorTestActivity.class.getName(),                                        // ???????????????????????????
            TimerTestActivity.class.getName(),                                                    // RTC????????????
            PosIDTestActivity.class.getName(),                                          // todo:SP????????????
            POSSensorTestActivity.class.getName(),                                      // todo:POS Sensor??????
    };

}
