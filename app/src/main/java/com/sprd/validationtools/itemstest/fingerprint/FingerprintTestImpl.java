//package com.sprd.validationtools.itemstest.fingerprint;
//
//import com.sprd.validationtools.itemstest.fingerprint.IFactoryTestImpl;
//import com.sprd.validationtools.itemstest.fingerprint.ISprdFingerDetectListener;
//
//import android.util.Slog;
//
//import vendor.sprd.hardware.fingerprintmmi.V1_0.IFingerprintmmi;
//import android.os.RemoteException;
//
//public class FingerprintTestImpl implements IFactoryTestImpl {
//
//        private IFingerprintmmi mDaemon;
//        private static final String TAG = "FingerprintTestImpl";
//        private enum FingerMmiStatus { IDLE, RUNNING, EXIT };
//        FingerMmiStatus status = FingerMmiStatus.IDLE;
//
//    @Override
//    public int factory_init() {
//            int ret = -1;
//            status = FingerMmiStatus.RUNNING;
//            synchronized(FingerprintTestImpl.class) {
//                if (mDaemon == null) {
//                    Slog.v(TAG, "factory_init mDeamon was null, connect to IFingerprintmmi");
//                    try {
//                        mDaemon = IFingerprintmmi.getService(true);
//                    } catch (java.util.NoSuchElementException e) {
//                        Slog.e(TAG, "factory_init Failed to get IFingerprintmmi interface NoSuchElementException", e);
//                    // Service doesn't exist or cannot be opened. Logged below.
//                    } catch (RemoteException e) {
//                        Slog.e(TAG, "factory_init Failed to get IFingerprintmmi interface RemoteException", e);
//                        e.printStackTrace();
//                    }
//                }
//
//                if (mDaemon == null) {
//                    Slog.w(TAG, "factory_init IFingerprintmmi HIDL not available");
//                    return ret;
//                }
//
//                try {
//                    ret = mDaemon.factory_init();
//                } catch (RemoteException e) {
//                    Slog.e(TAG, "Failed to do factory_init()", e);
//                    ret = -1;
//                    mDaemon = null; // try again later!
//                    e.printStackTrace();
//                }
//            }
//
//            return ret;
//    }
//
//    @Override
//    public int factory_exit() {
//            // TODO Auto-generated method stub
//            int ret = -1;
//            if(status != FingerMmiStatus.RUNNING){
//                status = FingerMmiStatus.IDLE;
//                Slog.v(TAG, "factory_exit no finger test,return");
//                return -1;
//            }
//            status = FingerMmiStatus.EXIT;
//            synchronized(FingerprintTestImpl.class) {
//                if (mDaemon == null) {
//                    Slog.v(TAG, "factory_exit mDeamon was null, connect to IFingerprintmmi");
//                    try {
//                        mDaemon = IFingerprintmmi.getService();
//                    } catch (java.util.NoSuchElementException e) {
//                    // Service doesn't exist or cannot be opened. Logged below.
//                    } catch (RemoteException e) {
//                        Slog.e(TAG, "factory_exit Failed to get IFingerprintmmi interface", e);
//                        e.printStackTrace();
//                    }
//                }
//
//                if (mDaemon == null) {
//                    Slog.w(TAG, "factory_exit IFingerprintmmi HIDL not available");
//                    return ret;
//                }
//
//                try {
//                    ret = mDaemon.factory_exit();
//                } catch (RemoteException e) {
//                    Slog.e(TAG, "Failed to do factory_exit()", e);
//                    ret = -1;
//                    mDaemon = null; // try again later!
//                    e.printStackTrace();
//                }
//            }
//            status = FingerMmiStatus.IDLE;
//            return ret;
//    }
//
//    @Override
//    public int spi_test() {
//            // TODO Auto-generated method stub
//            int ret = -1;
//            synchronized(FingerprintTestImpl.class) {
//                if (mDaemon == null) {
//                    Slog.v(TAG, "spi_test mDeamon was null, connect to IFingerprintmmi");
//                    try {
//                        mDaemon = IFingerprintmmi.getService();
//                    } catch (java.util.NoSuchElementException e) {
//                    // Service doesn't exist or cannot be opened. Logged below.
//                    } catch (RemoteException e) {
//                        Slog.e(TAG, "spi_test Failed to get IFingerprintmmi interface", e);
//                        e.printStackTrace();
//                    }
//                }
//
//                if (mDaemon == null) {
//                    Slog.w(TAG, "spi_test IFingerprintmmi HIDL not available");
//                    return ret;
//                }
//
//                try {
//                    ret = mDaemon.spi_test();
//                } catch (RemoteException e) {
//                    Slog.e(TAG, "Failed to do spi_test()", e);
//                    ret = -1;
//                    mDaemon = null; // try again later!
//                    e.printStackTrace();
//                }
//            }
//
//            return ret;
//    }
//
//    @Override
//    public int interrupt_test() {
//            // TODO Auto-generated method stub
//            int ret = -1;
//            synchronized(FingerprintTestImpl.class) {
//                if (mDaemon == null) {
//                    Slog.v(TAG, "interrupt_test mDeamon was null, connect to IFingerprintmmi");
//                    try {
//                        mDaemon = IFingerprintmmi.getService();
//                    } catch (java.util.NoSuchElementException e) {
//                    // Service doesn't exist or cannot be opened. Logged below.
//                    } catch (RemoteException e) {
//                        Slog.e(TAG, "interrupt_test Failed to get IFingerprintmmi interface", e);
//                        e.printStackTrace();
//                    }
//                }
//
//                if (mDaemon == null) {
//                    Slog.w(TAG, "interrupt_test IFingerprintmmi HIDL not available");
//                    return ret;
//                }
//
//                try {
//                    ret = mDaemon.interrupt_test();
//                } catch (RemoteException e) {
//                    Slog.e(TAG, "Failed to do interrupt_test()", e);
//                    ret = -1;
//                    mDaemon = null; // try again later!
//                    e.printStackTrace();
//                }
//            }
//
//            return ret;
//    }
//
//    @Override
//    public int deadpixel_test() {
//            // TODO Auto-generated method stub
//            int ret = -1;
//            synchronized(FingerprintTestImpl.class) {
//                if (mDaemon == null) {
//                    Slog.v(TAG, "deadpixel_test mDeamon was null, connect to IFingerprintmmi");
//                    try {
//                        mDaemon = IFingerprintmmi.getService();
//                    } catch (java.util.NoSuchElementException e) {
//                    // Service doesn't exist or cannot be opened. Logged below.
//                    } catch (RemoteException e) {
//                        Slog.e(TAG, "deadpixel_test Failed to get IFingerprintmmi interface", e);
//                        e.printStackTrace();
//                    }
//                }
//
//                if (mDaemon == null) {
//                    Slog.w(TAG, "deadpixel_test IFingerprintmmi HIDL not available");
//                    return ret;
//                }
//
//                try {
//                    ret = mDaemon.deadpixel_test();
//                } catch (RemoteException e) {
//                    Slog.e(TAG, "Failed to do deadpixel_test()", e);
//                    ret = -1;
//                    mDaemon = null; // try again later!
//                    e.printStackTrace();
//                }
//            }
//
//            return ret;
//    }
//
//    @Override
//    public int finger_detect(ISprdFingerDetectListener listener) {
//            // TODO Auto-generated method stub
//            int ret = -1;
//            synchronized(FingerprintTestImpl.class) {
//                int times = 100;
//                if (mDaemon == null) {
//                    Slog.v(TAG, "finger_detect mDeamon was null, connect to IFingerprintmmi");
//                    try {
//                        mDaemon = IFingerprintmmi.getService();
//                    } catch (java.util.NoSuchElementException e) {
//                    // Service doesn't exist or cannot be opened. Logged below.
//                    } catch (RemoteException e) {
//                        Slog.e(TAG, "finger_detect Failed to get IFingerprintmmi interface", e);
//                        e.printStackTrace();
//                    }
//                }
//
//                if (mDaemon == null) {
//                    Slog.w(TAG, "finger_detect IFingerprintmmi HIDL not available");
//                    return ret;
//                }
//
//                try {
//                    if(listener != null){
//                        while (times > 0 && (status != FingerMmiStatus.EXIT)) {
//                            ret = mDaemon.finger_detect();
//                            if (ret == 0) {
//                                break;
//                            }
//                            times--;
//                            Slog.w(TAG, "not detect fingerprint, try again... ret = " + ret);
//                            try {
//                                Thread.sleep(100); //delay 100ms to try again
//                            } catch (InterruptedException e) {
//
//                            }
//                        }
//
//                        if (ret != 0) {
//                            Slog.w(TAG, "finger_detect several times but failed");
//                        }
//                        listener.on_finger_detected(ret);
//                    }
//                } catch (RemoteException e) {
//                    Slog.e(TAG, "Failed to do finger_detect()", e);
//                    ret = -1;
//                    mDaemon = null; // try again later!
//                    e.printStackTrace();
//                }
//            }
//
//            return ret;
//    }
//
//}
