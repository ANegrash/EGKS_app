package nav.com.ru.egks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import nav.com.ru.egks.models.CardInfoModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class CardInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_info)

        var url = "https://nav-com.ru/egks?number="
        val intent = intent
        val cardNum = intent.getStringExtra("number")
        val balTw = findViewById<TextView>(R.id.balance)
        val cardImg = findViewById<ImageView>(R.id.cardImage)
        url += cardNum

        val getResponse = Get()

        getResponse.run(
            url,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Log.e("TAG", "ERROR:" + e)
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.body != null) {
                        val stringResponse = response.body!!.string()
                        val gson = Gson()

                        val cardInfo: CardInfoModel = gson.fromJson(stringResponse, CardInfoModel::class.java)
                        runOnUiThread {

                            balTw.text = cardInfo.rub.toString() + "r " + cardInfo.cent.toString() + "c"

                            Picasso.with(this@CardInfo)
                                .load(cardInfo.img)
                                .placeholder(R.drawable.card)
                                .error(R.drawable.card)
                                .into(cardImg)
                        }
                    } else {
                        runOnUiThread {
                            Log.e("TAG", "EMPTY BODY")
                        }
                    }
                }
            }
        )
    }
}