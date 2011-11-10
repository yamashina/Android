package jp.emitteeer.countdowntimetable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

public class CountDown extends ListActivity {
	private Handler handler = new Handler();
	private TextView tvTime;
	private List<TimeLine> timeLines = new ArrayList<TimeLine>();
//	private int timeLineCurrentPos = ListView.INVALID_POSITION;	// ListView#getSelectedItemPosition()が上手く取れないから、ローカル持ちする
	private static int lastMidnightHour = -1;	// 時刻表の深夜最終時間(-1：無効値　0以上：有効値)
	private final int MENU_ID_SETTING = Menu.FIRST;
	private final int MENU_ID_ABOUT = Menu.FIRST + 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Preferenceの取得＆反映
        preferenceSetting();

        tvTime = (TextView)findViewById(R.id.time);
        
        // Intentを取得する
        Intent intent = getIntent();
        String timetablePath = intent.getStringExtra("timetablePath");
//		Toast.makeText(this, timetablePath, Toast.LENGTH_LONG).show();
		
        // Activityのタイトルを設定する
		String fileName = getFileNameFromPath(timetablePath);
		this.setTitle("Top > " + fileName);
		
        // 時刻表の読み込み
        try {
        	// 文字コードを指定した読み込み
        	// プラットフォームのデフォルトの文字コードはSystem.getProperty("file.encoding")
        	// もしくはCharset.defaultCharset()で得られる
        	String encoding = System.getProperty("file.encoding");
        	InputStreamReader isr = new InputStreamReader(new FileInputStream(timetablePath), encoding);
        	BufferedReader reader = new BufferedReader(isr);
        	
        	String line;
        	while ((line = reader.readLine()) != null) {
        		if (line.startsWith("#")) {
        			continue;	// コメント(行頭が#の文字列)の場合は次の行に移る
        		}
        		// 時刻表の1行を解析し、Listに追加する
        		String[] strAry1 = line.split(":", 2);
        		String[] strAry2 = strAry1[1].split(",");
        		int hour = Integer.parseInt(strAry1[0]);
        		if (hour >= 24) {
        			lastMidnightHour = hour - 24;
        		}
        		int minute = Integer.parseInt(strAry2[0]);
        		String content = strAry2[1];
        		timeLines.add(new TimeLine(hour, minute, content));
        	}
        	
        	reader.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        
        TimeLineListAdapter adapter = new TimeLineListAdapter(this, timeLines);
        setListAdapter(adapter);

        // Timer
        Timer timer = new Timer(false);
        timer.schedule(
        	new TimerTask() {
	        	@Override
	        	public void run() {
	        		handler.post(new Runnable() {
	                	@Override
	        			public void run() {
	        				updateTime();
	        			}
	        		});
	        	}
        	}, 0, 1000);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();

/*		ListView#getSelectedItemPosition()が上手く取れない...
    	ListView lv = this.getListView();
    	int position = lv.getSelectedItemPosition();
    	if (position == ListView.INVALID_POSITION) {
    		Calendar calendar = Calendar.getInstance();
    		// 現在時刻
    		int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
    		int nowMinute = calendar.get(Calendar.MINUTE);

			int nowPos = getSpecificTimePosition(nowHour, nowMinute);
			if (nowPos >= 0) {
				lv.setSelection(nowPos);
			}
    	}
*/
    	if (getTimeLineCurrentPos() == ListView.INVALID_POSITION) {
//    		Calendar calendar = Calendar.getInstance();
//    		// 現在時刻
//    		int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
//    		int nowMinute = calendar.get(Calendar.MINUTE);
    		int nowHour = MyClock.getNowHour();
    		int nowMinute = MyClock.getNowMinute();

			int nowPos = getSpecificTimePosition(nowHour, nowMinute);
			if (nowPos >= 0) {
				this.getListView().setSelection(nowPos);	// 指定位置のリストを画面上部に移動
				setTimeLineCurrentPos(nowPos);
			}
    	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
        // Preferenceの取得＆反映
        preferenceSetting();
    }
    
    private void updateTime() {
		tvTime.setText("0:0:0");
		
//    	Calendar calendar = Calendar.getInstance();
//		// 現在時刻
//		int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
//    	if (nowHour <= lastMidnightHour) {
//    		nowHour += 24;	// 深夜補正
//    	}
//		int nowMinute = calendar.get(Calendar.MINUTE);
//		int nowSecond = calendar.get(Calendar.SECOND);
		int nowHour = MyClock.getNowHour();
		int nowMinute = MyClock.getNowMinute();
		int nowSecond = MyClock.getNowSecond();
		
		// 時刻表(時・分)
		int tlHour;
		int tlMinute;
		
		// 時刻表で選択中のリストの時・分を取得する
		// 未選択状態の場合は、現在時刻から一番近い時・分を取得する
//		ListView lv = this.getListView();
//    	int position = lv.getSelectedItemPosition();
//    	if (position != ListView.INVALID_POSITION) {
		int pos = getTimeLineCurrentPos();
		if (pos == ListView.INVALID_POSITION) {
    		// 時刻表(現在時刻から一番近い時・分)
    		pos = getSpecificTimePosition(nowHour, nowMinute);
    		if (pos < 0) {
    			return;
    		}
        	setTimeLineCurrentPos(pos);
		}
		tlHour = timeLines.get(pos).getHour();
		tlMinute = timeLines.get(pos).getMinute();

		// 時刻表と現在時刻の差
		int diffHour = tlHour - nowHour - 1;
		int diffMinute = 60 + tlMinute - nowMinute - 1;
		int diffSecond = 60 - nowSecond;
		
		if (diffSecond >= 60) {
			diffSecond -= 60;
			diffMinute += 1;
		}
		
		if (diffMinute >= 60) {
			diffMinute -= 60;
			diffHour += 1;
		}
    	
		// 時刻表の指定行が現在時刻を過ぎていた場合は0表示にする
		if (diffHour < 0) {
			diffHour = 0;
			diffMinute = 0;
			diffSecond = 0;
		}
		
		tvTime.setText(
			formatNumber(diffHour, 2) + ":" + 
			formatNumber(diffMinute, 2) + ":" + 
			formatNumber(diffSecond, 2));
    }

    // 指定時刻に一番近いリスト番号を取得する
    private int getSpecificTimePosition(int hour, int minute) {
//    	if (hour <= lastMidnightHour) {
//    		hour += 24;	// 深夜補正
//    	}
    	
		ListIterator<TimeLine> it = timeLines.listIterator();
		boolean timeDicision = false;
		while (it.hasNext() && (timeDicision == false)) {
			TimeLine tl = it.next();
			
			if (tl.getHour() == hour) {
				if (tl.getMinute() > minute) {
					timeDicision = true;
				}
			}
			else if (tl.getHour() > hour) {
				timeDicision = true;
			}
		}
		
		return it.nextIndex()-1;
    }
    
    private static class MyClock {
    	public static int getNowHour() {
        	Calendar calendar = Calendar.getInstance();
    		int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
        	if (nowHour <= lastMidnightHour) {
        		nowHour += 24;	// 深夜補正
        	}
        	
        	return nowHour;
    	}
    	
    	public static int getNowMinute() {
        	Calendar calendar = Calendar.getInstance();
    		int nowMinute = calendar.get(Calendar.MINUTE);

    		return nowMinute;
    	}
    	
    	public static int getNowSecond() {
        	Calendar calendar = Calendar.getInstance();
    		int nowSecond = calendar.get(Calendar.SECOND);

    		return nowSecond;
    	}
    }

    // リストを選択したときの処理
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
//		Toast.makeText(this, String.format("position:%d, id:%d", position, id), Toast.LENGTH_SHORT).show();
    	
    	// 選択したリスト位置を保持する
    	// ↑ListView#getSelectedItemPosition()が上手く取れないから、仕方なくローカル持ちする
    	setTimeLineCurrentPos(position);
    	
    	// 選択行を画面上部に移動する
//    	l.setSelection(position);
    	
    	// 選択色にする(フォーカスを当てる)
    	l.setFocusable(true);
    	l.setFocusableInTouchMode(true);
    	l.requestFocus();
    }
    
    // パスからファイル名を取得する
    private String getFileNameFromPath(String path) {
		String[] splistStr = path.split("/");
		return splistStr[3];
    }
    
    // 数値を指定桁でフォーマットする
    String formatNumber(int num, int digit) {
    	String pattern = "";
    	for (int i=0; i<digit; i++) {
    		pattern += "0";
    	}
    	DecimalFormat dicimalFormat = new DecimalFormat(pattern);
    	return dicimalFormat.format(num);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	menu.add(0, MENU_ID_SETTING, 0, "Setting").setIcon(android.R.drawable.ic_menu_preferences);
    	menu.add(0, MENU_ID_ABOUT, 1, "About").setIcon(android.R.drawable.ic_menu_info_details);
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case MENU_ID_SETTING:
    		transSetting();
    		return true;
    	case MENU_ID_ABOUT:
    		showAboutDialog();
    		return true;
    	}
    	return false;
    }
    
    // 設定画面への遷移
    private void transSetting() {
		Intent intent = new Intent(CountDown.this, Preference.class);
		startActivity(intent);
    }
    
    // Aboutダイアログの表示
    private void showAboutDialog() {
    	AlertDialog.Builder b = new AlertDialog.Builder(this);
    	b.setIcon(android.R.drawable.ic_dialog_info);
    	// 書式付きの文字列を取得するときはgetText()で。
    	b.setTitle(getString(R.string.app_name));
    	try {
        	PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
        	String message = "Version " + packageInfo.versionName + getString(R.string.about_dialog_message); 
        	b.setMessage(message);
    	} catch(NameNotFoundException e) {
    	}
    	
    	b.show();
    }
    
    // Preferenceの取得＆反映
    private void preferenceSetting() {
        boolean autoChangeScreen = Preference.isAutoChangeScreenOrientation(getBaseContext());
        if (autoChangeScreen) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        else {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        
        boolean keepScreen = Preference.isKeepScreenOn(getBaseContext());
        if (keepScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
	
	// 選択状態のリスト位置を取得する
	public int getTimeLineCurrentPos() {
		int pos = ListView.INVALID_POSITION;
		
		for (int i=0; i<timeLines.size(); i++) {
			if (timeLines.get(i).getSelected() == true) {
				pos = i;
				break;
			}
		}
		
		return pos;
	}
	
	// 指定位置のリスト位置を選択状態にする
	// 以前、選択状態だったものは非選択状態に戻す
	public void setTimeLineCurrentPos(int newPos) {
		int oldPos = getTimeLineCurrentPos();
		if (oldPos != ListView.INVALID_POSITION) {
			timeLines.get(oldPos).setSelected(false);
		}
		
		timeLines.get(newPos).setSelected(true);
	}
}