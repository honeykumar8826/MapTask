package map.com.maptask;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import map.com.maptask.adapter.ImageLoadAdapter;
import map.com.maptask.adapter.PlaceImageAdapter;
import map.com.maptask.adapter.TrendingImageAdapter;
import map.com.maptask.modal.ImageModal;
import map.com.maptask.modal.PlaceImageModal;
import map.com.maptask.modal.TrendingImageModal;
import map.com.maptask.network.NetworkClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String API_KEY = "9e5ef71432c64196a16273c85cfb94c1";
    private static final String TAG = "HomeActivity";
    private RecyclerView recycleFriends, recyclerTrendings, recyclerPlace;
    private GoogleMap mMap;
    private Location mLocation;
    private FusedLocationProviderClient mClient;
    private SupportMapFragment mapFragment;
    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout linearLayoutBSheet;
    private Retrofit retrofit;
    private NetworkClient api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInItId();
        callFriendsApi();
        callTrendingApi();
        callPlaceApi();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            mClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mLocation = location;
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location);
                    LatLng currentLatLong = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLatLong).title("Marker in India").icon(icon));
                    //Move the camera to the user's location and zoom in!
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));

                    CircleOptions circleOptions = new CircleOptions()
                            .center(currentLatLong).radius(1000)
                            .fillColor(Color.GRAY).strokeColor(Color.GREEN).strokeWidth(8);
                    Circle mCircle = mMap.addCircle(circleOptions);
                    Log.i(TAG, "onSuccess: " + location.getLongitude());
                }
            });
            mapFragment.getMapAsync(this);
            setUpBottomSheet();
        }

    }

    private void setUpBottomSheet() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                  /*  case BottomSheetBehavior.STATE_COLLAPSED:
                        Toast.makeText(MainActivity.this, "collaped", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Toast.makeText(MainActivity.this, "expand", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "default", Toast.LENGTH_SHORT).show();
                        break;*/
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
          /*      if (v < 0) {
                    Toast.makeText(MainActivity.this, "check", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "onSlide: " + view + "" + v);
                if (v == 0) {
                    Toast.makeText(MainActivity.this, "v=0", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void setInItId() {
        recycleFriends = findViewById(R.id.recycles_friends);
        recyclerTrendings = findViewById(R.id.recycles_trending);
        recyclerPlace = findViewById(R.id.recycle_places);
        linearLayoutBSheet = findViewById(R.id.ll_bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
        mClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        retrofit = new Retrofit.Builder().baseUrl(NetworkClient.BASE_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(NetworkClient.class);
    }

    private void callFriendsApi() {
        recycleFriends.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        api = retrofit.create(NetworkClient.class);
        Call<ResponseBody> call = api.getNews("in", API_KEY);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    if (result != null) {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = jsonObject.getString("status");
                        int totalItem = jsonObject.getInt("totalResults");
                        if (status.equals("ok") && totalItem > 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("articles");
                            if (jsonArray.length() > 0) {
                                List<ImageModal> imageModalList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonArticle = jsonArray.getJSONObject(i);
                                    String imgUrl = jsonArticle.getString("urlToImage");
                                    ImageModal imageModal = new ImageModal(imgUrl);
                                    imageModalList.add(imageModal);
                                }
                                ImageLoadAdapter imageLoadAdapter = new ImageLoadAdapter(MainActivity.this, imageModalList);
                                recycleFriends.setAdapter(imageLoadAdapter);
                                imageLoadAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MainActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(MainActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(MainActivity.this, "Internet Issue", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Big Issue", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callPlaceApi() {
        recyclerPlace.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        api = retrofit.create(NetworkClient.class);
        Call<ResponseBody> call = api.getNews("in", API_KEY);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    if (result != null) {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = jsonObject.getString("status");
                        int totalItem = jsonObject.getInt("totalResults");
                        if (status.equals("ok") && totalItem > 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("articles");
                            if (jsonArray.length() > 0) {
                                List<PlaceImageModal> imageModalList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonArticle = jsonArray.getJSONObject(i);
                                    String imgUrl = jsonArticle.getString("urlToImage");
                                    PlaceImageModal imageModal = new PlaceImageModal(imgUrl);
                                    imageModalList.add(imageModal);
                                }
                                PlaceImageAdapter imageLoadAdapter = new PlaceImageAdapter(MainActivity.this, imageModalList);
                                recyclerPlace.setAdapter(imageLoadAdapter);
                                imageLoadAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MainActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(MainActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(MainActivity.this, "Internet Issue", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Big Issue", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void callTrendingApi() {
        recyclerTrendings.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        api = retrofit.create(NetworkClient.class);
        Call<ResponseBody> call = api.getNews("in", API_KEY);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Log.i(TAG, "onResponse: " + response.body());
                try {
                    String result = response.body().string();
                    if (result != null) {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = jsonObject.getString("status");
                        int totalItem = jsonObject.getInt("totalResults");
                        if (status.equals("ok") && totalItem > 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("articles");
                            if (jsonArray.length() > 0) {
                                List<TrendingImageModal> imageModalList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonArticle = jsonArray.getJSONObject(i);
                                    String imgUrl = jsonArticle.getString("urlToImage");
                                    TrendingImageModal imageModal = new TrendingImageModal(imgUrl);
//                                Log.i(TAG, "values inside the for loop: " + authorName + "title" + title + "imgUrl" + imgUrl);
                                    imageModalList.add(imageModal);
                                }
                                TrendingImageAdapter imageLoadAdapter = new TrendingImageAdapter(MainActivity.this, imageModalList);
                                recyclerTrendings.setAdapter(imageLoadAdapter);
                                imageLoadAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MainActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(MainActivity.this, "Bad Request", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(MainActivity.this, "Internet Issue", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Big Issue", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
