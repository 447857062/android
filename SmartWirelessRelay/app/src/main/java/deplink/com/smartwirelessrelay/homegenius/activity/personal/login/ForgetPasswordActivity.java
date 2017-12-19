package deplink.com.smartwirelessrelay.homegenius.activity.personal.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class ForgetPasswordActivity extends Activity implements View.OnClickListener ,OnFocusChangeListener{

    private TextView textview_title;
    private FrameLayout image_back;
    private View view_phonenumber_dirverline;
    private View view_password_dirverline;
    private View view_yanzhen_dirverline;
    private EditText edittext_input_phone_number;
    private EditText edittext_verification_code;
    private EditText edittext_input_password;
    private FrameLayout layout_eye;
    private ImageView imageview_eye;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("找回密码");
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        edittext_input_phone_number.setOnFocusChangeListener(this);
        edittext_verification_code.setOnFocusChangeListener(this);
        edittext_input_password.setOnFocusChangeListener(this);
        layout_eye.setOnClickListener(this);
    }

    private void initViews() {
        textview_title = (TextView) findViewById(R.id.textview_title);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        view_phonenumber_dirverline =findViewById(R.id.view_phonenumber_dirverline);
        view_password_dirverline =findViewById(R.id.view_password_dirverline);
        view_yanzhen_dirverline =findViewById(R.id.view_yanzhen_dirverline);
        edittext_input_phone_number = (EditText) findViewById(R.id.edittext_input_phone_number);
        edittext_verification_code = (EditText) findViewById(R.id.edittext_verification_code);
        edittext_input_password = (EditText) findViewById(R.id.edittext_input_password);
        layout_eye = (FrameLayout) findViewById(R.id.layout_eye);
        imageview_eye = (ImageView) findViewById(R.id.imageview_eye);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.layout_eye:
                changeInputCipher();
                break;

        }
    }
    /**
     * 设置密文明文之间切换
     */
    private void changeInputCipher() {
        if (edittext_input_password.getTransformationMethod() instanceof PasswordTransformationMethod) {
            imageview_eye.setImageResource(R.drawable.hideicon);
            edittext_input_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        } else if (edittext_input_password.getTransformationMethod() instanceof HideReturnsTransformationMethod) {

            imageview_eye.setImageResource(R.drawable.displayicon);
            edittext_input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        }
        int length = edittext_input_password.getText().toString().trim().length();
        if (length != 0) {
            edittext_input_password.setSelection(length);
        }
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.edittext_input_phone_number:
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    view_phonenumber_dirverline.setBackgroundResource(R.color.white);
                } else {
                    // 此处为失去焦点时的处理内容
                    view_phonenumber_dirverline.setBackgroundResource(R.color.white_alpha60);
                }
                break;
            case R.id.edittext_verification_code:
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    view_yanzhen_dirverline.setBackgroundResource(R.color.white);
                } else {
                    // 此处为失去焦点时的处理内容
                    view_yanzhen_dirverline.setBackgroundResource(R.color.white_alpha60);
                }
                break;
            case R.id.edittext_input_password:
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    view_password_dirverline.setBackgroundResource(R.color.white);
                } else {
                    // 此处为失去焦点时的处理内容
                    view_password_dirverline.setBackgroundResource(R.color.white_alpha60);
                }
                break;
        }
    }
}
