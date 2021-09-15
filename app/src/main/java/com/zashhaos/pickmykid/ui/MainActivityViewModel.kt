package com.zashhaos.pickmykid.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zashhaos.pickmykid.data.Kid

const val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {

    private var _kids = MutableLiveData<List<Kid>>()

    fun kids(): LiveData<List<Kid>> {
        return _kids
    }

    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    init {
        db.collection("pickMyKid").document("123").collection("kids").get()


            .addOnSuccessListener { documents ->
                var kids = mutableListOf<Kid>()
                for (document in documents.documents) {
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        document.toObject(Kid::class.java)?.let { kids.add(it) }

                    } else {
                        Log.d(TAG, "No such document")
                    }
                }

                _kids.value = kids

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }


    fun getName(): String {
        return "shadi"
    }



}