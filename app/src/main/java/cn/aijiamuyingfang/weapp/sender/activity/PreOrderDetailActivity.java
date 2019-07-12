package cn.aijiamuyingfang.weapp.sender.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.aijiamuyingfang.client.rest.api.GoodControllerApi;
import cn.aijiamuyingfang.vo.goods.Good;
import cn.aijiamuyingfang.vo.preorder.PreOrderGood;
import cn.aijiamuyingfang.vo.response.ResponseBean;
import cn.aijiamuyingfang.vo.response.ResponseCode;
import cn.aijiamuyingfang.vo.utils.StringUtils;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.GoodControllerClient;
import cn.aijiamuyingfang.weapp.manager.access.server.utils.RxJavaUtils;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.Constant;
import cn.aijiamuyingfang.weapp.manager.commons.activity.BaseActivity;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.ClearEditText;
import cn.aijiamuyingfang.weapp.manager.widgets.GlideUtils;
import cn.aijiamuyingfang.weapp.sender.R;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PreOrderDetailActivity extends BaseActivity {
    private static final String TAG = PreOrderDetailActivity.class.getName();
    private static final GoodControllerApi goodControllerApi = new GoodControllerClient();

    @BindView(R.id.good_cover_img)
    ImageView mGoodCoverImageView;
    @BindView(R.id.good_name)
    TextView mGoodNameTextView;
    @BindView(R.id.good_price)
    TextView mGoodPriceTextView;
    @BindView(R.id.good_unit)
    TextView mGoodUnitTextView;
    @BindView(R.id.good_level)
    TextView mGoodLevelTextView;
    @BindView(R.id.good_count)
    ClearEditText mGoodCountView;
    @BindView(R.id.btn_save)
    Button mSaveButton;
    private final List<Disposable> goodDisposableList = new ArrayList<>();

    @Override
    protected void init() {
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        PreOrderGood mPreOrderGood = intent.getParcelableExtra(Constant.INTENT_SHOPORDER);
        Good good = mPreOrderGood.getGood();
        GlideUtils.load(this, good.getCoverImg().getUrl(), mGoodCoverImageView);
        mGoodNameTextView.setText(good.getName());
        mGoodPriceTextView.setText(getString(R.string.Price, good.getPrice()));
        mGoodUnitTextView.setText(getString(R.string.Good_Pack, good.getPack()));
        mGoodLevelTextView.setText(getString(R.string.Good_Level, good.getLevel()));
        mGoodCountView.setText(getString(R.string.Good_Count, good.getCount()));

    }

    @OnClick(R.id.btn_save)
    public void onClick(View view) {
        mSaveButton.setClickable(false);
        String goodCountStr = mGoodCountView.getText() != null ? mGoodCountView.getText().toString() : "";
        if (StringUtils.isEmpty(goodCountStr)) {
            mSaveButton.setClickable(true);
            ToastUtils.showSafeToast(PreOrderDetailActivity.this, "请输入商品数量");
            return;
        }
        try {
            int goodCount = Integer.parseInt(goodCountStr);
            Good good = new Good();
            good.setCount(goodCount);
            goodControllerApi.updateGood(good.getId(), good, CommonApp.getApplication().getUserToken()).subscribe(new Observer<ResponseBean<Good>>() {
                @Override
                public void onSubscribe(Disposable d) {
                    goodDisposableList.add(d);
                }

                @Override
                public void onNext(ResponseBean<Good> responseBean) {
                    if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                        PreOrderDetailActivity.this.finish();
                    } else {
                        Log.e(TAG, responseBean.getMsg());
                        ToastUtils.showSafeToast(PreOrderDetailActivity.this, "因服务端的原因,更新商品数量失败");
                    }
                    mSaveButton.setClickable(true);
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "update good failed", e);
                    ToastUtils.showSafeToast(PreOrderDetailActivity.this, "因客户端的原因,更新商品数量失败");
                }

                @Override
                public void onComplete() {
                    Log.i(TAG, "update good complete");
                }
            });
        } catch (NumberFormatException e) {
            mSaveButton.setClickable(true);
            ToastUtils.showSafeToast(PreOrderDetailActivity.this, "请输入数字");
        }
    }

    @Override
    public int getContentResourceId() {
        return R.layout.activity_preorder_detail;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxJavaUtils.dispose(goodDisposableList);
    }
}
