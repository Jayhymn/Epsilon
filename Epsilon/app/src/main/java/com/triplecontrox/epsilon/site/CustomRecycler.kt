package com.triplecontrox.epsilon.site

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.recyclerview.widget.RecyclerView
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.db.Maintenance
import com.triplecontrox.epsilon.site.fragments.ConditionFragment

class CustomRecycler(private val activity: AppCompatActivity, private val maintenance: List<Maintenance>?)
    : RecyclerView.Adapter<CustomRecycler.ViewHolder>() {
    private val conditionFragment = ConditionFragment()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView : AppCompatCheckedTextView = itemView.findViewById(R.id.eqpCode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.site_recycler, null, false)

        view.layoutParams =
            RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val equipment = maintenance?.get(position)

        // get the full equipment full name and change text of text view
        holder.textView.text = equipment?.eqpCode

         if(equipment?.Status == "YES"){
             holder.textView.setCheckMarkDrawable(R.drawable.ic_done_24px)
             holder.textView.isChecked = true
        }

        holder.textView.setOnClickListener{
            val bundle = Bundle()
            bundle.putParcelable("details", equipment)
            conditionFragment.arguments = bundle

            activity.supportFragmentManager.beginTransaction().addToBackStack(null).apply {
                replace(R.id.recycler_frame, conditionFragment)
                commit()
            }
        }
    }
    override fun getItemCount(): Int = maintenance?.size ?: 0

}