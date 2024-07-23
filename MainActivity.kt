package com.example.layeredpdf

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.layer.PdfLayer
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Create a file in external storage
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Layered.pdf")
        val outputStream = FileOutputStream(file)
        createLayeredPDF(outputStream)

    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun createImageXObject(bitmap: Bitmap): PdfImageXObject {
        val byteArray = bitmapToByteArray(bitmap)
        val imageData = ImageDataFactory.create(byteArray)
        return PdfImageXObject(imageData)
    }

    fun createLayeredPDF(filePath: FileOutputStream) {

        try {


            val pdfWriter = PdfWriter(filePath)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument, PageSize.A4)

            // Create a new layer
            val layer = PdfLayer("Layer 1", pdfDocument)

            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.dummy_10)
            val obj = createImageXObject(bitmap)


            // Add content to the layer
            val canvas = PdfCanvas(pdfDocument.addNewPage())
            canvas.beginLayer(layer)
            canvas.rectangle(0.0, 0.0, PageSize.A4.width.toDouble(), PageSize.A4.height.toDouble())
//            canvas.addXObjectAt(obj,0f,0f)
            canvas.addXObject(obj)
//            canvas.setColor(ColorConstants.RED, true)
            canvas.fill()
            canvas.endLayer()

            // Add another layer
            val layer2 = PdfLayer("Layer 2", pdfDocument)
            canvas.beginLayer(layer2)
            canvas.setColor(ColorConstants.BLUE, true)
            canvas.rectangle(200.0, 500.0, 200.0, 200.0)
            canvas.fill()
            canvas.endLayer()

            // Add some text outside of the layers
            document.add(Paragraph("This text is outside of any layer"))

            document.close()

        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    /*fun createLayeredPDF(image: Int, fileName: String) {
        try {
            // Get bitmap from ImageView
            val bitmap = BitmapFactory.decodeResource(resources, image)

            // Create a file in external storage
            val file = File(Environment.getExternalStorageDirectory().toString() + "/$fileName.pdf")
            val outputStream = FileOutputStream(file)

            // Initialize PDF writer and document
            val pdfWriter = PdfWriter(outputStream)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument, PageSize.A4)

            // Convert bitmap to Image
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val pdfImage = Image(ImageDataFactory.create(imageBytes))

            // Scale the image to fit the page
            pdfImage.scaleToFit(UnitValue.createPointValue(500f).value, UnitValue.createPointValue(500f).value)
            pdfImage.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)

            // Set a background color layer
            val backgroundColor = DeviceRgb(240, 240, 240)  // Light grey
            val backgroundLayer = Rectangle(PageSize.A4)
            backgroundLayer.setBackgroundColor(backgroundColor)

            // Add layers to the document
            document.add(backgroundLayer)
            document.add(pdfImage)

            // Close the document
            document.close()

            println("PDF Created Successfully")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/
}