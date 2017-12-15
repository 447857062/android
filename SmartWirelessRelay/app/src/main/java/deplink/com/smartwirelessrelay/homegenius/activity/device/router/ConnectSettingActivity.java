package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.json.Lan;
import com.deplink.sdk.android.sdk.json.Proto;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.SelectConnectTypeDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class ConnectSettingActivity extends Activity implements View.OnClickListener{
    private RelativeLayout layout_connect_type_setting;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private TextView textview_current_connect_type;
    private RelativeLayout layout_pppoe_account;
    private RelativeLayout layout_pppoe_password;
    private RelativeLayout layout_wan_ip_setting;
    private RelativeLayout layout_submask_setting;
    private RelativeLayout layout_gateway_setting;
    private RelativeLayout layout_dns_setting;
    private RelativeLayout layout_mtu;
    private RelativeLayout layout_mac;
    private RelativeLayout layout_wifissid;
    private RelativeLayout layout_wifipassword;
    private RelativeLayout layout_encryption;
    private RelativeLayout layout_encryption_algorithm;
    private RelativeLayout layout_channel;
    private TextView textview_pppoe_account;
    private TextView textview_pppoe_password;
    private TextView textview_wanip_value;
    private TextView textview_submask_value;
    private TextView textview_gateway_value;
    private TextView textview_dns_value;
    private TextView textview_mtu;
    private TextView textview_mac;
    private TextView textview_wifi_ssid;
    private TextView textview_wifipassword;
    private TextView textview_encryption;
    private TextView textview_encryption_algorithm;
    private TextView textview_channel;
    private SelectConnectTypeDialog selectConnectTypeDialog;
    private TextView textview_lan_ip;
    private TextView textview_netmask;
    private TextView textview_dhcp_status;
    private MakeSureDialog connectLostDialog;
    private RouterManager mRouterManager;
    private boolean isUserLogin;
    private TextView textview_title;
    private FrameLayout image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_setting);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mRouterManager.isStartFromExperience()){

        }else{
            routerDevice = (RouterDevice) manager.getDevice(mRouterManager.getRouterDeviceKey());
            manager.addEventCallback(ec);
            if (routerDevice != null) {
                routerDevice.queryWan();
                routerDevice.queryLan();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initDatas() {
        textview_title.setText("上网设置");
        mRouterManager=RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(ConnectSettingActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(ConnectSettingActivity.this, LoginActivity.class));
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
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {

            }

            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op) {
                    case RouterDevice.OP_GET_WAN:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Proto proto = null;
                                if (routerDevice != null) {
                                    proto = routerDevice.getProto();
                                }
                                String ConnectType;
                                if (proto != null) {
                                    if (proto.getAP_CLIENT() != null) {
                                        ConnectType = "中继功能";
                                        setApclientConnectTypeTextview(proto);
                                    } else if (proto.getDHCP() != null) {
                                        ConnectType = "动态IP";
                                        setDynamicConnectTypeTextview(proto);
                                    } else if (proto.getSTATIC() != null) {
                                        ConnectType = "静态IP";
                                        setStaticipConnecttypeTextview(proto);
                                    } else if (proto.getPPPOE() != null) {
                                        ConnectType = "拨号上网";
                                        setPPPOEConnectTypeTextview(proto);
                                    } else {
                                        ConnectType = "--";
                                    }
                                    textview_current_connect_type.setText(ConnectType);
                                }

                            }
                        });
                        break;
                    case  RouterDevice.OP_GET_LAN:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Lan lan=routerDevice.getLan();
                                    textview_lan_ip.setText(lan.getLANIP());
                                    textview_netmask.setText(lan.getNETMASK());
                                    if(lan.getDhcpStatus().equalsIgnoreCase("ON")){
                                        textview_dhcp_status.setText("开启");
                                    }else{
                                        textview_dhcp_status.setText("关闭");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        break;
                }
            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {

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
        selectConnectTypeDialog = new SelectConnectTypeDialog(ConnectSettingActivity.this);
    }

    /**
     * 设置拨号上网方式的信息显示
     *
     * @param proto
     */
    private void setPPPOEConnectTypeTextview(Proto proto) {
        layout_pppoe_account.setVisibility(View.VISIBLE);
        layout_pppoe_password.setVisibility(View.VISIBLE);
        layout_dns_setting.setVisibility(View.VISIBLE);
        layout_mtu.setVisibility(View.VISIBLE);
        layout_mac.setVisibility(View.VISIBLE);

        layout_wan_ip_setting.setVisibility(View.GONE);
        layout_submask_setting.setVisibility(View.GONE);
        layout_gateway_setting.setVisibility(View.GONE);
        layout_wifissid.setVisibility(View.GONE);
        layout_wifipassword.setVisibility(View.GONE);
        layout_encryption.setVisibility(View.GONE);
        layout_encryption_algorithm.setVisibility(View.GONE);
        layout_channel.setVisibility(View.GONE);
        layout_wan_ip_setting.setVisibility(View.GONE);
        layout_mac.setVisibility(View.GONE);
        textview_pppoe_account.setText("" + proto.getPPPOE().getUsername());
        textview_pppoe_password.setText("" + proto.getPPPOE().getPasswd());
        if (proto.getPPPOE().getDNS() == null) {
            textview_dns_value.setText("未设置");
        } else {
            textview_dns_value.setText("" + proto.getPPPOE().getDNS());
        }
        if (proto.getPPPOE().getMTU() == null) {
            textview_mtu.setText("未设置");
        } else {
            textview_mtu.setText("" + proto.getPPPOE().getMTU());
        }
        textview_mac.setText("" + proto.getPPPOE().getMAC());
    }

    /**
     * 设置静态ip上网方式的信息显示
     *
     * @param proto
     */
    private void setStaticipConnecttypeTextview(Proto proto) {
        layout_wan_ip_setting.setVisibility(View.VISIBLE);
        layout_submask_setting.setVisibility(View.VISIBLE);
        layout_gateway_setting.setVisibility(View.VISIBLE);
        layout_dns_setting.setVisibility(View.VISIBLE);
        layout_mtu.setVisibility(View.VISIBLE);

        layout_wifissid.setVisibility(View.GONE);
        layout_wifipassword.setVisibility(View.GONE);
        layout_encryption.setVisibility(View.GONE);
        layout_encryption_algorithm.setVisibility(View.GONE);
        layout_channel.setVisibility(View.GONE);
        layout_pppoe_account.setVisibility(View.GONE);
        layout_pppoe_password.setVisibility(View.GONE);
        layout_wan_ip_setting.setVisibility(View.GONE);
        layout_mac.setVisibility(View.GONE);

        textview_wanip_value.setText("" + proto.getSTATIC().getIPADDR());

        textview_gateway_value.setText("" + proto.getSTATIC().getGATEWAY());
        if (proto.getSTATIC().getDNS() == null) {
            textview_dns_value.setText("未设置");
        } else {
            textview_dns_value.setText("" + proto.getSTATIC().getDNS());
        }
        if (proto.getSTATIC().getMTU() == null) {
            textview_mtu.setText("未设置");
        } else {
            textview_mtu.setText("" + proto.getSTATIC().getMTU());
        }
        if (proto.getSTATIC().getNETMASK() == null) {
            textview_submask_value.setText("未设置");
        } else {
            textview_submask_value.setText("" + proto.getSTATIC().getNETMASK());
        }
    }

    /**
     * 设置动态ip上网方式 的信息显示
     *
     * @param proto
     */
    private void setDynamicConnectTypeTextview(Proto proto) {
        layout_dns_setting.setVisibility(View.VISIBLE);
        layout_mtu.setVisibility(View.VISIBLE);
        layout_wifissid.setVisibility(View.GONE);
        layout_wifipassword.setVisibility(View.GONE);
        layout_encryption.setVisibility(View.GONE);
        layout_encryption_algorithm.setVisibility(View.GONE);
        layout_channel.setVisibility(View.GONE);
        layout_pppoe_account.setVisibility(View.GONE);
        layout_pppoe_password.setVisibility(View.GONE);
        layout_wan_ip_setting.setVisibility(View.GONE);
        layout_submask_setting.setVisibility(View.GONE);
        layout_gateway_setting.setVisibility(View.GONE);
        layout_mac.setVisibility(View.GONE);
        if (proto.getDHCP().getDNS() == null) {
            textview_dns_value.setText("未设置");
        } else {
            textview_dns_value.setText("" + proto.getDHCP().getDNS());
        }
        if (proto.getDHCP().getMTU() == null) {
            textview_mtu.setText("未设置");
        } else {
            textview_mtu.setText("" + proto.getDHCP().getMTU());
        }
    }

    /**
     * 设置中继上网的信息显示
     *
     * @param proto
     */
    private void setApclientConnectTypeTextview(Proto proto) {
        layout_wifissid.setVisibility(View.VISIBLE);
        layout_wifipassword.setVisibility(View.VISIBLE);
        layout_encryption.setVisibility(View.VISIBLE);
        layout_encryption_algorithm.setVisibility(View.VISIBLE);
        layout_channel.setVisibility(View.VISIBLE);
        layout_pppoe_account.setVisibility(View.GONE);
        layout_pppoe_password.setVisibility(View.GONE);
        layout_wan_ip_setting.setVisibility(View.GONE);
        layout_submask_setting.setVisibility(View.GONE);
        layout_gateway_setting.setVisibility(View.GONE);
        layout_dns_setting.setVisibility(View.GONE);
        layout_mtu.setVisibility(View.GONE);
        layout_mac.setVisibility(View.GONE);
        textview_wifi_ssid.setText("" + proto.getAP_CLIENT().getApCliSsid());
        textview_wifipassword.setText("" + proto.getAP_CLIENT().getApCliWPAPSK());
        textview_encryption.setText("" + proto.getAP_CLIENT().getApCliAuthMode());
        textview_encryption_algorithm.setText("" + proto.getAP_CLIENT().getApCliEncrypType());
        textview_channel.setText("" + proto.getAP_CLIENT().getChannel());
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        layout_connect_type_setting.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (FrameLayout) findViewById(R.id.image_back);
        layout_connect_type_setting = (RelativeLayout) findViewById(R.id.layout_connect_type_setting);
        textview_current_connect_type = (TextView) findViewById(R.id.textview_current_connect_type);

        textview_pppoe_account = (TextView) findViewById(R.id.textview_pppoe_account);
        textview_pppoe_password = (TextView) findViewById(R.id.textview_pppoe_password);
        textview_wanip_value = (TextView) findViewById(R.id.textview_wanip_value);
        textview_submask_value = (TextView) findViewById(R.id.textview_submask_value);
        textview_gateway_value = (TextView) findViewById(R.id.textview_gateway_value);
        textview_dns_value = (TextView) findViewById(R.id.textview_dns_value);
        textview_mtu = (TextView) findViewById(R.id.textview_mtu);
        textview_mac = (TextView) findViewById(R.id.textview_mac);
        textview_wifi_ssid = (TextView) findViewById(R.id.textview_wifi_ssid);
        textview_wifipassword = (TextView) findViewById(R.id.textview_wifipassword);
        textview_encryption = (TextView) findViewById(R.id.textview_encryption);
        textview_encryption_algorithm = (TextView) findViewById(R.id.textview_encryption_algorithm);
        textview_channel = (TextView) findViewById(R.id.textview_channel);

        layout_pppoe_account = (RelativeLayout) findViewById(R.id.layout_pppoe_account);
        layout_pppoe_password = (RelativeLayout) findViewById(R.id.layout_pppoe_password);
        layout_wan_ip_setting = (RelativeLayout) findViewById(R.id.layout_wan_ip_setting);
        layout_submask_setting = (RelativeLayout) findViewById(R.id.layout_submask_setting);
        layout_gateway_setting = (RelativeLayout) findViewById(R.id.layout_gateway_setting);
        layout_dns_setting = (RelativeLayout) findViewById(R.id.layout_dns_setting);
        layout_mtu = (RelativeLayout) findViewById(R.id.layout_mtu);
        layout_mac = (RelativeLayout) findViewById(R.id.layout_mac);
        layout_wifissid = (RelativeLayout) findViewById(R.id.layout_wifissid);
        layout_wifipassword = (RelativeLayout) findViewById(R.id.layout_wifipassword);
        layout_encryption = (RelativeLayout) findViewById(R.id.layout_encryption);
        layout_encryption_algorithm = (RelativeLayout) findViewById(R.id.layout_encryption_algorithm);
        layout_channel = (RelativeLayout) findViewById(R.id.layout_channel);

        textview_lan_ip = (TextView) findViewById(R.id.textview_lan_ip);
        textview_netmask = (TextView) findViewById(R.id.textview_netmask);
        textview_dhcp_status = (TextView) findViewById(R.id.textview_dhcp_status);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
               onBackPressed();
                break;
            case R.id.layout_connect_type_setting:
                isUserLogin= Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
                if(isUserLogin){
                    selectConnectTypeDialog.show();
                }else{
                    ToastSingleShow.showText(this,"未登录，无法设置上网方式,请登录后重试");
                }

                break;
        }
    }
}
