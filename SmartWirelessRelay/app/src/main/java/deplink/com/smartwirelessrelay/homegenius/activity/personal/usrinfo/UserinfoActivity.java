package deplink.com.smartwirelessrelay.homegenius.activity.personal.usrinfo;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;
import com.zxy.tiny.core.FileKit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.util.bitmap.BitmapHandler;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.SexSelectDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.UserImagePickerDialog;
import deplink.com.smartwirelessrelay.homegenius.view.imageview.CircleImageView;
import deplink.com.smartwirelessrelay.homegenius.view.viewselector.TimeSelector;

public class UserinfoActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "UserinfoActivity";
    private TextView textview_title;
    private FrameLayout image_back;
    private RelativeLayout layout_user_header_image;
    private RelativeLayout layout_update_user_nickname;
    private RelativeLayout layout_update_sex;
    private RelativeLayout layout_birthday;
    private CircleImageView user_head_portrait;
    private TextView textview_show_birthday;
    private TextView textview_show_sex;
    private TextView textview_show_nicknamke;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;
    private SexSelectDialog mSexDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("个人信息");
        mSexDialog = new SexSelectDialog(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(UserinfoActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(UserinfoActivity.this, LoginActivity.class));
            }
        });
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {

            @Override
            public void onSuccess(SDKAction action) {
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {


            }

            @Override
            public void onGetImageSuccess(SDKAction action, final Bitmap bm) {

                //保存到本地
                try {
                    user_head_portrait.setImageBitmap(bm);
                    saveToSDCard(bm);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
                switch (action) {
                    case UPLOADIMAGE:
                        // ToastSingleShow.showText(UserInfoActivity.this,"上传头像失败");
                        break;
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }
        };
    }

    private void saveToSDCard(Bitmap bitmap) {
        String path = this.getFilesDir().getAbsolutePath();
        path = path + File.separator + "userIcon" + "userIcon.png";
        File dest = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_show_nicknamke = (TextView) findViewById(R.id.textview_show_nicknamke);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        layout_user_header_image = (RelativeLayout) findViewById(R.id.layout_user_header_image);
        layout_update_user_nickname = (RelativeLayout) findViewById(R.id.layout_update_user_nickname);
        layout_update_sex = (RelativeLayout) findViewById(R.id.layout_update_sex);
        layout_birthday = (RelativeLayout) findViewById(R.id.layout_birthday);
        user_head_portrait = (CircleImageView) findViewById(R.id.user_head_portrait);
        textview_show_birthday = (TextView) findViewById(R.id.textview_show_birthday);
        textview_show_sex = (TextView) findViewById(R.id.textview_show_sex);

    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        layout_user_header_image.setOnClickListener(this);
        layout_update_user_nickname.setOnClickListener(this);
        layout_update_sex.setOnClickListener(this);
        layout_birthday.setOnClickListener(this);
    }

    /**
     * 拍照选择图片
     */
    private void chooseFromCamera() {
        //构建隐式Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //调用系统相机
        startActivityForResult(intent, CAMERA_CODE);
    }

    private void showImagePopup() {
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Choose image");
        startActivityForResult(chooserIntent, 100);
    }

    /**
     * 拍照选取图片
     */
    private static final int CAMERA_CODE = 1;
    private static final int CROP_CODE = 3;
    private Handler mHandler = new Handler();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.layout_update_user_nickname:
                Intent intent = new Intent(this, UpdateNicknameActivity.class);
                intent.putExtra("nickname", textview_show_nicknamke.getText().toString());
                startActivity(intent);
                break;
            case R.id.layout_update_sex:
                mSexDialog.setmOnSexSelectClickListener(new SexSelectDialog.onSexSelectClickListener() {
                    @Override
                    public void onSexSelect(String selectMode) {
                        textview_show_sex.setText(selectMode);
                    }
                });
                mSexDialog.show();


                break;
            case R.id.layout_birthday:
                TimeSelector timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
                    @Override
                    public void handle(String time, Calendar selectedCalendar) {
                        //TODO 如果需要上传到服务器需要添加下面这句
                        // SelectedTime = selectedCalendar.getTimeInMillis() / 1000;
                        textview_show_birthday.setText(time);
                    }
                }, "1990-11-22 17:34", "2100-12-1 15:20");
                timeSelector.show();

                break;
            case R.id.layout_user_header_image:
                UserImagePickerDialog dialog = new UserImagePickerDialog(this);
                dialog.setOnFromCameraClickListener(new UserImagePickerDialog.onFromCameraClickListener() {
                    @Override
                    public void onFromCameraClicked() {
                        //拍照选择
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                chooseFromCamera();
                            }
                        });

                    }
                });
                dialog.setOnFromGalaryClickListener(new UserImagePickerDialog.onFromGalaryClickListener() {
                    @Override
                    public void onFromGalaryClicked() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showImagePopup();
                            }
                        });

                    }
                });
                dialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CROP_CODE:
                    isSelectPhoto = true;
                    if (data == null) {
                        return;
                    } else {
                        Bundle extras = data.getExtras();

                        if (extras != null) {
                            //获取到裁剪后的图像
                            Bitmap bm = extras.getParcelable("data");
                            File file = new File(path);
                            if (file.exists()) {
                                bm = BitmapFactory.decodeFile(file.getPath());
                                // 将图片显示到ImageView中
                                user_head_portrait.setImageBitmap(bm);
                            } else {
                                user_head_portrait.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                            }
                            user_head_portrait.setImageBitmap(bm);
                        }
                        manager.uploadImage(path);
                    }
                    break;
                case CAMERA_CODE:
                    isSelectPhoto = true;
                    //用户点击了取消
                    if (data == null) {
                        return;
                    } else {

                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            //获得拍的照片
                            Bitmap bm = extras.getParcelable("data");
                            //将Bitmap转化为uri
                            //user_head_portrait.setImageBitmap(bm);
                            Uri uri = saveBitmap(bm, "temp");
                            //启动图像裁剪
                            startImageZoom(uri, bm);
                        }
                    }
                    break;
                case 100:
                    isSelectPhoto = true;
                    if (data == null) {
                        Toast.makeText(getApplicationContext(), "Unable to pick image", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Uri imageUri = data.getData();
                    // user_head_portrait.setImageURI(imageUri);
                    Bitmap photoBmp = null;
                    if (imageUri != null) {
                        try {
                            photoBmp = BitmapHandler.getBitmapFormUri(UserinfoActivity.this, imageUri);
                            user_head_portrait.setImageBitmap(photoBmp);
                        } catch (IOException ignored) {

                        }
                    }
                    final String imagePath = getRealPathFromURI(imageUri);
                    saveToSDCard(photoBmp);
                    //  String imagePathCompress = imagePath + "compressPic.jpg";
                    //使用tiny框架压缩图片
                    Tiny.getInstance().init(UserinfoActivity.this.getApplication());
                    Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                    options.outfile = FileKit.getDefaultFileCompressDirectory() + "/tiny-useriamge.jpg";
                    Log.i(TAG, "options.outfile=" + options.outfile);
                    Tiny.getInstance().source(imagePath).asFile().withOptions(options).compress(new FileCallback() {
                        @Override
                        public void callback(boolean isSuccess, String outfile, Throwable t) {
                            //return the compressed file path
                            Log.i(TAG, "imagePath=" + imagePath + "   outfile=" + outfile);
                            manager.uploadImage(outfile);
                        }
                    });
                    break;

            }
        }

    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    /**
     * 通过Uri传递图像信息以供裁剪
     *
     * @param uri
     */
    private void startImageZoom(Uri uri, Bitmap bm) {
        //构建隐式Intent来启动裁剪程序
        Intent intent = new Intent("com.android.camera.action.CROP");
        //设置数据uri和类型为图片类型
        intent.setDataAndType(uri, "image/*");
        //显示View为可裁剪的
        intent.putExtra("crop", true);
        //裁剪的宽高的比例为1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", false);
        //输出图片的宽高均为150
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        //裁剪之后的数据是通过Intent返回
        intent.putExtra("return-data", true);
        intent.putExtra("uri", uri);
        startActivityForResult(intent, CROP_CODE);
    }

    private boolean isSelectPhoto = false;
    private String path = "";

    /**
     * 将Bitmap写入SD卡中的一个文件中,并返回写入文件的Uri
     *
     * @param bm
     * @param dirPath
     * @return
     */
    private Uri saveBitmap(Bitmap bm, String dirPath) {
        //新建文件夹用于存放裁剪后的图片
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/" + dirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }

        //新建文件存储裁剪后的图片
        File img = new File(tmpDir.getAbsolutePath() + "/avator.png");
        path = img.getPath();
        try {
            //打开文件输出流
            FileOutputStream fos = new FileOutputStream(img);
            //将bitmap压缩后写入输出流(参数依次为图片格式、图片质量和输出流)
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            //刷新输出流
            fos.flush();
            //关闭输出流
            fos.close();
            //返回File类型的Uri

            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
