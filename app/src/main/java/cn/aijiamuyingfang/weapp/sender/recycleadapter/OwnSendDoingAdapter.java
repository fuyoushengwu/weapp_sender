package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;

import java.util.List;

import cn.aijiamuyingfang.vo.shoporder.ShopOrder;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;
import cn.aijiamuyingfang.weapp.sender.utils.CommonAdapterUtils;

/**
 * Created by pc on 2018/5/7.
 */

public class OwnSendDoingAdapter extends CommonAdapter<ShopOrder> {
    public OwnSendDoingAdapter(Context context, List<ShopOrder> data) {
        super(context, data, R.layout.adapter_item_ownsend_doing);
    }

    @Override
    protected void convert(RecyclerViewHolder viewHolder, ShopOrder itemData, int position) {
        CommonAdapterUtils.doingOwnSendShopOrder(viewHolder, itemData);
    }
}
