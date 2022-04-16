package com.eways.customer.user.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.dashboard.activity.WelcomeActivity
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.google.android.gms.maps.SupportMapFragment
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.user.customer.network.CustomerApiServices
import com.proyek.infrastructures.user.customer.usecases.GetCustomerDetail
import com.proyek.infrastructures.user.customer.usecases.UploadImageProfileCustomer
import com.proyek.infrastructures.user.user.usecases.GetUserDetail
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ProfileActivity : BaseActivity() {
    private lateinit var user: UserCustomer
    private val GALLERY = 1
    private val CAMERA = 2
    private val IMAGE_DIRECTORY = "/profile"

    private lateinit var uploadImageProfileCustomer: UploadImageProfileCustomer
    private lateinit var getUserDetail: GetCustomerDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        uploadImageProfileCustomer = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UploadImageProfileCustomer::class.java)
        getUserDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetCustomerDetail::class.java)

        ivChangeImage.setOnClickListener {
            showPictureDialog()
        }

        requestPermission()

        moveToProfileUpdate()

        logout()

        CustomSupportActionBar.setCustomActionBar(this, "Profil" )
    }

    override fun onStart() {
        super.onStart()

        getData()
    }

    private fun getData() {
        if (Authenticated.isValidCacheMember()){
            setProfile()
        } else {
            this@ProfileActivity.showProgress()

            getUserDetail.set(user.ID!!)
            getUserDetail.get().observe(this, Observer {
                user = it.data[0]
                Authenticated.setUserCustomer(user)
                
                setProfile()

                this@ProfileActivity.dismissProgress()
            })
        }
    }

    private fun setProfile(){
        user = Authenticated.getUserCustomer()
        if(user.imagePath!=null)
            Glide.with(this)
                .load("http://13.229.200.77:8001/storage/${user.imagePath}")
                .into(civUserImage)

        tvUserName.setText(user.username)
        tvUserPhone.setText(user.phoneNumber)
        tvUserEmail.setText(user.email)
        tvUserAddress.setText(user.address)
    }

    fun requestPermission() {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.CAMERA
                    )
                ) {
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.CAMERA
                        ),
                        1
                    )
                }
            }
        }

        private fun showPictureDialog() {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Pilih foto dari")
            val pictureDialogItems = arrayOf("Galeri", "Kamera")
            pictureDialog.setItems(pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    0 -> choosePhotoFromGallary()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }

        fun choosePhotoFromGallary() {
            val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(galleryIntent, GALLERY)
        }

        private fun takePhotoFromCamera() {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
        }

        public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == GALLERY)
            {
                if (data != null)
                {
                    val contentURI = data!!.data
                    try
                    {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        Glide.with(this)
                            .load(bitmap)
                            .into(civUserImage)
                        val file = saveImage(bitmap)
                        uploadImage(file)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

            }
            else if (requestCode == CAMERA)
            {
                val bitmap = data!!.extras!!.get("data") as Bitmap
                Glide.with(this)
                    .load(bitmap)
                    .into(civUserImage)
                val file = saveImage(bitmap)
                uploadImage(file)
            }
        }

        fun uploadImage(file: File) {
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)
            showProgress()
            uploadImageProfileCustomer.set(user.ID!!, body)
            uploadImageProfileCustomer.get().observe(this, Observer {
                dismissProgress()
                getUserDetail.set(user.ID!!)
                getUserDetail.get().observe(this, Observer {
                    user = it.data[0]
                    Authenticated.setUserCustomer(user)

                    setProfile()
                })
            })
        }

        fun saveImage(bitmap: Bitmap): File {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile.jpg")
            val bos = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos)
            val bitmapdata = bos.toByteArray()

            try {
                val fos = FileOutputStream(file)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return file
        }

    private fun unverifiedAccount(){
        tvUserStatus.setBackgroundResource(R.color.colorPrimary)
        tvUserStatus.setText(R.string.unverified)
    }

    private fun  moveToProfileUpdate(){
        tvUpdate.setOnClickListener {
            val intent = Intent(this, ProfileUpdateActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

        private fun logout(){
            tvLogout.setOnClickListener {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                Authenticated.destroySession()
                startActivity(intent)
                finish()
            }
        }

    }