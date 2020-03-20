package org.haobtc.wallet.activities.onlywallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaquo.python.PyObject;
import com.yzq.zxinglibrary.common.Constant;

import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.ActivatedProcessing;
import org.haobtc.wallet.activities.WalletUnActivatedActivity;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.activities.manywallet.CustomerDialogFragment;
import org.haobtc.wallet.adapter.AddBixinKeyAdapter;
import org.haobtc.wallet.event.AddBixinKeyEvent;
import org.haobtc.wallet.fragment.ReadingPubKeyDialogFragment;
import org.haobtc.wallet.utils.Daemon;
import org.haobtc.wallet.utils.Global;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.wallet.activities.manywallet.CustomerDialogFragment.REQUEST_ACTIVE;
import static org.haobtc.wallet.activities.manywallet.CustomerDialogFragment.isNFC;
import static org.haobtc.wallet.activities.manywallet.CustomerDialogFragment.xpub;
import static org.haobtc.wallet.activities.manywallet.CustomerDialogFragment.futureTask;
import static org.haobtc.wallet.activities.manywallet.CustomerDialogFragment.executorService;


public class CreateOnlyChooseActivity extends BaseActivity {

    @BindView(R.id.img_backCreat)
    ImageView imgBackCreat;
    @BindView(R.id.tet_personalNum)
    TextView tetPersonalNum;
    @BindView(R.id.recl_BinxinKey)
    RecyclerView reclBinxinKey;
    @BindView(R.id.bn_add_key)
    LinearLayout bnAddKey;
    @BindView(R.id.bn_complete_add_cosigner)
    Button bnCompleteAddCosigner;
    private Dialog dialogBtom;
    private int sigNum;
    // new version code
    public String pin = "";
    private static boolean isActive;
    private boolean executable = true;
    private CustomerDialogFragment dialogFragment;
    public static final String TAG = CreateOnlyChooseActivity.class.getSimpleName();
    private String walletNames;
    private ArrayList<AddBixinKeyEvent> addEventsDatas;
    private int walletNameNum;
    private boolean isInit;
    private boolean ready;


    @Override
    public int getLayoutId() {
        return R.layout.activity_create_only_choose;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        sigNum = intent.getIntExtra("sigNum", 0);
        walletNames = intent.getStringExtra("walletNames");
        walletNameNum = intent.getIntExtra("walletNameNum", 0);
        init();

    }

    @SuppressLint("DefaultLocale")
    private void init() {
        tetPersonalNum.setText(String.format("%s(0/%d)", getResources().getString(R.string.creat_personal), sigNum));
    }

    @Override
    public void initData() {
        addEventsDatas = new ArrayList<>();
    }

    @OnClick({R.id.img_backCreat, R.id.bn_add_key, R.id.bn_complete_add_cosigner})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_backCreat:
                finish();
                break;
            case R.id.bn_add_key:
                // new version code
                showPopupAddCosigner1();
                break;
            case R.id.bn_complete_add_cosigner:
                Intent intent = new Intent(CreateOnlyChooseActivity.this, CreatFinishPersonalActivity.class);
                intent.putExtra("walletNames", walletNames);
                intent.putExtra("flagTag", "onlyChoose");
                intent.putExtra("strBixinlist", (Serializable) addEventsDatas);
                startActivity(intent);
                break;
        }
    }
    private Runnable runnable2 = () -> showConfirmPubDialog(this, R.layout.bixinkey_confirm, xpub);

    private void showPopupAddCosigner1() {
        List<Runnable> runnables = new ArrayList<>();
        runnables.add(null);
        runnables.add(runnable2);
        dialogFragment = new CustomerDialogFragment(TAG, runnables, "");
        dialogFragment.show(this.getSupportFragmentManager(), "");
    }

    private void showConfirmPubDialog(Context context, @LayoutRes int resource, String xpub) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);

        EditText edit_bixinName = view.findViewById(R.id.edit_keyName);
        TextView tet_Num = view.findViewById(R.id.txt_textNum);
        TextView textView = view.findViewById(R.id.text_public_key_cosigner_popup);
        textView.setText(xpub);
        edit_bixinName.addTextChangedListener(new TextWatcher() {
            CharSequence input;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                input = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tet_Num.setText(String.format(Locale.CHINA, "%d/20", input.length()));
                if (input.length() > 19) {
                    Toast.makeText(CreateOnlyChooseActivity.this, R.string.moreinput_text, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        view.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            String strBixinname = edit_bixinName.getText().toString();
            String strSweep = textView.getText().toString();
            if (TextUtils.isEmpty(strBixinname)) {
                mToast(getString(R.string.input_name));
                return;
            }
            if (TextUtils.isEmpty(strSweep)) {
                mToast(getString(R.string.input_public_address));
                return;
            }
            try {
                //add
                Daemon.commands.callAttr("add_xpub", strSweep);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            AddBixinKeyEvent addBixinKeyEvent = new AddBixinKeyEvent();
            addBixinKeyEvent.setKeyname(strBixinname);
            addBixinKeyEvent.setKeyaddress(strSweep);
            addEventsDatas.add(addBixinKeyEvent);
            //bixinKEY
            AddBixinKeyAdapter addBixinKeyAdapter = new AddBixinKeyAdapter(addEventsDatas);
            reclBinxinKey.setAdapter(addBixinKeyAdapter);
            tetPersonalNum.setText(String.format(getString(R.string.creat_personal) + "(%d/%d)", addEventsDatas.size(), sigNum));

            if (addEventsDatas.size() == sigNum) {
                bnCompleteAddCosigner.setEnabled(true);
                bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_blue));
                bnAddKey.setVisibility(View.GONE);
            }
            addBixinKeyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    switch (view.getId()) {
                        case R.id.img_deleteKey:
                            try {
                                Daemon.commands.callAttr("delete_xpub", strSweep);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            addEventsDatas.remove(position);
                            addBixinKeyAdapter.notifyDataSetChanged();
                            bnAddKey.setVisibility(View.VISIBLE);
                            tetPersonalNum.setText(String.format(getString(R.string.creat_personal) + "(%d/%d)", addEventsDatas.size(), sigNum));
                            bnCompleteAddCosigner.setBackground(getDrawable(R.drawable.little_radio_qian));
                            bnCompleteAddCosigner.setEnabled(false);

                            break;
                    }
                }
            });
            dialogBtoms.cancel();
            dialogFragment.dismiss();

        });

        //cancel dialog
        view.findViewById(R.id.img_cancle).setOnClickListener(v -> {
            dialogBtoms.cancel();
        });


        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.show();
    }

    private boolean isInitialized() throws Exception {
        boolean isInitialized = false;
        try {
            System.out.println("call is_initialized =====");
            isInitialized = executorService.submit(() -> Daemon.commands.callAttr("is_initialized")).get().toBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return isInitialized;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction(); // get the action of the coming intent
        if (Objects.equals(action, NfcAdapter.ACTION_NDEF_DISCOVERED) // NDEF type
                || Objects.equals(action, NfcAdapter.ACTION_TECH_DISCOVERED)
                || Objects.requireNonNull(action).equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            isNFC = true;
            if (executable) {
                Tag tags = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                PyObject nfc = Global.py.getModule("trezorlib.transport.nfc");
                PyObject nfcHandler = nfc.get("NFCHandle");
                nfcHandler.put("device", tags);
                executable = false;
            }
            if (ready) {
                CustomerDialogFragment.customerUI.put("pin", pin);
                getResult();
                ready = false;
            }
            try {
                isInit = isInitialized();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "communication error, get firmware info error", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isInit) {
                boolean pinCached = false;
                try {
                    pinCached = executorService.submit(() -> Daemon.commands.callAttr("get_pin_status")).get().toBoolean();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // todo: get xpub
                futureTask = new FutureTask<>(() -> Daemon.commands.callAttr("get_xpub_from_hw"));
                executorService.submit(futureTask);
                if (pinCached) {
                    try {
                        xpub = futureTask.get().toString();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                    showConfirmPubDialog(this, R.layout.bixinkey_confirm, xpub);
                }

            } else {
                // todo: Initialized
                if (isActive) {
                    executorService.execute(() -> {
                        try {
                            Daemon.commands.callAttr("init");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    Intent intent1 = new Intent(this, WalletUnActivatedActivity.class);
                    startActivityForResult(intent1, REQUEST_ACTIVE);
                }
            }
        }

    }
    private void getResult() {
        try {
            ReadingPubKeyDialogFragment dialog = dialogFragment.showReadingDialog();
            xpub = futureTask.get(40, TimeUnit.SECONDS).toString();
            dialog.dismiss();
            showConfirmPubDialog(this, R.layout.bixinkey_confirm, xpub);
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            if ("com.chaquo.python.PyException: BaseException: (7, 'PIN invalid')".equals(e.getMessage())) {
                mToast(getResources().getString(R.string.pin_wrong));
            } else {
                dialogFragment.showReadingFailedDialog();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CustomerDialogFragment.PIN_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                pin = data.getStringExtra("pin");
                int tag = data.getIntExtra("tag", 0);
                switch (tag) {
                    case CustomerDialogFragment.PIN_NEW_FIRST: // 激活
                        // ble 激活
                        if (CustomerDialogFragment.isActive) {
                            CustomerDialogFragment.customerUI.put("pin", pin);
                            CustomerDialogFragment.handler.sendEmptyMessage(CustomerDialogFragment.SHOW_PROCESSING);
                            CustomerDialogFragment.isActive = false;
                        } else if (isActive) {
                            // nfc 激活
                            CustomerDialogFragment.pin = pin;
                            CustomerDialogFragment.handler.sendEmptyMessage(CustomerDialogFragment.SHOW_PROCESSING);
                            isActive = false;
                        }
                        break;
                    case CustomerDialogFragment.PIN_CURRENT: // 创建
                        if (!isNFC) { // ble
                            CustomerDialogFragment.customerUI.put("pin", pin);
                            new Handler().postDelayed(this::getResult, (long) 0.2);                        } else { // nfc
                            ready = true;
                        }
                        break;
                    default:
                }
            }
        } else if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Log.i("CODED_CONTENT", "content=----: " + content);
//                edit_sweep.setText(content);
            }
        } else if (requestCode == REQUEST_ACTIVE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                isActive = data.getBooleanExtra("isActive", false);
            }
        }
    }

}