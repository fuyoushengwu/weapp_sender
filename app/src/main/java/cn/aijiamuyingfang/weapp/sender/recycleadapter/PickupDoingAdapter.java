package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;
import android.util.Log;

import java.util.List;

import cn.aijiamuyingfang.client.rest.api.UserControllerApi;
import cn.aijiamuyingfang.commons.domain.address.RecieveAddress;
import cn.aijiamuyingfang.commons.domain.response.ResponseBean;
import cn.aijiamuyingfang.commons.domain.response.ResponseCode;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.commons.domain.user.User;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.UserControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by pc on 2018/5/7.
 */

public class PickupDoingAdapter extends CommonAdapter<ShopOrder> {
    private static final String TAG = PickupDoingAdapter.class.getName();

    public PickupDoingAdapter(Context context, List<ShopOrder> data) {
        super(context, data, R.layout.adapter_item_pickup_doing);
    }

    private UserControllerApi userControllerApi = new UserControllerClient();

    @Override
    protected void convert(final RecyclerViewHolder viewHolder, ShopOrder itemData, int position) {
        StringBuilder sb = new StringBuilder();
        for (ShopOrderItem orderGood : itemData.getOrderItemList()) {
            sb.append(orderGood.getGood().getName()).append("*").append(orderGood.getCount()).append("\n");
        }
        viewHolder.setText(R.id.goods, sb.toString());
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());

        RecieveAddress recieveAddress = itemData.getRecieveAddress();
        viewHolder.setText(R.id.pickup_address, "取货地址:" + recieveAddress.getDetail());
        viewHolder.setText(R.id.store_contactNumber, "门店联系电话:" + recieveAddress.getPhone());

        userControllerApi.getUser(CommonApp.getApplication().getUserToken(), itemData.getUserid()).subscribe(new Observer<ResponseBean<User>>() {
            @Override
            public void onSubscribe(Disposable d) {
                //DO NOT NEED IMPLEMENT
            }

            @Override
            public void onNext(ResponseBean<User> responseBean) {
                if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                    viewHolder.setText(R.id.user_phoneNumber, "用户电话:" + responseBean.getData().getPhone());
                } else {
                    Log.e(TAG, responseBean.getMsg());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "get user failed", e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "get user complete");
            }
        });
        viewHolder.setText(R.id.pichup_time, "取货时间" + itemData.getPickupTime());
        viewHolder.setText(R.id.unstart_days, "订单未处理天数:" + itemData.getLastModifyTime());
    }


}
