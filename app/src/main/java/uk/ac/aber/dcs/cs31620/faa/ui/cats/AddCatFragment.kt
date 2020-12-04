package uk.ac.aber.dcs.cs31620.faa.ui.cats

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import uk.ac.aber.dcs.cs31620.faa.R
import uk.ac.aber.dcs.cs31620.faa.databinding.FragmentAddCatBinding
import uk.ac.aber.dcs.cs31620.faa.model.AddCatViewModel
import uk.ac.aber.dcs.cs31620.faa.model.Cat
import uk.ac.aber.dcs.cs31620.faa.model.Gender
import uk.ac.aber.dcs.cs31620.faa.model.util.ResourceUtil
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val NAME_KEY = "NAME"
private const val DATE_KEY = "DATE"
private const val IMAGE_PATH_KEY = "IMAGE_PATH"
private const val DESCRIPTION_KEY = "DESCRIPTION"
private const val REQUEST_TAKE_PHOTO = 1

class AddCatFragment : Fragment(), View.OnClickListener {

    // Many of these could go in the ViewModel
    private var photoFile: File? = null
    private var imagePath = ""
    private var selectedDate: LocalDateTime? = null
    private var selectedDateStr: String = ""
    private var selectedBreed: String = ""
    private var selectedGender: String = ""
    private lateinit var addCatFragmentBinding: FragmentAddCatBinding
    private val addCatViewModel: AddCatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        handleBackButton()

        addCatFragmentBinding = FragmentAddCatBinding.inflate(inflater, container, false)

        // We don't want to show them the default "any" value at the start. That way we don't
        // need to check this when we save. copyOfRange gives us the part we're interested in
        var values = resources.getStringArray(R.array.gender_array)
        val genderValues = values.copyOfRange(1, values.size)
        values = resources.getStringArray(R.array.breed_array)
        val breedValues = values.copyOfRange(1, values.size)

        addCatFragmentBinding.datePicker.setOnClickListener(this)
        addCatFragmentBinding.add.setOnClickListener(this)

        restoreInstanceState(savedInstanceState)

        // We need to add a default image where there is none
        Glide.with(this)
            .load(Uri.parse(imagePath))
            .error(R.drawable.default_image)
            .into(addCatFragmentBinding.catImage)

        addCatFragmentBinding.catImage.setOnClickListener(this)

        setupSpinner(addCatFragmentBinding.breedsSpinner, breedValues)
        setupSpinner(addCatFragmentBinding.genderSpinner, genderValues)

        // Hide the BottomNavigationView
        val bnv: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        bnv.isVisible = false

        return addCatFragmentBinding.root
    }

    private fun handleBackButton() {
        // When back button is pressed we will navigate up the fragment
        // hierarchy. navigateUp will pop the fragment back stack automatically.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        // We add the callback to those that are called when the back button is pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        // Start by switching off the toggle button in the parent
        // activity so that it doesn't cause navigation drawer
        // to open
        val parent = requireActivity() as ToggleState
        parent.setNavigationDrawerState(false)
    }

    private fun setupSpinner(spinner: Spinner, values: Array<String>) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Check the parent to see which spinner
                if (parent === addCatFragmentBinding.breedsSpinner) {
                    selectedBreed = values[position]
                } else if (parent === addCatFragmentBinding.genderSpinner) {
                    selectedGender = values[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // We won't need this
            }
        }

        // Creating the ArrayAdapter instance having the name list and our own layout
        // so that the text is right justified and the correct size
        val adapter =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item
            )
        adapter.addAll(values.toList())
        // Specify the layout to use when the list of items appears. A predefined layout will do

        // Specify the layout to use when the list of items appears. A predefined layout will do
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            val catName = savedInstanceState.getString(NAME_KEY, "")
            if (catName.isNotEmpty()) addCatFragmentBinding.catName.setText(catName)
            val description = savedInstanceState.getString(DESCRIPTION_KEY, "")
            if (description.isNotEmpty()) addCatFragmentBinding.description.setText(description)
            imagePath = savedInstanceState.getString(IMAGE_PATH_KEY, "")
            selectedDateStr = savedInstanceState.getString(DATE_KEY, "")
            if (selectedDateStr.isNotEmpty()) {
                selectedDate = LocalDateTime.parse(
                    selectedDateStr,
                    DateTimeFormatter.ofPattern(getString(R.string.date_format))
                )
                addCatFragmentBinding.datePicker.text = selectedDateStr
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.datePicker -> {
                displayDatePicker()
            }
            R.id.add -> {
                insertCat()
            }
            R.id.catImage -> {
                takePicture()
            }
            else -> {
                // Ignore
            }
        }
    }

    private fun takePicture() {
        // Code obtained and adapted from: https://developer.android.com/training/camera/photobasics
        // See configuration instructions added to AndroidManifest.xml
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Create the File where the photo should go
        try {
            photoFile = ResourceUtil.createImageFile(requireContext())
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Toast.makeText(
                requireContext(),
                getString(R.string.cannot_create_image_file),
                Toast.LENGTH_SHORT
            ).show()
        }
        // Continue only if the File was successfully created
        photoFile?.let {
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                requireActivity().packageName,
                it
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            // Only continue if a receiver exists
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.let {
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                // We can get the file
                photoFile?.let {
                    imagePath = "file://${it.absolutePath}"
                    Glide.with(this)
                        .load(Uri.parse(imagePath))
                        .error(R.drawable.default_image)
                        .into(addCatFragmentBinding.catImage)
                }
            }
        }
    }

    private fun insertCat() {
        // We need to check that values are sensible. If they're not then we won't insert
        // a new cat record.

        if (selectedDate != null && addCatFragmentBinding.catName.text.isNotEmpty()) {
            if (imagePath.isEmpty()) {
                imagePath = getString(R.string.default_image_path)
            }
            val cat = Cat(
                0,
                addCatFragmentBinding.catName.text.toString(),
                Gender.valueOf(selectedGender.toUpperCase(Locale.getDefault())),
                selectedBreed,
                addCatFragmentBinding.description.text.toString(),
                selectedDate!!,
                LocalDateTime.now(),
                imagePath
            )
            addCatViewModel.insertCat(cat)

            // We should now navigate back
            findNavController().navigateUp()
        } else {
            Toast.makeText(requireContext(), getString(R.string.missing_data), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun displayDatePicker() {
        val today = LocalDateTime.now()
        // Note that DatePicker has months starting from 0! So we have to adjust for that
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Use 0s for the time component
                selectedDate = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0, 0, 0)
                selectedDateStr =
                    selectedDate!!.format(DateTimeFormatter.ofPattern(getString(R.string.date_format)))
                addCatFragmentBinding.datePicker.setText(selectedDateStr)
            }, today.year, today.monthValue - 1, today.dayOfMonth
        )
        datePickerDialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(NAME_KEY, addCatFragmentBinding.catName.text.toString())
        if (selectedDateStr.isNotEmpty())
            outState.putString(DATE_KEY, selectedDateStr)
        if (imagePath.isNotEmpty())
            outState.putString(IMAGE_PATH_KEY, imagePath)
        if (addCatFragmentBinding.description.text.isNotEmpty())
            outState.putString(DESCRIPTION_KEY, addCatFragmentBinding.description.text.toString())

        super.onSaveInstanceState(outState)
    }

    /**
     * Needed to set the toggle button back to handling
     * opening the navigation drawer
     */
    override fun onDestroyView() {
        val parent = requireActivity() as ToggleState
        parent.setNavigationDrawerState(true)

        // Display the BottomNavigationView
        val bnv: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_view)
        bnv.isVisible = true

        super.onDestroyView()
    }

}