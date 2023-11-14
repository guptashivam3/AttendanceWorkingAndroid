package edu.uwp.appfactory.attendance

import android.content.Context
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.concurrent.thread


//Class to pull from firebase
class Datasource(val context: Context) {
    val db= Firebase.firestore//the database
    val fAuth = FirebaseAuth.getInstance()//used to get firebase user
    val classesTaken= ArrayList<ArrayList<String>>()//this is a list of the classes being taken
    //each class is its own array list
    var tobegone = arrayListOf<String>()//used to parse the classes into a string array

    //gathers the classes the user is taking calls parse to properly parse out the strings
    fun fetch()
    {
        //db.collection(("users"))
        //gets the classes the user is taking
        val ref = db.collection("classes").whereArrayContains("students", fAuth.currentUser?.uid.toString())
        ref.get().addOnSuccessListener {documents ->
            if (documents != null) {
                for (document in documents)
                {
                    //iterates through the classes the user is taking and storing the class details into an array list
                        //then storing that arraylist into the array list of array lists
                    val classDetails = ArrayList<String>()
                    classDetails.add(document.getDate("endDate").toString())//end date is spot 0
                    classDetails.add(document.getString("location").toString())//location is spot 1
                    classDetails.add(document.getString("name").toString())//name of class is spot 2

                    classesTaken.add(classDetails)//add the class to the list of classes of being taken
                }
                tobegone= parse(classesTaken)//sets an array of strings that are already parsed to be presented
            } else {
                println("no didnt work coudnt find document")//probably store this error somewhere
            }
        }
            .addOnFailureListener { exception ->
                println("get failed with: ${exception}")//this one too
            }
    }

    //parses each class into a string then saves the string
    fun parse(list: ArrayList<ArrayList<String>>): ArrayList<String>
    {
        val toBeConverted= ArrayList<String>()//array list of class details
        var fString = ""//temp string
        for (i in list)
        {
            //iterates through the classes being taken and stores their details into one string
            fString=i.get(2)+"\nRoom "+i.get(1)+"\n"+i.get(0).dropLast(18)+ " is the last day of class\n"
            toBeConverted.add(fString)//add the string to the array list
        }
        return toBeConverted//return the arraylist
    }
}