package com.codeshot.home_perfect.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.USERS_REF
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.databinding.FragmentProfileBinding
import com.codeshot.home_perfect.models.User
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var profileBinding: FragmentProfileBinding
    private var user:User= User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel=ViewModelProvider.AndroidViewModelFactory(requireActivity().application).create(
            ProfileViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false)
        return profileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.user.observe(viewLifecycleOwner, Observer {
            user=it
            profileBinding.user=it
        })

        profileBinding.userImage.setOnClickListener {
            showImageInDialog()
        }
        profileBinding.layoutUserName.setOnClickListener {
            showEditDialog("User Name","Your name makes it easy to Providers to confirm who they's booking",
                R.drawable.ic_person_black_24dp,"userName")
        }
        profileBinding.layoutEmail.setOnClickListener {
            showEditDialog("Email Address","Receive info about new update and awesome promos in your inbox",
                R.drawable.ic_email_black_24dp,"email")
        }
        profileBinding.layoutGender.setOnClickListener {
            showGenderDialog()
        }
        profileBinding.layoutDob.setOnClickListener {
            showDobDialog()
        }
    }

    private fun showEditDialog(title:String,des:String,icon:Int, tag:String){
        val editText=EditText(context)
        val layoutParams= FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        editText.isSingleLine=true
        layoutParams.marginStart=dpToPx(16)
        layoutParams.marginEnd=dpToPx(16)
        editText.layoutParams=layoutParams
        editText.hint=title
        editText.marqueeRepeatLimit=16
        if (tag=="userName"){
            editText.inputType=android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME

        }else if (tag=="email"){
            editText.inputType=android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        val editDialog= AlertDialog.Builder(context)
            .setTitle(title).setMessage(des)
            .setPositiveButton("Update", DialogInterface.OnClickListener { dialog, which ->
                USERS_REF.document(CURRENT_USER_KEY)
                    .update(tag,editText.text.toString()).addOnSuccessListener {
                        if (tag=="userName"){
                            profileBinding.tvUserNAme.text=editText.text.toString()
                        }else if (tag=="email"){
                            profileBinding.tvUserEmail.text=editText.text.toString()
                        }
                    }
            }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            }).setView(editText).setIcon(icon).create()
        editDialog.show()
    }
    private fun showGenderDialog(){
        val genderDialog=AlertDialog.Builder(context)
        val genderArray= arrayOf("Male","Female")

        genderDialog.setTitle("Your Gender")
            .setItems(genderArray, DialogInterface.OnClickListener { dialog, which ->
            if (which==0){
                USERS_REF.document(CURRENT_USER_KEY)
                    .update("gender","Male").addOnSuccessListener {
                        profileBinding.tvUserGender.text="Male"
                    }
            }else if (which==1){
                USERS_REF.document(CURRENT_USER_KEY)
                    .update("gender","Female").addOnSuccessListener {
                        profileBinding.tvUserGender.text="Female"
                    }
            }
        })
        genderDialog.show()
    }
    private fun showDobDialog(){
        val dateDialog=DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val c=Calendar.getInstance()
            c.set(Calendar.YEAR,year)
            c.set(Calendar.MONTH,month)
            c.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            val birthay= SimpleDateFormat("dd MMM YYYY", Locale.getDefault()).format(c.time)
            USERS_REF.document(CURRENT_USER_KEY)
                .update("bod",birthay).addOnSuccessListener {
                    profileBinding.tvDobNAme.text=birthay
                }
        }, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH)
        dateDialog.show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    private val Gellary_Key = 7000
    private var imageUri: Uri? = null

    private fun showImageInDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose!")
        if (imageUri!=null || user.personalImageUri!=null){
            builder.setPositiveButton("View") { dialog, which ->
                if (imageUri!=null){
                    showImageViewer(imageUri.toString())
                }else if (user.personalImageUri!=null){
                    showImageViewer(user.personalImageUri!!)

                } else Toast.makeText(context, "Image Not Founded", Toast.LENGTH_SHORT).show()
            }.setNegativeButton("Edit") { dialog, which ->
                chooseImage()
            }
        }else{
            builder.setTitle("Choose Personal Image").setMessage("please Select you Image !")
                .setPositiveButton("Add"){dialog, which ->
                    chooseImage()
                }
        }

        builder.show()
    }
    private fun showImageViewer(imageUri: String) {
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
            Gellary_Key
        )
    }
    private fun uploadProviderImage(localImageURI: Uri) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Set Profile Image")
        progressDialog.setMessage("Please wait....")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val imagesStorageRef = FirebaseStorage.getInstance().reference.child("Users Images")
        val filePath = imagesStorageRef.child(Common.CURRENT_USER_PHONE + ".jpg")
        val uploadTask = filePath.putFile(localImageURI)
        uploadTask.addOnProgressListener { taskSnapshot ->
            val parsente=(100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.bytesTransferred
            progressDialog.setMessage(parsente.toString() + " %")
        }
            .continueWithTask {
                filePath.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result.toString()
                    USERS_REF.document(CURRENT_USER_KEY)
                        .update("personalImageUri",downloadUri)
                        .addOnSuccessListener {
                            Picasso.get().load(downloadUri).into(profileBinding.userImage)
                            progressDialog.dismiss()
                        }
                } else {
                    val errorMsg = task.exception.toString()
                    Toast.makeText(context, "Error $errorMsg", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss()
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Gellary_Key && resultCode == Activity.RESULT_OK && data != null) {

                val localImageURI=data.data!!
                uploadProviderImage(localImageURI)
        }
    }

}