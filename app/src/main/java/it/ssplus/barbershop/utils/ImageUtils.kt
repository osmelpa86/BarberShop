package it.ssplus.barbershop.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.Xml
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayOutputStream
import java.lang.reflect.Method
import java.util.*


object ImageUtils {
    /*Obtener un arreglo de bytes de un bitmap*/
    fun getImageBytes(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    /*Obtener bitmap de un arreglo de bytes*/
    fun getImage(image: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    /*Convertir ImageView a arreglo de bytes*/
    fun getBytesFromImageView(imageView: ImageView): ByteArray {
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    /*Escalar(Reducir tamaño) de una imagen*/
    fun scaledImage(image: ByteArray, witdh: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(getImage(image), witdh, height, true)
    }

    /*Escalar(Reducir tamaño) de un bitmap*/
    fun scaledBitmap(bitmap: Bitmap, witdh: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, witdh, height, true)
    }

    /*Redondear y Escalar(Reducir tamaño) de un bitmap*/
    fun roundedAndScaled(
        context: Context,
        bitmap: Bitmap,
        witdh: Int,
        height: Int,
        cornerRadius: Float
    ): RoundedBitmapDrawable {
        val b = Bitmap.createScaledBitmap(bitmap, witdh, height, true)
        val rounded = RoundedBitmapDrawableFactory.create(context.resources, b)
        rounded.cornerRadius = cornerRadius
        return rounded
    }

    /*Redondear los border del contenido de un ImageView*/
    fun roundedBitmap(
        context: Context,
        bitmap: Bitmap,
        cornerRadius: Float
    ): RoundedBitmapDrawable {
        val rounded = RoundedBitmapDrawableFactory.create(context.resources, bitmap)
        rounded.cornerRadius = cornerRadius
        return rounded
    }

    /*Se compara dos images utilizado los arreglos de bytes que las conforman*/
    fun compareImagesInBytes(image1: ByteArray, image2: ByteArray): Boolean {
        return Arrays.equals(image1, image2)
    }

    /*Obtener un Bitmap desde una imagen ubicada en la carpeta Drawable*/
    fun createBitmapFromResource(activity: Activity, drawable: Int): Bitmap {
        return BitmapFactory.decodeResource(activity.resources, drawable)
    }

    fun createBitmapFromResource(context: Context, drawable: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources, drawable)
    }

    /*Obtener un Bitmap del contenido de un ImageView con los border redondos*/
    fun getBitmapFromRoundedImageView(imageView: ImageView): Bitmap? {
        val drawable = imageView.drawable as RoundedBitmapDrawable
        return drawable.bitmap
    }

    /*Obtener un Bitmap del contenido de un ImageView con los border normales*/
    fun getBitmapFromImageView(imageView: ImageView): Bitmap {
        val drawable = imageView.drawable as BitmapDrawable
        return drawable.bitmap
    }

    /*Reducir tamaño de la una imagen*/
    fun compressImageToMax(image: Bitmap, maxBytes: Int): Bitmap? {
        var image: Bitmap = image
        var oldSize = image.byteCount

        // attempt to resize the image as much as possible while valid
        while (image != null && image.byteCount > maxBytes) {

            // Prevent image from becoming too small
            if (image.width <= 20 || image.height <= 20)
                return null

            // scale down the image by a factor of 2
            image = Bitmap.createScaledBitmap(image, image.width / 2, image.height / 2, false)

            // the byte count did not change for some reason, can not be made any smaller
            if (image.byteCount == oldSize)
                return null

            oldSize = image.byteCount
        }

        return image
    }

    fun getBytesFromDrawable(d: Drawable): ByteArray {
        val bitmap = (d as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val bitmapdata = stream.toByteArray()
        return bitmapdata
    }

    //    Obtner Bitmap de un VectorDrawable
    fun getBitmapFromVectorDrawable(
        context: Context?,
        drawable: Drawable /*drawableId: Int*/
    ): Bitmap? {
//        var drawable = ContextCompat.getDrawable(context!!, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.wrap(drawable!!).mutate()
        }
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Create a vector drawable from a binary XML byte array.
     * @param context Any context.
     * @param binXml Byte array containing the binary XML.
     * @return The vector drawable or null it couldn't be created.
     */
    fun getVectorDrawable(context: Context, binXml: ByteArray): Drawable? {
        try {
            // Get the binary XML parser (XmlBlock.Parser) and use it to create the drawable
            // This is the equivalent of what AssetManager#getXml() does
            @SuppressLint("PrivateApi") val xmlBlock = Class.forName("android.content.res.XmlBlock")
            val xmlBlockConstr = xmlBlock.getConstructor(ByteArray::class.java)
            val xmlParserNew: Method = xmlBlock.getDeclaredMethod("newParser")
            xmlBlockConstr.setAccessible(true)
            xmlParserNew.setAccessible(true)
            val parser: XmlPullParser = xmlParserNew.invoke(
                xmlBlockConstr.newInstance(binXml as Any)
            ) as XmlPullParser
            return if (Build.VERSION.SDK_INT >= 24) {
                Drawable.createFromXml(context.resources, parser)
            } else {
                // Before API 24, vector drawables aren't rendered correctly without compat lib
                val attrs: AttributeSet = Xml.asAttributeSet(parser)
                var type: Int = parser.next()
                while (type != XmlPullParser.START_TAG) {
                    type = parser.next()
                }
                VectorDrawableCompat.createFromXmlInner(context.resources, parser, attrs, null)
            }
        } catch (e: Exception) {
            Log.e("TAG", "Vector creation failed", e)
        }
        return null
    }
}


