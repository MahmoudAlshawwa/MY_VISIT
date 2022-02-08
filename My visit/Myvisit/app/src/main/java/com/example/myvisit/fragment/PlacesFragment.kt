package com.example.myvisit.fragment



import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myvisit.Adapter.AdapterPlace
import com.example.myvisit.R
import com.example.myvisit.modle.Place
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_places.*
import kotlinx.android.synthetic.main.fragment_places.view.*


class PlacesFragment : Fragment() {

    private lateinit var root: View
    val db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_places, container, false)
        root.rc_places.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)


        getplaces()



        var callback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val data = ArrayList<Place>()
                val placesadapter = AdapterPlace(requireActivity(), data)
                data.removeAt(viewHolder.adapterPosition)
                placesadapter.notifyItemRemoved(viewHolder.adapterPosition)
                placesadapter.notifyItemRangeChanged(viewHolder.layoutPosition,data.size)

            }

        }

        var itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rc_places)


        root.addNewPlace.setOnClickListener {
            findNavController().navigate(R.id.action_placesFragment_to_addplaceFragment)

        }



        return root


    }



    fun getplaces(): ArrayList<Place> {
        val data = ArrayList<Place>()
        db.collection("place")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val place = document.toObject(Place::class.java)
                    data.add(place)
                }
                Log.e("vvvvvv", "data $data")
                val placesadapter = AdapterPlace(requireActivity(),data)
                rc_places.adapter = placesadapter
               // root.rc_places.adapter = placesadapter
                Log.d("tt","ok")
            }
            .addOnFailureListener { e ->
                Log.e("mah", "Error getting document", e)

                //allow read, write: if request.time < timestamp.date(2021, 12, 12);
            }

        return data
    }

}