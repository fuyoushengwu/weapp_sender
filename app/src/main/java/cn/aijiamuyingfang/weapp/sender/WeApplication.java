package cn.aijiamuyingfang.weapp.sender;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.utils.FileUtils;
import cn.aijiamuyingfang.weapp.manager.commons.utils.ToastUtils;

/**
 * 主程序
 */
public class WeApplication extends CommonApp {
    private static final String TAG = WeApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        File imageDir = new File(getDefaultImageDir());
        if (imageDir.exists()) {
            try {
                FileUtils.cleanDirectory(imageDir);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                ToastUtils.showSafeToast(this, "清理上一次的图片缓存失败");
            }
        }
    }
}
