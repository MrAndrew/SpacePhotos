package com.example.spacephotos.ui.photos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.spacephotos.R
import com.example.spacephotos.data.PhotoInfoModel
import com.example.spacephotos.databinding.FragmentPhotoDetailBinding
import com.example.spacephotos.databinding.FragmentPhotoListBinding
import com.squareup.picasso.Picasso
import java.util.*

/**
 * A fragment representing a single photo detail screen.
 * This fragment is either contained in a [PhotoListFragment]
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
class PhotoDetailFragment : Fragment() {

    /**
     * The placeholder content this fragment is presenting.
     */
    private var item: PhotoInfoModel? = null
    lateinit var pictureEarthDateTextView: TextView
    lateinit var roverInfoTextView: TextView
    lateinit var cameraInfoTextView: TextView
    lateinit var itemDetailImageView: ImageView
    private var toolbarLayout: CollapsingToolbarLayout? = null
    private var _binding: FragmentPhotoDetailBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val TAG: String = PhotoDetailFragment::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun initializeUI() {
        Log.d(TAG, "initializeUI()")
        val viewModel = ViewModelProvider(requireActivity()).get(PhotosViewModel::class.java)
        viewModel.selectedPhoto.observe(viewLifecycleOwner, { newPhoto ->
            // Update the UI
            Log.d(TAG, "selected photo: $newPhoto")
            updateContent(newPhoto)
        })
        viewModel.selectedPhoto.value?.let { updateContent(it) }
        Log.d(TAG, "initializeUI() photo: ${viewModel.selectedPhoto.value}")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPhotoDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root
        toolbarLayout = binding.phtoDetailToolbarLayout
        pictureEarthDateTextView = binding.photoDetailEarthdateTv
        cameraInfoTextView = binding.photoDetailCameraInfoTv
        roverInfoTextView = binding.photoDetailRoverInfoTv
        itemDetailImageView = binding.detailToolbarIv
        initializeUI()
        return rootView
    }

    private fun updateContent(photo: PhotoInfoModel?) {
        // Show the placeholder content as text in a TextView.
        photo?.let {
            val missionDayText = "Mission Day: ${photo.missionDayNumber}"
            val earthDateText = "Earth Date Picture Taken: ${photo.earthDate.toString()}"
            val roverInfoText = "Rover: ${photo.roverInfo?.roverName}"
            val cameraInfoText = """Camera: ${photo.cameraInfo?.fullName}"""
            toolbarLayout?.title = missionDayText
            pictureEarthDateTextView.text = earthDateText
            roverInfoTextView.text = roverInfoText
            cameraInfoTextView.text = cameraInfoText
            Picasso.get().isLoggingEnabled = true
            val secureUrl: String = photo.imageURL.replace("http", "https")
            Picasso.get()
                .load(secureUrl)
                .resize(1024, 1024)
                .centerInside()
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.ic_baseline_error_red_24)
                .into(itemDetailImageView)
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "rover_photo"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}