package Ex11_Calendar.org;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

	public MyDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		//Today 테이블에 ID, 제목, 날짜, 시간, 내용, 비밀번호, 그룹명을 설정한다.
		db.execSQL("CREATE TABLE today(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "title TEXT, " + "date TEXT , " + "time TEXT, " + "memo TEXT, " + "gp TEXT, " + "pw TEXT );");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		//테이블의 변화가 있을 시 변경을 시켜줌
		db.execSQL("DROP TABLE IF EXIST today;");
		onCreate(db);
	}

}
