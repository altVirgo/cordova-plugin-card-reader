package com.plugin.cardreader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.corewise.idcardreadersdk.BaseNFCActivity;
import com.corewise.idcardreadersdk.Bean.IdCardInfo;
import com.corewise.idcardreadersdk.Interface.ReadListener;
import com.corewise.idcardreadersdk.NFC.NFCConn;
import com.corewise.idcardreadersdk.ReadAPI;
import com.corewise.idcardreadersdk.Interface.Conn;

import android.content.res.Resources;
import android.os.Handler;
import java.util.TimerTask;

import android.support.v4.content.LocalBroadcastManager;
public class CardActivity extends BaseNFCActivity implements ReadListener {

    private String m_RootPath;
    private int success = 0;
    private int fail = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRootPath(); //m_RootPath 其实用不到
        Conn conn= new NFCConn();
        Resources resources = getResources();

        new Handler().postDelayed(new Runnable(){
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("errorInfo", "您已超时,请重新扫描身份证...");
                setResult( RESULT_CANCELED, intent);
                finish();
            }
        }, 10000);
        // 读取res/raw下的license文件
        api = new ReadAPI(getApplicationContext(),
                resources.getIdentifier("base", "raw", this.getPackageName()),
                resources.getIdentifier("license", "raw", this.getPackageName()), m_RootPath, this, conn);
        String isRegister =getIntent().getStringExtra("com.plugin.cardreader.isRegister");
        if(!isRegister.equals("true")){
            api.registerDevice(getApplicationContext(),"BWJDYCUZDOUKPKL6");
        }

    }



    private void setRootPath() {
        PackageManager packageManager = this.getPackageManager();

        try {
            PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);
            this.m_RootPath = info.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void  start(){
        Intent intent = new Intent("com.plugin.cardreader");
        intent.putExtra("startFlag",true);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcastSync(intent);
    }

    @Override
    public void error(final int errorCode) {
        String errorInfo="";
        switch (errorCode) {
            case ReadListener.RET_DECODE_FAIL:
                errorInfo="解析身份证失败";
                break;
            case ReadListener.RET_GET_UID_FAIL:
                errorInfo="获取uid失败";
                break;
            case ReadListener.RET_RF_CONNECT_FAIL:
                errorInfo="射频连接失败";
                break;
            case ReadListener.RET_CALLBACK_SENDDATA_ERROR:
                errorInfo="射频交互失败";
                break;
            case ReadListener.RET_READ_PACKAGE_RESPONSE_ERROR1:
                errorInfo="和业务服务器交互出错";
                break;
            case ReadListener.RET_ILLEGAL_DEVICE:
                errorInfo="非法设备";
                break;
            case ReadListener.RET_LICENSE_OVERDUE:
                errorInfo="授权过期";
                break;
            case ReadListener.RET_NO_SERVER:
                errorInfo="没有加密模块";
                break;
            case ReadListener.RET_UNKNOW_ERROR:
                errorInfo="未知错误";
                break;
            case ReadListener.RET_READ_IMEI_ERROR:
                errorInfo="读取设备的imei失败";
                break;
            case ReadListener.RET_READ_WIFI_MAC_ERROR:
                errorInfo="读取设备mac失败";
                break;
            case ReadListener.RET_NET_ERROR:
                errorInfo="请求后台接口失败";
                break;
            case ReadListener.RET_RESPONSE_JSON_ERROR:
                errorInfo="后台返回数据非json格式";
                break;
            case ReadListener.RET_RESPONSE_PARAM_ERROR:
                errorInfo="后台返回数据缺少字段";
                break;
            case ReadListener.RET_REGIST_SYSTEM_ERROR:
                errorInfo="后台返回数据code为sys-err";
                break;
            case ReadListener.RET_REGIST_CODE_ERROR1:
                errorInfo="后台返回数据code为code-err1";
                break;
            case ReadListener.RET_REGIST_CODE_ERROR2:
                errorInfo="后台返回数据code为code-err2";
                break;
            case ReadListener.RET_REGIST_PARAM_ERROR:
                errorInfo="后台返回数据code为param-err";
                break;
            case ReadListener.RET_REGIST_DEVICE_ERROR:
                errorInfo="后台返回数据code为device-err";
                break;
            case ReadListener.RET_REGIST_DEVICE_EXISTS:
                errorInfo="后台返回数据code为device-exists";
                break;
            case ReadListener.RET_SAVE_LICENSE_ERROR:
                errorInfo="交互成功后license文件中的count字段加1时保存文件错误";
                break;
            case ReadListener.RET_CHECK_LICENSE_ERROR:
                errorInfo="保存的license文件中的imei和mac和读取的本机imei，mac不一致";
                break;
            case ReadListener.RET_CALLBACK_GETKEY_ERROR:
                errorInfo="找不到getKey方法，或getKey方法返回null";
                break;
            case ReadListener.RET_SOCKET_CONNECT_ERROR:
                errorInfo="与业务服务器连接失败";
                break;
            case ReadListener.RET_SOCKET_SENDDATA_ERROR:
                errorInfo= "向业务服务器发送数据失败";
                break;
            case ReadListener.RET_SOCKET_TIMEOUT_ERROR:
                errorInfo= "接收业务服务器数据时超时1s未收到数据";
                break;
            case ReadListener.RET_SOCKET_DISCONNECT_ERROR:
                errorInfo="接收业务服务器数据时断开连接";
                break;
            case ReadListener.RET_READ_REGIST_ERROR:
                errorInfo="向业务服务器注册失败";
                break;
            case ReadListener.RET_CALLBACK_IDCONTENT_ERROR:
                errorInfo="找不到IDContent方法";
                break;
            case ReadListener.RET_CALLBACK_READSUCCESS_ERROR:
                errorInfo="找不到readSuccess方法";
                break;
            default:
                break;
        }
        Intent intent = new Intent();
        intent.putExtra("errorInfo", errorInfo);
        setResult( RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void registerSuccess() {
        Toast.makeText(getApplicationContext(), "激活成功！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void readSuccess(final IdCardInfo idCardInfo) {
        Intent intent = new Intent();
        intent.putExtra("userName", idCardInfo.getName());
        intent.putExtra("userIdNo", idCardInfo.getIdNum());
        intent.putExtra("userSex", idCardInfo.getSex());
        intent.putExtra("userNation", idCardInfo.getNation());
        intent.putExtra("userBirthday", idCardInfo.getBirthday());
        intent.putExtra("userAddress", idCardInfo.getAddress());
        intent.putExtra("userEndDate", idCardInfo.getEndDate());
        intent.putExtra("userStartDate", idCardInfo.getStartDate());
        setResult( RESULT_OK, intent);
        finish();
    }

    @Override
    public void exception(final String ex) {
        Intent intent = new Intent();
        intent.putExtra("errorInfo", ex);
        setResult( RESULT_CANCELED, intent);
        finish();
    }
}
