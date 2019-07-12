package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.client.rest.api.UserControllerApi;
import cn.aijiamuyingfang.vo.response.ResponseCode;
import cn.aijiamuyingfang.vo.shoporder.ShopOrder;
import cn.aijiamuyingfang.vo.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.vo.store.StoreAddress;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.UserControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.utils.DateUtils;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;

/**
 * Created by pc on 2018/5/7.
 */

public class FinishedPickupAdapter extends CommonAdapter<ShopOrder> {
    private static final String TAG = FinishedPickupAdapter.class.getName();
    private static final UserControllerApi userControllerApi = new UserControllerClient();
    private static final ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();

    public FinishedPickupAdapter(Context context, List<ShopOrder> data) {
        super(context, data, R.layout.adapter_item_finished_pickup);
    }

    @Override
    protected void convert(final RecyclerViewHolder viewHolder, final ShopOrder itemData, final int position) {
        StringBuilder sb = new StringBuilder();
        for (ShopOrderItem orderGood : itemData.getOrderItemList()) {
            sb.append(orderGood.getGood().getName()).append("*").append(orderGood.getCount()).append("\n");
        }
        viewHolder.setText(R.id.goods, sb.toString());
        viewHolder.setText(R.id.total_price, mContext.getString(R.string.TotalPrice, itemData.getTotalPrice()));

        StoreAddress storeAddress = itemData.getStoreAddress();
        viewHolder.setText(R.id.pickup_address, "取货地址:" + storeAddress.getDetail());
        viewHolder.setText(R.id.store_contactNumber, "门店联系电话:" + storeAddress.getPhone());


        userControllerApi.getUserPhone(itemData.getUsername(), CommonApp.getApplication().getUserToken()).subscribe(responseBean -> {
            if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                viewHolder.setText(R.id.user_phoneNumber, "用户电话:" + responseBean.getData());
            } else {
                Log.e(TAG, responseBean.getMsg());
                ToastUtils.showSafeToast(mContext, mContext.getString(R.string.SERVER_USER_EXCEPTION_GET_PHONE_MSG));
            }
        }, throwable -> {
            Log.e(TAG, "get user phone failed", throwable);
            ToastUtils.showSafeToast(mContext, mContext.getString(R.string.CLIENT_USER_EXCEPTION_GET_PHONE_MSG));
        });

        viewHolder.setText(R.id.pickup_time, "取货时间:" + DateUtils.date2String(itemData.getPickupTime(), DateUtils.YMD_HMS_FORMAT));
        viewHolder.setText(R.id.order_create_time, "订单创建时间:" + DateUtils.date2String(itemData.getCreateTime(), DateUtils.YMD_HMS_FORMAT));
        viewHolder.setText(R.id.order_finish_time, "订单结束时间:" + DateUtils.date2String(itemData.getFinishTime(), DateUtils.YMD_HMS_FORMAT));
        viewHolder.setText(R.id.order_operator, "订单处理人:" + Arrays.toString(itemData.getOperator().toArray()));
        viewHolder.setOnClickListener(R.id.btn_delete, v -> {
            int finishedDays = itemData.getLastModifyTime();
            if (finishedDays <= 100) {
                ToastUtils.showSafeToast(mContext, "只有订单已完成100天以上，才能删除");
                return;
            }
            shopOrderControllerApi.delete100DaysFinishedShopOrder(itemData.getId(), CommonApp.getApplication().getUserToken()).subscribe(responseBean -> {
                if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                    FinishedPickupAdapter.this.removeData(position);
                } else {
                    Log.e(TAG, responseBean.getMsg());
                    ToastUtils.showSafeToast(mContext, mContext.getString(R.string.SERVER_SHOPORDER_EXCEPTION_DELETE_FAILED_MSG));
                }
            }, throwable -> {
                Log.e(TAG, "delete 100 days finished shoporder failed", throwable);
                ToastUtils.showSafeToast(mContext, mContext.getString(R.string.CLIENT_SHOPORDER_EXCEPTION_DELETE_FAILED_MSG));
            });

        });
    }
}
