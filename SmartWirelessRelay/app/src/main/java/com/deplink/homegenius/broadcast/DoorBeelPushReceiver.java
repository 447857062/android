package com.deplink.homegenius.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.manager.device.doorbeel.DoorbeelManager;
import com.google.gson.Gson;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ${kelijun} on 2018/1/30.
 */
public class DoorBeelPushReceiver extends XGPushBaseReceiver {
    public static final String LogTag = "TPushReceiver";

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        Log.i("DoorBeelPushReceiver", "注册结果" + xgPushRegisterResult.getAccount());
    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.i("DoorBeelPushReceiver", "推送的消息" + xgPushTextMessage.toString());
    }

    private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult message) {
        Log.i("DoorBeelPushReceiver", "消息点击" + message.toString());
        if (context == null || message == null) {
            return;
        }
        String text = "";
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + message;
            if (pushMessage != null) {
                if (pushMessage.getBell_uid() == null) {
                    return;
                }
                Log.i("DoorBeelPushReceiver", "发送广播");
                SmartDev dbSmartDev = DataSupport.where("Uid = ?", pushMessage.getBell_uid()).findFirst(SmartDev.class);
                DoorbeelManager.getInstance().setCurrentSelectedDoorbeel(dbSmartDev);
                Bundle bundle = new Bundle();
                bundle.putParcelable("message", pushMessage);
                intent.putExtra("message", bundle);
                context.sendBroadcast(intent);
            }
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + message;
        }
    }

    private PushMessage pushMessage;

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult notifiShowedRlt) {
        if (context == null || notifiShowedRlt == null) {
            return;
        }
        Log.i("DoorBeelPushReceiver", "推送的消息" + notifiShowedRlt.toString());
        Gson gson = new Gson();
        pushMessage = gson.fromJson(notifiShowedRlt.getContent(), PushMessage.class);
        XGNotification notific = new XGNotification();
        notific.setMsg_id(notifiShowedRlt.getMsgId());
        notific.setTitle(notifiShowedRlt.getTitle());
        notific.setContent(notifiShowedRlt.getContent());
        // notificationActionType==1为Activity，2为url，3为intent
        notific.setNotificationActionType(notifiShowedRlt
                .getNotificationActionType());
        // Activity,url,intent都可以通过getActivity()获得
        notific.setActivity(notifiShowedRlt.getActivity());
        notific.setUpdate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance().getTime()));
        NotificationService.getInstance(context).save(notific);
        if (pushMessage != null) {
            if (pushMessage.getBell_uid() == null) {
                return;
            }
            SmartDev dbSmartDev = DataSupport.where("Uid = ?", pushMessage.getBell_uid()).findFirst(SmartDev.class,true);
            DoorbeelManager.getInstance().setCurrentSelectedDoorbeel(dbSmartDev);
            Bundle bundle = new Bundle();
            bundle.putParcelable("message", pushMessage);
            intent.putExtra("message", bundle);
            context.sendBroadcast(intent);
        }
        //   show(context, "您有1条新消息, " + "通知被展示 ， " + notifiShowedRlt.toString());
    }
}
