package com.triplecontrox.epsilon.site.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.camera.SnappedDocs
import com.triplecontrox.epsilon.db.Maintenance
import com.triplecontrox.epsilon.site.SiteGoogleForm

/**
 * A simple [Fragment] subclass.
 */
class ConditionFragment : Fragment(R.layout.fragment_condition) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val formLink = view.findViewById(R.id.formLink) as TextView
        formLink.text = getString(R.string.snap_doc_msg)

        val maintenance = arguments?.getParcelable<Maintenance?>("details")
        val condition = arrayListOf(maintenance?.Cond1, maintenance?.Cond2, maintenance?.Cond3)
        val isUploaded = maintenance?.uploaded == "YES"

        activity?.findViewById<Button>(R.id.signout)?.visibility  = View.GONE
        activity?.title = maintenance?.eqpCode

        if (maintenance?.Status == "YES") {
            formLink.text = getString(R.string.completed_task)
            formLink.isEnabled = false
        }
        if (!isUploaded){
            formLink.text = getString(R.string.fill_form)
        }

        // start new activity to
        formLink.setOnClickListener{
            var intent = Intent(context, SiteGoogleForm::class.java)

            if (isUploaded) {
                intent = Intent(context, SnappedDocs::class.java)
            }
            intent.putExtras(arguments)
            startActivity(intent)
        }

        val recyclerView = view.findViewById(R.id.recyclerCond) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        recyclerView.adapter = MyAdapter(condition)
    }

    private class MyAdapter(private val condition: ArrayList<String?>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView  = itemView.findViewById(R.id.eqpCode) as AppCompatCheckedTextView
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.site_recycler,
                parent, false) as View

            view.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = condition[position]
        }
        override fun getItemCount() = condition.size
    }
}
