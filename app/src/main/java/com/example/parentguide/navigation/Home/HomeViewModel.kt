package com.example.parentguide.navigation.Home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parentguide.Models.KidData
import com.example.parentguide.Models.KidResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()
    fun onKidResult(result: KidResult) {
        _state.update {
            it.copy(
                isCreateKidUserSuccessful  = result.data != null,
                CreateKidUserError  = result.errorMessage
            )
        }
    }

    private val dbRef = FirebaseDatabase.getInstance().getReference("your_data_path")

    // LiveData to observe data changes
    val liveData = MutableLiveData<KidData?>()

    fun fetchData() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue<KidData>()
                liveData.value = data
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    val response: MutableState<DataState> = mutableStateOf(DataState.Empty)

    init {
        fetchDataFromFirebase()
    }

     fun fetchDataFromFirebase() {
        val tempList = mutableListOf<KidData>()
        response.value = DataState.Loading
        FirebaseDatabase.getInstance().getReference("Kids Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (DataSnap in snapshot.children) {
                        val kidItem = DataSnap.getValue(KidData::class.java)
                        if (kidItem != null)
                            tempList.add(kidItem)
                    }
                    response.value = DataState.Success(tempList)
                }

                override fun onCancelled(error: DatabaseError) {
                    response.value = DataState.Failure(error.message)
                }

            })
    }
}

