package org.haobtc.onekey.mvp.presenter;

import android.Manifest;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.mvp.base.BasePresenter;
import org.haobtc.onekey.mvp.view.ISearchDevicesView;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class SearchDevicesPresenter extends BasePresenter<ISearchDevicesView> {

    private String mWay;
    //ble info
    private Ble<BleDevice> mBle;
    private BleScanCallback<BleDevice> mBleScanCallBack;
    private final long SCAN_TIME = 5 * 1000L;
    //nfc info

    //usb info

    public SearchDevicesPresenter(ISearchDevicesView view) {
        super(view);
        mWay = MyApplication.getInstance().getDeviceWay();
    }

    public void init() {
        switch (mWay) {
            case Constant.WAY_MODE_NFC:
                if (getView() != null) {
                    getView().addNfcView();
                }
                initNfc();
                break;
            case Constant.WAY_MODE_USB:
                if (getView() != null) {
                    getView().addUsbView();
                }
                initUsb();
                break;
            default:
                if (getView() != null) {
                    getView().addBleView();
                }
                initBle();
                break;
        }
    }

    /**
     * init ble
     */
    public void initBle() {
        mBle = Ble.getInstance();
        mBleScanCallBack = new BleScanCallback<BleDevice>() {
            @Override
            public void onLeScan(BleDevice device, int rssi, byte[] scanRecord) {
                synchronized (mBle.getLocker()) {
                    if (getView() != null) {
                        getView().onBleScanDevice(device);
                    }
                }
            }

            @Override
            public void onStop() {
                super.onStop();
                if (getView() != null) {
                    getView().onBleScanStop();
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                if (getView() != null) {
                    getView().onBleScanStop();
                }
            }
        };
        final RxPermissions permissions = new RxPermissions(getActivity());
        permissions.request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(
                        granted -> {
                            if (granted) {
                                if (mBle.isBleEnable()) {
                                    refreshBleDeviceList();
                                    return;
                                }
                                openBle();
                            } else {
                                if (getView() != null) {
                                    getView().showToast(R.string.no_permission);
                                }
                            }
                        }
                ).dispose();
    }

    /**
     * jump to open bluetooth
     */
    public void openBle() {
        if (mBle != null) {
            mBle.turnOnBlueTooth(getActivity());
        }
    }

    /**
     * search ble devices
     */
    public void refreshBleDeviceList() {
        if (mBle == null) {
            return;
        }
        if (mBle.isBleEnable()) {
            mBle.startScan(mBleScanCallBack, SCAN_TIME);
        } else {
            mBle.turnOnBlueTooth(getActivity());
        }

    }


    public void initNfc() {

    }


    public void initUsb() {

    }
}
