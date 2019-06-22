package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;


import java.util.List;

import cn.aijiamuyingfang.client.commons.domain.ResponseCode;
import cn.aijiamuyingfang.client.domain.goods.Good;
import cn.aijiamuyingfang.client.domain.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.client.rest.api.GoodControllerApi;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.GoodControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.GlideUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;

/**
 * Created by pc on 2018/5/7.
 */

public class DetailActivityGoodAdapter extends CommonAdapter<ShopOrderItem> {
    private static final String TAG = DetailActivityGoodAdapter.class.getName();
    private static final GoodControllerApi goodControllerApi = new GoodControllerClient();

    public DetailActivityGoodAdapter(Context context, List<ShopOrderItem> data) {
        super(context, data, R.layout.adapter_item_detail_activity_good);
    }

    @Override
    protected void convert(RecyclerViewHolder viewHolder, ShopOrderItem itemData, int position) {
        goodControllerApi.getGood(itemData.getGoodId()).subscribe(responseBean -> {
            if (ResponseCode.OK.getCode().equals(responseBean.getCode())) {
                Good good = responseBean.getData();
                GlideUtils.load(mContext, good.getCoverImg().getUrl(), (ImageView) viewHolder.getView(R.id.good_cover_img));
                viewHolder.setText(R.id.good_name, good.getName());
                viewHolder.setText(R.id.good_price, mContext.getString(R.string.Price, good.getPrice()));
                viewHolder.setText(R.id.good_count, good.getPack() + " " + good.getLevel() + "  *" + itemData.getCount());
            } else {
                Log.e(TAG, responseBean.getMsg());
                ToastUtils.showSafeToast(mContext, mContext.getString(R.string.SERVER_GOOD_EXCEPTION_GET_FAILED_MSG));
            }
        }, throwable -> {
            Log.e(TAG, mContext.getString(R.string.CLIENT_GOOD_EXCEPTION_GET_FAILED_MSG), throwable);
            ToastUtils.showSafeToast(mContext, mContext.getString(R.string.CLIENT_GOOD_EXCEPTION_GET_FAILED_MSG));
        });
    }
}
