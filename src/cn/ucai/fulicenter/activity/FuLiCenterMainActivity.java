package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.UserBean;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.CategoryFragment;
import cn.ucai.fulicenter.fragment.NewGoodFragment;
import cn.ucai.fulicenter.fragment.PersonalCenterFragment;

/**
 * Created by clawpo on 16/4/16.
 */
public class FuLiCenterMainActivity extends BaseActivity {
    public static final String TAG = FuLiCenterMainActivity.class.getName();

    // 菜单项按钮
    TextView mtvCartHint;
    RadioButton mLayoutCart;
    RadioButton mLayoutNewGood;
    RadioButton mLayoutBoutique;
    RadioButton mLayoutCategory;
    RadioButton mLayoutPersonalCenter;

    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    PersonalCenterFragment mPersonalCenterFragment;
    Fragment[] mFragments = new Fragment[5];

    int index;
    int currentIndex;
    RadioButton[] mRadios = new RadioButton[5];

    UserBean currentUser;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
        initFragment();
        // 添加显示第一个fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mNewGoodFragment)
                .add(R.id.fragment_container, mBoutiqueFragment)
                .add(R.id.fragment_container,mCategoryFragment)
//                .add(R.id.fragment_container,mPersonalCenterFragment)
                .hide(mBoutiqueFragment).hide(mCategoryFragment)
//                .hide(mPersonalCenterFragment)
                .show(mNewGoodFragment)
                .commit();
        currentUser = FuLiCenterApplication.getInstance().getUser();
    }

    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        mPersonalCenterFragment = new PersonalCenterFragment();
        mFragments[0] = mNewGoodFragment;
        mFragments[1] = mBoutiqueFragment;
        mFragments[2] = mCategoryFragment;
        mFragments[4] = mPersonalCenterFragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRadioDefaultChecked(currentIndex);
        setFragment();
    }

    private void setRadioDefaultChecked(int index) {
        for(int i = 0; i< mRadios.length; i++){
            if(i==index){
                mRadios[i].setChecked(true);
            }else{
                mRadios[i].setChecked(false);
            }
        }
    }

    private void initView() {
        mtvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mLayoutNewGood = (RadioButton) findViewById(R.id.layout_new_good);
        mLayoutBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        mLayoutCategory = (RadioButton) findViewById(R.id.layout_category);
        mLayoutCart = (RadioButton) findViewById(R.id.layout_cart);
        mLayoutPersonalCenter = (RadioButton) findViewById(R.id.layout_personal_center);
        mRadios[0] = mLayoutNewGood;
        mRadios[1] = mLayoutBoutique;
        mRadios[2] = mLayoutCategory;
        mRadios[3] = mLayoutCart;
        mRadios[4] = mLayoutPersonalCenter;
    }

    public void onCheckedChange(View view){
        switch (view.getId()){
            case R.id.layout_new_good:
                index = 0;
                break;
            case R.id.layout_boutique:
                index = 1;
                break;
            case R.id.layout_category:
                index = 2;
                break;
            case R.id.layout_cart:
                index = 3;
                break;
            case R.id.layout_personal_center:
                currentUser = FuLiCenterApplication.getInstance().getUser();
                Log.e(TAG,"onCheckedChange,user="+currentUser);
                if(currentUser!=null) {
                    index = 4;
                }else{
                    gotoLogin("person");
                }
                break;
        }

        if (currentIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(R.id.fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
        }
        setRadioDefaultChecked(index);
        currentIndex = index;
    }

    private void gotoLogin(String action) {
        Intent intent = new Intent(FuLiCenterMainActivity.this,LoginActivity.class).putExtra("action",action);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        action = getIntent().getStringExtra("action");
    }

    @Override
    protected void onStart() {
        super.onStart();
        action = getIntent().getStringExtra("action");
    }

    private void setFragment(){
        currentUser = FuLiCenterApplication.getInstance().getUser();
        action = getIntent().getStringExtra("action");
        Log.e(TAG,"setFragment,action="+action+",currentIndex="+currentIndex+",user="+currentUser);
        if(action!=null&&action.equals("person")&& currentUser!=null){
            index = 4;
            getIntent().removeExtra("action");
        }
        if(currentUser==null && currentIndex==4){
            index=0;
        }
        if (currentIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(R.id.fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();
        }
        setRadioDefaultChecked(index);
        currentIndex = index;
    }
}
