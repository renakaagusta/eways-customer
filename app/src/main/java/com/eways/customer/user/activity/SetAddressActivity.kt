package com.eways.customer.user.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.dashboard.activity.MainActivity
import com.eways.customer.dashboard.activity.WelcomeActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.proyek.infrastructures.user.customer.usecases.CreateCustomer
import kotlinx.android.synthetic.main.activity_set_address.*
import java.util.*


class SetAddressActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var googleMap: GoogleMap

    private var latitude = 0F
    private var longitude = 0F

    private lateinit var marker: Marker
    private var set = 0

    private lateinit var createUser: CreateCustomer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_address)
        supportActionBar?.hide()

        createUser = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateCustomer::class.java)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Membutuhkan Izin Lokasi", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1
                )
            }
        }

        if(isLocationEnabled(this)!=true) {
            startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        } else {
            SetMap()
        }

        moveToRegisterOTP()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(isLocationEnabled(this)!=true) {
            startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        } else {
            SetMap()
        }
    }

    fun SetMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            googleMap.isMyLocationEnabled = true
            setMapClick(it)
            getDeviceLocation(it)
        })
    }


    fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(
                    context.getContentResolver(),
                    Settings.Secure.LOCATION_MODE
                )
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.LOCATION_MODE
            )
            !TextUtils.isEmpty(locationProviders)
        }
    }


    override fun onMapReady(p0: GoogleMap?) {
        //getDeviceLocation(p0!!)
    }

    private fun getDeviceLocation(map: GoogleMap) {
        val fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        fusedLocation.lastLocation.addOnSuccessListener(this) { location ->
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude,
                        location.longitude), 20F))

            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            tietAddress.setText(addresses[0].getAddressLine(0).toString())
            Log.d("location1", location.latitude.toString() + " " + location.longitude.toString())
        }.addOnFailureListener {
            Log.d("exception", it.message)
        }
    }

    private fun setMapClick(map: GoogleMap) {
        map.clear()
        map.setOnMapClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            latitude = latLng.latitude.toFloat()
            longitude = latLng.longitude.toFloat()
            if(set == 1) marker.remove()
            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Ditambahkan")
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            set = 1

            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            tietAddress.setText(addresses[0].getAddressLine(0).toString())
        }
    }


    private fun moveToRegisterOTP(){
        tvRegister.setOnClickListener {
            if (tietAddress.text?.length!=0) {
                if (longitude != 0F) {
                    intent.apply {
                        createUser.set(
                            getStringExtra("userName")!!,
                            getStringExtra("userName")!!,
                            getStringExtra("phoneNumber")!!,
                            getStringExtra("email")!!,
                            "tes",
                            tietAddress.text.toString(),
                            latitude,
                            longitude
                        )
                    }
                    this@SetAddressActivity.showProgress()
                    createUser.get().observe(this, Observer {
                        this@SetAddressActivity.dismissProgress()
                        if (it.errors.message.isEmpty()) {
                            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Berhasil membuat akun")
                                .setConfirmButton("Oke",{
                                    val intent = Intent(this, WelcomeActivity::class.java)
                                    startActivity(intent)
                                })
                                .show()
                        } else {
                            this@SetAddressActivity.showError(it.errors.message[0])
                        }
                    })
                } else {
                    this@SetAddressActivity.showError("Lokasi belum di pin")
                }
            } else {
                this@SetAddressActivity.showError("Mohon isi alamat anda")
            }
        }
    }
}