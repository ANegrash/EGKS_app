package nav.com.ru.egks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
        setFrameLayoutContent(0, 1, 0)

        var url = "https://nav-com.ru/egks?number="
        val intent = intent
        val cardNum = intent.getStringExtra("number")
        val cardName = intent.getStringExtra("name")
        val balTw = findViewById<TextView>(R.id.bal)
        val expTw = findViewById<TextView>(R.id.exp)
        val toolbarText = findViewById<TextView>(R.id.custom_title)
        val cardImg = findViewById<ImageView>(R.id.cardImage)
        val backBtn = findViewById<ImageView>(R.id.backButton)
        toolbarText.text = cardName

        backBtn.setOnClickListener {
            finish()
        }

        url += cardNum

        val getResponse = Get()

        getResponse.run(
            url,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        setFrameLayoutContent(0, 0, 1)
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.body != null) {
                        val stringResponse = response.body!!.string()
                        val gson = Gson()

                        val cardInfo: CardInfoModel = gson.fromJson(stringResponse, CardInfoModel::class.java)
                        runOnUiThread {
                            supportActionBar?.hide()
                            balTw.text = cardInfo.rub.toString() + "р. " + cardInfo.cent.toString() + "к."
                            expTw.text = cardInfo.exp

                            Picasso.with(this@CardInfo)
                                .load(cardInfo.img)
                                .placeholder(R.drawable.card)
                                .error(R.drawable.card)
                                .into(cardImg)
                            setFrameLayoutContent(1, 0, 0)
                        }
                    } else {
                        runOnUiThread {
                            setFrameLayoutContent(0, 0, 1)
                        }
                    }
                }
            }
        )
    }

    private fun setFrameLayoutContent (
        main: Int = 1,
        loading: Int = 0,
        error: Int = 0
    ) {
        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        val loadingLayout = findViewById<ConstraintLayout>(R.id.loading)
        val errorLayout = findViewById<ConstraintLayout>(R.id.error)

        when (main) {
            0 -> mainLayout.visibility = View.GONE
            1 -> mainLayout.visibility = View.VISIBLE
        }

        when (loading) {
            0 -> loadingLayout.visibility = View.GONE
            1 -> loadingLayout.visibility = View.VISIBLE
        }

        when (error) {
            0 -> errorLayout.visibility = View.GONE
            1 -> errorLayout.visibility = View.VISIBLE
        }
    }
}