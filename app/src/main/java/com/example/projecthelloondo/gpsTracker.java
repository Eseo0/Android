package com.example.projecthelloondo;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.core.content.ContextCompat;



@SuppressWarnings("deprecation")
public class gpsTracker extends Service implements LocationListener {

        private final Context mContext;
        Location location;
        double latitude;
        double longitude;

        //최소 GPS 정보 업데이트 거리 10미터
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
        //최소 GPS 정보 업데이트 시간 밀리세컨드 이므로 1분
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
        protected LocationManager locationManager;


        public gpsTracker(Context context) {
            this.mContext = context;
            getLocation();
        }

        //퍼미션 체크
        public Location getLocation() {
            try {
                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {

                } else {

                    int hasFineLocationPermission = ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION);
                    int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION);


                    if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

                        ;
                    } else
                        return null;

                    //네트워크 사용 유무
                    if (isNetworkEnabled) {


                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null)
                            {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }

                    //현재 GPS 사용 유무
                    if (isGPSEnabled)
                    {
                        if (location == null)
                        {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            if (locationManager != null)
                            {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null)
                                {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Log.d("@@@", ""+e.toString());
            }

            return location;
        }

        //위도값을 가져온다.
        public double getLatitude()
        {
            if(location != null)
            {
                latitude = location.getLatitude();
            }

            return latitude;
        }
        //경도값을 가져온다.
        public double getLongitude()
        {
            if(location != null)
            {
                longitude = location.getLongitude();
            }

            return longitude;
        }

        @Override

        public void onLocationChanged(Location location)
        {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();



        }

        @Override
        public void onProviderDisabled(String provider)
        {
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }

        @Override
        public IBinder onBind(Intent arg0)
        {
            return null;
        }


        //GPS 종료
        public void stopUsingGPS()
        {
            if(locationManager != null)
            {
                locationManager.removeUpdates(gpsTracker.this);
            }
        }







}