package cn.aijiamuyingfang.weapp.sender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.aijiamuyingfang.client.oauth2.OAuthResponse;
import cn.aijiamuyingfang.vo.utils.StringUtils;
import cn.aijiamuyingfang.weapp.manager.access.server.utils.OAuth2Utils;
import cn.aijiamuyingfang.weapp.manager.access.server.utils.RxJavaUtils;
import cn.aijiamuyingfang.weapp.manager.commons.CommonApp;
import cn.aijiamuyingfang.weapp.manager.commons.activity.BaseActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getName();
    private final List<Disposable> authDisposableList = new ArrayList<>();
    @BindView(R.id.login_progress)
    ProgressBar mProgressView;
    @BindView(R.id.login_form)
    ScrollView mLoginFormView;

    @BindView(R.id.et_username)
    EditText mUserNameEditView;
    @BindView(R.id.iv_username)
    ImageView mUserNameImageView;

    @BindView(R.id.et_password)
    EditText mPasswordEditView;
    @BindView(R.id.iv_password)
    ImageView mPasswordImageView;

    @Override
    protected void init() {
        mPasswordEditView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });
        mUserNameEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NOT NEED IMPLEMENT
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //NOT NEED IMPLEMENT
            }

            @Override
            public void afterTextChanged(Editable s) {
                //如果有输入内容长度大于0那么显示clear按钮
                if (s.length() > 0) {
                    mUserNameImageView.setVisibility(View.VISIBLE);
                } else {
                    mUserNameImageView.setVisibility(View.INVISIBLE);
                }
            }
        });
        mPasswordEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NOT NEED IMPLEMENT
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //NOT NEED IMPLEMENT
            }

            @Override
            public void afterTextChanged(Editable s) {
                //如果有输入内容长度大于0那么显示clear按钮
                if (s.length() > 0) {
                    mPasswordImageView.setVisibility(View.VISIBLE);
                } else {
                    mPasswordImageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void attemptLogin() {
        mUserNameEditView.setError(null);
        mPasswordEditView.setError(null);

        String username = mUserNameEditView.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mUserNameEditView.setError("账号不能为空");
            mUserNameEditView.requestFocus();
            return;
        }

        String password = mPasswordEditView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordEditView.setError("密码不能为空");
            mPasswordEditView.requestFocus();
            return;
        }

        showProgress(true);
        Observable.create((ObservableOnSubscribe<OAuthResponse>) e -> {
            OAuthResponse oAuthResponse = OAuth2Utils.getAccessToken(username, password);
            e.onNext(oAuthResponse);
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<OAuthResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                authDisposableList.add(d);
            }

            @Override
            public void onNext(OAuthResponse oAuthResponse) {
                showProgress(false);
                if (null == oAuthResponse || StringUtils.isEmpty(oAuthResponse.getAccessToken())) {
                    mPasswordEditView.setError("用户名密码不正确");
                    mPasswordEditView.requestFocus();
                    return;
                }
                CommonApp.getApplication().setUserToken(oAuthResponse.getAccessToken());
                CommonApp.getApplication().setUsername(username);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                Log.e(TAG, "user login failed", e);
                mPasswordEditView.setError("用户名密码不正确");
                mPasswordEditView.requestFocus();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "user login complete");
            }
        });
    }

    @OnClick({R.id.btn_login, R.id.iv_username, R.id.iv_password})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                attemptLogin();
                break;
            case R.id.iv_username:
                mUserNameEditView.setText("");
                break;
            case R.id.iv_password:
                mPasswordEditView.setText("");
                break;
            default:
                break;
        }
    }


    @Override
    protected int getContentResourceId() {
        return R.layout.activity_login;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxJavaUtils.dispose(authDisposableList);
    }
}