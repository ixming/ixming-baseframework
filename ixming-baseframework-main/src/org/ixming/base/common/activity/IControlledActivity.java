package org.ixming.base.common.activity;

import android.app.Activity;
import android.content.Intent;

/**
 * 定义被ActivityControl管理的Activity
 * 
 * @author Yin Yong
 *
 */
interface IControlledActivity {

	public void startActivity(Class<? extends Activity> clz);
	
	public void startActivity(Class<? extends Activity> clz, int flags);
	
	public void startActivityForResult(Class<? extends Activity> clz,
			int requestCode) ;
	
	public void finish();
	
	public void superStartActivity(Intent intent);
	
	public void superStartActivityForResult(Intent intent, int requestCode);
	
	public void superFinish();
}
