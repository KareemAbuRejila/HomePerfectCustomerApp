package com.codeshot.home_perfect.ui.user_profile


import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.Common.USERS_REF
import com.codeshot.home_perfect.HomeActivity
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.databinding.DialogUpdateUserInfoBinding
import com.codeshot.home_perfect.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 */
class DialogUpdateUserInfo : DialogFragment() {
    private lateinit var updateUserInfoBinding: DialogUpdateUserInfoBinding

    private var imageUri:Uri?=null


    fun newInstance(): DialogUpdateUserInfo {
        return DialogUpdateUserInfo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set Dialog To FullScreenTheme
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BookingDialogTheme)
        super.setCancelable(false)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        updateUserInfoBinding=DialogUpdateUserInfoBinding.inflate(layoutInflater, container, false)
        return updateUserInfoBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUserInfoBinding.fullDialogClose.setOnClickListener { v: View? ->
            super.dismiss()
        }
        updateUserInfoBinding.edtUserNameDialog.onFocusChangeListener =
            OnFocusChangeListener { v, hasFocus ->
                if (hasFocus)
                    updateUserInfoBinding.btnBookingDialog.visibility= VISIBLE
            }

        updateUserInfoBinding.btnBookingDialog
            .setOnClickListener {
                saveUserData()
            }
        updateUserInfoBinding.imgUserImage.setOnClickListener{showImageInDialog()}
        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.CACHE
        USERS_REF.document(Common.CURRENT_USER_KEY)
            .get(source).addOnSuccessListener { document ->
                if (!document!!.exists()) {
                    updateUserInfoBinding.fullDialogClose.visibility=View.GONE
                }

            }
    }

    private fun saveUserData() {
        if (updateUserInfoBinding.edtUserNameDialog.text!!.isNotEmpty()){
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Set Profile Image")
            progressDialog.setMessage("Please wait....")
            progressDialog.setCancelable(false)
            progressDialog.show()
            val imagesStorageRef=FirebaseStorage.getInstance().reference.child("Users Images")
            val filePath=imagesStorageRef.child(Common.CURRENT_USER_KEY+".jpg")
            val  uploadTask=filePath.putFile(imageUri!!)
            uploadTask.addOnProgressListener { taskSnapshot ->
                val persante=(100.0*taskSnapshot.bytesTransferred)/taskSnapshot.bytesTransferred
                progressDialog.setMessage(persante.toString()+ " %")
            }
                .continueWithTask{
                filePath.downloadUrl }
                .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    val downloadUri=task.result.toString()
                    val user=User(downloadUri,updateUserInfoBinding.edtUserNameDialog.text.toString())
                    user.phone=FirebaseAuth.getInstance().currentUser!!.phoneNumber
                    USERS_REF.document(Common.CURRENT_USER_KEY)
                        .set(user).addOnSuccessListener {
                            progressDialog.dismiss()
                            super.dismiss()
                        }
                }else {
                    // Handle failures
                    // ...
                    val errotMsg = task .exception.toString()
                    Toast.makeText(context, "Error " + errotMsg, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss()
                }
            }

        }else updateUserInfoBinding.edtUserNameDialog.error="Please Enter Your UserName"
    }

    private fun showImageInDialog() {
        val builder = AlertDialog.Builder(context)
        if (imageUri!=null){
            builder.setPositiveButton(
                "View"
            ) { dialog, which ->
                showImageViewer()
            }.setTitle("Choose")
                .setNegativeButton(
                "Edit"
            ) { dialog, which -> chooseImage() }
        }else{
            builder.setTitle("Add personal image")
                .setMessage("please choose your image from your photos")
                .setPositiveButton("Add"){dialog, which ->
                    chooseImage()
                }
        }

        builder.show()
    }
    private fun showImageViewer(){
        val imageDialog =
            AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val imageViewLayout: View = inflater.inflate(R.layout.item_imagesview, null)
        imageDialog.setView(imageViewLayout)
        val imageViewDialog =
            imageViewLayout.findViewById<ImageView>(R.id.imageViewer)
        Picasso.get().load(imageUri).into(imageViewDialog)
        imageDialog.show()
    }
    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select your Pic"),
            1
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data!!
            Picasso.get().load(imageUri).into(updateUserInfoBinding.imgUserImage)
        }

    }
    override fun onDestroy() {
        val parent=activity as HomeActivity
        parent.checkUserData()
        super.onDestroy()
    }
}
