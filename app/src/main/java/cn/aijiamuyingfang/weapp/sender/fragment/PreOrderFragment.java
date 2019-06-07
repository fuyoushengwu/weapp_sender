package cn.aijiamuyingfang.weapp.sender.fragment;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.aijiamuyingfang.client.domain.PageResponse;
import cn.aijiamuyingfang.client.domain.ResponseBean;
import cn.aijiamuyingfang.client.domain.previeworder.PreOrderGood;
import cn.aijiamuyingfang.client.domain.previeworder.response.GetPreOrderGoodListResponse;
import cn.aijiamuyingfang.client.domain.shoporder.SendType;
import cn.aijiamuyingfang.client.domain.shoporder.ShopOrderStatus;
import cn.aijiamuyingfang.client.domain.shoporder.response.GetShopOrderListResponse;
import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.Constant;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.OnItemClickListener;
import cn.aijiamuyingfang.weapp.sender.R;
import cn.aijiamuyingfang.weapp.sender.activity.PreOrderDetailActivity;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.PreOrderGoodAdapter;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.PreOrderOrderAdapter;
import io.reactivex.Observable;

/**
 * Created by pc on 2018/5/7.
 */
@SuppressWarnings("unchecked")
public final class PreOrderFragment extends RefreshableTabFragment<Object, PageResponse<Object>> {
    private static final ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();
    private static final List<Integer> mTabTitleList = Arrays.asList(R.string.Tab_PreOrder_Layout_Good_Title, R.string.Tab_PreOrder_Layout_Order_Title);
    private final List<CommonAdapter> mAdapterList = Arrays.asList(
            new PreOrderGoodAdapter(CommonApp.getApplication(), new ArrayList<>()),
            new PreOrderOrderAdapter(CommonApp.getApplication(), new ArrayList<>())
    );
    private final int[] mTotalPageArray = new int[]{1, 1};
    private final int[] mCurrentPageArray = new int[]{1, 1};
    private int mTabIndex = 0;
    private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
            Intent intent = new Intent(getContext(), PreOrderDetailActivity.class);
            intent.putExtra(Constant.INTENT_SHOPORDER, (PreOrderGood) mAdapter.getData(position));
            startActivity(intent);
        }

        @Override
        public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
            return false;
        }
    };

    private Observable<ResponseBean<PageResponse<Object>>> getPreOrderGoodList;
    private Observable<ResponseBean<PageResponse<Object>>> getShopOrderList;

    @NonNull
    @Override
    public List<Integer> getTabTitleList() {
        return mTabTitleList;
    }

    @NonNull
    @Override
    public String getToolBarTitle() {
        return "预订的订单";
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int tag = (int) tab.getTag();
        mTabIndex = mTabTitleList.indexOf(tag);
        mAdapter = mAdapterList.get(mTabIndex);
        mRecyclerView.setAdapter(mAdapter);
        super.refreshData();
    }

    @Override
    public int getCurrentPage() {
        return mCurrentPageArray[mTabIndex];
    }

    @Override
    public void setCurrentPage(int currentPage) {
        mCurrentPageArray[mTabIndex] = currentPage;
    }

    @Override
    public int getTotalPage() {
        return mTotalPageArray[mTabIndex];
    }

    @Override
    public void setTotalPage(int totalPage) {
        mTotalPageArray[mTabIndex] = totalPage;
    }

    @NonNull
    @Override
    public CommonAdapter<Object> getRecyclerViewAdapter() {
        return mAdapterList.get(0);
    }

    @Override
    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }


    @Override
    protected Observable<ResponseBean<PageResponse<Object>>> customGetData(int mCurrPage, int mPageSize) {
        synchronized (this) {
            if (null == getPreOrderGoodList) {
                Observable<ResponseBean<GetPreOrderGoodListResponse>> observable1 = shopOrderControllerApi.getPreOrderGoodList(
                        mCurrPage, mPageSize, CommonApp.getApplication().getUserToken());
                getPreOrderGoodList = observable1.map(responseBean -> {
                    if (null == responseBean) {
                        return null;
                    }
                    ResponseBean<PageResponse<Object>> result = new ResponseBean<>();
                    result.setCode(responseBean.getCode());
                    result.setMsg(responseBean.getMsg());
                    result.setData((PageResponse) responseBean.getData());
                    return result;
                });
            }
            if (null == getShopOrderList) {
                List<SendType> sendType = new ArrayList<>();
                List<ShopOrderStatus> status = new ArrayList<>();
                status.add(ShopOrderStatus.PREORDER);
                Observable<ResponseBean<GetShopOrderListResponse>> observable2 = shopOrderControllerApi.getShopOrderList(
                        status, sendType, mCurrPage, mPageSize, CommonApp.getApplication().getUserToken());
                getShopOrderList = observable2.map(responseBean -> {
                    if (null == responseBean) {
                        return null;
                    }
                    ResponseBean<PageResponse<Object>> result = new ResponseBean<>();
                    result.setCode(responseBean.getCode());
                    result.setMsg(responseBean.getMsg());
                    result.setData((PageResponse) responseBean.getData());
                    return result;
                });
            }
        }
        switch (mTabIndex) {
            case 0:
                return getPreOrderGoodList;
            case 1:
                return getShopOrderList;
            default:
                return getPreOrderGoodList;
        }
    }

    @Override
    protected List<Object> customBeforeServerData() {
        return Collections.emptyList();
    }
}
