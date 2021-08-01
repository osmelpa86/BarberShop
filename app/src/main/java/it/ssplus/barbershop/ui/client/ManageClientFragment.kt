package it.ssplus.barbershop.ui.client

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputFilter.LengthFilter
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yalantis.ucrop.UCrop
import it.ssplus.barbershop.R
import it.ssplus.barbershop.databinding.FragmentManageClientBinding
import it.ssplus.barbershop.model.entity.Client
import it.ssplus.barbershop.utils.ImageUtils
import it.ssplus.barbershop.utils.colorStateList
import it.ssplus.barbershop.utils.isNull
import it.ssplus.barbershop.utils.snackbar
import it.ssplus.barbershop.utils.validators.CellFieldValidator
import it.ssplus.barbershop.utils.validators.PhoneFieldValidator
import it.ssplus.barbershop.utils.validators.RequiredFieldValidator
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*


class ManageClientFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentManageClientBinding? = null
    private val binding get() = _binding!!

    private lateinit var clientViewModel: ClientViewModel
    var client: Client? = null
    private lateinit var menu: Menu

    private var mCapturedPhotoPath: String? = null
    private var mCroppedPhotoPath: String? = null
    private var photo: ByteArray? = null
    private var hasImage = false

    companion object {
        private const val GALLERY = 5
        private const val CAMERA = 2
        private const val CAMERA_AND_READ_EXTERNAL_STORAGE_PERMISSION = 3
        private const val READ_EXTERNAL_STORAGE_PERMISSION = 4
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("WrongThread")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentManageClientBinding.inflate(inflater, container, false)
        val root: View = binding.root

        clientViewModel = ViewModelProvider(this).get(ClientViewModel::class.java)

        binding.fabClientTakePicture.setOnClickListener(this)
        binding.tilPhoneNumberClient.editText!!.filters = arrayOf(LengthFilter(8))
        binding.tilCellClient.editText!!.filters = arrayOf(LengthFilter(8))
        binding.tilDescriptionClient.editText!!.filters = arrayOf(LengthFilter(60))

        if (arguments?.getSerializable("client") != null) {
            client = arguments?.getSerializable("client") as Client

            requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
                requireActivity().resources.getString(R.string.menu_edit)

            if (client!!.picture != null) {
                photo = client!!.picture
                val bitmap: Bitmap = ImageUtils.getImage(client!!.picture!!)
                binding.ivPhotoClient.setImageBitmap(bitmap)
            }
            binding.tilNameClient.editText!!.setText(client!!.name)
            if (client!!.phoneNumber != "") binding.tilPhoneNumberClient.editText!!.setText(client!!.phoneNumber)
            if (client!!.cellPhone != "") binding.tilCellClient.editText!!.setText(client!!.cellPhone)
            if (client!!.observation != null) binding.tilDescriptionClient.editText!!.setText(client!!.observation)
        }

        if (!checkPermissionCameraAndReadExternalStorage()) requestPermissionCameraAndReadExternalStorage()
        if (!checkPermissionReadExternalStorage()) requestPermissionReadExternalStorage()

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.client_manage_action, menu)
        menu.let { this.menu = it }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.client_manage_accept -> manageClient()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun manageClient() {
        val validName = RequiredFieldValidator(
            binding.tilNameClient,
            requireActivity()
        ).validate(binding.tilNameClient.editText!!.text.toString())

        val validCell = CellFieldValidator(
            binding.tilCellClient,
            requireActivity()
        ).validateIgnoreNull(binding.tilCellClient.editText!!.text.toString())

        val validPhone = PhoneFieldValidator(
            binding.tilPhoneNumberClient,
            requireActivity()
        ).validateIgnoreNull(binding.tilPhoneNumberClient.editText!!.text.toString())

        if (validName && validCell && validPhone) {
            if (client.isNull()) { //Add
                clientViewModel.insert(
                    Client(
                        name = binding.tilNameClient.editText!!.text.toString(),
                        phoneNumber = if (binding.tilPhoneNumberClient.editText!!.text.toString() != "") binding.tilPhoneNumberClient.editText!!.text.toString() else null,
                        cellPhone = binding.tilCellClient.editText!!.text.toString(),
                        observation = if (binding.tilDescriptionClient.editText!!.text.toString() != "") binding.tilDescriptionClient.editText!!.text.toString() else null,
                        picture = photo
                    )
                )

                snackbar(binding.root, R.string.message_success_add)
                findNavController().navigate(R.id.action_manage_client_to_nav_client)
            } else //Editar
            {
                clientViewModel.update(
                    Client(
                        id = client!!.id,
                        name = binding.tilNameClient.editText!!.text.toString(),
                        phoneNumber = if (binding.tilPhoneNumberClient.editText!!.text.toString() != "") binding.tilPhoneNumberClient.editText!!.text.toString() else null,
                        cellPhone = binding.tilCellClient.editText!!.text.toString(),
                        observation = if (binding.tilDescriptionClient.editText!!.text.toString() != "") binding.tilDescriptionClient.editText!!.text.toString() else null,
                        picture = photo
                    )
                )

                snackbar(binding.root, R.string.message_success_edit)
                client = null
                findNavController().navigate(R.id.action_manage_client_to_nav_client)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabClientTakePicture -> takePhotoMenu()
        }
    }

    private fun checkPermissionCameraAndReadExternalStorage(): Boolean {
        return (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissionCameraAndReadExternalStorage() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
            CAMERA_AND_READ_EXTERNAL_STORAGE_PERMISSION
        )
    }

    private fun checkPermissionReadExternalStorage(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) ==
                PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissionReadExternalStorage() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_EXTERNAL_STORAGE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_AND_READ_EXTERNAL_STORAGE_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                } else {
                    Toast.makeText(
                        requireActivity(),
                        R.string.you_must_grant_camera_and_external_storage_permission,
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }

            READ_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(
                        requireActivity(),
                        R.string.you_must_grant_read_external_storage_permission,
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
            handleCaptureImage()
        } else if (requestCode == GALLERY && resultCode == Activity.RESULT_OK) {
            handlePickImage(data)
        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            handleCropImage(data)
        }
    }

    private fun performFileSearch() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "PNG_${timeStamp}_",
            ".png",
            storageDir
        ).apply {
            mCapturedPhotoPath = absolutePath
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        val uri: Uri = FileProvider.getUriForFile(
            requireActivity(),
            "it.ssplus.barbershop.fileprovider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA)
    }

    private fun handleCaptureImage() {
        //To get the File for further usage
        val captureImage = File(mCapturedPhotoPath)
        val storageDir = this.requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var croppedImage: File? = null
        storageDir?.let {
            mCroppedPhotoPath = it.absolutePath + "/croppedImage.png"
            croppedImage = File(mCroppedPhotoPath)
        }

        croppedImage?.let {
            val options = UCrop.Options()
            options.setStatusBarColor(
                colorStateList(R.color.primaryColor)
            )
            options.setToolbarColor(
                colorStateList(R.color.primaryColor)
            )
            options.setToolbarTitle(getString(R.string.crop_image))
            options.setToolbarWidgetColor(
                colorStateList(android.R.color.white)
            )

            options.setActiveControlsWidgetColor(
                colorStateList(R.color.primaryColor)
            )
            UCrop.of(captureImage.toUri(), it.toUri())
                .withOptions(options)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(200, 200)
                .start(requireActivity(), this)
        }
    }

    private fun handleCropImage(data: Intent?) {
        data?.let {
            val resultUri = UCrop.getOutput(data)
            val bitmap = BitmapFactory.decodeFile(resultUri?.path)
            photo = ImageUtils.getImageBytes(bitmap)
            binding.ivPhotoClient.setImageBitmap(bitmap)
            hasImage = true
            if (hasImage) binding.fabClientTakePicture.setImageResource(R.drawable.ic_more_vert)
            else binding.fabClientTakePicture.setImageResource(R.drawable.ic_photo_camera)
        }
        try {
            File(mCapturedPhotoPath).delete()
        } catch (e: Exception) {
        }
    }

    private fun handlePickImage(data: Intent?) {
        data?.data?.let { uri ->
            val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            var croppedImage: File? = null
            storageDir?.let {
                mCroppedPhotoPath = it.absolutePath + "/croppedImage.png"
                croppedImage = File(mCroppedPhotoPath)
            }

            croppedImage?.let {
                val options = UCrop.Options()
                options.setStatusBarColor(
                    colorStateList(R.color.primaryColor)
                )
                options.setToolbarColor(
                    colorStateList(R.color.primaryColor)
                )
                options.setToolbarTitle(getString(R.string.crop_image))
                options.setToolbarWidgetColor(
                    colorStateList(android.R.color.white)
                )
                options.setActiveControlsWidgetColor(
                    colorStateList(R.color.primaryColor)
                )
                UCrop.of(uri, it.toUri())
                    .withOptions(options)
                    .withAspectRatio(1f, 1f)
                    .withMaxResultSize(200, 200)
                    .start(requireActivity(), this)
            }
        }
    }

    private fun deleteCroppedImage() {
        mCroppedPhotoPath?.let {
            val croppedImage = File(mCroppedPhotoPath)
            if (croppedImage.exists())
                croppedImage.delete()
        }
        hasImage = false
        binding.ivPhotoClient.setImageResource(R.drawable.ic_account_circle)
    }

    private fun takePhotoMenu() {
        val wrapper = ContextThemeWrapper(requireActivity(), R.style.AppTheme_PopupMenu)
        val popup = PopupMenu(wrapper, binding.fabClientTakePicture)
        try {
            val fields: Array<Field> = popup.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper: Any = field.get(popup)
                    val classPopupHelper =
                        Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons: Method = classPopupHelper.getMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popup.menuInflater.inflate(R.menu.client_take_photo_actions, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.client_select_image_delete -> {
                    deleteCroppedImage()
                    true
                }
                R.id.client_select_image_select -> {
                    performFileSearch()
                    true
                }
                R.id.client_select_image_capture -> {
                    takePicture()
                    true
                }
                else -> false
            }
        }

        if (!hasImage) popup.menu.removeItem(R.id.client_select_image_delete)
        popup.show()
    }

}