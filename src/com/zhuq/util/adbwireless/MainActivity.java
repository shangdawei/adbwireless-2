package com.zhuq.util.adbwireless;

import com.zhuq.util.adbwireless.ShellUtils.CommandResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("NewApi") public class MainActivity extends Activity {

	private static final String STR_IP = "设备IP:";
	private static final String STR_STATE = "ADB无线调试状态:";
	private static final String STR_SWITCH_ON = "关闭ADB无线调试";
	private static final String STR_SWITCH_OFF = "打开ADB无线调试";
	private static final String STR_TIS = "请在PC的终端输入： \n adb connect ";
	
	private static final int ON = 0;
	private static final int OFF = 1;
	
	private String[] command_adb_start = new String[] { 
			"setprop service.adb.tcp.port 5556", "stop adbd", "start adbd",
			"netstat" };
	private String command_adb_stop = "setprop service.adb.tcp.port -1";
	private String command_checkadb = "getprop service.adb.tcp.port";
	
	
	private TextView ip,adbstate,tis;
	
	
	
	@SuppressLint("HandlerLeak") private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ON:
				adbstate.setText(STR_STATE+"已开启");
				Bundle data = msg.getData();
				String result = (String) data.get("result");
				String ip = getIP();
				tis.setText(STR_TIS+ip+":"+result);
				break;

			case OFF:
				adbstate.setText(STR_STATE+"已关闭");
				tis.setVisibility(View.GONE);
				break;
			}
		};
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip = (TextView) findViewById(R.id.ip);
        adbstate = (TextView) findViewById(R.id.adbstate);
        tis = (TextView) findViewById(R.id.tis);
        checkState();
    }
    
    private void checkState(){
    	new Thread(){
    		public void run() {
    			CommandResult commandResult = ShellUtils.execCommand(command_checkadb, false);
    			if(commandResult.result==0 && commandResult.successMsg!=null && !commandResult.successMsg.equals("")){
    				Message msg = new Message();
    				msg.what = ON;
    				Bundle data = new Bundle();
    				data.putString("result", commandResult.successMsg);
    				msg.setData(data);
    				handler.sendMessage(msg);
    			}else{
    				handler.sendEmptyMessage(OFF);
    			}
    		};
    	}.start();
    }
    
    public void open(View view){
    	Toast.makeText(MainActivity.this, "open",Toast.LENGTH_LONG).show();
    	new Thread(){
    		public void run() {
    			CommandResult commandResult = ShellUtils.execCommand(command_adb_start, true);
    			if(commandResult.result==0 ){
    				handler.sendEmptyMessage(ON);
    			}else{
    				
    			}
    		};
    	}.start();
    }
    
    public void close(View view){
    	Toast.makeText(MainActivity.this, "close",Toast.LENGTH_LONG).show();
    	new Thread(){
    		public void run() {
    			CommandResult commandResult = ShellUtils.execCommand(command_adb_stop, true);
    			if(commandResult.result==0){
    				handler.sendEmptyMessage(OFF);
    			}else{
    				
    			}
    		};
    	}.start();
    }
    
    public String getIP(){
    	return null;
    }

}
