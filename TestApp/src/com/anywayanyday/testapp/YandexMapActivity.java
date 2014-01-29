package com.anywayanyday.testapp;

import java.util.ArrayList;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class YandexMapActivity extends ActionBarActivity {
	private MapController mMapController;
    private OverlayManager mOverlayManager;
    private Overlay mOverlay;
    
    private ArrayList<Point> mPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		initMap();
		loadPoints();
		drawPoints();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
            	finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	private void initMap() {
        final MapView mapView = (MapView) findViewById(R.id.map);
        mMapController = mapView.getMapController();
        mOverlayManager = mMapController.getOverlayManager();
        mOverlayManager.getMyLocation().setEnabled(false);
        mOverlay = new Overlay(mMapController);
        mOverlayManager.addOverlay(mOverlay);
    }
	
	private void loadPoints(){
		mPoints = new ArrayList<Point>();
		DBManager db = new DBManager(this);
		try {
			db.open();
			
			Cursor cursor = db.getCursor();
			try {
				while(cursor.moveToNext()){
					mPoints.add(new Point(cursor));
				}
				
			} finally{
				if (cursor != null) {
					cursor.close();
				}
			}
		} finally{
			if (db != null) {
				db.close();
			}
		}
	}
	
	private void drawPoints(){
		mOverlay.clearOverlayItems();

        Drawable drawable = getResources().getDrawable(R.drawable.map_point);

        for (Point point : mPoints) {
            GeoPoint gp = new GeoPoint(point.lat, point.lon);
            OverlayItem p = new OverlayItem(gp, drawable);
            p.setOffsetY(24);
            p.setOffsetX(-6);
            BalloonItem baloon = new BalloonItem(this, gp);
            baloon.setText("Название: " + point.name + " \n Адрес: " + point.addr);
            p.setBalloonItem(baloon);
            mOverlay.addOverlayItem(p);
        }
	}
}
