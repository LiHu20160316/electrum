package org.haobtc.wallet.activities.onlywallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.wallet.MainActivity;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.adapter.HelpWordAdapter;
import org.haobtc.wallet.event.FirstEvent;
import org.haobtc.wallet.utils.Daemon;
import org.haobtc.wallet.utils.MyDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerificationMnemonicWordActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.recl_helpWord)
    RecyclerView reclHelpWord;
    @BindView(R.id.edit_one)
    TextView editOne;
    @BindView(R.id.edit_two)
    TextView editTwo;
    @BindView(R.id.edit_three)
    TextView editThree;
    @BindView(R.id.edit_four)
    TextView editFour;
    @BindView(R.id.edit_five)
    TextView editFive;
    @BindView(R.id.edit_six)
    TextView editSix;
    @BindView(R.id.edit_seven)
    TextView editSeven;
    @BindView(R.id.edit_eight)
    TextView editEight;
    @BindView(R.id.edit_nine)
    TextView editNine;
    @BindView(R.id.edit_ten)
    TextView editTen;
    @BindView(R.id.edit_eleven)
    TextView editEleven;
    @BindView(R.id.edit_twelve)
    TextView editTwelve;
    private String strRemeber = "";
    private String strPass1;
    private final String FIRST_RUN = "is_first_run";
    private SharedPreferences.Editor edit;
    private MyDialog myDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_verification_mnemonic_word;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        edit = preferences.edit();
        myDialog = MyDialog.showDialog(VerificationMnemonicWordActivity.this);
        Intent intent = getIntent();
        String strSeeds = intent.getStringExtra("strSeeds");
        strPass1 = intent.getStringExtra("strPass1");
        String[] wordsList = strSeeds.split(" ");

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < wordsList.length; i++) {
            strings.add(wordsList[i]);
        }

        reclHelpWord.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        HelpWordAdapter helpWordAdapter = new HelpWordAdapter(strings);
        reclHelpWord.setAdapter(helpWordAdapter);
        helpWordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                mIntent(AppWalletCreateFinishActivity.class);
                String strWord = strings.get(position);
                helpWord(strWord);
            }
        });

    }

    private void helpWord(String strWord) {
        if (strWord.equals(strRemeber)){
            mToast(getResources().getString(R.string.have_word));
        }else{
            strRemeber = strWord;
            if (TextUtils.isEmpty(editOne.getText().toString())){
                editOne.setText(strWord);
            }else if (TextUtils.isEmpty(editTwo.getText().toString())){
                editTwo.setText(strWord);
            }else if (TextUtils.isEmpty(editThree.getText().toString())){
                editThree.setText(strWord);
            }else if (TextUtils.isEmpty(editFour.getText().toString())){
                editFour.setText(strWord);
            }else if (TextUtils.isEmpty(editFive.getText().toString())){
                editFive.setText(strWord);
            }else if (TextUtils.isEmpty(editSix.getText().toString())){
                editSix.setText(strWord);
            }else if (TextUtils.isEmpty(editSeven.getText().toString())){
                editSeven.setText(strWord);
            }else if (TextUtils.isEmpty(editEight.getText().toString())){
                editEight.setText(strWord);
            }else if (TextUtils.isEmpty(editNine.getText().toString())){
                editNine.setText(strWord);
            }else if (TextUtils.isEmpty(editTen.getText().toString())){
                editTen.setText(strWord);
            }else if (TextUtils.isEmpty(editEleven.getText().toString())){
                editEleven.setText(strWord);
            }else if (TextUtils.isEmpty(editTwelve.getText().toString())){
                editTwelve.setText(strWord);
                //if ok
                helpWordOk();

            }
        }

    }

    private void helpWordOk() {
        myDialog.show();
        String strone = editOne.getText().toString();
        String strtwo = editTwo.getText().toString();
        String strthree = editThree.getText().toString();
        String strfour = editFour.getText().toString();
        String strfive = editFive.getText().toString();
        String strsix = editSix.getText().toString();
        String strseven = editSeven.getText().toString();
        String streight = editEight.getText().toString();
        String strnine = editNine.getText().toString();
        String strten = editTen.getText().toString();
        String streleven = editEleven.getText().toString();
        String strtwelve = editTwelve.getText().toString();

        String strNewseed  = strone+" "+strtwo+" "+strthree+" "+strfour+" "+strfive+" "+strsix+" "+strseven+" "+streight+" "+strnine+" "+strten+" "+streleven+" "+strtwelve;


        try {
            Daemon.commands.callAttr("check_seed",strNewseed,strPass1);
            //FIRST_RUN,if frist run
            edit.putBoolean(FIRST_RUN, true);
            edit.apply();
            EventBus.getDefault().post(new FirstEvent("11"));
            myDialog.dismiss();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

}
