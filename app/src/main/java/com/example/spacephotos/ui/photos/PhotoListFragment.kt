package com.example.spacephotos.ui.photos

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.spacephotos.data.PhotoInfoModel
import com.example.spacephotos.databinding.FragmentPhotoListBinding
import com.example.spacephotos.databinding.PhotoListContentBinding
import android.widget.ImageView
import com.example.spacephotos.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_photos.*

/**
 * A Fragment representing a list of Pings. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class PhotoListFragment : Fragment() {

    val TAG: String = PhotoListFragment::class.java.name
    private var _binding: FragmentPhotoListBinding? = null
    private var viewModel: PhotosViewModel? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPhotoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = binding.photoList
//        val itemDetailFragmentContainer: View? = view.findViewById(R.id.nav_host_fragment_photo_detail)
        val itemDetailFragmentContainer: View? = nav_host_fragment_photo_detail
        /** Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        val onClickListener = View.OnClickListener { itemView ->
            val item = itemView.tag as PhotoInfoModel?
            val bundle = Bundle()
            bundle.putString(
                PhotoDetailFragment.ARG_ITEM_ID,
                item?.id.toString()
            )
            item?.let { viewModel?.selectPhoto(it) }
            if (itemDetailFragmentContainer != null) {
                itemDetailFragmentContainer.findNavController()
                    .navigate(R.id.photo_detail_fragment, bundle)
            } else {
                itemView.findNavController().navigate(R.id.show_item_detail, bundle)
            }
        }

        /**
         * Context click listener to handle Right click events
         * from mice and trackpad input to provide a more native
         * experience on larger screen devices
         */
        val onContextClickListener = View.OnContextClickListener { v ->
            val item = v.tag as PhotoInfoModel?
            Toast.makeText(
                v.context,
                "Context click of item " + item?.id.toString(),
                Toast.LENGTH_LONG
            ).show()
            true
        }

        setupRecyclerView(recyclerView, onClickListener, onContextClickListener)

        initializeUI()
    }

    private fun initializeUI() {
        viewModel = ViewModelProvider(requireActivity()).get(PhotosViewModel::class.java)
        viewModel?.launchDataLoad()
        viewModel?.photos?.observe(viewLifecycleOwner, { photos ->
            Log.d(this.tag, "photos: $photos")
            (binding.photoList.adapter as SimpleItemRecyclerViewAdapter).setPhotos(
                photos
            )
        })
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, onClickListener: View.OnClickListener, onContextClickListener: View.OnContextClickListener) {

        recyclerView.adapter = SimpleItemRecyclerViewAdapter(
            listOf(),
            onClickListener,
            onContextClickListener
        )
    }

    class SimpleItemRecyclerViewAdapter(
        private var values: List<PhotoInfoModel?>,
        private val onClickListener: View.OnClickListener,
        private val onContextClickListener: View.OnContextClickListener
    ) : RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                PhotoListContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val photo = values[position]
            val photoListText = "${photo?.roverInfo?.roverName} - ${photo?.cameraInfo?.cameraID}"
            holder.listPhotoTv.text = photoListText
            val url: String = photo?.imageURL ?: ""
            //Need to change to https as android only loads https by default and urls from server return http
            val secureUrl: String = url.replace("http", "https")
            Picasso.get().isLoggingEnabled = true
            Picasso.get()
                .load(secureUrl)
                .resize(256, 256)
                .centerCrop()
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.ic_baseline_error_red_24)
                .into(holder.imagePreviewView)

            with(holder.itemView) {
                tag = photo
                setOnClickListener(onClickListener)
                setOnContextClickListener(onContextClickListener)

                setOnLongClickListener { v ->
                    // Setting the item id as the clip data so that the drop target is able to
                    // identify the id of the content
                    val clipItem = ClipData.Item(photo?.id.toString())
                    val dragData = ClipData(
                        v.tag as? CharSequence,
                        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                        clipItem
                    )

                    v.startDragAndDrop(
                        dragData,
                        View.DragShadowBuilder(v),
                        null,
                        0
                    )
                }
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(binding: PhotoListContentBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val listPhotoTv: TextView = binding.textLabel
            val imagePreviewView: ImageView = binding.imagePreview
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setPhotos(photos: List<PhotoInfoModel>) {
            values = photos
            notifyDataSetChanged()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}