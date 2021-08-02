package com.example.xpensetracker.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensetracker.Adapter.RecyclerViewAdapter
import com.example.xpensetracker.R
import com.example.xpensetracker.database.XpenseData
import com.example.xpensetracker.database.XpenseDatabase
import kotlinx.coroutines.launch



class HistoryFragment : BaseFragment() {

    lateinit var dataList:ArrayList<XpenseData>
    lateinit var historyRecyclerView: RecyclerView
    lateinit var totalBalance:TextView
    lateinit var deleteAllBtn:Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        dataList= ArrayList()
        totalBalance=view.findViewById(R.id.totalBalance)
        deleteAllBtn=view.findViewById(R.id.deleteAllBtn)
        historyRecyclerView=view.findViewById(R.id.historyRecyclerView)
        historyRecyclerView.setHasFixedSize(true)
        historyRecyclerView.layoutManager=LinearLayoutManager(context)

        getDataFromDatabase()

        deleteAllBtn.setOnClickListener {

                val builder= AlertDialog.Builder(activity)
                builder.setTitle("Delete Everything")
                builder.setMessage("Are you sure?")
                builder.setPositiveButton("Yes") { dialogInterface, which ->
                    launch {
                        context?.let { it1 -> XpenseDatabase.getDatabase(it1).getXpenseDao().deleteAll() }
                        Toast.makeText(context?.applicationContext,"Deleted everything",Toast.LENGTH_SHORT).show()
                        totalBalance.text="Total: 0"
                        getDataFromDatabase()
                    }

                }
                builder.setNegativeButton("No") { dialogInterface, which ->}
                builder.show()

        }



        return view
    }

    fun getDataFromDatabase(){
        launch {
           dataList = context?.applicationContext?.let { XpenseDatabase.getDatabase(it).getXpenseDao().getAllData() } as ArrayList<XpenseData>
            var Balance=0;
            for(i in dataList){
                Balance+=Integer.parseInt(i.amount)
            }
            totalBalance.text="Total: $Balance"
            val adapter= activity?.let { RecyclerViewAdapter(dataList, context!!, it,totalBalance,Balance) }


            historyRecyclerView.adapter=adapter
            Log.d("dataList","$dataList")


        }

    }


}