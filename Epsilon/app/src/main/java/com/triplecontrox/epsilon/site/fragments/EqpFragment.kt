package com.triplecontrox.epsilon.site.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.db.Maintenance
import com.triplecontrox.epsilon.site.CustomRecycler

class EqpFragment : Fragment(R.layout.fragment_eqp) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.title = resources.getString(R.string.equipmentOnSite)

        activity?.findViewById<Button>(R.id.signout)?.visibility = View.VISIBLE
        val equipment = arguments?.getParcelableArrayList<Maintenance>("equipment")!!

        val recyclerView = view.findViewById(R.id.recyclerEqp) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        recyclerView.adapter = CustomRecycler(activity as AppCompatActivity, equipment)
    }
}
