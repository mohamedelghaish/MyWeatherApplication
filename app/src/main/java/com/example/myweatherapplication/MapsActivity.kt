package com.example.myweatherapplication

import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.myweatherapplication.database.LocalDataSourceImp

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myweatherapplication.databinding.ActivityMapsBinding
import com.example.myweatherapplication.favorite.viewmodel.FavoriteViewModel
import com.example.myweatherapplication.favorite.viewmodel.FavoriteViewModelFactory
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.Repository
import com.example.myweatherapplication.network.RemoteDataSourceImp
import com.google.android.gms.maps.model.Marker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private  val TAG = "GoogleMapsActivity"


    private lateinit var googleMap: GoogleMap
    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var selectedPlace: String
    private var selectedDate: Long = 0
    private lateinit var mapDoneFloatingActionButton: FloatingActionButton
    private lateinit var showSelectedLocation: TextView
    private lateinit var root: ConstraintLayout
    private var isPlaceSelected = false
    private lateinit var viewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()

        initUI()

        val factory = FavoriteViewModelFactory(
            Repository.getInstance(

                RemoteDataSourceImp.getInstance(),
                LocalDataSourceImp.getInstance(this)

            ))
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)

    }

    private fun initUI() {
        selectedPlace = getString(R.string.undefined_place)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        root = findViewById(R.id.map_root)
        showSelectedLocation = findViewById(R.id.show_selected_location_textView)
        mapDoneFloatingActionButton = findViewById(R.id.map_done_FloatingButton)

        mapDoneFloatingActionButton.setOnClickListener {
            if (isPlaceSelected) {
                selectedDate = System.currentTimeMillis() / 1000
                viewModel.insertToFavorite(
                    FavoriteLocation(
                        latitude.toDouble(),
                        longitude.toDouble(),
                        selectedPlace,
                        selectedDate
                    )
                )
                finish()
            } else {
                showSnackBar()
            }
        }
    }


    private fun showSnackBar() {
        val snackBar = Snackbar.make(
            root,
            getString(R.string.no_place_selected),
            Snackbar.LENGTH_SHORT
        ).setActionTextColor(Color.WHITE)
        snackBar.view.setBackgroundColor(Color.BLACK)
        snackBar.show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        val currentLocation = LatLng(Const.latitude.toDouble(), Const.longitude.toDouble())

        val marker = this.googleMap.addMarker(
            MarkerOptions().position(currentLocation).draggable(true)
        )

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        this.googleMap.moveCamera(CameraUpdateFactory.zoomTo(5f))

        this.googleMap.setOnMapClickListener { p0 ->
            if (marker != null) {
                marker.position = p0
                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                val addresses: List<Address>?
                try {
                    addresses = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        showSelectedLocation.text = addresses[0].adminArea
                        marker.title = addresses[0].adminArea
                        latitude = marker.position.latitude.toString()
                        longitude = marker.position.longitude.toString()
                        selectedPlace = addresses[0].adminArea
                        isPlaceSelected = true
                    }
                } catch (exception: Exception) {
                    Log.e(TAG, "onMapClick:exception--------------------> ${exception.message}")
                }
            }
        }

        this.googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}

            override fun onMarkerDragEnd(marker: Marker) {
                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                val addresses: List<Address>?
                try {
                    addresses = geocoder.getFromLocation(
                        marker.position.latitude,
                        marker.position.longitude,
                        1
                    )
                    if (!addresses.isNullOrEmpty()) {
                        showSelectedLocation.text = addresses[0].adminArea
                        latitude = marker.position.latitude.toString()
                        longitude = marker.position.longitude.toString()
                        selectedPlace = addresses[0].adminArea
                        marker.title = selectedPlace
                        isPlaceSelected = true
                    }
                } catch (exception: IOException) {
                    Log.e(TAG, "onMarkerDragEnd: exception-----------------> ${exception.message} ")
                    finish()
                }
            }
        })

    }

}