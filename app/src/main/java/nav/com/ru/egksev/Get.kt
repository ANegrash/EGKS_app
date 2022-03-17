package nav.com.ru.egksev

import okhttp3.*


class Get {

    fun run(
        url: String,
        callback: Callback
    ) {

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        okHttpClient.newCall(request).enqueue(callback)
    }
}