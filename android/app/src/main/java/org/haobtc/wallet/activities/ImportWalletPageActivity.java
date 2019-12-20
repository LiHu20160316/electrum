package org.haobtc.wallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.gyf.immersionbar.ImmersionBar;

import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.utils.CommonUtils;

public class ImportWalletPageActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "org.haobtc.wallet.activities.ImportWalletPageActivity";

    @Override
    public int getLayoutId() {
        return R.layout.import_wallet;
    }

    public void initView() {
        CardView cardViewImport, cardViewPublicKey;
        CommonUtils.enableToolBar(this, R.string.import_mutiSig);
        cardViewImport = findViewById(R.id.use_hardware);
        cardViewPublicKey = findViewById(R.id.public_key);
        cardViewImport.setOnClickListener(this);
        cardViewPublicKey.setOnClickListener(this);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.use_hardware:
                Intent intent = new Intent(this, TouchHardwareActivity.class);
                intent.putExtra(TouchHardwareActivity.FROM, TAG);
                startActivity(intent);
                break;
            case R.id.public_key:
                Intent intent1 = new Intent(this, PublicKeyInputEditActivity.class);
                startActivity(intent1);
        }

    }
}
