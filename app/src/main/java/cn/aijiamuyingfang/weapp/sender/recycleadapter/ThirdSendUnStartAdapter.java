package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;

import java.util.List;

import cn.aijiamuyingfang.vo.shoporder.ShopOrder;
import cn.aijiamuyingfang.vo.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.vo.user.RecieveAddress;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;

/**
 * Created by pc on 2018/5/7.
 */

public class ThirdSendUnStartAdapter extends CommonAdapter<ShopOrder> {

    public ThirdSendUnStartAdapter(Context context, List<ShopOrder> data) {
        super(context, data, R.layout.adapter_item_thirdsend_unstart);
    }

    @Override
    protected void convert(RecyclerViewHolder viewHolder, ShopOrder itemData, int position) {
        StringBuilder sb = new StringBuilder();
        for (ShopOrderItem orderGood : itemData.getOrderItemList()) {
            sb.append(orderGood.getGood().getName()).append("*").append(orderGood.getCount()).append("\n");
        }
        viewHolder.setText(R.id.goods, sb.toString());
        viewHolder.setText(R.id.total_price, "总价:" + itemData.getTotalPrice());
        RecieveAddress recieveAddress = itemData.getRecieveAddress();
        viewHolder.setText(R.id.address_recipient, "收件人:" + recieveAddress.getReciever());
        viewHolder.setText(R.id.address_contactNumber, "联系电话:" + recieveAddress.getPhone());
        viewHolder.setText(R.id.address_detailAddress, "收件地址:" + recieveAddress.getProvince().getName() + recieveAddress.getCity().getName() + recieveAddress.getCounty().getName() + recieveAddress.getDetail());
        viewHolder.setText(R.id.un_start_days, "订单未处理天数:" + itemData.getLastModifyTime());
    }


}
