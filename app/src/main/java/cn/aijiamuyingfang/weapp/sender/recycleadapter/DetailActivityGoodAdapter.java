package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;
import android.widget.ImageView;


import java.util.List;

import cn.aijiamuyingfang.vo.goods.Good;
import cn.aijiamuyingfang.vo.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.weapp.manager.widgets.GlideUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;

/**
 * Created by pc on 2018/5/7.
 */

public class DetailActivityGoodAdapter extends CommonAdapter<ShopOrderItem> {

    public DetailActivityGoodAdapter(Context context, List<ShopOrderItem> data) {
        super(context, data, R.layout.adapter_item_detail_activity_good);
    }

    @Override
    protected void convert(RecyclerViewHolder viewHolder, ShopOrderItem itemData, int position) {
        Good good = itemData.getGood();
        GlideUtils.load(mContext, good.getCoverImg().getUrl(), (ImageView) viewHolder.getView(R.id.good_cover_img));
        viewHolder.setText(R.id.good_name, good.getName());
        viewHolder.setText(R.id.good_price, mContext.getString(R.string.Price, good.getPrice()));
        viewHolder.setText(R.id.good_count, good.getPack() + " " + good.getLevel() + "  *" + itemData.getCount());
    }
}
