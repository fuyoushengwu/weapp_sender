package cn.aijiamuyingfang.weapp.sender.fragment;


import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.aijiamuyingfang.client.rest.api.ShopOrderControllerApi;
import cn.aijiamuyingfang.vo.response.ResponseBean;
import cn.aijiamuyingfang.vo.shoporder.PagableShopOrderList;
import cn.aijiamuyingfang.vo.shoporder.SendType;
import cn.aijiamuyingfang.vo.shoporder.ShopOrder;
import cn.aijiamuyingfang.vo.shoporder.ShopOrderStatus;
import cn.aijiamuyingfang.weapp.manager.access.server.impl.ShopOrderControllerClient;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.OnItemClickListener;
import cn.aijiamuyingfang.weapp.sender.R;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.FinishedOwnSendAdapter;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.FinishedPickupAdapter;
import cn.aijiamuyingfang.weapp.sender.recycleadapter.FinishedThirdSendAdapter;
import io.reactivex.Observable;

import static java.util.Arrays.*;

/**
 * Created by pc on 2018/5/7.
 */

public final class FinishedFragment extends RefreshableTabFragment<ShopOrder, PagableShopOrderList> {
    private static final ShopOrderControllerApi shopOrderControllerApi = new ShopOrderControllerClient();
    private static final List<Integer> mTabTitleList = asList(R.string.Tab_Finished_Layout_ThirdSend_Title,
            R.string.Tab_Finished_Layout_OwnSend_Title, R.string.Tab_Finished_Layout_PickUP_Title);
    private final List<CommonAdapter<ShopOrder>> mAdapterList = asList(
            new FinishedThirdSendAdapter(CommonApp.getApplication(), new ArrayList<>()),
            new FinishedOwnSendAdapter(CommonApp.getApplication(), new ArrayList<>()),
            new FinishedPickupAdapter(CommonApp.getApplication(), new ArrayList<>())
    );
    private final int[] mTotalPageArray = new int[]{1, 1, 1};
    private final int[] mCurrentPageArray = new int[]{1, 1, 1};
    private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int position) {
        }

        @Override
        public boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, int i) {
            return false;
        }
    };
    private final List<ShopOrderStatus> mCurShopOrderStatus = new ArrayList<>();
    private final List<SendType> mCurShopOrderSendType = new ArrayList<>();
    private int mTabIndex = 0;

    @NonNull
    @Override
    public List<Integer> getTabTitleList() {
        return mTabTitleList;
    }

    @NonNull
    @Override
    public String getToolBarTitle() {
        return "已完成的订单";
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int tag = (int) tab.getTag();
        mTabIndex = mTabTitleList.indexOf(tag);
        mAdapter = mAdapterList.get(mTabIndex);
        mRecyclerView.setAdapter(mAdapter);
        mCurShopOrderSendType.clear();
        mCurShopOrderStatus.clear();
        switch (tag) {
            case R.string.Tab_Finished_Layout_ThirdSend_Title:
                mCurShopOrderSendType.add(SendType.THIRDSEND);
                mCurShopOrderStatus.add(ShopOrderStatus.FINISHED);
                break;
            case R.string.Tab_Finished_Layout_OwnSend_Title:
                mCurShopOrderSendType.add(SendType.OWNSEND);
                mCurShopOrderStatus.add(ShopOrderStatus.FINISHED);
                break;
            case R.string.Tab_Finished_Layout_PickUP_Title:
                mCurShopOrderSendType.add(SendType.PICKUP);
                mCurShopOrderStatus.add(ShopOrderStatus.FINISHED);
                break;
            default:
                break;
        }
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
    public CommonAdapter<ShopOrder> getRecyclerViewAdapter() {
        for (CommonAdapter<ShopOrder> adapter : mAdapterList) {
            adapter.setOnItemClickListener(mOnItemClickListener);
        }
        mCurShopOrderSendType.add(SendType.THIRDSEND);
        mCurShopOrderStatus.add(ShopOrderStatus.FINISHED);
        return mAdapterList.get(0);
    }

    @Override
    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    @Override
    protected Observable<ResponseBean<PagableShopOrderList>> customGetData(int mCurrPage, int mPageSize) {
        return shopOrderControllerApi.getShopOrderList(mCurShopOrderStatus, mCurShopOrderSendType, mCurrPage, mPageSize, CommonApp.getApplication().getUserToken());
    }

    @Override
    protected List<ShopOrder> customBeforeServerData() {
        return Collections.emptyList();
    }
}
