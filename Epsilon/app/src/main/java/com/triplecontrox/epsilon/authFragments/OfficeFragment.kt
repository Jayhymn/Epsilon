package com.triplecontrox.epsilon.authFragments

import android.content.*
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.triplecontrox.epsilon.AuthScreen
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.camera.ReVerify
import com.triplecontrox.epsilon.camera.Verification
import com.triplecontrox.epsilon.dataClasses.DistinctOffice
import com.triplecontrox.epsilon.dataClasses.WebRequestBody
import com.triplecontrox.epsilon.db.AppDatabase
import com.triplecontrox.epsilon.office.OfficeActivity

class OfficeFragment(private val username: String) : Fragment(R.layout.auth_site_office) {
    private val listItems =  ArrayList<String>()
    private var location = Location("No address")
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var shdPrf: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = AppDatabase.getInstance(requireContext())
        shdPrf = PreferenceManager.getDefaultSharedPreferences(requireContext())

        if (activity != null){
            (activity as AuthScreen).changeHeaderText(resources.getString(R.string.location_msg))
        }

        // prepare the list-view to show new locations
        val listView: ListView = view.findViewById(R.id.listView)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter

        // lead user to verify photo and after that, send request to store in remote server
        listView.onItemClickListener = AdapterView.OnItemClickListener{ _, _, _, _ ->
          val webRequestBody=
              WebRequestBody(0, location, location.provider, "NO TIME", username,
                  "OFFICE", "http://hr.watershedcorporation.com//epsilon-data/office.php")

            val requestIntent = Intent(requireContext(), Verification::class.java)
            requestIntent.putExtra("requestBody", webRequestBody)
            startActivity(requestIntent)
        }

        val sitesList = db.officeDao().getEachOffice()
        sitesList.observe(viewLifecycleOwner, Observer { loadSite(it) })
    }

    override fun onStart() {
        super.onStart()
        shdPrf.registerOnSharedPreferenceChangeListener(listener)

    }

    override fun onStop() {
        super.onStop()
        shdPrf.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun loadSite(site: List<DistinctOffice>?) {
        listItems.clear()
        shdPrf.apply {
            if (contains("address")){
                shdPrf.apply {
                    if (activity != null){
                        (activity as AuthScreen).changeHeaderText(resources.getString(R.string.officeMsg))
                    }
                    location = Location(getString("address", "No address Found"))
                    location.latitude = getFloat("latitude", 0.0F).toDouble()
                    location.longitude = getFloat("longitude", 0.0F).toDouble()

                    listItems.add(location.provider)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        if (!site.isNullOrEmpty()){
            site.find{it.shouldVerify == 1}.also {
                if (it != null){

                    val body = WebRequestBody(it.Id, location, it.siteName, it.clockedIn, it.assignee,
                        "RE_VERIFY", "http://hr.watershedcorporation.com/epsilon-data/reverify.php",
                        it.verifyCount, it.shouldVerify)

                    val mIntent = Intent(requireContext(), ReVerify::class.java).apply {
                        putExtra("requestBody", body)
                    }
                    Log.d("Reverify", body.toString())

                    requireContext().startActivity(mIntent)
                    activity?.finish()
                    return
                }
                checkLoggedIn(site)
            }
        }
        (activity as AuthScreen).checkPermissions()
    }
    private fun checkLoggedIn(site: List<DistinctOffice>){
        (activity as AuthScreen).changeHeaderText(resources.getString(R.string.officeMsg))

        site.forEach {
            if (it.clockedIn == "00:00:00"){
                listItems.add(it.siteName)
                adapter.notifyDataSetChanged()
            }
            else{
                Log.d("paramsLogged", it.toString())
                (activity as AuthScreen).runService()

                Intent(requireContext(), OfficeActivity::class.java).apply {
                    putExtra("UID", it.Id)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    requireContext().startActivity(this)
                    activity?.finish()
                }
            }
        }
    }


    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { shdPref, key ->
            adapter.notifyDataSetChanged()

            if (activity != null){
                (activity as AuthScreen).changeHeaderText(resources.getString(R.string.officeMsg))
            }

            location.provider = shdPref.getString("address", "No address Found")
            location.latitude = shdPref.getFloat("latitude", 0.0F).toDouble()
            location.longitude = shdPref.getFloat("longitude", 0.0F).toDouble()

            listItems.clear()
            listItems.add(location.provider)
            adapter.notifyDataSetChanged()
    }
}