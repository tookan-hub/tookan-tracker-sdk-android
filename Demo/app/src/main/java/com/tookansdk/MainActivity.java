package com.tookansdk;

import android.content.DialogInterface;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import tookan.tookanlocationtrackinglibrary.TookanLocationListening;
import tookan.tookanlocationtrackinglibrary.TookanLocationListeningImpl;

public class MainActivity extends AppCompatActivity implements TookanLocationListeningImpl.LocationListener, OnMapReadyCallback
{
	private TookanLocationListening tookanListener;

	private GoogleMap mMap;
	private Marker marker;
	private boolean firstLocation;
	private Location lastLocation;
	private final String apiKey = "0da4f7ee0ac89bc1dce9a32a43cfdb1d7909cb5a024f217b8a6f783778c4b0b8";


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		firstLocation = true;

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	public void start(View view)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Enter JobID");

		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(input);

		builder.setPositiveButton("Start", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String jobID = input.getText().toString();

				if (tookanListener == null)
				{
					tookanListener = TookanLocationListening.getInstance(MainActivity.this);
				}

				mMap.clear();
				tookanListener.startLocationListening(MainActivity.this, jobID, apiKey, MainActivity.this);
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});
		builder.show();
	}

	public void stop(View view)
	{
		if (tookanListener != null)
		{
			tookanListener.stopLocationListening(this);
			firstLocation = true;
		}
		else
		{
			Toast.makeText(this, "Location Listener already stopped.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLocationArrive(Location location, String locationData)
	{
		if (firstLocation)
		{
			moveToCurrentLocation(location);
		}
		else
		{
			updateMap(location, false);
		}

		Log.e("MQTT_MESSAGE", locationData);
	}

	@Override
	public void onJobComplete()
	{
		Log.e("UPDATE", "JOB COMPLETED!");
	}

	private void updateMap(Location location, boolean moveToCurrent)
	{
		LatLng currentLatlng = new LatLng(location.getLatitude(), location.getLongitude());

		lastLocation = location;

		if(moveToCurrent)
		{
			CameraPosition.Builder builder = new CameraPosition.Builder();
			builder.zoom(15);
			builder.target(currentLatlng);
			this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
		}
		else
		{
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatlng);
			mMap.animateCamera(cameraUpdate);
		}

		animateMarker(marker, currentLatlng);
	}

	public void animateMarker(final Marker marker, final LatLng toPosition)
	{
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = mMap.getProjection();
		Point startPoint = proj.toScreenLocation(marker.getPosition());
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 500;
		final Interpolator interpolator = new LinearInterpolator();
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				long elapsed = SystemClock.uptimeMillis() - start;
				try
				{
					float t = interpolator.getInterpolation((float) elapsed
							/ duration);
					double lng = t * toPosition.longitude + (1 - t)
							* startLatLng.longitude;
					double lat = t * toPosition.latitude + (1 - t)
							* startLatLng.latitude;
					marker.setPosition(new LatLng(lat, lng));
					if (t < 1.0)
					{
						// Post again 16ms later.
						handler.postDelayed(this, 16);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;
		if (googleMap != null)
		{
			mMap.getUiSettings().setMyLocationButtonEnabled(false);
			mMap.getUiSettings().setCompassEnabled(false);
			mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
		}
	}

	public void moveView(View view)
	{
		if (lastLocation != null)
			updateMap(lastLocation, true);
	}

	private void moveToCurrentLocation(Location location)
	{
		if (this.mMap != null)
		{
			if (location != null)
			{
				MarkerOptions options = new MarkerOptions()
						.position(new LatLng(location.getLatitude(), location.getLongitude()))
						.icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
				marker = mMap.addMarker(options);

				LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
				CameraPosition.Builder builder = new CameraPosition.Builder();
				builder.zoom(15);
				builder.target(target);

				this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
				firstLocation = false;
				lastLocation = location;
			}
		}
	}
}
