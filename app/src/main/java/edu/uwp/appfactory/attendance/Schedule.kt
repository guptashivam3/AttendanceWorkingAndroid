package edu.uwp.appfactory.attendance;

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class Schedule : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val data = Datasource(context = this)
        data.fetch()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val layoutManager = GridLayoutManager(this, 2) // 2 columns in the grid
        recyclerView.layoutManager = layoutManager
        val scheduleList = data.tobegone.toTypedArray()
        recyclerView.adapter = ScheduleAdapter(scheduleList)


        //TODO Delete later next line
        Log.d("LeonardDebug", data::class.toString())

        //wait till fetch is done to do this
        //TODO find a better way to sync async?
        Handler().postDelayed({
            println("everything done")
            val scheduleList = data.tobegone.toTypedArray()

            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            recyclerView.adapter = ScheduleAdapter(scheduleList)

            val addButton: ImageButton = findViewById(R.id.imageView)
            addButton.setOnClickListener {
                showAddDialog()
            }
        }, 2000)
    }

    private fun showAddDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_layout, null)

        val codeEditText: EditText = dialogView.findViewById(R.id.e1)
        builder.setView(dialogView)

        builder.setPositiveButton("OK") { dialog, which ->
            try {
                val enteredCode = codeEditText.text.toString()

                // Validate the entered code against Firebase
                validateCode(enteredCode) { isValid ->
                    if (isValid)
                    {
                        Toast.makeText(this@Schedule, "Valid code", Toast.LENGTH_SHORT).show()
                        enrollStudentToClass(enteredCode)
                        val intent = Intent(this@Schedule, Schedule::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        // Display toast for invalid code
                        Toast.makeText(this@Schedule, "Invalid code. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            catch (e: Exception) {
                // Handle exceptions related to code validation or enrollment
                Toast.makeText(this@Schedule, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            try {
                Toast.makeText(this@Schedule, "Canceled", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } catch (e: Exception) {
                // Handle exceptions related to cancel action
                Toast.makeText(this@Schedule, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun validateCode(enteredCode: String, callback: (Boolean) -> Unit) {
        Log.d("ValidationDebug", "Entered code: $enteredCode")

        db.collection("classes").document(enteredCode.trim()) // Trim to remove any leading or trailing whitespace
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    Log.d("ValidationDebug", "Document data: ${documentSnapshot.data}")
                    Log.d("ValidationDebug", "Code is valid.")
                    callback(true)
                } else {
                    Log.d("ValidationDebug", "No such document with code: $enteredCode")
                    callback(false)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ValidationDebug", "Validation failed: ${exception.message}", exception)
                callback(false)
            }
    }

    private fun enrollStudentToClass(enteredCode: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Get the current user's UID
            val userId = user.uid
            // Access the Firebase Firestore instance
            val db = FirebaseFirestore.getInstance()

            // Add the user's ID to the "students" array under the specified class code
            db.collection("classes").document(enteredCode)
                .update("students", FieldValue.arrayUnion(userId))
                .addOnSuccessListener {
                    // Student enrolled successfully
                    Toast.makeText(this@Schedule, "Enrolled successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception: Exception ->
                    // Handle enrollment failure
                    Toast.makeText(this@Schedule, "Enrollment failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


}