package com.example.kerne.potato;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.left_one_button)
    ImageView leftOneButton;
    @BindView(R.id.left_one_layout)
    LinearLayout leftOneLayout;
    @BindView(R.id.title_text)
    TextView titleText;
    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private EditText accountEdit;

    private EditText passwordEdit;

    private Button login;

    private CheckBox rememberPass;

    String account_input;
    String password_input;
    String userRole;

    public String result;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LoginActivity", "hello");
        super.onCreate(savedInstanceState);

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
            Log.d("Login_isremember", isRemember ? "true" : "false");
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入的账号密码
                account_input = accountEdit.getText().toString().trim();
                password_input = passwordEdit.getText().toString().trim();
                //登录到后台进行验证，若验证通过则获取用户角色
                login(account_input, password_input);
//                JSONObject jsonObject = HttpRequest.HttpLogin(LoginActivity.this, account_input, password_input);
                Log.d("Login1", "after okHttp is invoked");

//                String account_remote = "admin";
//                String password_remote = "admin";
//                boolean isAuthorizedUser = account_input.equals(account_remote) && password_input.equals(password_remote);
//                String userRole = "farmer";
//                String userRole = "admin";
//                String userRole = "experimenter";
//                Log.d("LoginActivity", String.valueOf(isAuthorizedUser));
                //记住密码
//                if (isAuthorizedUser) {
//                    editor = pref.edit();
//                    if (rememberPass.isChecked()) { // 检查复选框是否被选中
//                        editor.putBoolean("remember_password", true);
//                        editor.putString("account_input", account_input);
//                        editor.putString("password_input", password_input);
//                    } else {
//                        editor.clear();
//                    }
//                    editor.apply();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtra("userRole", userRole);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Toast.makeText(LoginActivity.this, "账号或密码无效",
//                            Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    private void initToolBar() {
        titleText.setText("账号登录");
        leftOneButton.setBackgroundResource(R.drawable.left_back);

        leftOneLayout.setOnClickListener(toolBarOnClickListener);
    }


    public void login(final String name, final String pwd) {
        new Thread(new Runnable() {//开启线程
            @Override
            public void run() {
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
                    Log.d("Login2", result.toString());
                    JX(result);    //解析

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void JX(String body) {
        try {
            JSONObject jsonObject = new JSONObject(body);
            if (jsonObject.getBoolean("success")) {
                Log.d("Login3", body.toString());
                userRole = jsonObject.getJSONObject("data").getString("role");
                Log.d("Login_userRole", userRole);

                editor = pref.edit();
                if (rememberPass.isChecked()) { // 检查复选框是否被选中
                    editor.putBoolean("remember_password", true);
                    editor.putString("account", account_input);
                    editor.putString("password", password_input);
                    Log.d("Login_pref_editor", "checked");
                } else {
                    editor.clear();
                    Log.d("Login_pref_editor", "not checked");
                }
                editor.apply();
                Log.d("Login_pre_test", pref.getString("account", ""));
                Log.d("Login_pre_test", pref.getString("account", ""));
                Intent intent = new Intent();
                UserRole.setUserRole(userRole);
//                intent.putExtra("userRole", userRole);
                setResult(RESULT_OK, intent);
                finish();

            } else {
                Log.d("Login4", body.toString());
                //Looper解决闪退bug
                Looper.prepare();
                Toast.makeText(LoginActivity.this, "账号或密码无效",
                        Toast.LENGTH_SHORT).show();
                Looper.loop();
//                is = jsonObject.getString("description");
            }
//            Message message = new Message();
//            message.what = 1;
//            handler.sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

//    private Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//
//                case 1:
//                    Toast.makeText(MainActivity.this, is,Toast.LENGTH_LONG).show();
//                    break;
//            }
//        }
//    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            default:
        }
        return true;
    }


}
