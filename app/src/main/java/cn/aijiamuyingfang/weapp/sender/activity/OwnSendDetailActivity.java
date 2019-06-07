package cn.aijiamuyingfang.weapp.sender.activity;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.aijiamuyingfang.client.domain.ResponseBean;
import cn.aijiamuyingfang.client.domain.ResponseCode;
import cn.aijiamuyingfang.client.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.client.domain.shoporder.ShopOrderStatus;
import cn.aijiamuyingfang.client.domain.shoporder.request.UpdateShopOrderStatusRequest;
import cn.aijiamuyingfang.client.domain.user.RecieveAddress;
import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.client.rest.api.UserControllerApi;
import cn.aijiamuyingfang.client.rest.utils.StringUtils;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.UserControllerClient;
import cn.aijiamuyingfang.weapp.manager.access.server.utils.RxJavaUtils;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.Constant;
import cn.aijiamuyingfang.weapp.manager.commons.activity.BaseActivity;
import cn.aijiamuyingfang.weapp.manager.commons.utils.DateUtils;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.ClearEditText;
import cn.aijiamuyingfang.weapp.manager.widgets.WeToolBar;
import cn.aijiamuyingfang.weapp.sender.R;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.DetailActivityGoodAdapter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class OwnSendDetailActivity extends BaseActivity {
    private static final String TAG = OwnSendDetailActivity.class.getName();
    private static final ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();
    private static final UserControllerApi userControllerApi = new UserControllerClient();

    @BindView(R.id.toolbar)
    WeToolBar toolBar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    DetailActivityGoodAdapter mGoodAdapter;
    @BindView(R.id.send_price)
    TextView mSendPriceTextView;
    @BindView(R.id.total_price)
    TextView mTotalPriceTextView;
    @BindView(R.id.send_type)
    TextView mSendTypeTextView;
    @BindView(R.id.address_detailAddress)
    TextView mDetailAddressTextView;
    @BindView(R.id.shoporder_no)
    TextView mOrderNoTextView;
    @BindView(R.id.shoporder_create_time)
    TextView mCreateTimeTextView;
    @BindView(R.id.sender_name)
    ClearEditText mSenderNameEditText;
    @BindView(R.id.sender_phone)
    ClearEditText mSenderPhoneEditText;

    @BindView((R.id.operator_ll))
    LinearLayout mOperatorLinearLayout;
    @BindView(R.id.operator)
    ClearEditText mOperatorEditText;

    @BindView(R.id.btn_save)
    Button mSaveButton;

    private final List<Disposable> disposableList = new ArrayList<>();
    private ShopOrder mShopOrder;

    @Override
    protected void init() {
        initData();
        initAdapter();
    }

    private void initData() {
        Intent intent = getIntent();
        mShopOrder = intent.getParcelableExtra(Constant.INTENT_SHOPORDER);
        if (mShopOrder.getStatus().equals(ShopOrderStatus.UNSTART)) {
            toolBar.setTitle("订单详情(未开始)");
            mSaveButton.setVisibility(View.VISIBLE);
        }

        if (mShopOrder.getStatus().equals(ShopOrderStatus.DOING)) {
            toolBar.setTitle("订单详情(进行中)");
            mSaveButton.setVisibility(View.INVISIBLE);
            mOperatorLinearLayout.setVisibility(View.INVISIBLE);
        }

        mSendPriceTextView.setText(getString(R.string.Price, mShopOrder.getSendPrice()));
        mTotalPriceTextView.setText(Html.fromHtml(getString(R.string.TotalPrice, mShopOrder.getTotalPrice()), Html.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE);

        mSendTypeTextView.setText(mShopOrder.getSendType().name());
        userControllerApi.getRecieveAddress(mShopOrder.getUsername(), mShopOrder.getRecieveAddressId(), CommonApp.getApplication().getUserToken())
                .subscribe(responseBean -> {
                    if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                        RecieveAddress recieveAddress = responseBean.getData();
                        String addressInfo = recieveAddress.getReciever() + " " + recieveAddress.getPhone() + '\n' + recieveAddress.getProvince().getName()
                                + recieveAddress.getCity().getName() + recieveAddress.getCounty().getName() + recieveAddress.getDetail();
                        mDetailAddressTextView.setText(addressInfo);
                        mOrderNoTextView.setText(mShopOrder.getOrderNo());
                        mCreateTimeTextView.setText(DateUtils.date2String(mShopOrder.getCreateTime(), DateUtils.YMD_HMS_FORMAT));
                        mSenderNameEditText.setText(mShopOrder.getThirdsendNo());
                    } else {
                        Log.e(TAG, responseBean.getMsg());
                        ToastUtils.showSafeToast(OwnSendDetailActivity.this, getString(R.string.SERVER_SHOPORDER_RECIEVE_ADDRESS_EXCEPTION_GET_FAILED_MSG));
                    }
                }, throwable -> {
                    Log.e(TAG, "get ShopOrder recieve address failed", throwable);
                    ToastUtils.showSafeToast(OwnSendDetailActivity.this, getString(R.string.CLIENT_SHOPORDER_RECIEVE_ADDRESS_EXCEPTION_GET_FAILED_MSG));
                });
    }

    private void initAdapter() {
        mGoodAdapter = new DetailActivityGoodAdapter(OwnSendDetailActivity.this, mShopOrder.getOrderItemList());
        mRecyclerView.setAdapter(mGoodAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(OwnSendDetailActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(OwnSendDetailActivity.this,
                DividerItemDecoration.HORIZONTAL));
    }

    @OnClick(R.id.btn_save)
    public void onClick(View view) {
        mSaveButton.setClickable(false);
        String senderName = mSenderNameEditText.getText() != null ? mSenderNameEditText.getText().toString() : "";
        if (StringUtils.isEmpty(senderName)) {
            ToastUtils.showSafeToast(OwnSendDetailActivity.this, "请输入送货员姓名");
            mSaveButton.setClickable(true);
            return;
        }

        String senderPhone = mSenderPhoneEditText.getText() != null ? mSenderPhoneEditText.getText().toString() : "";
        if (StringUtils.isEmpty(senderPhone)) {
            ToastUtils.showSafeToast(OwnSendDetailActivity.this, "请输入送货员电话");
            mSaveButton.setClickable(true);
            return;
        }
        String operator = mOperatorEditText.getText() != null ? mOperatorEditText.getText().toString() : "";
        if (StringUtils.isEmpty(operator)) {
            ToastUtils.showSafeToast(OwnSendDetailActivity.this, "请输操作员姓名");
            mSaveButton.setClickable(true);
            return;
        }
        UpdateShopOrderStatusRequest updateBean = new UpdateShopOrderStatusRequest();
        updateBean.setStatus(ShopOrderStatus.DOING);
        updateBean.setOperator(operator);
        updateBean.setThirdsendCompany("送货上门");
        updateBean.setThirdsendno(senderName + " " + senderPhone);
        shopOrderControllerApi.updateShopOrderStatus(mShopOrder.getId(), updateBean, CommonApp.getApplication().getUserToken()).subscribe(new Observer<ResponseBean<Void>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposableList.add(d);
            }

            @Override
            public void onNext(ResponseBean<Void> responseBean) {
                if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                    OwnSendDetailActivity.this.finish();
                } else {
                    mSaveButton.setClickable(true);
                    Log.e(TAG, responseBean.getMsg());
                    ToastUtils.showSafeToast(OwnSendDetailActivity.this, "因服务端的原因,更新任务状态失败");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "update shoporder status failed", e);
                ToastUtils.showSafeToast(OwnSendDetailActivity.this, "因客户端的原因,更新任务状态失败");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "update shoporder status complete");
            }
        });

    }

    @Override
    public int getContentResourceId() {
        return R.layout.activity_ownsend_detail;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxJavaUtils.dispose(disposableList);
    }
}
