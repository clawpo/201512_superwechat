package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by clawpo on 16/4/16.
 */
public class FuLiCenterMainActivity extends BaseActivity {

    // 菜单项按钮
    TextView mtvCartHint;
    RadioButton mLayoutCart;
    RadioButton mLayoutNewGood;
    RadioButton mLayoutBoutique;
    RadioButton mLayoutCategory;
    RadioButton mLayoutPersonalCenter;

    int index;
    int currentIndex = -1;
    RadioButton[] mRadios = new RadioButton[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRadioDefaultChecked(currentIndex);
    }

    private void setRadioDefaultChecked(int index) {
        if(index == -1){
            index = 0;
        }
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
                index = 4;
                break;
        }
        if(currentIndex!=index){
            currentIndex = index;
            setRadioDefaultChecked(index);
        }
    }
}
