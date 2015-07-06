package com.hltc.mtmap.activity.map;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hltc.mtmap.R;
import com.hltc.mtmap.gmodel.ClusterGrain;
import com.hltc.mtmap.util.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by redoblue on 15-7-3.
 */
public class GrainInfoDialog extends Activity {

    @InjectView(R.id.iv_grain_info_blur)
    ImageView ivGrainInfoBlur;
    @InjectView(R.id.civ_grain_info_portrait)
    CircleImageView civGrainInfoPortrait;
    @InjectView(R.id.tv_grain_info_nickname)
    TextView tvGrainInfoNickname;
    @InjectView(R.id.iv_grain_info_exit)
    ImageView ivGrainInfoExit;
    @InjectView(R.id.iv_grain_info_ignore)
    ImageView ivGrainInfoIgnore;
    @InjectView(R.id.tv_grain_info_address)
    TextView tvGrainInfoAddress;
    @InjectView(R.id.tv_grain_info_text)
    TextView tvGrainInfoText;
    @InjectView(R.id.tv_grain_info_detail)
    TextView tvGrainInfoDetail;

    private ClusterGrain mGrainItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_grain_info);
        ButterKnife.inject(this);

        initData();
        initView();
    }

    private void initData() {
        mGrainItem = getIntent().getParcelableExtra("grain");
    }

    private void initView() {
        civGrainInfoPortrait.setImageDrawable(Drawable.createFromPath(mGrainItem.userPortrait));
        tvGrainInfoNickname.setText(StringUtils.isEmpty(
                mGrainItem.remark) ? mGrainItem.nickName : mGrainItem.remark);
        tvGrainInfoAddress.setText(mGrainItem.site.name);
        tvGrainInfoText.setText(mGrainItem.text);
    }

    @OnClick({
            R.id.iv_grain_info_exit,
            R.id.iv_grain_info_ignore,
            R.id.tv_grain_info_detail
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_grain_info_exit:
                finish();
                break;
            case R.id.iv_grain_info_ignore:
                //TODO ignore this grain
                break;
            case R.id.tv_grain_info_detail:
                //TODO check detailed information
                break;
        }
    }

}
