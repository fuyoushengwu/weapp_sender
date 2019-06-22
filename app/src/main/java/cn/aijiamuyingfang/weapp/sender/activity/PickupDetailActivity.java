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
import cn.aijiamuyingfang.client.commons.domain.ResponseBean;
import cn.aijiamuyingfang.client.commons.domain.ResponseCode;
import cn.aijiamuyingfang.client.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.client.domain.shoporder.ShopOrderStatus;
import cn.aijiamuyingfang.client.domain.shoporder.request.UpdateShopOrderStatusRequest;
import cn.aijiamuyingfang.client.domain.store.StoreAddress;
import cn.aijiamuyingfang.client.domain.user.response.GetUserPhoneResponse;
import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.client.rest.api.StoreControllerApi;
import cn.aijiamuyingfang.client.rest.api.UserControllerApi;
import cn.aijiamuyingfang.client.commons.utils.StringUtils;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.StoreControllerClient;
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

public class PickupDetailActivity extends BaseActivity {
    private static final String TAG = PickupDetailActivity.class.getName();
    private static final ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();
    private static final UserControllerApi userControllerApi = new UserControllerClient();
    private static final StoreControllerApi storeControllerApi = new StoreControllerClient();
    @BindView(R.id.toolbar)
    WeToolBar toolBar;
    @BindView(R.id.operator_ll)
    LinearLayout mOperatorLinearLayout;
    @BindView(R.id.operator)
    ClearEditText mOperatorEditText;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    DetailActivityGoodAdapter mGoodAdapter;
    @BindView(R.id.total_price)
    TextView mTotalPriceTextView;
    @BindView(R.id.send_type)
    TextView mSendTypeTextView;
    @BindView(R.id.pickup_address)
    TextView mPickupAddressTextView;
    @BindView(R.id.store_contactNumber)
    TextView mStoreContactNumberTextView;
    @BindView(R.id.user_phoneNumber)
    TextView mUserPhoneNumberTextView;
    @BindView(R.id.pickup_time)
    TextView mPickupTimeTextView;
    @BindView(R.id.shoporder_no)
    TextView mShopOrderNoTextView;
    @BindView(R.id.shoporder_create_time)
    TextView mCreateTimeTextView;

    private ShopOrder mShopOrder;
    private final List<Disposable> disposableList = new ArrayList<>();

    @Override
    protected void init() {
        initData();
        initAdapter();
    }

    private void initData() {
        Intent intent = getIntent();
        mShopOrder = intent.getParcelableExtra(Constant.INTENT_SHOPORDER);
        if (mShopOrder.getStatus().equals(ShopOrderStatus.UNSTART)) {
            initUNStart();
        }

        if (mShopOrder.getStatus().equals(ShopOrderStatus.DOING)) {
            initDoing();
        }

        mTotalPriceTextView.setText(Html.fromHtml(getString(R.string.TotalPrice, mShopOrder.getTotalPrice()), Html.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE);
        mSendTypeTextView.setText(mShopOrder.getSendType().name());

        storeControllerApi.getStoreAddressByAddressId(mShopOrder.getPickupStoreAddressId()).subscribe(responseBean -> {
            if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                StoreAddress storeAddress = responseBean.getData();
                if (storeAddress != null) {
                    String addressInfo = storeAddress.getPhone() + '\n' + storeAddress.getProvince().getName() + storeAddress.getCity().getName()
                            + storeAddress.getCounty().getName() + storeAddress.getDetail();
                    mPickupAddressTextView.setText(addressInfo);
                    mStoreContactNumberTextView.setText(storeAddress.getPhone());
                }
            } else {
                Log.e(TAG, responseBean.getMsg());
                ToastUtils.showSafeToast(PickupDetailActivity.this, getString(R.string.SERVER_SHOPORDER_STORE_ADDRESS_EXCEPTION_GET_FAILED_MSG));
            }
        }, throwable -> {
            Log.e(TAG, "get ShopOrder pickup address failed", throwable);
            ToastUtils.showSafeToast(PickupDetailActivity.this, getString(R.string.CLIENT_SHOPORDER_STORE_ADDRESS_EXCEPTION_GET_FAILED_MSG));
        });
        userControllerApi.getUserPhone(mShopOrder.getUsername(), CommonApp.getApplication().getUserToken()).subscribe(new Observer<ResponseBean<GetUserPhoneResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposableList.add(d);
            }

            @Override
            public void onNext(ResponseBean<GetUserPhoneResponse> responseBean) {
                if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                    mUserPhoneNumberTextView.setText(responseBean.getData().getPhone());
                } else {
                    Log.e(TAG, responseBean.getMsg());
                    ToastUtils.showSafeToast(PickupDetailActivity.this, getString(R.string.SERVER_USER_EXCEPTION_GET_PHONE_MSG));
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "get user phone failed", e);
                ToastUtils.showSafeToast(PickupDetailActivity.this, getString(R.string.CLIENT_USER_EXCEPTION_GET_PHONE_MSG));
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "get user phone complete");
            }
        });

        mPickupTimeTextView.setText(DateUtils.date2String(mShopOrder.getPickupTime(), DateUtils.YMD_HMS_FORMAT));
        mCreateTimeTextView.setText(DateUtils.date2String(mShopOrder.getCreateTime(), DateUtils.YMD_HMS_FORMAT));
        mShopOrderNoTextView.setText(mShopOrder.getOrderNo());
    }

    private void initUNStart() {
        toolBar.setTitle("订单详情(未开始)");
        toolBar.setRightButtonText("开始");
        toolBar.setRightButtonOnClickListener(v -> {
            final Button button = toolBar.getRightButton();
            button.setClickable(false);
            String operator = mOperatorEditText.getText() != null ? mOperatorEditText.getText().toString() : "";
            if (StringUtils.isEmpty(operator)) {
                ToastUtils.showSafeToast(PickupDetailActivity.this, "请输操作员姓名");
                button.setClickable(true);
                return;
            }
            UpdateShopOrderStatusRequest updateBean = new UpdateShopOrderStatusRequest();
            updateBean.setStatus(ShopOrderStatus.DOING);
            updateBean.setOperator(operator);
            updateBean.setThirdsendCompany("自取");
            shopOrderControllerApi.updateShopOrderStatus(mShopOrder.getId(), updateBean, CommonApp.getApplication().getUserToken()).subscribe(responseBean -> {
                if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                    PickupDetailActivity.this.finish();
                } else {
                    Log.e(TAG, responseBean.getMsg());
                    button.setClickable(true);
                    ToastUtils.showSafeToast(PickupDetailActivity.this, "因服务端的原因,更新任务状态失败");
                }
            }, throwable -> {
                Log.e(TAG, "update shoporder status failed", throwable);
                ToastUtils.showSafeToast(PickupDetailActivity.this, "因客户端的原因,更新任务状态失败");
            });
        });
    }

    private void initDoing() {
        toolBar.setTitle("订单详情(进行中)");
        mOperatorLinearLayout.setVisibility(View.INVISIBLE);
    }

    private void initAdapter() {
        mGoodAdapter = new DetailActivityGoodAdapter(PickupDetailActivity.this, mShopOrder.getOrderItemList());
        mRecyclerView.setAdapter(mGoodAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(PickupDetailActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(PickupDetailActivity.this,
                DividerItemDecoration.HORIZONTAL));
    }

    @Override
    public int getContentResourceId() {
        return R.layout.activity_pickup_detail;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxJavaUtils.dispose(disposableList);
    }
}
