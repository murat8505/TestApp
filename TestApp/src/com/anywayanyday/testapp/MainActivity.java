package com.anywayanyday.testapp;

import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	 private ListView mListView;
	 private DBManager mDb;
	 private SimpleCursorAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDb = new DBManager(this);
		mDb.open();
	    
		initBtn();
		initList();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mDb = new DBManager(this);
		mDb.open();
		//initList();
		mAdapter.changeCursor(mDb.getCursor());
	}
	
	@Override
	protected void onDestroy() {
		if (mDb != null) {
			mDb.close();
			mDb = null;
		}
		
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		if (mDb != null) {
			mDb.close();
			mDb = null;
		}
		
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(this, YandexMapActivity.class));
		return true;
		//return super.onOptionsItemSelected(item);
	}
	
	private void initList(){
		String[] from = new String[] { DBHelper.COLUMN_NAME };
	    int[] to = new int[] { R.id.tvName };

	    mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_point, null, from, to, 0);
	    mListView = (ListView) findViewById(R.id.list_points);
	    mListView.setAdapter(mAdapter);
	    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				startActivity(new Intent(MainActivity.this, PointInfoActivity.class).putExtra("pointId", id));
			}
		});
	    
	    getSupportLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				return new MyCursorLoader(MainActivity.this, mDb);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
				mAdapter.swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(Loader<Cursor> arg0) { }
		});
	}
	
	private void initBtn(){
		findViewById(R.id.btn_search).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isNetworkAvailable()) {
					Toast.makeText(MainActivity.this, R.string.error_connection, Toast.LENGTH_LONG).show();
					return;
				}
				
				String name = ((EditText) findViewById(R.id.input_name)).getText().toString().trim();
				
				if (name.length() == 0) {
					Toast.makeText(MainActivity.this, R.string.error_name, Toast.LENGTH_LONG).show();
					return;
				}
				
				String addr = ((EditText) findViewById(R.id.input_addr)).getText().toString();
				
				new RequestTask(name, addr).execute();
			}
		});
	}
	
	private void addPoint(String name, String addr, double lat, double lon){
		mDb.addPoint(name, addr, lat, lon);
		mAdapter.changeCursor(mDb.getCursor());
		
		((EditText) findViewById(R.id.input_name)).setText("");
		((EditText) findViewById(R.id.input_addr)).setText("");
		
		Toast.makeText(this, R.string.result_point_added, Toast.LENGTH_LONG).show();
	}
	
	public boolean isNetworkAvailable() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    
	    return false;
	}
	
	static class MyCursorLoader extends CursorLoader {
		DBManager mDB;

		public MyCursorLoader(Context context, DBManager db) {
			super(context);
			mDB = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor = mDB.getCursor();
			return cursor;
		}
	}
	
	private class RequestTask extends AsyncTask<Void, Void, String> {
		private static final String URL = "http://geocode-maps.yandex.ru/1.x/?format=json&results=1&geocode=";
		
		private String mName;
		private String mAddr;
		private ProgressDialog mProgress;
		
		public RequestTask(String name, String addr){
			mName = name;
			mAddr = addr;
		}
		
		@Override
		protected void onPreExecute() {
			mProgress = ProgressDialog.show(MainActivity.this, null, "Loading...", true, false);
		}
		
		@Override
		protected void onProgressUpdate(Void... progress) { }
		
		@Override
		protected String doInBackground(Void... params) {
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String query = URLEncoder.encode(mAddr, "utf-8");
				HttpGet httpGet = new HttpGet(URL + query);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				return EntityUtils.toString(httpEntity);
				
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			mProgress.dismiss();
			
			if (result == null){
				Toast.makeText(MainActivity.this, R.string.error_req, Toast.LENGTH_LONG).show();
				return;
			}
			
			try {
				JSONArray jFeatureMember = new JSONObject(result)
					.getJSONObject("response")
					.getJSONObject("GeoObjectCollection")
					.getJSONArray("featureMember");
				
				JSONObject jGeoObject = jFeatureMember.getJSONObject(0).getJSONObject("GeoObject");
				
				if (jFeatureMember.length() == 0){
					Toast.makeText(MainActivity.this, R.string.error_addr, Toast.LENGTH_LONG).show();
					return;
				}

				String point = jGeoObject
						.getJSONObject("Point")
						.getString("pos");
				
				String addr = jGeoObject
						.getJSONObject("metaDataProperty")
						.getJSONObject("GeocoderMetaData")
						.getJSONObject("AddressDetails")
						.getJSONObject("Country")
						.getString("AddressLine");
				
				String[] points = point.split(" ");
				Double lat = Double.parseDouble(points[1]);
				Double lon = Double.parseDouble(points[0]);
				
				MainActivity.this.addPoint(mName, addr, lat, lon);
				
				
			} catch (Exception e) {
				Toast.makeText(MainActivity.this, R.string.error_req, Toast.LENGTH_LONG).show();
			}
		}
	}
	
}
