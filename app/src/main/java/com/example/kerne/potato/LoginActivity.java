package com.example.kerne.potato;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kerne.potato.Util.UserRole;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.kerne.potato.Util.ChangeStatusBar.setStatusBarColor;
import static com.example.kerne.potato.Util.CustomToast.showShortToast;
import static com.example.kerne.potato.Util.ShowKeyBoard.delayShowSoftKeyBoard;

public class LoginActivity extends AppCompatActivity {

    private static final int LOGIN_OK = 0;
    public String result;
    @BindView(R.id.left_one_button)
    ImageView leftOneButton;
    @BindView(R.id.left_one_layout)
    LinearLayout leftOneLayout;
    @BindView(R.id.title_text)
    TextView titleText;
    String account_input;
    String password_input;
    String userRole;
    View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_one_layout:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private CheckBox rememberPass;
//    @SuppressLint("HandlerLeak")
//    private Handler myHandler = new Handler() {
//        @SuppressLint("SetTextI18n")
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case LOGIN_OK:
//                    finish();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.primary_background);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initToolBar();

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);

        login = (Button) findViewById(R.id.login);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            // 将账号和密码都设置到文本框中
            accountEdit.setText(pref.getString("account", ""));
            passwordEdit.setText(pref.getString("password", ""));
            rememberPass.setChecked(true);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入的账号密码
                account_input = accountEdit.getText().toString().trim();
                password_input = passwordEdit.getText().toString().trim();
                if (account_input.length() == 0 || password_input.length() == 0) {
                    showShortToast(LoginActivity.this, getString(R.string.account_password_null));
                } else {
                    //登录到后台进行验证，若验证通过则获取用户角色
                    login(account_input, password_input);
                }
            }
        });
    }

    private void initToolBar() {
        titleText.setText(getText(R.string.account_log_in));
        leftOneButton.setBackgroundResource(R.drawable.left_back);
        leftOneLayout.setBackgroundResource(R.drawable.selector_trans_button);
        leftOneLayout.setOnClickListener(toolBarOnClickListener);
    }


    public void login(final String name, final String pwd) {
        new Thread(new Runnable() {//开启线程
            @Override
            public void run() {
                Looper.prepare();
                RequestBody body = new FormEncodingBuilder()
                        .add("name", name)   //提交参数电话和密码
                        .add("password", pwd)
                        .build();
                Request request = new Request.Builder()
                        .url("http://120.78.130.251:9527/userV2/login")  //请求的地址
                        .post(body)
                        .build();
                OkHttpClient client = new OkHttpClient();

                try {
                    Response response = client.newCall(request).execute();
                    result = response.body().string();           //获得值
                    JX(result);    //解析

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }

    private void JX(String body) {
        try {
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.getBoolean("success")) {
                userRole = jsonObject.getJSONObject("data").getString("role");

                editor = pref.edit();
                if (rememberPass.isChecked()) { // 检查复选框是否被选中
                    editor.putBoolean("remember_password", true);
                    editor.putString("account", account_input);
                    editor.putString("password", password_input);
                } else {
                    editor.clear();
                }
                editor.apply();
                Intent intent = new Intent();
                UserRole.setUserRole(userRole);
//                intent.putExtra("userRole", userRole);
                setResult(RESULT_OK, intent);

                showShortToast(LoginActivity.this, getString(R.string.log_in_success));
                finish();

            } else {
                showShortToast(LoginActivity.this, getString(R.string.account_password_error));
//                is = jsonObject.getString("description");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
