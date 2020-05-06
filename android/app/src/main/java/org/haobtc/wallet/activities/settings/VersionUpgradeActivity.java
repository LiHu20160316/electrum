package org.haobtc.wallet.activities.settings;

import android.Manifest;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.activities.service.CommunicationModeSelector;
import org.haobtc.wallet.aop.SingleClick;
import org.haobtc.wallet.entries.FsActivity;
import org.haobtc.wallet.event.ExceptionEvent;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dr.android.fileselector.FileSelectConstant;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class VersionUpgradeActivity extends BaseActivity {

    @BindView(R.id.btn_toUpgrade)
    Button btnToUpgrade;
    @BindView(R.id.tet_firmware)
    TextView tetFirmware;
    @BindView(R.id.checkBox_firmware)
    CheckBox checkBoxFirmware;
    @BindView(R.id.tet_bluetooth)
    TextView tetBluetooth;
    @BindView(R.id.checkBox_bluetooth)
    CheckBox checkBoxBluetooth;
    public final static String TAG = VersionUpgradeActivity.class.getSimpleName();
    @BindView(R.id.btn_import_file)
    Button btnImportFile;
    private int checkWitch = 1;
    public static final String UPDATE_PROCESS = "org.haobtc.wallet.activities.settings.percent";
    private RxPermissions rxPermissions;
    public static String filePath;

    @Override
    public int getLayoutId() {
        return R.layout.activity_version_upgrade;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        rxPermissions = new RxPermissions(this);
        Intent intent = getIntent();
        String firmwareVersion = intent.getStringExtra("firmwareVersion");
        String bleVerson = intent.getStringExtra("bleVerson");
        tetFirmware.setText(firmwareVersion);
        tetBluetooth.setText(bleVerson);

    }

    @Override
    public void initData() {
        checkBoxClick();
    }

    private void checkBoxClick() {
        checkBoxFirmware.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxBluetooth.setChecked(false);
                    checkWitch = 1;
                } else {
                    checkWitch = 0;
                }
            }
        });
        checkBoxBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxFirmware.setChecked(false);
                    checkWitch = 2;
                } else {
                    checkWitch = 0;
                }
            }
        });
    }
    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_toUpgrade, R.id.btn_import_file})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_toUpgrade:
                switch (checkWitch) {
                    case 0:
                        mToast(getString(R.string.please_choose_firmware));
                        break;
                    case 1:
                        Intent intent = new Intent(this, CommunicationModeSelector.class);
                        intent.putExtra("tag", TAG);
                        intent.putExtra("extras", "hardware");
                        startActivity(intent);
                        break;
                    case 2:
                        Intent intent1 = new Intent(this, CommunicationModeSelector.class);
                        intent1.putExtra("tag", TAG);
                        intent1.putExtra("extras", "ble");
                        startActivity(intent1);
                        break;
                }
                break;
            case R.id.btn_import_file:
                rxPermissions
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                Intent intent1 = new Intent();
                                intent1.setClass(getApplicationContext(), FsActivity.class);
                                intent1.putExtra(FileSelectConstant.SELECTOR_REQUEST_CODE_KEY, FileSelectConstant.SELECTOR_MODE_FILE);
                                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                                intent1.putExtra("keyFile", "1");
                                startActivityForResult(intent1, 1);

                            } else { // Oups permission denied
                                Toast.makeText(this, R.string.reservatpion_photo, Toast.LENGTH_SHORT).show();
                            }
                        }).dispose();
                break;
        }
    }



    private final DfuProgressListener dfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDfuCompleted(@NonNull String deviceAddress) {
            super.onDfuCompleted(deviceAddress);
            CommunicationModeSelector.isDfu = false;
            mIntent(UpgradeFinishedActivity.class);
        }

        @Override
        public void onDfuProcessStarted(@NonNull String deviceAddress) {
            super.onDfuProcessStarted(deviceAddress);
        }

        Intent intent;

        @Override
        public void onDfuProcessStarting(@NonNull String deviceAddress) {
           VersionUpgradeActivity.filePath = "";
            super.onDfuProcessStarting(deviceAddress);
            if (intent == null) {
                intent = new Intent(VersionUpgradeActivity.this, UpgradeBixinKEYActivity.class);
                intent.putExtra("tag", 2);
                startActivity(intent);
            }
        }

        @Override
        public void onDfuAborted(@NonNull String deviceAddress) {
            super.onDfuAborted(deviceAddress);
        }

        @Override
        public void onProgressChanged(@NonNull String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            super.onProgressChanged(deviceAddress, percent, speed, avgSpeed, currentPart, partsTotal);
            Intent intent = new Intent();
            intent.setAction(UPDATE_PROCESS);
            intent.putExtra("process", percent);
            LocalBroadcastManager.getInstance(VersionUpgradeActivity.this).sendBroadcast(intent);
        }

        @Override
        public void onError(@NonNull String deviceAddress, int error, int errorType, String message) {
            super.onError(deviceAddress, error, errorType, message);
            EventBus.getDefault().post(new ExceptionEvent(message));
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DfuServiceListenerHelper.unregisterProgressListener(this, dfuProgressListener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //import file
            assert data != null;
            ArrayList<String> listExtra = data.getStringArrayListExtra(FileSelectConstant.SELECTOR_BUNDLE_PATHS);
            assert listExtra != null;
            String str = listExtra.toString();
            String substring = str.substring(1);
            filePath = substring.substring(0, substring.length() - 1);
            Log.i("listExtra", "listExtra--: " + listExtra + "   strPath ---  " + filePath);
            mToast(filePath);
        }
    }
}