package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;
import android.util.Log;


import java.util.List;

import cn.aijiamuyingfang.client.commons.domain.ResponseBean;
import cn.aijiamuyingfang.client.commons.domain.ResponseCode;
import cn.aijiamuyingfang.client.domain.shoporder.ShopOrder;
import cn.aijiamuyingfang.client.domain.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.client.domain.user.response.GetUserPhoneResponse;
import cn.aijiamuyingfang.client.rest.api.UserControllerApi;
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

public class PreOrderOrderAdapter extends CommonAdapter<ShopOrder> {
    private static final String TAG = PreOrderOrderAdapter.class.getName();
    private static final UserControllerApi userControllerApi = new UserControllerClient();

    public PreOrderOrderAdapter(Context context, List<ShopOrder> data) {
        super(context, data, R.layout.adapter_item_preorder_order);
    }

    @Override
    protected void convert(final RecyclerViewHolder viewHolder, ShopOrder itemData, int position) {
        StringBuilder sb = new StringBuilder();
        for (ShopOrderItem orderGood : itemData.getOrderItemList()) {
            sb.append(orderGood.getGoodName()).append("*").append(orderGood.getCount()).append("\n");
        }
        viewHolder.setText(R.id.goods, sb.toString());
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());


        userControllerApi.getUserPhone(itemData.getUsername(), CommonApp.getApplication().getUserToken()).subscribe(new Observer<ResponseBean<GetUserPhoneResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {
                // NOT NEED IMPLEMENT
            }

            @Override
            public void onNext(ResponseBean<GetUserPhoneResponse> responseBean) {
                if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                    viewHolder.setText(R.id.user_phoneNumber, "用户电话:" + responseBean.getData().getPhone());
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
        viewHolder.setText(R.id.order_days, "订单未处理天数:" + itemData.getLastModifyTime());
    }
}
