package com.example.myvisit.fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myvisit.R
import com.example.myvisit.modle.Place
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.theartofdev.edmodo.cropper.CropImageView.OnCropImageCompleteListener
import kotlinx.android.synthetic.main.fragment_addplace.*
import kotlinx.android.synthetic.main.fragment_addplace.view.*
import kotlin.collections.HashMap


class AddPlaceFragment : Fragment() , OnCropImageCompleteListener {
    var db: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var resultUri = "result"
    private lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = Firebase.firestore
        storage = Firebase.storage
        root = inflater.inflate(R.layout.fragment_addplace, container, false)
        root.addImage.setOnClickListener {
            getContext()?.let { it1 ->
                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(it1, this)
            }
        }
        root.addplace.setOnClickListener {
            val name = addname.text.toString()
            val address = addadress.text.toString()
            when {
                name.isEmpty() -> {
                    Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show()
                }
                address.isEmpty() -> {
                    Toast.makeText(context, "Address is required", Toast.LENGTH_SHORT).show()
                }
                resultUri.isEmpty() -> {
                    Toast.makeText(context, "Image  is required", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val sharedPreference = context?.getSharedPreferences("sh_app", Context.MODE_PRIVATE)
                    var lat: String = sharedPreference?.getString("latitude", "").toString()
                    var lang: String = sharedPreference?.getString("longitude", "").toString()
                    Addplace(name, address, resultUri.toString(), lat.toDouble(), lang.toDouble())

                    findNavController().navigate(R.id.action_addplaceFragment_to_placesFragment)
                }
            }
        }
        return root
    }

    private fun Addplace(name: String, address: String, image: String, lat: Double, lan: Double) {
        Log.d("c","6")
        var place = Place(name, address, image, lat, lan)
        Log.d("c","c1")
        db!!.collection("place")
            .add(place)
            .addOnSuccessListener { documentReference ->
                Log.d("c","c2")
                var id = documentReference.id
                val map = HashMap<String, Any>()
                map["id"] = id
                Log.d("c","c3")
                db!!.collection("place").document(id).update(map)
                Log.d("c","c4")
            }
            .addOnFailureListener { e ->
                Log.w("mm", "Error adding document", e)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
               resultUri = result.uri.toString()
                Picasso.get().load(resultUri).into(root.addImage)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
    override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
        Log.e("oo","loadcomp")
        resultUri = result!!.uri.toString()
    }
}