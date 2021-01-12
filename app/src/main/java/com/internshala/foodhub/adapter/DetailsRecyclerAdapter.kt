package com.internshala.foodhub.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodhub.R
import com.internshala.foodhub.activity.DetailsActivity
import com.internshala.foodhub.database.MenuEntity
import com.internshala.foodhub.model.Menu

class DetailsRecyclerAdapter(val context: Context, val menuList: ArrayList<Menu>): RecyclerView.Adapter<DetailsRecyclerAdapter.DetailsViewHolder>() {

    class DetailsViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtItemNumber: TextView = view.findViewById(R.id.txtItemNumber)
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_details_single_row, parent, false)

        return DetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        val menu = menuList[position]
        holder.txtItemNumber.text = menu.menuId
        holder.txtItemName.text = menu.menuName
        holder.txtItemPrice.text = "Rs. " + menu.menuCostForOne + "/person"

        val menuEntity = MenuEntity(
                menu.menuId,
                menu.menuName,
                menu.menuCostForOne,
                menu.resId
        )

        val isAdded = DetailsActivity.DBAsyncTask(context, menuEntity, 1).execute()
        val result = isAdded.get()

        if (result){
             holder.btnAdd.text = "Remove"
             val removeColor = ContextCompat.getColor(context,R.color.orange)
             holder.btnAdd.setBackgroundColor(removeColor)
        } else {
            holder.btnAdd.text = "Add"
            val addColor = ContextCompat.getColor(context, R.color.button)
            holder.btnAdd.setBackgroundColor(addColor)
        }

        holder.btnAdd.setOnClickListener {
            if (!DetailsActivity.DBAsyncTask(context, menuEntity,1).execute().get()){

                val async = DetailsActivity.DBAsyncTask(context, menuEntity, 2).execute()
                val result = async.get()

                if (result){
                    holder.btnAdd.text = "Remove"
                    val removeColor = ContextCompat.getColor(context,R.color.orange)
                    holder.btnAdd.setBackgroundColor(removeColor)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }

            } else {
                val async = DetailsActivity.DBAsyncTask(context, menuEntity, 3).execute()
                val result = async.get()

                if (result){
                    holder.btnAdd.text = "Add"
                    val addColor = ContextCompat.getColor(context, R.color.button)
                    holder.btnAdd.setBackgroundColor(addColor)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}