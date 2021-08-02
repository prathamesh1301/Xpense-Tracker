package com.example.xpensetracker.Fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.xpensetracker.R
import com.example.xpensetracker.database.XpenseData
import com.example.xpensetracker.database.XpenseDatabase
import kotlinx.android.synthetic.main.dialogbox_layout.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment() {

    lateinit var sharedPreferences: SharedPreferences

    private lateinit var availableBalance: TextView
    var currentDateAndTime: String=""

    lateinit var dataList:ArrayList<XpenseData>
    lateinit var viewx:View
    var food=0
    var electronics=0
    var travelling=0
    var others=0


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewx=view
        val date = view.findViewById<TextView>(R.id.dateTV)
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        currentDateAndTime= simpleDateFormat.format(Date())
        date.text = currentDateAndTime

        dataList= ArrayList()

        sharedPreferences =
            activity?.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)!!
        availableBalance = view.findViewById<TextView>(R.id.availableBalance)
        availableBalance.text = sharedPreferences.getInt("balance", 0).toString()

        fillPieChartValues(view)




        view.addMoneyBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity)  //alert dialog box for add money

            val v: View =
                LayoutInflater.from(context).inflate(R.layout.dialogbox_layout, null, false)
            builder.setView(v)
            builder.setCancelable(true)
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                val moneyInput: EditText = v.findViewById<EditText>(R.id.moneyInput)
                val money: String = moneyInput.text.toString().trim()
                println(money)
                if (money.equals("")) {
                    Toast.makeText(
                        context?.applicationContext,
                        "Fill the field",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                var moneyInt = 0

                try {
                    moneyInt = Integer.parseInt(money)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    val newMoney = sharedPreferences.getInt("balance", 0) + moneyInt
                    availableBalance.text = newMoney.toString()
                    editor.putInt("balance", newMoney)
                    editor.apply()
                    editor.commit()
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
            builder.setNegativeButton("Cancel") { dialogInterface, which -> }
            builder.show()

        }
        val spendMoneyBtn: Button = view.findViewById(R.id.spendMoneyBtn)
        spendMoneyBtn.setOnClickListener {
            spendMoneyFun()
        }

        return view
    }



    fun spendMoneyFun() {
        val builder = AlertDialog.Builder(activity)


        val it: View = LayoutInflater.from(context?.applicationContext)
            .inflate(R.layout.spenddialogbox, null, false)
        builder.setView(it)
        val title: EditText = it.findViewById(R.id.title)
        val amountSpent: EditText = it.findViewById(R.id.amountSpent)
        val spinner: Spinner = it.findViewById(R.id.spinner)
        val categories = resources.getStringArray(R.array.Categories)
        val Category = arrayOf("Food","Electronics","Travelling","Other")
        print("cat: $categories")

        if (spinner != null) {    //setting the spinner with adapter
            val adapter = context?.let { it1 ->
                ArrayAdapter(
                    it1,
                    R.layout.spinnerlayout, categories
                )
            }
            spinner.adapter = adapter
        }

            builder.setPositiveButton("Spend") { dialogInterface, which ->
                val titleTxt: String = title.text.toString().trim()
                val amountSpentTxt: String = amountSpent.text.toString().trim()

                println(amountSpentTxt)

                if (titleTxt.equals("")) {
                    Toast.makeText(context?.applicationContext,"Fill the field",Toast.LENGTH_SHORT).show()

                    return@setPositiveButton
                }
                if (amountSpentTxt.equals("")) {
                    Toast.makeText(context?.applicationContext,"Fill the field",Toast.LENGTH_SHORT).show()

                    return@setPositiveButton
                }
                val moneySpent: Int = Integer.parseInt(amountSpentTxt)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                val newMoney = sharedPreferences.getInt("balance", 0) - moneySpent

                var selectedCategory=""
                spinner.onItemSelectedListener=object :AdapterView.OnItemClickListener,
                    AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                        selectedCategory=Category[p2]
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        // hTODO("Not yet implemented")
                    }

                    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        // TODO("Not yet implemented")
                        selectedCategory=Category[p2]
                    }
                }
                selectedCategory=spinner.selectedItem.toString()
                println("Selected: $selectedCategory")

                if (newMoney >= 0) {
                    val xpenseData=XpenseData(titleTxt,amountSpentTxt,currentDateAndTime,selectedCategory)

                    launch {
                        context?.let { it1 -> XpenseDatabase.getDatabase(it1).getXpenseDao().insert(xpenseData) }
                    }
                    Toast.makeText(context?.applicationContext,"Added successfully to database",Toast.LENGTH_SHORT).show()
                    //fillPieChartValues(viewx)
                    availableBalance.text = newMoney.toString()  //updating the available balance
                    editor.putInt("balance", newMoney)
                    editor.apply()
                    editor.commit()
                } else {
                    Toast.makeText(
                        context?.applicationContext,
                        "Not enough amount in balance",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }




            }

            builder.setNegativeButton("Cancel") { dialogInterface, which -> }

            builder.show()



        }

    fun fillPieChartValues(view:View){

        getDataFromDatabase()
        val mPieChart = view.findViewById<PieChart>(R.id.pieChart)
            Log.d("cheese","$electronics")
            mPieChart.addPieSlice(PieModel("Food", food.toFloat(), Color.parseColor("#FFA500")))
            mPieChart.addPieSlice(PieModel("Electronics", electronics.toFloat(), Color.parseColor("#0000FF")))
            mPieChart.addPieSlice(PieModel("Travelling", travelling.toFloat(), Color.parseColor("#FF0000")))
            mPieChart.addPieSlice(PieModel("Other", others.toFloat(), Color.parseColor("#00FF00")))

            mPieChart.startAnimation()



    }
    fun getDataFromDatabase(){

        runBlocking {
            dataList = context?.applicationContext?.let { XpenseDatabase.getDatabase(it).getXpenseDao().getAllData() } as ArrayList<XpenseData>
            //Log.d("size","${dataList.size}")
            food=0
            travelling=0
            electronics=0
            others=0
            for(item in dataList){
                println("item ${item.category}")
                if(item.category.equals("Food"))
                    food+=Integer.parseInt(item.amount)
                if(item.category.equals("Travelling"))
                    travelling+=Integer.parseInt(item.amount)
                if(item.category.equals("Electronics"))
                    electronics+=Integer.parseInt(item.amount)
                if(item.category.equals("Other"))
                    others+=Integer.parseInt(item.amount)

            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //fillPieChartValues(viewx)
    }


    }
