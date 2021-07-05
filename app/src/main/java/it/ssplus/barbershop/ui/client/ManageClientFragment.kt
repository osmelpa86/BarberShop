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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import it.ssplus.barbershop.R
import it.ssplus.barbershop.model.entity.Client
import it.ssplus.barbershop.utils.ImageUtils
import it.ssplus.barbershop.utils.SnackBarUtil
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
    private lateinit var root: View
    private lateinit var ivPhotoClient: CircleImageView
    private lateinit var fabClientTakePicture: FloatingActionButton
    private lateinit var tilNameClient: TextInputLayout
    private lateinit var tilPhoneNumberClient: TextInputLayout
    private lateinit var tilCellClient: TextInputLayout
    private lateinit var tilDescriptionClient: TextInputLayout
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
        root = inflater.inflate(R.layout.fragment_manage_client, container, false)
        clientViewModel = ViewModelProvider(this).get(ClientViewModel::class.java)
        ivPhotoClient = root.findViewById(R.id.ivPhotoClient)
        fabClientTakePicture = root.findViewById(R.id.fabClientTakePicture)
        fabClientTakePicture.setOnClickListener(this)
        tilNameClient = root.findViewById(R.id.tilNameClient)
        tilPhoneNumberClient = root.findViewById(R.id.tilPhoneNumberClient)
        tilPhoneNumberClient.editText!!.filters = arrayOf(LengthFilter(8))
        tilCellClient = root.findViewById(R.id.tilCellClient)
        tilCellClient.editText!!.filters = arrayOf(LengthFilter(8))
        tilDescriptionClient = root.findViewById(R.id.tilDescriptionClient)

        if (arguments?.getSerializable("client") != null) {
            client = arguments?.getSerializable("client") as Client

            requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
                requireActivity().resources.getString(R.string.menu_edit)

            if (client!!.picture != null) {
                photo = client!!.picture
                var bitmap: Bitmap = ImageUtils.getImage(client!!.picture!!)
                ivPhotoClient.setImageBitmap(bitmap)
            }
            tilNameClient.editText!!.setText(client!!.name)
            if (client!!.phoneNumber != "") tilPhoneNumberClient.editText!!.setText(client!!.phoneNumber)
            if (client!!.cellPhone != "") tilCellClient.editText!!.setText(client!!.cellPhone)
            if (client!!.observation != null) tilDescriptionClient.editText!!.setText(client!!.observation)
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
            tilNameClient,
            requireActivity()
        ).validate(tilNameClient.editText!!.text.toString())

        val validCell = CellFieldValidator(
            tilCellClient,
            requireActivity()
        ).validate(tilCellClient.editText!!.text.toString())

        val validPhone = PhoneFieldValidator(
            tilPhoneNumberClient,
            requireActivity()
        ).validateIgnoreNull(tilPhoneNumberClient.editText!!.text.toString())

        if (validName && validCell && validPhone) {
            if (client == null) { //Add
                clientViewModel.insert(
                    Client(
                        name = tilNameClient.editText!!.text.toString(),
                        phoneNumber = if (tilPhoneNumberClient.editText!!.text.toString() != "") tilPhoneNumberClient.editText!!.text.toString() else null,
                        cellPhone = tilCellClient.editText!!.text.toString(),
                        observation = if (tilDescriptionClient.editText!!.text.toString() != "") tilDescriptionClient.editText!!.text.toString() else null,
                        picture = photo
                    )
                )

                val customSnackBar: Snackbar = Snackbar.make(
                    requireActivity().findViewById(R.id.manageClientFragment),
                    "",
                    Snackbar.LENGTH_LONG
                )

                SnackBarUtil.getColorfulAndDrawableBacgroundSnackBar(
                    customSnackBar,
                    requireActivity(),
                    R.drawable.snackbar_background_roud_shape,
                    R.color.primaryTextColor,
                    R.color.primaryTextColor
                )

                val layout: Snackbar.SnackbarLayout =
                    customSnackBar.view as Snackbar.SnackbarLayout
                val customSnackView: View =
                    layoutInflater.inflate(R.layout.snackbar_message_simple, null)
                val smpMessageSimple =
                    customSnackView.findViewById<View>(R.id.smpSimpleMessage) as TextView
                smpMessageSimple.text = resources.getString(R.string.message_success_add)
                val smpCancelSimple =
                    customSnackView.findViewById<View>(R.id.smpCancel) as ImageView
                smpCancelSimple.setOnClickListener { customSnackBar.dismiss() }
                layout.setPadding(0, 0, 0, 0)
                layout.addView(customSnackView, 0)
                customSnackBar.show()
                findNavController().navigate(R.id.nav_client)
            } else //Editar
            {
                clientViewModel.update(
                    Client(
                        id = client!!.id,
                        name = tilNameClient.editText!!.text.toString(),
                        phoneNumber = if (tilPhoneNumberClient.editText!!.text.toString() != "") tilPhoneNumberClient.editText!!.text.toString() else null,
                        cellPhone = tilCellClient.editText!!.text.toString(),
                        observation = if (tilDescriptionClient.editText!!.text.toString() != "") tilDescriptionClient.editText!!.text.toString() else null,
                        picture = photo
                    )
                )

                val customSnackBar: Snackbar = Snackbar.make(
                    requireActivity().findViewById(R.id.manageClientFragment),
                    "",
                    Snackbar.LENGTH_LONG
                )

                SnackBarUtil.getColorfulAndDrawableBacgroundSnackBar(
                    customSnackBar,
                    requireActivity(),
                    R.drawable.snackbar_background_roud_shape,
                    R.color.primaryTextColor,
                    R.color.primaryTextColor
                )

                val layout: Snackbar.SnackbarLayout =
                    customSnackBar.view as Snackbar.SnackbarLayout
                val customSnackView: View =
                    layoutInflater.inflate(R.layout.snackbar_message_simple, null)
                val smpMessageSimple =
                    customSnackView.findViewById<View>(R.id.smpSimpleMessage) as TextView
                smpMessageSimple.text = resources.getString(R.string.message_success_edit)
                val smpCancelSimple =
                    customSnackView.findViewById<View>(R.id.smpCancel) as ImageView
                smpCancelSimple.setOnClickListener { customSnackBar.dismiss() }
                layout.setPadding(0, 0, 0, 0)
                layout.addView(customSnackView, 0)
                customSnackBar.show()
                findNavController().navigate(R.id.nav_client)
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
                AppCompatResources.getColorStateList(
                    requireActivity(),
                    R.color.primaryColor
                ).defaultColor
            )
            options.setToolbarColor(
                AppCompatResources.getColorStateList(
                    requireActivity(),
                    R.color.primaryColor
                ).defaultColor
            )
            options.setToolbarTitle(getString(R.string.crop_image))
            options.setToolbarWidgetColor(
                AppCompatResources.getColorStateList(
                    requireActivity(),
                    android.R.color.white
                ).defaultColor
            )
            options.setActiveControlsWidgetColor(
                AppCompatResources.getColorStateList(
                    requireActivity(),
                    R.color.primaryColor
                ).defaultColor
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
            ivPhotoClient.setImageBitmap(bitmap)
            hasImage = true
            if (hasImage) fabClientTakePicture.setImageResource(R.drawable.ic_more_vert)
            else fabClientTakePicture.setImageResource(R.drawable.ic_photo_camera)
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
                    AppCompatResources.getColorStateList(
                        requireActivity(),
                        R.color.primaryColor
                    ).defaultColor
                )
                options.setToolbarColor(
                    AppCompatResources.getColorStateList(
                        requireActivity(),
                        R.color.primaryColor
                    ).defaultColor
                )
                options.setToolbarTitle(getString(R.string.crop_image))
                options.setToolbarWidgetColor(
                    AppCompatResources.getColorStateList(
                        requireActivity(),
                        android.R.color.white
                    ).defaultColor
                )
                options.setActiveControlsWidgetColor(
                    AppCompatResources.getColorStateList(
                        requireActivity(),
                        R.color.primaryColor
                    ).defaultColor
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
        ivPhotoClient.setImageResource(R.drawable.ic_account_circle)
    }

    private fun takePhotoMenu() {
        val wrapper = ContextThemeWrapper(requireActivity(), R.style.AppTheme_PopupMenu)
        val popup = PopupMenu(wrapper, fabClientTakePicture)
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