package Ex11_Calendar.org;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Detail extends Activity implements OnClickListener {
	
	//서버 주소 설정(127.0.0.1이나 localhost는 안된다)
	private final String SERVER_ADDRESS = "http://192.168.0.9/Calendar";
	
	MyDBHelper mDBHelper;	//DB를 열기 위한 객체 생성
	int mId;				//인텐트 받은 ID를 처리하기 위한 변수
	int sendpoint,deletepoint,updatepoint;	//Detial 클래스에서 기능 수행시 통제를 위한 변수
	String today;			//해당 날짜를 받기 위한 변수
	EditText editDate, editTitle, editTime, editMemo, editPW,editGp;	


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);

		editDate = (EditText) findViewById(R.id.editdate);
		editTitle = (EditText) findViewById(R.id.edittitle);
		editTime = (EditText) findViewById(R.id.edittime);
		editMemo = (EditText) findViewById(R.id.editmemo);
		editGp = (EditText) findViewById(R.id.editgp);
		editPW = (EditText) findViewById(R.id.editpw);

		Intent intent = getIntent();
		mId = intent.getIntExtra("ParamID", -1);		//해당 일정 ID값
		today = intent.getStringExtra("ParamDate");		//해당 일자 정보

		mDBHelper = new MyDBHelper(this, "Today.db", null, 1);	//읽기 원하는 테이블을 설정

		if (mId == -1) {	//일정을 처음 쓸 경우
			editDate.setText(today);	//날짜 값만 출력	
			 
		} else {	//DB에 일정이 저장되어있을 경우
			
			//DB에서 해당 일정 ID값을 조회하여 일정 정보값을 출력
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM today WHERE _id='" + mId
					+ "'", null);

			if (cursor.moveToNext()) {
				editTitle.setText(cursor.getString(1));
				editDate.setText(cursor.getString(2));
				editTime.setText(cursor.getString(3));
				editMemo.setText(cursor.getString(4));
				editGp.setText(cursor.getString(5));
				editPW.setText(cursor.getString(6));
			}
			mDBHelper.close();
		}

		Button btn1 = (Button) findViewById(R.id.btnsave);	//일정 저장 버튼
		btn1.setOnClickListener(this);
		Button btn2 = (Button) findViewById(R.id.btndel);	//일정 삭제 버튼
		btn2.setOnClickListener(this);
		Button btn3 = (Button) findViewById(R.id.btncancel); //취소 버튼
		btn3.setOnClickListener(this);
		Button btn4 = (Button) findViewById(R.id.btnsud);	//일정 수정 버튼
		btn4.setOnClickListener(this);

		if (mId == -1) {	//일정을 처음 쓸 경우 
			btn2.setVisibility(View.GONE);	//일정 삭제와 수정 버튼을 숨긴다.
			btn4.setVisibility(View.GONE);

		}
		
		else if(mId > 0)	//DB에 일정이 있을 경우
		{
			btn1.setVisibility(View.GONE);	//저장 버튼을 숨긴다.
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		switch (v.getId()) {
		case R.id.btnsave:
			
			sendpoint = 0;	//저장 버튼을 동작을 위한 변수
			
			if( editTitle.getText().toString().equals("") ||	//일정 정보에 빈칸이 있을 경우
					editTime.getText().toString().equals("")||
					editMemo.getText().toString().equals("")||
					editPW.getText().toString().equals("")|| 
					editGp.getText().toString().equals("")){ 
			
					//토스트 메세지 출력
					Toast.makeText(Detail.this,"일정을 입력하시오", Toast.LENGTH_SHORT).show();
					return;
			}
			
			//일정 정보를 DB에 저장한다
			db.execSQL("INSERT INTO today VALUES(null, '"
					+ editTitle.getText().toString() + "', '"
					+ editDate.getText().toString() + "', '"
					+ editTime.getText().toString() + "', '"
					+ editMemo.getText().toString() + "', '"
					+ editGp.getText().toString() + "', '"
					+ editPW.getText().toString() + "');");
			
			//DB에 일정 저장시 sendpoint를 1로 바꾼다
			sendpoint = 1;
		
			mDBHelper.close();
			setResult(RESULT_OK);	
			
			
			if(sendpoint == 1)	//sendpoint가 1일 경우 
			{
				String title = editTitle.getText().toString();
				String Date = today.toString();
				String time = editTime.getText().toString();
				String memo = editMemo.getText().toString();
				String gp = editGp.getText().toString();
				String pw = editPW.getText().toString();
				
				
				try {
					Log.e("tag", Date);
					URL url = new URL(SERVER_ADDRESS + "/insert.php?"		//서버주소/insert.php에
							+ "title=" + URLEncoder.encode(title, "UTF-8")	//각 일정의 일정 정보를 UTF-8 형식으로 웹 인코딩을 한다
							+ "&Date=" + URLEncoder.encode(Date, "UTF-8")
							+ "&time=" + URLEncoder.encode(time, "UTF-8")
							+ "&memo=" + URLEncoder.encode(memo, "UTF-8")
							+ "&gp=" + URLEncoder.encode(gp, "UTF-8")
							+ "&pw=" + URLEncoder.encode(pw, "UTF-8")); 
					url.openStream(); 	//서버의 DB에 입력하기 위해 웹서버의 insert.php파일에 입력된 일정 정보를 넘김
					
					
					Log.e("tag", ""+url);
					
					//아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기
					String result = getXmlData("insertresult.xml", "result"); //입력 성공여부
					
					if(result.equals("1")) { //result값이 1일 경우 
						
						//토스트 메시지 출력
						Toast.makeText(Detail.this,"DB insert 성공", Toast.LENGTH_SHORT).show();
						
						//일정 정보 EditText를 빈칸으로 초기화 시킨다
						editTitle.setText("");
						editDate.setText("");
						editTime.setText("");
						editMemo.setText("");
						editGp.setText("");
						editPW.setText("");
						
						
						sendpoint = 0;	//sendpoint를 다시 0으로 바꾼다
					}
					else 
						//result값이 1이 아닐 경우 토스트 메시지 출력
						Toast.makeText(Detail.this,"DB insert 실패", Toast.LENGTH_SHORT).show();
				} catch(Exception e) {
					Log.e("Error", e.getMessage());	//에러 메세지를 출력 한다.
				}
				
				
			}
			
			
			
			break;
		case R.id.btndel:
			
			String title = editTitle.getText().toString();
			String Date = editDate.getText().toString();
			String time = editTime.getText().toString();
			String memo = editMemo.getText().toString();
			String pw = editPW.getText().toString();
			String gp = editGp.getText().toString();
			
			try {
				
				URL url = new URL(SERVER_ADDRESS + "/delete.php?"	   //서버주소/insert.php에
						+ "title=" + URLEncoder.encode(title, "UTF-8") //각 일정의 일정 정보를 UTF-8 형식으로 웹 인코딩을 한다
						+ "&Date=" + URLEncoder.encode(Date, "UTF-8")
						+ "&time=" + URLEncoder.encode(time, "UTF-8")
						+ "&memo=" + URLEncoder.encode(memo, "UTF-8")
						+ "&pw=" + URLEncoder.encode(pw, "UTF-8")
						+ "&gp=" + URLEncoder.encode(gp, "UTF-8")); 
				url.openStream(); //서버의 DB에 입력하기 위해 웹서버의 delete.php파일에 입력된 일정 정보를 넘김

				//아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기
				String result = getXmlData("insertresult1.xml", "result"); //삭제 성공여부
				
				if(result.equals("1")) { 
					Toast.makeText(Detail.this,"DB delete 성공", Toast.LENGTH_SHORT).show();
					
					editTitle.setText("");
					editDate.setText("");
					editTime.setText("");
					editMemo.setText("");
					editPW.setText("");
					editGp.setText("");
					
					deletepoint = 0;
				}
			
				else 
					Toast.makeText(Detail.this,"DB delete 실패", Toast.LENGTH_SHORT).show();
				
			} catch(Exception e) {
				Log.e("Error", e.getMessage());
			}
			
			if (mId != -1) {	
				db.execSQL("DELETE FROM today WHERE _id='" + mId + "';"); //DB에 저장된 해당 일정을 지운다
				mDBHelper.close();
			}
			setResult(RESULT_OK);

			break;
		case R.id.btncancel:
			setResult(RESULT_CANCELED);
			break;
			
		case R.id.btnsud:	
			
			updatepoint = 0;	//updatepoint를 0으로 설정
			
			if (mId != -1) {	//DB에 일정이 있을 경우 수정된 일정 정보로 업데이트 시킨다
				db.execSQL("UPDATE today SET title='"					
						+ editTitle.getText().toString() + "',date='"
						+ editDate.getText().toString() + "', time='"
						+ editTime.getText().toString() + "', memo='"
						+ editMemo.getText().toString() + "', gp='"
						+ editGp.getText().toString() + "', pw='"
						+ editPW.getText().toString() + "' WHERE _id='" + mId
						+ "';");
				
				
				updatepoint = 1;	//updatepoint를 1로 설정
			} 
			mDBHelper.close();
			setResult(RESULT_OK);
			
			if(updatepoint == 1)
			{
				String title1 = editTitle.getText().toString();
				String Date1 = editDate.getText().toString();
				String time1 = editTime.getText().toString();
				String memo1 = editMemo.getText().toString();
				String pw1 = editPW.getText().toString();
				String gp1 = editGp.getText().toString();
				
				
				try {
					Log.e("tag", Date1);
					URL url = new URL(SERVER_ADDRESS + "/update.php?"		//서버주소/insert.php에
							+ "title=" + URLEncoder.encode(title1, "UTF-8") //각 일정의 일정 정보를 UTF-8 형식으로 웹 인코딩을 한다
							+ "&Date=" + URLEncoder.encode(Date1, "UTF-8")
							+ "&time=" + URLEncoder.encode(time1, "UTF-8")
							+ "&memo=" + URLEncoder.encode(memo1, "UTF-8")
							+ "&gp=" + URLEncoder.encode(gp1, "UTF-8")
							+ "&pw=" + URLEncoder.encode(pw1, "UTF-8")); 
					url.openStream(); //서버의 DB에 입력하기 위해 웹서버의 delete.php파일에 입력된 일정 정보를 넘김
					Log.e("tag", ""+url);
					
					//아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기
					String result = getXmlData("insertresult2.xml", "result"); //수정 성공여부
					
					if(result.equals("1")) { 
						Toast.makeText(Detail.this,"DB update 성공", Toast.LENGTH_SHORT).show();
						
						editTitle.setText("");
						editDate.setText("");
						editTime.setText("");
						editMemo.setText("");
						editPW.setText("");
						editGp.setText("");
						
						updatepoint = 0;
					}
					else 
						Toast.makeText(Detail.this,"DB update 실패", Toast.LENGTH_SHORT).show();
				} catch(Exception e) {
					Log.e("Error", e.getMessage());
				}
			
			}
			break;
		}
		finish();
	}

private String getXmlData(String filename, String str) { //태그값 하나를 받아오기위한 String형 함수
String rss = SERVER_ADDRESS + "/";
String ret = "";

try { //XML 파싱을 위한 과정
	XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); //xml 파싱을 위해 새로운 인스턴스 취득
	factory.setNamespaceAware(true);	//팩토리로 작성된 파서가 XML의 Namespace 서포트하도록 지정
	XmlPullParser xpp = factory.newPullParser();	//새로운 PullParser 생성
	URL server = new URL(rss + filename);			//해당 xml 불러오기
	InputStream is = server.openStream();			//XML문서를 파싱한다.
	xpp.setInput(is, "UTF-8");						//언어 코드는 UTF-8
	
	int eventType = xpp.getEventType();				//PullParser 데이터를 반환한다
	
	while(eventType != XmlPullParser.END_DOCUMENT) { //XML 파일의 끝에 도달했을 때 반환
		if(eventType == XmlPullParser.START_TAG) { //요소의 시작 태그를 만났을 때 반환
			if(xpp.getName().equals(str)) {	//태그 이름이 str 인자값과 같은 경우
				ret = xpp.nextText();		//String 변수 ret에 반환
			}
		}
		eventType = xpp.next();				//eventType에 반환
	}
} catch(Exception e) {
	Log.e("Error", e.getMessage());
}

return ret;
}

private ArrayList<String> getXmlDataList(String filename, String str) { //태그값 여러개를 받아오기위한 ArrayList<string>형 변수
String rss = SERVER_ADDRESS + "/";
ArrayList<String> ret = new ArrayList<String>();

try { //XML 파싱을 위한 과정
	XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); //xml 파싱을 위해 새로운 인스턴스 취득
	factory.setNamespaceAware(true);		//팩토리로 작성된 파서가 XML의 Namespace 서포트하도록 지정
	XmlPullParser xpp = factory.newPullParser();	//새로운 PullParser 생성
	URL server = new URL(rss + filename);			//해당 xml 불러오기
	InputStream is = server.openStream();			//XML문서를 파싱한다.
	xpp.setInput(is, "UTF-8");						//언어 코드는 UTF-8
	
	int eventType = xpp.getEventType();				//PullParser 데이터를 반환한다
	
	while(eventType != XmlPullParser.END_DOCUMENT) {//XML 파일의 끝에 도달했을 때 반환
		if(eventType == XmlPullParser.START_TAG) {//요소의 시작 태그를 만났을 때 반환
			if(xpp.getName().equals(str)) { //태그 이름이 str 인자값과 같은 경우
				ret.add(xpp.nextText());	//String 변수 ret에 반환
			}
		}
		eventType = xpp.next();		//eventType에 반환
	}
} catch(Exception e) {
	Log.e("Error", e.getMessage());
}

return ret;
}
}
