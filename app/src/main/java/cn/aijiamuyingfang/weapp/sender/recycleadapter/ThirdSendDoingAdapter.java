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

public class ThirdSendDoingAdapter extends CommonAdapter<ShopOrder> {

    public ThirdSendDoingAdapter(Context context, List<ShopOrder> data) {
        super(context, data, R.layout.adapter_item_thirdsend_doing);
    }

    @Override
    protected void convert(RecyclerViewHolder viewHolder, ShopOrder itemData, int position) {
        CommonAdapterUtils.doingThirdSendShopOrder(viewHolder, itemData);
    }


}
