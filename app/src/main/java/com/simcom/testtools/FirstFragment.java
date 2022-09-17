package com.simcom.testtools;

//import static android.content.Context.VIRTUAL_LED_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.VirtualLedManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.simcom.testtools.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private static final String TAG = "FirstFragment";

    private FragmentFirstBinding binding;
    private Context mContext;
    private Toast mToast;

    VirtualLedManager virtualLedManager;

    ActivityManager activityManager;


    MainHandler mainHandler = new MainHandler(getActivity());

    private static class MainHandler extends WeakHandler<FragmentActivity> {

        public MainHandler(FragmentActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getOwner();
            if (activity == null)
                return;

//            updateUI();
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        mContext = getActivity();
        return binding.getRoot();

    }

    @SuppressLint("WrongConstant")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        virtualLedManager = (VirtualLedManager)
                getActivity().getSystemService("virtual_led_service");
        activityManager = (ActivityManager)
                getActivity().getSystemService("activity");

        binding.buttonShowBlue.setOnClickListener(v -> {
            try {
                showBlue();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        binding.buttonHideBlue.setOnClickListener(v -> {
            try {
                hideBlue();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });


        binding.buttonShowYellow.setOnClickListener(v -> {
            try {
                showYellow();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });


        binding.buttonHideYellow.setOnClickListener(v -> {
            try {
                hideYellow();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        binding.buttonShowGreen.setOnClickListener(v -> {
            try {
                showGreen();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });


        binding.buttonHideGreen.setOnClickListener(v -> {
            try {
                hideGreen();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        binding.buttonShowRed.setOnClickListener(v -> {
            try {
                showRed();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });


        binding.buttonHideRed.setOnClickListener(v -> {
            try {
                hideRed();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        binding.buttonIsAllShow.setOnClickListener(view1 -> {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//                binding.textviewFirst.setText(getCurrentChargingVoltage() + "");
//                stopAppByForce();
            try {
                isAllShow();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        binding.buttonIsAllHide.setOnClickListener(view1 -> {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//                binding.textviewFirst.setText(getCurrentChargingVoltage() + "");
//                stopAppByForce();
            try {
                isAllHide();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

//    /**
//     * 当前充电电压 uV
//     *
//     * adb shell "cat /sys/class/power_supply/battery/batt_vol"
//     */
//    private int getCurrentChargingVoltage() {
//        int result = 0;
//        BufferedReader br = null;
//        try {
//            String line;
//            br = new BufferedReader(new FileReader("/sys/class/power_supply/battery/batt_vol"));
//            if ((line = br.readLine()) != null) {
//                result = Integer.parseInt(line);
//            }
//
//            br.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return result;
//    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
//        getContext().registerReceiver(mBroadcastReceiver, filter);
//
//
//    }
//
//    private  BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (null == intent) {
//                return;
//            }
//
//            String action = intent.getAction();
//            // 当前电池电压
//            volt = intent.getIntExtra(EXTRA_VOLTAGE, -1);
//
//            Log.i(TAG, "volt " + volt);
//
//            // 当前电流
//            level = intent.getIntExtra(EXTRA_LEVEL, 0);
//            Log.i(TAG, "level " + level);
//
//            mainHandler.sendEmptyMessage(0);
//        }
//    };

    private void showBlue() throws RemoteException {
//        virtualLedManager.showBlue(true);
        activityManager.showBlue(true);
    }

    private void hideBlue() throws RemoteException {
        activityManager.showBlue(false);
    }

    private void showYellow() throws RemoteException {
        activityManager.showYellow(true);
    }

    private void hideYellow() throws RemoteException {
        activityManager.showYellow(false);
    }

    private void showGreen() throws RemoteException {
        activityManager.showGreen(true);
    }

    private void hideGreen() throws RemoteException {
        activityManager.showGreen(false);
    }

    private void showRed() throws RemoteException {
        activityManager.showRed(true);
    }

    private void hideRed() throws RemoteException {
        activityManager.showRed(false);
    }

    private void isAllShow() throws RemoteException {
        boolean isAllShow = activityManager.isAllLedShow();
        makeToast(isAllShow ? "all led is showing" : "not all led is showing");
        showToast();
    }

    private void isAllHide() throws RemoteException {
        boolean isAllHide = activityManager.isAllLedHide();
        makeToast(isAllHide ? "all led is hiding" : "not all led is hiding");
        showToast();
    }

    private void makeToast(String str) {
        mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
    }

    private void showToast() {
        getActivity().runOnUiThread(() -> mToast.show());
    }

}