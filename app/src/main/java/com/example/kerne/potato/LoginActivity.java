package com.example.kerne.potato;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private EditText accountEdit;

    private EditText passwordEdit;

    private Button login;

    private CheckBox rememberPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LoginActivity", "hello");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        login = (Button) findViewById(R.id.login);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            // 将账号和密码都设置到文本框中
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入的账号密码
                String account_input = accountEdit.getText().toString().trim();
                String password_input = passwordEdit.getText().toString().trim();
                //登录到后台进行验证，若验证通过则获取用户角色
                String account_remote = "admin";
                String password_remote = "admin";
                boolean isAuthorizedUser = account_input.equals(account_remote) && password_input.equals(password_remote);
                String userRole = "farmer";
//                String userRole = "admin";
//                String userRole = "experimenter";
                Log.d("LoginActivity", String.valueOf(isAuthorizedUser));
                //记住密码
                if (isAuthorizedUser) {
                    editor = pref.edit();
                    if (rememberPass.isChecked()) { // 检查复选框是否被选中
                        editor.putBoolean("remember_password", true);
                        editor.putString("account_input", account_input);
                        editor.putString("password_input", password_input);
                    } else {
                        editor.clear();
                    }
                    editor.apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userRole", userRole);
                    startActivity(intent);
//                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "账号或密码无效",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
