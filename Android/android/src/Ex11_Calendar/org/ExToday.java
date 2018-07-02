package Ex11_Calendar.org;

import Ex11_Calendar.org.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class ExToday extends Activity implements OnItemClickListener,
		OnClickListener {
	MyDBHelper mDBHelper;			//DB를 열기 위한 객체 생성
	String today;					//날짜 출력 변수
	Cursor cursor;					//DB 검색을 위한 커서
	SimpleCursorAdapter adapter;	//DB 정보를 리스트뷰로 출력하기 위한 SimpleCursorAdapter
	ListView list;					//저장 일정 출력 리스트뷰

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.extoday);

		Intent intent = getIntent();
		today = intent.getStringExtra("Param1");	//getIntent를 통해 날짜 값을 수신

		TextView text = (TextView) findViewById(R.id.texttoday);
		text.setText(today);	//수신 받은 날짜값 출력

		mDBHelper = new MyDBHelper(this, "Today.db", null, 1);	//읽기 원하는 테이블을 설정
		SQLiteDatabase db = mDBHelper.getWritableDatabase();	//DB값을 읽기 위해 사용

		//수신받은 날짜와 동일한 값을 모두 출력
		cursor = db.rawQuery("SELECT * FROM today WHERE date = '" + today + "'", null);
		
		
		//DB값의 제목과 시간 값 어댑터에 적용
		adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, cursor, new String[] {
						"title", "time" }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		ListView list = (ListView) findViewById(R.id.list1);
		list.setAdapter(adapter);	//리스트뷰로 출력
		list.setOnItemClickListener(this);

		mDBHelper.close();

		Button btn = (Button) findViewById(R.id.btnadd);	//일정 추가 버튼
		btn.setOnClickListener(this);
		
		Button btn1 = (Button)findViewById(R.id.btnlist);	//그룹 일정 버튼
		btn1.setOnClickListener(this);

	}

	@Override
	//리스트뷰의 일정을 클릭시
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, Detail.class);	//Detail 클래스로 인텐트
		cursor.moveToPosition(position);				//해당 DB 일정 값으로 커서를 이동
		intent.putExtra("ParamID", cursor.getInt(0)); 	//일정의 ID값을 인텐트
		startActivityForResult(intent, 0);				//양방향 인텐트 동작
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnadd:	//일정 추가 버튼 클릭 시
			Intent intent = new Intent(this, Detail.class);		//Detail 클래스로 인텐트
			intent.putExtra("ParamDate", today);				//해당 날짜값을 전달
			startActivityForResult(intent, 1);					//양방향 인텐트 동작
			break;
			
		case R.id.btnlist:	//그룹 일정 보기 버튼 클릭 시
			Intent list = new Intent(getApplicationContext(), List.class);	//List 클래스로 인텐트
			startActivity(list);
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
		case 1:
			if (resultCode == RESULT_OK) {			//Detail 클래스에서 resultCode를 수신

				//해당 일자의 DB 정보를 호출
				SQLiteDatabase db = mDBHelper.getWritableDatabase();
				cursor = db.rawQuery("SELECT * FROM today WHERE date = '"
						+ today + "'", null);
				adapter.changeCursor(cursor);
				mDBHelper.close();
			}
			break;
		}
	}
}
