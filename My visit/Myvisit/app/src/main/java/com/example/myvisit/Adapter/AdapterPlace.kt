package com.example.myvisit.Adapter

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myvisit.R
import com.example.myvisit.fragment.LocationFragment
import com.example.myvisit.modle.Place
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.place_item.view.*


class AdapterPlace(var activity: Activity, var data: ArrayList<Place>) : RecyclerView.Adapter<AdapterPlace.MyViewHolder>() {
    var db: FirebaseFirestore? = null

    var f_manager: FragmentManager? = null

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.nameplace
        val address = itemView.Address
        val placeimg = itemView.imageplace
        val card = itemView.cardview

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.place_item,
            parent,
            false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.name.text = data[position].name
        holder.address.text = data[position].address
        Picasso.get().load(Uri.parse(data[position].imageplace))
            .into(holder.placeimg)
        Log.e("mah", data[position].imageplace!!)
        // notifyDataSetChanged()


        holder.itemView.setOnClickListener {

          //  var intent = Intent(holder.itemView.context, LocationFragment::class.java)


//            findNavController(Fragment()).navigate(R.id.action_placesFragment_to_locationFragment)
        var locf = LocationFragment()
//            activity.getFragmentManager().beginTransaction()
//                .replace(R.id.container, locf as android.app.Fragment)
//                .commit()



        //    findNavController(Fragment()).navigate(R.id.action_placesFragment_to_locationFragment)




            val fragmentTransaction = activity.fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment_container,locf as android.app.Fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

       }

       holder.itemView.setOnLongClickListener {


            val options = arrayOf<CharSequence>("Yes", "No", "Cancel")
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
            builder.setTitle("Delete Place --> Are You Sure")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
                if (options[item] == "Yes") {
                    deleteUserById(data[position].id!!)
                    data.removeAt(position);
                    deletephotobyID(data[position].imageplace!!)
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, data.size)
                    notifyDataSetChanged()

                    Log.e("moh", data[position].imageplace!!)

                } else if (options[item] == "No") {

                    dialog.dismiss()


                } else if (options[item] == "Cancel") {
                    dialog.dismiss()
                }
            })
            builder.show()







            true
        }
    }


    private fun deletephotobyID(id: String) {
        var storage = Firebase.storage
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images")

        val image = imagesRef.child(id)

        image.delete().addOnSuccessListener {
            Log.e("moh", "-> Image Deleted")

        }.addOnFailureListener {
            // Uh-oh, an error occurred!
        }
    }


    private fun deleteUserById(id: String) {
        db = Firebase.firestore
        db!!.collection("Place").document(id)
            .delete().addOnSuccessListener {

            }.addOnFailureListener {
                Log.e("AppConstants.TAG", "aaaaa")
            }
    }
}

