package com.example.findyourpets.fragment

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.findyourpets.R
import com.example.findyourpets.`object`.Report
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ReportDialogFragment : BottomSheetDialogFragment() {
    private lateinit var reportButtonConfirm: Button
    private lateinit var reportButtonCancel: TextView
    private lateinit var ref : DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var curUser: FirebaseUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.report_sheet, container, false)

        val prefs: SharedPreferences = activity?.getSharedPreferences("reportPrefs", MODE_PRIVATE)!!
        val postGetReportedID : String = prefs.getString("postGetReportedID",null)!!
        val userGetReportedID: String = prefs.getString("userGetReported", null)!!

        mAuth= FirebaseAuth.getInstance()
        curUser = mAuth.currentUser!!
        ref =FirebaseDatabase.getInstance().reference

        reportButtonConfirm = rootView.findViewById(R.id.reportButtonConfirm)
        reportButtonCancel = rootView.findViewById(R.id.reportButtonCancel)

        val reportID = ref.child("Reports/").push().key

        val report= Report(curUser.uid.toLowerCase(), userGetReportedID, System.currentTimeMillis(), postGetReportedID)




        reportButtonCancel.setOnClickListener{
            dismiss()
        }

        reportButtonConfirm.setOnClickListener {
            ref.child("Reports/").child(reportID!!).setValue(report)
            Toast.makeText(context, "Report Successfully", Toast.LENGTH_SHORT).show()
            dismiss()
        }



        return rootView
    }
}