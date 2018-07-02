package Ex11_Calendar.org;

import java.util.ArrayList;
import java.util.Date;

import Ex11_Calendar.org.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

public class Ex11_CalendarActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	ArrayList<String> mItems;		//캘린더 출력을 위한 날짜값 배열
	ArrayAdapter<String> adapter;	//캘린더 출력을 위한 어댑터 배열
	EditText textYear;				//년도 검색을 위한 editText
	EditText textMon;				//월 검색을 위한 editText

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		textYear = (EditText) this.findViewById(R.id.edit1);
		textMon = (EditText) this.findViewById(R.id.edit2);

		mItems = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mItems);

		GridView gird = (GridView) this.findViewById(R.id.grid1);	//캘린더 형식을 위한 그리드 레이아웃
		gird.setAdapter(adapter);			//어댑터 배열을 그리드뷰에 적용
		gird.setOnItemClickListener(this);	//해달 일자를 클릭했을 경우를 위한 리스너

		Date date = new Date();				//오늘 날짜를 세팅 해준다.
		int year = date.getYear() + 1900;	//년 설정
		int mon = date.getMonth() + 1;		//월 설정
		textYear.setText(year + "");		//해당 년을 출력
		textMon.setText(mon + "");			//해당 월을 출력

		fillDate(year, mon);	//일자와 요일을 출력하기 위해 년,월 값을 대입

		Button btnmove = (Button) this.findViewById(R.id.bt1);	//원하는 날짜로 이동하기 위한 검색 버튼
		btnmove.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.bt1) {
			int year = Integer.parseInt(textYear.getText().toString());		
			int mon = Integer.parseInt(textMon.getText().toString());
			fillDate(year, mon);	//원하는 날짜로 이동시 해당 날짜의 년도와 월값을 함수에 적용
		}

	}

	private void fillDate(int year, int mon) {
		mItems.clear();			//배열값을 초기화 

		mItems.add("일");		//요일 출력
		mItems.add("월");
		mItems.add("화");
		mItems.add("수");
		mItems.add("목");
		mItems.add("금");
		mItems.add("토");

		Date current = new Date(year - 1900, mon - 1, 1);
		int day = current.getDay(); 

		//해당 월의 시작점 찾기
		for (int i = 0; i < day; i++) {
			mItems.add("");
		}
		
		//그달의 마지막날 구하는식 32일까지 입력하면 1일로 바꿔준다.
		current.setDate(32);
		int last = 32 - current.getDate();

		//날짜 채우기
		for (int i = 1; i <= last; i++) {
			mItems.add(i + "");
		}
		
		//바뀐 어댑터 배열을 설정해 준다 
		adapter.notifyDataSetChanged();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (mItems.get(arg2).equals("")) {
			;
		} else {
			//일정을 저장하고자 하는 일자를 클릭시
			Intent intent = new Intent(this, ExToday.class);	//ExToday 클래스로 인텐트
			intent.putExtra("Param1", textYear.getText().toString() + "/"	//해당 년,월,일자 값이 전달
					+ textMon.getText().toString() + "/" + mItems.get(arg2));
			startActivity(intent);
		}
	}
}
