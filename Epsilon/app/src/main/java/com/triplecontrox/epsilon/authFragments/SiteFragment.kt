package com.triplecontrox.epsilon.authFragments

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.triplecontrox.epsilon.AuthScreen
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.camera.Verification
import com.triplecontrox.epsilon.dataClasses.WebRequestBody
import com.triplecontrox.epsilon.db.AppDatabase
import com.triplecontrox.epsilon.db.DistinctSite
import com.triplecontrox.epsilon.db.Maintenance
import com.triplecontrox.epsilon.site.MaintenanceActivity

class SiteFragment(private val username: String) : Fragment(R.layout.auth_site_office) {
    private val listItems =  ArrayList<String>()
    private lateinit var site: List<DistinctSite>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sitesList =
            AppDatabase.getInstance(requireContext()).maintenanceDao().getEachSites()

        (activity as AuthScreen).changeHeaderText(resources.getString(R.string.location_msg))

        // prepare the list-view to show new locations
        val listView: ListView = view.findViewById(R.id.listView)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener{ parent, _, position, _ ->
            val siteName = parent.getItemAtPosition(position) as String
            val mSite = site.find { it.siteName == siteName }!!

            //prepare server request body as intent to dispatcher activity
            val body = WebRequestBody(mSite.Id, Location(listItems[position]), listItems[position], "00:00:00",
                username, "SITE", "http://hr.watershedcorporation.com//epsilon-data/updateStatus.php")

            val mIntent = Intent(requireContext(), Verification::class.java)
            mIntent.putExtra("requestBody", body)

            startActivity(mIntent)
        }

        sitesList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()){
                site = it
                (activity as AuthScreen).changeHeaderText(resources.getString(R.string.siteListMsg))
                loadSite(it)
                Log.d("SiteFragment", it.toString())
            }
            (activity as AuthScreen).checkPermissions()
        })
    }

    private fun loadSite(list: List<DistinctSite>) {
        // direct user to verify photo hence, store information on remote database through the url
        list.forEach {
            if (it.clockedIn == "00:00:00"){
                listItems.clear()
                listItems.add(it.siteName)
                adapter.notifyDataSetChanged()
            } else{
                (activity as AuthScreen).runService()

                val intent = Intent(requireContext(), MaintenanceActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("SiteName", it.siteName)
                requireContext().startActivity(intent)
                activity?.finish()
                return
            }
        }
    }
}
