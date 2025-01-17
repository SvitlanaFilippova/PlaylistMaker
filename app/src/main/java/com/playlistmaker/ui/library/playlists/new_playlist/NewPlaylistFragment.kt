package com.playlistmaker.ui.library.playlists.new_playlist


import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.ui.library.playlists.new_playlist.NewPlaylistViewModel.CreatingPlaylistState
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding: FragmentNewPlaylistBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }
    private var newPlaylistTitle = ""
    private var newPlaylistDescription: String? = null
    private var newPlaylistCoverPath: String? = null
    private var creatingWasStarted = false
    private var savedPlaylist: Playlist? = null
    private val args by navArgs<NewPlaylistFragmentArgs>()
    private val gson: Gson by inject()
    private val viewModel: NewPlaylistViewModel by viewModel<NewPlaylistViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val jsonPlaylist = args.playlistJson
        savedPlaylist = gson.fromJson(jsonPlaylist, Playlist::class.java)

        binding.apply {
            toolbar.setNavigationOnClickListener {
                checkIfSaveIsNeeded()
            }
            inputPlaylistTitle.addTextChangedListener(getTextWatcher(InputType.TITLE))
            inputPlaylistDescription.addTextChangedListener(getTextWatcher(InputType.DESCRIPTION))

        }
        viewModel.checkIfSavedPlaylistExist(savedPlaylist)
        viewModel.getStateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

        setPhotoPicker()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    checkIfSaveIsNeeded()
                }
            })

    }

    private fun render(state: CreatingPlaylistState) {
        when (state) {
            CreatingPlaylistState.Loading -> showProgressBar()
            CreatingPlaylistState.NewPlaylistState -> setCreatingScreen()
            is CreatingPlaylistState.Success -> processSavingResult(state.message)
            is CreatingPlaylistState.EditingPlaylist -> setEditingScreen(state.playlist)
        }
    }

    private fun setEditingScreen(playlist: Playlist) {
        binding.apply {
            with(playlist) {
                toolbar.title = getString(R.string.edit)
                Glide.with(requireActivity())
                    .load(coverPath)
                    .centerCrop()
                    .placeholder(R.drawable.ic_cover_placeholder)
                    .into(ivAddPhoto)
                btCreate.text = getString(R.string.save)
                if (playlist.coverPath != null) newPlaylistCoverPath = coverPath

                newPlaylistTitle = title
                inputPlaylistTitle.setText(title)
                if (description != null) {
                    inputPlaylistDescription.setText(description)
                    newPlaylistDescription = description
                }

                btCreate.setOnClickListener {
                    viewModel.savePlaylist(
                        Playlist(
                            id = id,
                            title = newPlaylistTitle,
                            description = newPlaylistDescription,
                            coverPath = newPlaylistCoverPath,
                            tracks = tracks,
                            tracksQuantity = tracksQuantity
                        )
                    )
                }
            }
        }
    }


    private fun setCreatingScreen() {
        binding.btCreate.setOnClickListener {
            viewModel.savePlaylist(
                Playlist(
                    id = 0,
                    title = newPlaylistTitle,
                    description = newPlaylistDescription,
                    coverPath = newPlaylistCoverPath,
                    tracks = listOf(),
                    tracksQuantity = 0
                )
            )
        }
    }

    private fun showProgressBar() {
        binding.apply {
            btCreate.isVisible = false
            progressbar.isVisible = true
        }
    }

    private fun processSavingResult(message: String?) {
        if (message != null) {
            showToast(message)
        }
        findNavController().navigateUp()
    }

    private fun setPhotoPicker() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    creatingWasStarted = true

                    saveImageToPrivateStorage(uri)

                    Glide.with(requireActivity())
                        .load(uri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_cover_placeholder)
                        .into(binding.ivAddPhoto)
                }
            }

        binding.ivAddPhoto.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath =
            File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "playlistCovers"
            )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")

        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        newPlaylistCoverPath = file.path
    }

    private fun getTextWatcher(inputType: InputType): TextWatcher {
        return object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEmpty = s.isNullOrEmpty()
                creatingWasStarted = !isEmpty

                val strokeColor = getStrokeColor(isEmpty)
                val hintColor = getHintColorStateList(isEmpty)

                binding.apply {
                    when (inputType) {
                        InputType.TITLE -> {
                            fieldPlaylistTitle.apply {
                                boxStrokeColor = strokeColor
                                hintTextColor = hintColor
                            }
                            btCreate.isEnabled = !isEmpty
                        }

                        InputType.DESCRIPTION -> {
                            fieldPlaylistDescription.apply {
                                boxStrokeColor = strokeColor
                                hintTextColor = hintColor
                            }
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                when (inputType) {
                    InputType.TITLE -> {
                        newPlaylistTitle = s.toString()
                        binding.btCreate.isEnabled = !s.isNullOrEmpty()
                    }

                    InputType.DESCRIPTION -> {
                        newPlaylistDescription = s.toString()

                    }
                }
            }
        }
    }

    private fun getHintColorStateList(isEmpty: Boolean): ColorStateList {
        if (isEmpty) {
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)

            return ColorStateList.valueOf(typedValue.data)
        } else {
            return ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue))
        }
    }

    private fun getStrokeColor(isEmpty: Boolean): Int {
        return ContextCompat.getColor(
            requireContext(),
            if (isEmpty) R.color.grey else R.color.blue
        )
    }

    private fun checkIfSaveIsNeeded() {
        val currentState = viewModel.getStateLiveData().value
        if (currentState is CreatingPlaylistState.NewPlaylistState) showExitConfirmationDialog()
        else {
            findNavController().navigateUp()
        }
    }


    private fun showExitConfirmationDialog() {
        if (creatingWasStarted) {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.q_close_creating_playlist))
                .setMessage(getString(R.string.all_unsaved_data_will_be_lost))
                .setPositiveButton(getString(R.string.close)) { _, _ ->
                    findNavController().navigateUp()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        } else findNavController().navigateUp()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        enum class InputType {
            TITLE,
            DESCRIPTION
        }
    }
}