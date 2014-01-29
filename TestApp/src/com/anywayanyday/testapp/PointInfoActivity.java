package com.anywayanyday.testapp;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class PointInfoActivity extends ActionBarActivity {
	private MapController mMapController;
    private OverlayManager mOverlayManager;
    private Overlay mOverlay;
    
    private Point mPoint;
    private DBManager mDb;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_point_info);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		long id = getIntent().getLongExtra("pointId", -1);
		if (id == -1) {
			return;
		}
		
		mDb = new DBManager(this);
		mDb.open();
		
		mPoint = mDb.getPoint(id);
		
		initMap();
		drawPoint();
		
		((TextView) findViewById(R.id.tvAddr)).setText(mPoint.addr);
		getSupportActionBar().setTitle(mPoint.name);
		mMapController.setPositionNoAnimationTo(new GeoPoint(mPoint.lat, mPoint.lon));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mDb = new DBManager(this);
		mDb.open();
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
		getMenuInflater().inflate(R.menu.point_info, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
            	finish();
                return true;
            case R.id.action_delete:
        		mDb.deletePoint(mPoint.id);
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
	
	private void drawPoint(){
		mOverlay.clearOverlayItems();
        Drawable drawable = getResources().getDrawable(R.drawable.map_point);
        GeoPoint gp = new GeoPoint(mPoint.lat, mPoint.lon);
        OverlayItem p = new OverlayItem(gp, drawable);
        p.setOffsetY(24);
        p.setOffsetX(-6);
        BalloonItem baloon = new BalloonItem(this, gp);
        baloon.setText("Название: " + mPoint.name + " \n Адрес: " + mPoint.addr);
        p.setBalloonItem(baloon);
        mOverlay.addOverlayItem(p);
	}
}
