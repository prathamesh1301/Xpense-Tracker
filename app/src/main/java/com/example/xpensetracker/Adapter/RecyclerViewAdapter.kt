package com.example.xpensetracker.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity

import androidx.recyclerview.widget.RecyclerView
import com.example.xpensetracker.Fragments.BaseFragment
import com.example.xpensetracker.database.XpenseData
import com.example.xpensetracker.R
import com.example.xpensetracker.database.XpenseDatabase
import kotlinx.coroutines.runBlocking

class RecyclerViewAdapter(val dataList:ArrayList<XpenseData>,val context:Context,val activity:FragmentActivity,val totalBalance:TextView,var balance:Int): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val titleTV=itemView.findViewById<TextView>(R.id.historyTitle)
        val amountTV=itemView.findViewById<TextView>(R.id.historyAmount)
        val dateTV=itemView.findViewById<TextView>(R.id.historyDate)
        val deleteImg=itemView.findViewById<ImageView>(R.id.deleteImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.history_item,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val xpenseObject=dataList.get(position)
        holder.titleTV.text=xpenseObject.title
        holder.dateTV.text=xpenseObject.date
        holder.amountTV.text=xpenseObject.amount
        holder.deleteImg.setOnClickListener {
            val builder=AlertDialog.Builder(activity)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure?")
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                runBlocking {
                    XpenseDatabase.getDatabase(context).getXpenseDao().delete(xpenseObject)
                    dataList.remove(xpenseObject)
                    balance-=Integer.parseInt(xpenseObject.amount)
                    totalBalance.text="Total: $balance"
                    notifyDataSetChanged()
                    Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show()
                }

            }
            builder.setNegativeButton("Cancel") { dialogInterface, which -> }
            builder.show()

        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}