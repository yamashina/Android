package jp.emitteeer.countdowntimetable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TimeTableList extends ListActivity {
	private final String TIMETABLE_PATH = "/CountDownTimeTable";
	private final int MENU_ID_SETTING = Menu.FIRST;
	private final int MENU_ID_ABOUT = Menu.FIRST + 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      setContentView(R.layout.main);
        this.setTitle("Top");
        
        // Preferenceの取得＆反映
        preferenceSetting();

        // タイムテーブルファイルの確認
        // SDカードにタイムテーブルファイルを格納するフォルダまたはタイムテーブルファイルがない場合は生成する
        File externalStorage = getExternalStorageDirectory();
        if (externalStorage != null) {
        	File timetableDir = new File(externalStorage.toString() + TIMETABLE_PATH);
        	if (timetableDir.exists()) {
        		if (timetableDir.list().length == 0) {
        			boolean retMakeFile = makeDefaultTimeTable(new File(timetableDir.toString() + "/" + this.getResources().getString(R.string.default_timetable_filename)));
        			if (retMakeFile) {
        				showFirstDialog();	// 初回起動ダイアログの表示
        			}
        			else {
            			// ★todo：エラーダイアログ(ファイル作成失敗)を表示する
        			}
        		}
        		
            	ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    	this,
                    	android.R.layout.simple_list_item_1,
                    	timetableDir.list()
                    );
                setListAdapter(adapter);
        	}
        	else {
        		boolean retMakeDir = timetableDir.mkdirs();	// "/sdcard/CountDownTimeTable"ディレクトリの作成
        		if (retMakeDir) {
        			boolean retMakeFile = makeDefaultTimeTable(new File(timetableDir.toString() + "/" + this.getResources().getString(R.string.default_timetable_filename)));
        			if (retMakeFile) {
                    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            	this,
                            	android.R.layout.simple_list_item_1,
                            	timetableDir.list()
                            );
                        setListAdapter(adapter);
                		
        				showFirstDialog();	// 初回起動ダイアログの表示
        			}
        			else {
            			// ★todo：エラーダイアログ(ファイル作成失敗)を表示する
        			}
        		}
        		else {
        			// ★todo：エラーダイアログ(ディレクトリ作成失敗)を表示する
        		}
        	}
        }
        else {
        	// error
        }
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
        // Preferenceの取得＆反映
        preferenceSetting();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	
//		Toast.makeText(
//				this, 
//				String.format("position:%d, id:%d", position, id), 
//				Toast.LENGTH_SHORT).show();
			
		Intent intent = new Intent(TimeTableList.this, CountDown.class);
    	File timetableDir = new File(getExternalStorageDirectory().toString() + TIMETABLE_PATH);
		File[] fileList = timetableDir.listFiles();
		intent.putExtra("timetablePath", fileList[position].getAbsolutePath());
		startActivity(intent);
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
    
    // 外部ストレージ(SDカード)のFileオブジェクト取得
    private File getExternalStorageDirectory() {
    	String state = Environment.getExternalStorageState();
    	if (!state.equals(Environment.MEDIA_MOUNTED)) {
    		return null;
    	}
    	
    	return Environment.getExternalStorageDirectory();
    }
    
    // デフォルトタイムテーブルの作成
    private boolean makeDefaultTimeTable(File timetableFile) {
    	final String[] timeLine = this.getResources().getStringArray(R.array.defaultTimeLines);
    	
    	try {
        	timetableFile.createNewFile();
        	BufferedWriter writer = new BufferedWriter(new FileWriter(timetableFile));
        	for (int i=0; i<timeLine.length; i++) {
        		writer.append(timeLine[i]);
        		writer.newLine();
        	}
        	writer.close();
    	} catch (IOException e) {
    		// error
    		return false;
    	}
    	
    	return true;
    }
    
    // 設定画面への遷移
    private void transSetting() {
		Intent intent = new Intent(TimeTableList.this, Preference.class);
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
    
    // 初回起動ダイアログの表示
    private void showFirstDialog() {
    	AlertDialog.Builder b = new AlertDialog.Builder(this);
    	b.setIcon(android.R.drawable.ic_dialog_info);
    	// 書式付きの文字列を取得するときはgetText()で。
    	b.setTitle(getString(R.string.app_name));
    	b.setMessage(getString(R.string.first_message));
    	
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
}