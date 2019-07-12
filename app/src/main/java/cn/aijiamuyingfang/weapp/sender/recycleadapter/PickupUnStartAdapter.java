package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.aijiamuyingfang.client.rest.api.UserControllerApi;
import cn.aijiamuyingfang.vo.response.ResponseBean;
import cn.aijiamuyingfang.vo.response.ResponseCode;
import cn.aijiamuyingfang.vo.shoporder.ShopOrder;
import cn.aijiamuyingfang.vo.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.vo.store.StoreAddress;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.UserControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.utils.DateUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by pc on 2018/5/7.
 */

public class PickupUnStartAdapter extends CommonAdapter<ShopOrder> {
    private static final String TAG = PickupUnStartAdapter.class.getName();
    private static final UserControllerApi userControllerApi = new UserControllerClient();

    public PickupUnStartAdapter(Context context, List<ShopOrder> data) {
        super(context, data, R.layout.adapter_item_pickup_unstart);
    }

    @Override
    protected void convert(final RecyclerViewHolder viewHolder, final ShopOrder itemData, int position) {
        StringBuilder sb = new StringBuilder();
        for (ShopOrderItem orderGood : itemData.getOrderItemList()) {
            sb.append(orderGood.getGood().getName()).append("*").append(orderGood.getCount()).append("\n");
        }
        viewHolder.setText(R.id.goods, sb.toString());
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());

        StoreAddress storeAddress = itemData.getStoreAddress();
        if (storeAddress != null) {
            viewHolder.setText(R.id.pickup_address, "取货地址:" + storeAddress.getDetail());
            viewHolder.setText(R.id.store_contactNumber, "门店联系电话:" + storeAddress.getPhone());
        }

        userControllerApi.getUserPhone(itemData.getUsername(), CommonApp.getApplication().getUserToken()).subscribe(new Observer<ResponseBean<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                //DO NOT NEED IMPLEMENT
            }

            @Override
            public void onNext(ResponseBean<String> responseBean) {
                if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                    viewHolder.setText(R.id.user_phoneNumber, "用户电话:" + responseBean.getData());
                } else {
                    Log.e(TAG, responseBean.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "get user phone failed", e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "get user phone complete");
            }
        });
        viewHolder.setText(R.id.pickup_time, "取货时间:" + DateUtils.date2String(itemData.getPickupTime(), DateUtils.YMD_HMS_FORMAT));
        viewHolder.setText(R.id.un_start_days, "订单未处理天数:" + itemData.getLastModifyTime());
    }


}
