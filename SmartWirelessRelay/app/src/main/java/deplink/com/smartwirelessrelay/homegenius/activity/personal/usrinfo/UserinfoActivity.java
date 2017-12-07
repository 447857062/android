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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.util.bitmap.BitmapHandler;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.UserImagePickerDialog;
import deplink.com.smartwirelessrelay.homegenius.view.imageview.CircleImageView;
import deplink.com.smartwirelessrelay.homegenius.view.viewselector.SexSelector;
import deplink.com.smartwirelessrelay.homegenius.view.viewselector.TimeSelector;

public class UserinfoActivity extends Activity implements View.OnClickListener {
    private TextView textview_title;
    private FrameLayout image_back;
    private RelativeLayout layout_user_header_image;
    private RelativeLayout layout_update_user_nickname;
    private RelativeLayout layout_update_sex;
    private RelativeLayout layout_birthday;
    private CircleImageView user_head_portrait;
    private TextView textview_show_birthday;
    private TextView textview_show_sex;
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
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (FrameLayout) findViewById(R.id.image_back);
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
            case R.id.imageview_back:
                onBackPressed();
                break;
            case R.id.layout_update_user_nickname:
                startActivity(new Intent(this, UpdateNicknameActivity.class));
                break;
            case R.id.layout_update_sex:
                //TODO
                SexSelector sexSelector=new SexSelector(this, new SexSelector.ResultHandler() {
                    @Override
                    public void handle(String sex) {
                        textview_show_sex.setText(sex);
                    }
                });
                sexSelector.show();

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
                        //TODO 上传照片
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
                    Bitmap photoBmp;
                    if (imageUri != null) {
                        try {
                            photoBmp = BitmapHandler.getBitmapFormUri(UserinfoActivity.this, imageUri);
                            user_head_portrait.setImageBitmap(photoBmp);
                        } catch (IOException ignored) {

                        }
                    }
                    String imagePath = getRealPathFromURI(imageUri);
                    //TODO 上传照片
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
