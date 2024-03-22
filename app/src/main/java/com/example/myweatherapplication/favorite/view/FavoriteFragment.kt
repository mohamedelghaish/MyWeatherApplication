package com.example.myweatherapplication.favorite.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapplication.MapsActivity
import com.example.myweatherapplication.R
import com.example.myweatherapplication.RoomState
import com.example.myweatherapplication.database.LocalDataSourceImp
import com.example.myweatherapplication.databinding.FragmentFavoriteBinding
import com.example.myweatherapplication.favorite.viewmodel.FavoriteViewModel
import com.example.myweatherapplication.favorite.viewmodel.FavoriteViewModelFactory
import com.example.myweatherapplication.model.FavoriteLocation
import com.example.myweatherapplication.model.Repository
import com.example.myweatherapplication.network.RemoteDataSourceImp
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment(),OnFavoriteClick {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var fAdapter:FavoriteAdapter
    private lateinit var viewModel: FavoriteViewModel
    private var isInternetExist = false
    private var connectivityManager:ConnectivityManager? = null
    private var networkInfo: NetworkInfo? = null
    lateinit var snackbar:Snackbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_favorite, container, false)
        binding = FragmentFavoriteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         snackbar = Snackbar.make(binding.root, "Are you sure you want to delete this item?", Snackbar.LENGTH_LONG)
        initUI()
        setViewModel()
        NetworkConnection()
        lifecycleScope.launch {
            viewModel._favoriteLocation.collectLatest {
                when(it){
                    is RoomState.Loading->{
                    binding.noFavoriteTextView.visibility = View.VISIBLE
                    binding.recyclerViewFavorites.visibility = View.GONE
                    }
                    is RoomState.Success ->{
                        if (it.data.isEmpty()) {
                            // Handle case when there are no favorite locations
                            binding.noFavoriteTextView.visibility = View.VISIBLE
                            binding.imageViewFav.visibility=View.VISIBLE
                            binding.recyclerViewFavorites.visibility = View.GONE
                        } else {
                            // Display the list of favorite locations
                            binding.noFavoriteTextView.visibility = View.GONE
                            binding.imageViewFav.visibility=View.GONE
                            binding.recyclerViewFavorites.visibility = View.VISIBLE
                            fAdapter.submitList(it.data)
                        }
                    }
                    else->{
                        binding.noFavoriteTextView.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "there is a problem",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    private fun setViewModel(){
        val factory = FavoriteViewModelFactory(
            Repository.getInstance(

                RemoteDataSourceImp.getInstance(),
                LocalDataSourceImp.getInstance(requireContext())

            ))
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)
    }

    private fun initUI(){
            fAdapter = FavoriteAdapter (this)
        binding.recyclerViewFavorites.layoutManager= LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = fAdapter
        binding.addFavoriteFloatingActionButton.setOnClickListener {
            if (isInternetExist) {
                startActivity(Intent(requireContext(), MapsActivity::class.java))
            } else {
                showSnackBar()
            }
        }
    }


    private fun showSnackBar() {
        val snackBar = Snackbar.make(
            binding.root,
            getString(R.string.error_network),
            Snackbar.LENGTH_SHORT
        ).setActionTextColor(Color.WHITE)
        snackBar.view.setBackgroundColor(Color.BLACK)
        snackBar.setAction(getString(R.string.enable)) {
            connectInternet(requireContext())
        }
        snackBar.show()
    }

    fun connectInternet(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        } else {
            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }
    }

    fun NetworkConnection(){
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkInfo = connectivityManager!!.activeNetworkInfo

        if (networkInfo==null){
            isInternetExist = false
        } else {
            isInternetExist = true
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.getStoredFavoritePlaces()
    }

    override fun deleteFromRoom(favoritePlaces: FavoriteLocation) {
        showDialog(favoritePlaces)
    }

    override fun showDetails(latitude: String, longitude: String, language: String) {
        if (isInternetExist) {
            val intent = Intent(activity, DetailsFavorite::class.java)
            intent.putExtra("LAT", latitude)
            intent.putExtra("LONG", longitude)
            startActivity(intent)
        } else {
            showSnackBar()
        }
    }

    private fun showDialog(favoritePlaces: FavoriteLocation){
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.item_dialog, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val dialog = dialogBuilder.create()

        dialogView.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            viewModel.deleteFromRoom(favoritePlaces)

            // Dismiss the dialog
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_no).setOnClickListener {
            // Dismiss the dialog
            dialog.dismiss()
        }

        dialog.show()
    }


}