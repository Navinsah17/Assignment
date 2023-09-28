package com.example.vahanasiignment.ui.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vahanasiignment.MainActivity
import com.example.vahanasiignment.utils.Resource
import com.example.vahanasiignment.UniversityViewModel
import com.example.vahanasiignment.adapter.UniversityAdapter
import com.example.vahanasiignment.databinding.FragmentHomeBinding


class Home_Fragment : Fragment() {

    lateinit var viewModel: UniversityViewModel
    lateinit var adapter: UniversityAdapter
    lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setUpRecyclerView()
        viewModel = (activity as MainActivity).viewModel


        viewModel.universityList.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { univeResponse ->
                        adapter.differ.submitList(univeResponse)
                        Log.d(TAG, univeResponse[0].country)

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.d(TAG, "An error occured $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()

                }

            }
        })
        return binding.root
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        binding.rvhome.visibility = View.VISIBLE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        binding.rvhome.visibility = View.INVISIBLE

    }

/*//    private fun setUpRecyclerView() {
//        adapter = UniversityAdapter()
//        binding.rvhome.apply {
//            adapter = adapter
//            layoutManager = LinearLayoutManager(activity)
//        }
//    }*/
    private fun setUpRecyclerView() {
    adapter = UniversityAdapter(object : UniversityAdapter.OnItemsClick {
        override fun onClickWebsite(link: String) {
            // Handle the click event here, e.g., open a website using an Intent
            /*val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)*/
            val builder = CustomTabsIntent.Builder()
            val CustomTabsIntent = builder.build()
            CustomTabsIntent.launchUrl(binding.root.context,Uri.parse(link))
        }
    })
            binding.rvhome.adapter = adapter // Set the adapter for the RecyclerView
            binding.rvhome.layoutManager = LinearLayoutManager(activity)
}


}