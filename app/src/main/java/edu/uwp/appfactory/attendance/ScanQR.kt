package edu.uwp.appfactory.attendance

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator

class ScanQR : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)

        val textView: TextView = findViewById(R.id.textView)
        val qrButton: Button = findViewById(R.id.qr_button)
        val backButton: Button = findViewById(R.id.back_button)

        // Go back to the schedule page
        backButton.setOnClickListener {
            val intent = Intent(this, Schedule::class.java)
            ContextCompat.startActivity(this, intent, Bundle())
        }

        // Listen for the QR code
        qrButton.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setDesiredBarcodeFormats(listOf(IntentIntegrator.QR_CODE))
            intentIntegrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var result = IntentIntegrator.parseActivityResult(resultCode, data)
        if (result != null) {
            val classID = result.contents // Assuming the QR code contains classID
            if (classID.isNullOrEmpty()) {
                // Show a toast message indicating invalid QR code
                Toast.makeText(this, "Invalid QR code. Please try again.", Toast.LENGTH_SHORT).show()
                return
            }

            val currentUserID = auth.currentUser?.uid

            if (currentUserID != null) {
                // Get the current date (you can format it as per your requirement)
                val currentDate = System.currentTimeMillis()

                // Create a data object to store in the checkins collection
                val checkinData = hashMapOf(
                    "classID" to classID,
                    "date" to currentDate,
                    "students" to arrayListOf(currentUserID)
                )

                // Add the checkinData to the checkins collection
                db.collection("checkins")
                    .add(checkinData)
                    .addOnSuccessListener { documentReference ->
                        // Check-in data added successfully
                        Log.d("ScanQR", "Check-in data added successfully: ${documentReference.id}")

                        // Show a toast message indicating successful check-in
                        Toast.makeText(this, "Checked in successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        // Handle errors while adding check-in data
                        Log.e("ScanQR", "Error adding check-in data: ${e.message}", e)

                        // Show a toast message indicating check-in failure
                        Toast.makeText(this, "Check-in failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Show a toast message indicating user not authenticated
                Toast.makeText(this, "User not authenticated. Please log in and try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}