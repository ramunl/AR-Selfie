package us.cyberstar.data.internal.network.s3

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import us.cyberstar.data.external.s3.S3TransferUtilityProvider
import us.cyberstar.data.SnapshotLoadListener
import java.io.IOException
import javax.inject.Inject


//TODO refactor it to separate in several providers
internal class S3TransferUtilityProviderImpl @Inject constructor(
    private val context: Context,
    private val gson: Gson,
    private val url: String,
    private val token: String
) : S3TransferUtilityProvider {

    private var okHttpClient: OkHttpClient

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.HEADERS
        okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
    }

    private fun buildRequest(bytes: ByteArray?, mediaType: String): Request? {
        var request: Request? = null
        bytes?.let {
            val multipartBodyBuilder = MultipartBody.Builder()
            multipartBodyBuilder.setType(MultipartBody.FORM)
            multipartBodyBuilder.addFormDataPart(
                "file",
                "temp",
                okhttp3.RequestBody.create(MediaType.parse(mediaType), bytes)//"image/jpg" "video/mp4"
            )
            request = Request.Builder()
                .url(url)
                .addHeader("x-arm-upload-bucket", "chat")
                .addHeader("x-arm-token", token)
                .addHeader("Content-Type", "multipart/form-data")
                .post(multipartBodyBuilder.build())
                .build()
        }
        return request
    }

    override fun uploadMediaSynch(bytes: ByteArray?, mimeType: String): String? {
        Timber.d("uploadMediaSynch $mimeType bytes = ${bytes?.size ?: 0}")
        var pathRes: String? = null
        val request = buildRequest(bytes, mimeType)
        request?.let {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body()
            Timber.e("body ${body.toString()}")
            val json = body?.let { gson.fromJson(body.string(), JsonObject::class.java) }
            val fileName = json?.get("file_url")?.asString
            val err = json?.get("error")?.asString
            Timber.d("fileName = $fileName")
            if (fileName == null) {
                throw Exception("Upload media failed with: $err")
            } else {
                pathRes = fileName.toString()
            }
        } ?: Timber.e("uploadMediaAsynch request null!!")
        return pathRes
    }

    override fun uploadMediaAsynch(bytes: ByteArray?, listener: SnapshotLoadListener, mimeType: String) {
        val request = buildRequest(bytes, mimeType)
        request?.let {
            val call = okHttpClient.newCall(it)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Timber.e(e)
                    listener.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    Timber.d(response.message())
                    try {
                        val body = response.body()
                        val json = body?.let { gson.fromJson(body.string(), JsonObject::class.java) }
                        val fileName = json?.get("file_url")?.asString
                        val err = json?.get("error")?.asString
                        Timber.d("fileName = $fileName")
                        if (fileName == null) {
                            listener.onError(Exception(err))
                        } else {
                            listener.onUploaded(fileName.toString())
                        }
                    } catch (e: Exception) {
                        Timber.e(e)
                        listener.onError(e)
                    }
                }
            })
        } ?: Timber.e("uploadMediaAsynch bitmap null!!")
    }

    override fun downloadMediaSynch(imgUrl: String): Bitmap? {
        Timber.d("requestImage  imgUrl = $imgUrl")
        val uri = Uri.parse(imgUrl.replace("\"", "").trim())
        return Glide.with(context)
            .asBitmap()
            .load(uri)
            .submit()
            .get()
    }

    override fun downloadMediaAsynch(imgUrl: String, listener: SnapshotLoadListener) {
        Timber.d("requestImage  imgUrl = $imgUrl")

        val uri = Uri.parse(imgUrl.replace("\"", "").trim())

        //val testImg = loadBlanketImage()
        //addImageToDb(imgUrl, testImg!!, width)

        //TODO move image loader to separate module
        Glide.with(context)
            .asBitmap()
            .load(uri).addListener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Timber.e("onLoadFailed $e")
                    return true
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.let {
                        //val testImg = loadBlanketImage()
                        //addImageToDb(imgUrl, testImg!!, width)
                        listener.onDownloaded(it)
                    } ?: Timber.e("addImageToDb onResourceReady but resource = null!")
                    return true
                }
            }).preload()
    }
}