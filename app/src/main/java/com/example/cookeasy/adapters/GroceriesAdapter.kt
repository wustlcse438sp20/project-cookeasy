package com.example.cookeasy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cookeasy.R
import com.example.cookeasy.objects.GroceryItem
import com.example.cookeasy.objects.IngredientItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_groceries.view.*
import kotlinx.android.synthetic.main.grocery_item.view.*
import kotlinx.android.synthetic.main.ingredient_item.view.*
import kotlinx.android.synthetic.main.ingredient_item.view.deleteButton
import kotlinx.android.synthetic.main.ingredient_item.view.itemName

class GroceriesAdapter(private val groceryList: ArrayList<GroceryItem>) : RecyclerView.Adapter<GroceriesAdapter.GroceriesViewHolder>() {

    private var index: Int = 0
    private var checkedList = ArrayList<GroceryItem>()
    private var moveItems: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceriesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.grocery_item, parent, false)
        return GroceriesViewHolder(itemView)
    }

    override fun getItemCount() = groceryList.size

    override fun onBindViewHolder(holder: GroceriesViewHolder, position: Int) {
        val currentItem = groceryList[position]
        holder.textView.text = currentItem.name
        holder.quantityTextView.text = currentItem.quantity.toString()

//        holder.button.setOnClickListener {
//            deleteGroceryFromDatabase(currentItem.name)
//            addGroceryItemToIngredients(currentItem)
//            index = holder.adapterPosition
//            groceryList.removeAt(index)
//            notifyDataSetChanged()
//        }

        holder.button2.setOnClickListener {
            if(holder.checkbox.isChecked) {
                holder.checkbox.isChecked = false
                index = holder.adapterPosition
                checkedList.remove(groceryList[index])
            }
            deleteGroceryFromDatabase(currentItem.name)
            index = holder.adapterPosition
            groceryList.removeAt(index)
            notifyDataSetChanged()
        }

        holder.checkbox.setOnClickListener {
            if(holder.checkbox.isChecked) {
                index = holder.adapterPosition
                checkedList.add(groceryList[index])
            }
            else {
                index = holder.adapterPosition
                checkedList.remove(groceryList[index])
            }
        }

//        holder.moveItemsBtn.setOnClickListener {
//            for(item in checkedList) {
//                deleteGroceryFromDatabase(item.name)
//                addGroceryItemToIngredients(item)
//                groceryList.remove(item)
//                notifyDataSetChanged()
//            }
//        }

        if(moveItems == true) {
            holder.checkbox.isChecked = false
            moveItems == false
        }
    }

    fun moveItemsToIngredients() {
        moveItems = true
        for(item in checkedList) {
            deleteGroceryFromDatabase(item.name)
            addGroceryItemToIngredients(item)
            groceryList.remove(item)
            notifyDataSetChanged()
        }
    }


    fun removeItem(position: Int) {
        groceryList.removeAt(index)
        notifyDataSetChanged()
//        notifyItemRemoved(index)
//        notifyItemRangeChanged(index, groceryList.size)
    }

    private fun deleteGroceryFromDatabase(item: String) {
        val uid = FirebaseAuth.getInstance().uid?: ""
        val query = FirebaseDatabase.getInstance().getReference("/groceries/$uid").orderByChild("name").equalTo(item.toString())

        query?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun addGroceryItemToIngredients(groceryItem: GroceryItem) {
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ingredient = IngredientItem(groceryItem.name, groceryItem.quantity)

        val newId = FirebaseDatabase.getInstance().getReference("/ingredients/$uid").push().key
        FirebaseDatabase.getInstance().getReference("/ingredients/$uid/$newId/name").setValue(ingredient.name)
        FirebaseDatabase.getInstance().getReference("/ingredients/$uid/$newId/quantity").setValue(ingredient.quantity)

//        val query = FirebaseDatabase.getInstance().getReference("/ingredients/$uid").orderByChild("name").equalTo(groceryItem.name)
//        query?.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (snapshot in dataSnapshot.children) {
//                    snapshot.ref.removeValue()
//                    snapshot.ref.child("quantity").value
//                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//
//            }
//        })
    }


    class GroceriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.itemName
        val quantityTextView: TextView = itemView.groceryItemQuantity
//        val button: Button = itemView.addToIngredientsButton
        val button2: Button = itemView.deleteButton
        val checkbox: CheckBox = itemView.checkbox
    }
}