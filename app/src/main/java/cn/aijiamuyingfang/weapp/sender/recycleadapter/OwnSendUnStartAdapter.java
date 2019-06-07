package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.aijiamuyingfang.client.domain.ResponseCode;
import cn.aijiamuyingfang.client.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.client.domain.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.client.domain.user.RecieveAddress;
import cn.aijiamuyingfang.client.rest.api.UserControllerApi;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.UserControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;

/**
 * Created by pc on 2018/5/7.
 */

public class OwnSendUnStartAdapter extends CommonAdapter<ShopOrder> {
    private static final String TAG = OwnSendUnStartAdapter.class.getName();
    private static final UserControllerApi userControllerApi = new UserControllerClient();

    public OwnSendUnStartAdapter(Context context, List<ShopOrder> data) {
        super(context, data, R.layout.adapter_item_ownsend_unstart);
    }

    @Override
    protected void convert(RecyclerViewHolder viewHolder, ShopOrder itemData, int position) {
        StringBuilder sb = new StringBuilder();
        for (ShopOrderItem orderGood : itemData.getOrderItemList()) {
            sb.append(orderGood.getGoodName()).append("*").append(orderGood.getCount()).append("\n");
        }
        viewHolder.setText(R.id.goods, sb.toString());
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());

        userControllerApi.getRecieveAddress(itemData.getUsername(), itemData.getRecieveAddressId(), CommonApp.getApplication().getUserToken()).subscribe(responseBean -> {
            if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                RecieveAddress recieveAddress = responseBean.getData();
                viewHolder.setText(R.id.address_recipient, "收件人:" + recieveAddress.getReciever());
                viewHolder.setText(R.id.address_contactNumber, "联系电话:" + recieveAddress.getPhone());
                viewHolder.setText(R.id.address_detailAddress, "收件地址:" + recieveAddress.getProvince().getName() + recieveAddress.getCity().getName() + recieveAddress.getCounty().getName() + recieveAddress.getDetail());
            } else {
                Log.e(TAG, responseBean.getMsg());
                ToastUtils.showSafeToast(mContext, mContext.getString(R.string.SERVER_SHOPORDER_RECIEVE_ADDRESS_EXCEPTION_GET_FAILED_MSG));
            }
        }, throwable -> {
            Log.e(TAG, "get ShopOrder recieve address failed", throwable);
            ToastUtils.showSafeToast(mContext, mContext.getString(R.string.CLIENT_SHOPORDER_RECIEVE_ADDRESS_EXCEPTION_GET_FAILED_MSG));
        });


        viewHolder.setText(R.id.un_start_days, "订单未处理天数:" + itemData.getLastModifyTime());
    }
}
