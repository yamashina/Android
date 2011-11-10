package jp.emitteeer.countdowntimetable;

import java.text.DecimalFormat;

// �����\��1�s�ɂ�����N���X
public class TimeLine {
	private int hour = 0;
	private int minute = 0;
	private String content = "";
	private boolean selected = false;	// ���X�g�I����ԁ@�����̃N���X�Ŏ��������ł͂Ȃ��̂Ō�Ń��t�@�N�^�����O���ׂ�
	
	public TimeLine(int h, int m, String d) {
		hour = h;
		minute = m;
		content = d;
	}
	
	public String toString() {
		String s = hour + ":" + minute + " " + content;
		return s;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public String getContent() {
		return content;
	}

	public String getHourString(int digit) {
		return formatNumber((hour<24)?(hour):(hour-24), digit);
	}
	
	public String getMinuteString(int digit) {
		return formatNumber(minute, digit);
	}
	
    // ���l���w�茅�Ńt�H�[�}�b�g����
    String formatNumber(int num, int digit) {
    	String pattern = "";
    	for (int i=0; i<digit; i++) {
    		pattern += "0";
    	}
    	DecimalFormat dicimalFormat = new DecimalFormat(pattern);
    	return dicimalFormat.format(num);
    }
    
    public boolean getSelected() {
    	return selected;
    }
    
    public void setSelected(boolean s) {
    	selected = s;
    }
}