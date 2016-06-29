package com.example.targerjunit.test;

import com.example.targerjunit.MainActivity;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private Instrumentation mInstrumentation;
	private MainActivity mMainActivity;
	private static final String LOGIN_INFO = "account：phicomm password：1234";
	// 控件
	EditText etAccount, etPassword;
	Button btSubmit;
	TextView tvShow;
	// 标识
	String TAG = "huanhui";

	public MainActivityTest() {
		super(MainActivity.class);
	}

	// 重写setUp方法，在该方法中进行相关的初始化操作
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		/**
		 * 这个程序中需要输入用户信息和密码，也就是说需要发送key事件， 所以，必须在调用getActivity之前，调用下面的方法来关闭
		 * touch模式，否则key事件会被忽略
		 */
		// 关闭touch模式
		setActivityInitialTouchMode(false);
		mInstrumentation = getInstrumentation();
		// 获取被测试的MainActivity
		mMainActivity = getActivity();

		// 获取FxLoginActivity相关的UI组件
		etAccount = (EditText) mMainActivity.findViewById(com.example.targerjunit.R.id.etAccount);
		etPassword = (EditText) mMainActivity.findViewById(com.example.targerjunit.R.id.etPassword);
		btSubmit = (Button) mMainActivity.findViewById(com.example.targerjunit.R.id.btSubmit);
		tvShow = (TextView) mMainActivity.findViewById(com.example.targerjunit.R.id.tvShow);
	}

	// 该测试用例实现在测试其他用例之前，测试确保获取的组件不为空
	public void testPreConditions() {
		assertNotNull(etAccount);
		assertNotNull(etPassword);
		assertNotNull(btSubmit);
		assertNotNull(tvShow);
	}

	/**
	 * 该方法实现在登录界面上输入相关的登录信息。由于UI组件的 相关处理（如此处的请求聚焦）需要在UI线程上实现，
	 * 所以需调用Activity的runOnUiThread方法实现。
	 */
	public void input() {
		mMainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				etAccount.requestFocus();
				etAccount.performClick();
			}
		});
		/*
		 * 由于测试用例在单独的线程上执行，所以此处需要同步application，
		 * 调用waitForIdleSync等待测试线程和UI线程同步，才能进行输入操作。
		 * waitForIdleSync和sendKeys不允许在UI线程里运行
		 */
		mInstrumentation.waitForIdleSync();
		// 调用sendKeys方法，输入用户名
		sendKeys(KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_H, KeyEvent.KEYCODE_I, KeyEvent.KEYCODE_C, KeyEvent.KEYCODE_O,
				KeyEvent.KEYCODE_M, KeyEvent.KEYCODE_M);
		mMainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				etPassword.requestFocus();
				etPassword.performClick();
			}
		});
		// 调用sendKeys方法，输入密码
		sendKeys(KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_4);
	}

	// 测试输入的用户信息
	public void testInput() {
		// 调用测试类的input方法，实现输入用户信息(sendKeys实现输入)
		input();
		Log.i(TAG, "测试输入的用户信息 " + etAccount.getText().toString() + " - " + etPassword.getText().toString());
		// 测试验证用户信息的预期值是否等于实际值
		assertEquals("phicomm", etAccount.getText().toString());
		// 密码的预期值1234与实际值1234不符，Failure;
		assertEquals("123", etPassword.getText().toString());
	}

	// 测试提交按钮
	public void testSubmit() {
		Log.i(TAG, "测试提交按钮");
		input();
		// 开新线程，并通过该线程在实现在UI线程上执行操作
		mInstrumentation.runOnMainSync(new Runnable() {

			@Override
			public void run() {
				btSubmit.requestFocus();
				btSubmit.performClick();
			}
		});
		Log.i(TAG, "应该赋值后== " + tvShow.getText().toString());
		Log.i(TAG, "对比== " + LOGIN_INFO);
		assertEquals(LOGIN_INFO, tvShow.getText().toString());
	}

}
