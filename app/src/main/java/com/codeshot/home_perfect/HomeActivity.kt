package com.codeshot.home_perfect

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import cc.cloudist.acplibrary.ACProgressBaseDialog
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.Common.CURRENT_LOCATION
import com.codeshot.home_perfect.common.Common.CURRENT_TOKEN
import com.codeshot.home_perfect.common.Common.CURRENT_USER_IMAGE
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.CURRENT_USER_NAME
import com.codeshot.home_perfect.common.Common.CURRENT_USER_PHONE
import com.codeshot.home_perfect.common.Common.LOADING_DIALOG
import com.codeshot.home_perfect.common.Common.SHARED_PREF
import com.codeshot.home_perfect.common.Common.USERS_REF
import com.codeshot.home_perfect.common.StandardActivity
import com.codeshot.home_perfect.databinding.ActivityHomeBinding
import com.codeshot.home_perfect.databinding.DialogRaringBinding
import com.codeshot.home_perfect.databinding.NavHeaderMainBinding
import com.codeshot.home_perfect.models.Token
import com.codeshot.home_perfect.models.User
import com.codeshot.home_perfect.ui.LoginActivity
import com.codeshot.home_perfect.ui.notifications.NotificationsDialog
import com.codeshot.home_perfect.ui.user_profile.DialogUpdateUserInfo
import com.codeshot.home_perfect.util.PermissionUtil
import com.codeshot.home_perfect.util.UIUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Source
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import org.imperiumlabs.geofirestore.GeoFirestore

class HomeActivity : StandardActivity(),
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    lateinit var activityHomeBinding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding
    private lateinit var navController: NavController
    private lateinit var dialogUpdateUserInfo: DialogUpdateUserInfo
    private lateinit var loadingDialog: ACProgressBaseDialog

    private var currentLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        dialogUpdateUserInfo = DialogUpdateUserInfo()
            .newInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        loadingDialog = LOADING_DIALOG(this)

        CURRENT_USER_KEY = FirebaseAuth.getInstance().currentUser!!.uid
        CURRENT_USER_PHONE = FirebaseAuth.getInstance().currentUser!!.email.toString()

        checkIntent()
        setUpDrawerNav()
        setUpLocation()

    }

    private fun checkIntent() {
        if (intent != null) {
            if (intent.getStringExtra("user") != null) {
                if (intent.getStringExtra("user") == "new") {
                    checkNewUserData()
                    checkToken()
                } else if (intent.getStringExtra("user") == "old") {
                    checkUserData()
                }
            } else if (intent.getStringExtra("type") != null) {
                val type = intent.getStringExtra("type")
                if (type == "rating") {
                    showRatingDialog()
                }
            }
        }
    }

    private fun showRatingDialog() {
        val dialogRaringBinding = DialogRaringBinding.inflate(layoutInflater)
        val ratingDialog = AlertDialog.Builder(this)
            .setView(dialogRaringBinding.root)
            .create()
        ratingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogRaringBinding.btnRate.setOnClickListener { ratingDialog.dismiss() }
        dialogRaringBinding.ratingBarRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            dialogRaringBinding.btnRate.isEnabled = rating != 0F
        }
        ratingDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                NotificationsDialog().show(supportFragmentManager, "NotificationsDialog")
                true
            }
            else -> false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun checkToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                updateTokenToServer(token)
                CURRENT_TOKEN = token
                val sharedPreferences = getSharedPreferences(
                    "com.codeshot.home_perfect",
                    Context.MODE_PRIVATE
                )
                sharedPreferences.edit().putString("token", token).apply()
            }
        }


    }

    private fun updateTokenToServer(newToken: String) {
        val token = Token(newToken)
        if (FirebaseAuth.getInstance().currentUser != null) {
            Common.TOKENS_REF.document(CURRENT_USER_KEY)
                .set(token)
                .addOnSuccessListener { Log.i("Saved Token", "Yesssssssssssssssssssssss") }
                .addOnFailureListener { e -> Log.i("ERROR TOKEN", e.message!!) }

        }
    }

    fun checkUserData() {
        loadingDialog.show()
        // Source can be CACHE, SERVER, or DEFAULT.
        USERS_REF.document(CURRENT_USER_KEY)
            .get().addOnSuccessListener { document ->
                if (!document!!.exists()) {
                    dialogUpdateUserInfo.show(this.supportFragmentManager, "tag")
                } else {
                    val currentUser = document.toObject(User::class.java)
                    CURRENT_USER_NAME = currentUser!!.userName!!
                    CURRENT_USER_IMAGE = currentUser.personalImageUri!!
                    navHeaderMainBinding =
                        NavHeaderMainBinding.bind(activityHomeBinding.navView.getHeaderView(0))
                    navHeaderMainBinding.user = currentUser
                    currentUser.id = CURRENT_USER_KEY
                    currentUser.phone = CURRENT_USER_PHONE
                    val userGSON = Gson().toJson(currentUser)
                    SHARED_PREF(this).edit().putString("user", userGSON).apply()
                }
                loadingDialog.dismiss()
            }.addOnFailureListener {
                val userGSON = SHARED_PREF(this).getString("user", null)
                if (userGSON != null) {
                    navHeaderMainBinding =
                        NavHeaderMainBinding.bind(activityHomeBinding.navView.getHeaderView(0))
                    val user = Gson().fromJson<User>(userGSON, User::class.java)
                    navHeaderMainBinding.user = user
                    loadingDialog.dismiss()
                } else {
                    signOut()
                    return@addOnFailureListener
                }
            }
    }

    private fun checkNewUserData() {

        loadingDialog.show()

        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.SERVER
        USERS_REF.document(CURRENT_USER_KEY)
            .get().addOnSuccessListener { document ->
                if (!document!!.exists()) {
                    dialogUpdateUserInfo.show(this.supportFragmentManager, "tag")
                } else {
                    val user = document.toObject(User::class.java)
                    CURRENT_USER_NAME = user!!.userName!!
                    CURRENT_USER_IMAGE = user.personalImageUri!!
                    navHeaderMainBinding =
                        NavHeaderMainBinding.bind(activityHomeBinding.navView.getHeaderView(0))
                    user.id = CURRENT_USER_KEY
                    user.phone = CURRENT_USER_PHONE
                    val userGSON = Gson().toJson(user)
                    SHARED_PREF(this).edit().putString("user", userGSON).apply()
                    navHeaderMainBinding.user = user
                    Snackbar.make(
                        activityHomeBinding.navView,
                        "Welcome ${user.userName}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                loadingDialog.hide()
            }.addOnFailureListener {
                val userGSON = SHARED_PREF(this).getString("user", null)
                if (userGSON != null) {
                    val user = Gson().fromJson<User>(userGSON, User::class.java)
                    navHeaderMainBinding =
                        NavHeaderMainBinding.bind(activityHomeBinding.navView.getHeaderView(0))
                    navHeaderMainBinding.user = user
                } else {
                    signOut()
                    return@addOnFailureListener
                }
            }
    }

    private fun setUpDrawerNav() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_wishlist,
                R.id.nav_myBooking, R.id.nav_aboutUs
            ), activityHomeBinding.drawerLayout
        )
        activityHomeBinding.navView.setupWithNavController(navController)
        val collapsingToolbarLayout: CollapsingToolbarLayout = findViewById(R.id.colappsingtoolbar)
        collapsingToolbarLayout.setupWithNavController(toolbar, navController, appBarConfiguration)


        val itemLogout = activityHomeBinding.navView.menu.findItem(R.id.nav_logOut)
        itemLogout.setOnMenuItemClickListener {
            signOut()
            return@setOnMenuItemClickListener true
        }

    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            )
        )
        SHARED_PREF(this).edit().clear().apply()
        finish()
    }

    private fun setUpLocation() {
        if (PermissionUtil.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION, this)
            && PermissionUtil.isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION, this)
        ) {
            //Request runtime permission
            PermissionUtil.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient()
                createLocationRequest()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkPlayServices()) {
                buildGoogleApiClient()
                createLocationRequest()
            }
        }
    }

    //PlayService
    private val LOCATION_PERMISSION_REQUEST_CODE = 7000
    private val PLAY_SERVICE_RES_REQUEST = 7001

    //LocationRequest
    private var UPDATE_INTERVAL: Int = 5000
    private val FASTEST_INTERVAL = 3000
    private val DISPLACEMENT = 10

    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private val mLastLocation: Location? = null
    private fun checkPlayServices(): Boolean {
        val resultCode =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    PLAY_SERVICE_RES_REQUEST
                ).show()
            } else {
                UIUtil.showLongToast("This Device is not supported", this)
            }
            return false
        }
        return true
    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL.toLong()
        mLocationRequest!!.fastestInterval = FASTEST_INTERVAL.toLong()
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.smallestDisplacement = DISPLACEMENT.toFloat()
    }

    override fun onConnected(p0: Bundle?) {
        startLocationUpdates()
    }

    override fun onConnectionSuspended(p0: Int) {
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        UIUtil.showShortToast(p0.errorMessage!!, this)
    }

    override fun onLocationChanged(p0: Location?) {
        this.currentLocation = p0
        CURRENT_LOCATION = p0
        val geo = GeoFirestore(USERS_REF.document(CURRENT_USER_KEY).collection("location"))
        geo.setLocation("currentLocation", GeoPoint(p0!!.latitude, p0.longitude))
//            .update("currentLocation",p0)
    }

    private fun startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
        }
    }

}
