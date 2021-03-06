package com.ycb.mobliesafe.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.ycb.mobliesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class CallSafeService extends Service {

    private BlackNumberDao dao;

    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);
        //获取到系统的电话服务
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyPhoneStateListener listener = new MyPhoneStateListener();
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
        //初始化短信的广播
        InnerReceiver innerReceiver = new InnerReceiver();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(innerReceiver, intentFilter);
    }
    private  class MyPhoneStateListener extends PhoneStateListener{
        //电话状态改变的监听
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
//             * @see TelephonyManager#CALL_STATE_IDLE  电话闲置
//            * @see TelephonyManager#CALL_STATE_RINGING 电话铃响的状态
//            * @see TelephonyManager#CALL_STATE_OFFHOOK 电话接通
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    String mode = dao.findNumber(incomingNumber);
                    /**
                     * 黑名单拦截模式
                     * 1 全部拦截 电话拦截 + 短信拦截
                     * 2 电话拦截
                     * 3 短信拦截
                     */
                    if(mode.equals("1")||mode.equals("2")){
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler(),incomingNumber));
                        endCall();
                    }
                    break;
            }
        }
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        try {
            //通过类加载器加载ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射得到当前的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            for (Object object : objects) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                // 短信来源号码
                 String originatingAddress = message.getOriginatingAddress();
                // 短信内容
                String messageBody = message.getMessageBody();
                //通过短信的电话号码查询
                String mode = dao.findNumber(originatingAddress);
                /**
                 * 黑名单拦截模式
                 * 1 全部拦截 电话拦截 + 短信拦截
                 * 2 电话拦截
                 * 3 短信拦截
                 */
                if(mode.equals("1")){
                    abortBroadcast();
                }else if (mode.equals("3")){
                    abortBroadcast();
                }
                //智能拦截模式  拦截发票  你的头发漂亮也会被拦截  要分词
                if (messageBody.contains("where")){
                    abortBroadcast();
                }
            }
        }
    }

    private class MyContentObserver extends ContentObserver {
        String incomingNumber;
        //当数据改变的时候调用的方法
        @Override
        public void onChange(boolean selfChange) {
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber);
            super.onChange(selfChange);
        }

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler,String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }
    }

    /**
     * 删除电话记录中拦截的电话号码
     * @param incomingNumber
     */
    private void deleteCallLog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri,"number=?",new String[]{incomingNumber});
    }
}