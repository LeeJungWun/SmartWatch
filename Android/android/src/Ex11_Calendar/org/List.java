package Ex11_Calendar.org;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;



public class List extends Activity {

	private final String SERVER_ADDRESS = "http://192.168.0.9/Calendar";

	EditText edSearch;
	Button btnSearch;

	ListView list;
	ArrayList<String> data;
	ArrayAdapter<String> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		// TODO Auto-generated method stub

		edSearch = (EditText) findViewById(R.id.edsearch);
		btnSearch = (Button) findViewById(R.id.btnsearch);

		list = (ListView) findViewById(R.id.listView1);
		data = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, data);
		list.setAdapter(adapter);

		btnSearch.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
							
				// TODO Auto-generated method stub
				final Handler handler = new Handler();
				runOnUiThread(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						final ProgressDialog dialog = ProgressDialog.show(
								List.this,
								"불러오는중.....", "잠시만 기다려주세요");
						
						handler.post(new Runnable() {
							
							public void run() {
								// TODO Auto-generated method stub
								String gp = edSearch.getText().toString();
								try {
									URL url = new URL(SERVER_ADDRESS + "/search.php?"
											+ "gp=" + URLEncoder.encode(gp, "UTF-8"));
 
									data.clear(); 
									url.openStream(); 
						
									ArrayList<String> title = getXmlDataList("searchresult.xml", "title");
									ArrayList<String> time = getXmlDataList("searchresult.xml", "time");
									ArrayList<String> memo = getXmlDataList("searchresult.xml", "memo"); 
									
									if(title.isEmpty())
										data.add("아무것도 검색되지 않았습니다.");
									else {
										for(int i = 0; i < title.size(); i++) {
											String str = "        "+title.get(i) + "             "+ time.get(i) + "             "+ memo.get(i);
											data.add(str);
										}
									}
								} catch(Exception e) {
									Log.e("Error", e.getMessage());
								} finally{
									dialog.dismiss();
									adapter.notifyDataSetChanged();
								}
							}
						});
					}
				});
				
			}
		});
    }
    
	private String getXmlData(String filename, String str) {
		String rss = SERVER_ADDRESS + "/";
		String ret = "";
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			URL server = new URL(rss + filename);
			InputStream is = server.openStream();
			xpp.setInput(is, "UTF-8");
			
			int eventType = xpp.getEventType();
			
			while(eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					if(xpp.getName().equals(str)) {
						ret = xpp.nextText();
					}
				}
				eventType = xpp.next();
			}
		} catch(Exception e) {
			Log.e("Error", e.getMessage());
		}
		
		return ret;
	}
    
	private ArrayList<String> getXmlDataList(String filename, String str) { 
		String rss = SERVER_ADDRESS + "/";
		ArrayList<String> ret = new ArrayList<String>();
		
		try { 
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			URL server = new URL(rss + filename);
			InputStream is = server.openStream();
			xpp.setInput(is, "UTF-8");
			
			int eventType = xpp.getEventType();
			
			while(eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					if(xpp.getName().equals(str)) { 
						ret.add(xpp.nextText());
					}
				}
				eventType = xpp.next();
			}
		} catch(Exception e) {
			Log.e("Error", e.getMessage());
		}
		
		return ret;
	}
}