package cn.aijiamuyingfang.weapp.sender.utils;


import android.content.Context;
import android.util.Log;

import java.util.Arrays;

import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.client.rest.api.UserControllerApi;
import cn.aijiamuyingfang.vo.address.City;
import cn.aijiamuyingfang.vo.address.County;
import cn.aijiamuyingfang.vo.address.Province;
import cn.aijiamuyingfang.vo.response.ResponseBean;
import cn.aijiamuyingfang.vo.response.ResponseCode;
import cn.aijiamuyingfang.vo.shoporder.ShopOrder;
import cn.aijiamuyingfang.vo.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.vo.store.StoreAddress;
import cn.aijiamuyingfang.vo.user.RecieveAddress;
import cn.aijiamuyingfang.vo.utils.CollectionUtils;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.UserControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.utils.DateUtils;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * CommonAdapter子类用户展示的工具类
 */
public class CommonAdapterUtils {
    private CommonAdapterUtils() {
    }

    private static final String UNSTART_DAYS_PREFIX = "订单未处理天数:";

    public static boolean unStartShopOrder(RecyclerViewHolder viewHolder, ShopOrder itemData) {
        if (null == itemData || null == viewHolder) {
            return false;
        }
        showGoodInfo(viewHolder, itemData);
        showRecieveAddressInfo(viewHolder, itemData);
        viewHolder.setText(R.id.un_start_days, UNSTART_DAYS_PREFIX + itemData.getLastModifyTime());
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());
        return true;
    }

    public static void doingThirdSendShopOrder(RecyclerViewHolder viewHolder, ShopOrder itemData) {
        if (unStartShopOrder(viewHolder, itemData)) {
            viewHolder.setText(R.id.third_send_company, "快递公司:" + itemData.getThirdsendCompany());
            viewHolder.setText(R.id.third_send_no, "快递单号:" + itemData.getThirdsendNo());
        }
    }

    public static void doingOwnSendShopOrder(RecyclerViewHolder viewHolder, ShopOrder itemData) {
        if (unStartShopOrder(viewHolder, itemData)) {
            viewHolder.setText(R.id.third_send_no, "送货员:" + itemData.getThirdsendNo());
        }
    }

    public static void pickUpShopOrder(RecyclerViewHolder viewHolder, ShopOrder itemData) {
        if (null == itemData || null == viewHolder) {
            return;
        }
        showGoodInfo(viewHolder, itemData);
        showPickupAddressInfo(viewHolder, itemData);
        showUserPhone(viewHolder, itemData);
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());
        viewHolder.setText(R.id.pickup_time, "取货时间:" + DateUtils.date2String(itemData.getPickupTime(), DateUtils.YMD_HMS_FORMAT));
        viewHolder.setText(R.id.un_start_days, UNSTART_DAYS_PREFIX + itemData.getLastModifyTime());
    }

    private static void showGoodInfo(RecyclerViewHolder viewHolder, ShopOrder itemData) {
        if (CollectionUtils.isEmpty(itemData.getOrderItemList())) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (ShopOrderItem orderGood : itemData.getOrderItemList()) {
            if (orderGood != null && orderGood.getGood() != null) {
                sb.append(orderGood.getGood().getName()).append("*").append(orderGood.getCount()).append("\n");
            }
        }
        viewHolder.setText(R.id.goods, sb.toString());
    }

    private static void showRecieveAddressInfo(RecyclerViewHolder viewHolder, ShopOrder itemData) {
        RecieveAddress recieveAddress = itemData.getRecieveAddress();
        if (null == recieveAddress) {
            return;
        }
        StringBuilder sb = new StringBuilder("收件地址:");
        Province province = recieveAddress.getProvince();
        if (province != null) {
            sb.append(province.getName());
        }
        City city = recieveAddress.getCity();
        if (city != null) {
            sb.append(city.getName());
        }
        County county = recieveAddress.getCounty();
        if (county != null) {
            sb.append(county.getName());
        }
        sb.append(recieveAddress.getDetail());

        viewHolder.setText(R.id.address_detailAddress, sb.toString());
        viewHolder.setText(R.id.address_recipient, "收件人:" + recieveAddress.getReciever());
        viewHolder.setText(R.id.address_contactNumber, "联系电话:" + recieveAddress.getPhone());
    }

    private static void showPickupAddressInfo(RecyclerViewHolder viewHolder, ShopOrder itemData) {
        StoreAddress storeAddress = itemData.getStoreAddress();
        if (storeAddress != null) {
            viewHolder.setText(R.id.pickup_address, "取货地址:" + storeAddress.getDetail());
            viewHolder.setText(R.id.store_contactNumber, "门店联系电话:" + storeAddress.getPhone());
        }
    }

    private static final UserControllerApi userControllerApi = new UserControllerClient();

    private static void showUserPhone(RecyclerViewHolder viewHolder, ShopOrder itemData) {
        userControllerApi.getUserPhone(itemData.getUsername(), CommonApp.getApplication().getUserToken()).subscribe(new Observer<ResponseBean<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                //DO NOT NEED IMPLEMENT
            }

            @Override
            public void onNext(ResponseBean<String> responseBean) {
                if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                    viewHolder.setText(R.id.user_phoneNumber, "用户电话:" + responseBean.getData());
                }
            }

            @Override
            public void onError(Throwable e) { //DO NOT NEED IMPLEMENT
            }

            @Override
            public void onComplete() { //DO NOT NEED IMPLEMENT
            }
        });
    }

    public static void finishedOwnSendShopOrder(RecyclerViewHolder viewHolder, ShopOrder itemData, int position, Context context, CommonAdapter commonAdapter) {
        if (null == itemData || null == viewHolder || null == context || null == commonAdapter) {
            return;
        }
        showGoodInfo(viewHolder, itemData);
        showRecieveAddressInfo(viewHolder, itemData);
        showFinshedInfo(viewHolder, itemData);
        setOnClickListener(viewHolder, itemData, position, context, commonAdapter);
        viewHolder.setText(R.id.third_send_no, "送货员:" + itemData.getThirdsendNo());
    }

    public static void finishedThirdSendShopOrder(RecyclerViewHolder viewHolder, ShopOrder itemData, int position, Context context, CommonAdapter commonAdapter) {
        if (null == itemData || null == viewHolder || null == context || null == commonAdapter) {
            return;
        }
        showGoodInfo(viewHolder, itemData);
        showRecieveAddressInfo(viewHolder, itemData);
        showFinshedInfo(viewHolder, itemData);
        setOnClickListener(viewHolder, itemData, position, context, commonAdapter);
        viewHolder.setText(R.id.third_send_company, "快递公司:" + itemData.getThirdsendCompany());
        viewHolder.setText(R.id.third_send_no, "快递单号:" + itemData.getThirdsendNo());
    }

    public static void finishedPickUpShopOrder(RecyclerViewHolder viewHolder, ShopOrder itemData, int position, Context context, CommonAdapter commonAdapter) {
        if (null == itemData || null == viewHolder || null == context || null == commonAdapter) {
            return;
        }
        showGoodInfo(viewHolder, itemData);
        showPickupAddressInfo(viewHolder, itemData);
        showFinshedInfo(viewHolder, itemData);
        showUserPhone(viewHolder, itemData);
        setOnClickListener(viewHolder, itemData, position, context, commonAdapter);
        viewHolder.setText(R.id.pickup_time, "取货时间:" + DateUtils.date2String(itemData.getPickupTime(), DateUtils.YMD_HMS_FORMAT));
    }

    public static void preorderOrder(RecyclerViewHolder viewHolder, ShopOrder itemData) {
        if (null == itemData || null == viewHolder) {
            return;
        }
        showGoodInfo(viewHolder, itemData);
        showUserPhone(viewHolder, itemData);
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());
        viewHolder.setText(R.id.order_days, UNSTART_DAYS_PREFIX + itemData.getLastModifyTime());
    }

    private static void showFinshedInfo(RecyclerViewHolder viewHolder, ShopOrder itemData) {
        viewHolder.setText(R.id.order_create_time, "订单创建时间:" + DateUtils.date2String(itemData.getCreateTime(), DateUtils.YMD_HMS_FORMAT));
        viewHolder.setText(R.id.order_finish_time, "订单结束时间:" + DateUtils.date2String(itemData.getFinishTime(), DateUtils.YMD_HMS_FORMAT));
        viewHolder.setText(R.id.order_operator, "订单处理人:" + Arrays.toString(itemData.getOperator().toArray()));
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());
    }

    private static final String TAG = CommonAdapterUtils.class.getName();
    private static final ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();

    private static void setOnClickListener(RecyclerViewHolder viewHolder, ShopOrder itemData, int position, Context context, CommonAdapter commonAdapter) {
        viewHolder.setOnClickListener(R.id.btn_delete, v -> {
            double finishedDays = itemData.getLastModifyTime();
            if (finishedDays <= 100) {
                ToastUtils.showSafeToast(context, "只有订单已完成100天以上，才能删除");
                return;
            }
            shopOrderControllerApi.delete100DaysFinishedShopOrder(itemData.getId(), CommonApp.getApplication().getUserToken()).subscribe(responseBean -> {
                if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                    commonAdapter.removeData(position);
                } else {
                    Log.e(TAG, responseBean.getMsg());
                    ToastUtils.showSafeToast(context, context.getString(R.string.SERVER_SHOPORDER_EXCEPTION_DELETE_FAILED_MSG));
                }
            }, throwable -> {
                Log.e(TAG, "delete 100 days finished shoporder failed", throwable);
                ToastUtils.showSafeToast(context, context.getString(R.string.CLIENT_SHOPORDER_EXCEPTION_DELETE_FAILED_MSG));
            });
        });
    }
}
