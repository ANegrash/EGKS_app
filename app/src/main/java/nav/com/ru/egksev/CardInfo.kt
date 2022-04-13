package nav.com.ru.egksev

import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import nav.com.ru.egksev.models.CardInfoModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class CardInfo : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#34e879")
        setContentView(R.layout.new_card_info)
        setFrameLayoutContent(0, 1, 0)

        var url = "https://nav-com.ru/egks/v3.php?query=getInfo&number="
        val intent = intent
        val cardNum = intent.getStringExtra("number")
        val cardName = intent.getStringExtra("name")
        val balTw = findViewById<TextView>(R.id.balanceText)
        val expTw = findViewById<TextView>(R.id.expiresDate)
        val cardImg = findViewById<ImageView>(R.id.cardImageInfo)
        val backBtn = findViewById<ImageView>(R.id.backToMenu)
        val delBtn = findViewById<ImageView>(R.id.deleteCardButton)
        val deleteCardError = findViewById<TextView>(R.id.deleteCardError)
        val cardImageInArray = intent.getStringExtra("image")

        val urlImage = "https://nav-com.ru/egks/v3.php?query=getCard&number=$cardNum"
        Picasso.get()
            .load(urlImage)
            .placeholder(R.drawable.card_loading)
            .error(R.drawable.card000)
            .into(cardImg)


        backBtn.setOnClickListener {
            finish()
        }

        delBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Удаление карты")
            builder.setMessage("Вы уверены, что хотите удалить \"$cardName\"?")

            builder.setPositiveButton("Да") { _, _ ->
                val cardsString = getSavedCards()
                val cardsArray = cardsString?.split("--divider--")?.toTypedArray()
                val array2 = arrayListOf<String>()
                val array3 = arrayListOf<String>()
                cardsArray?.filterTo(array2) { it != "$cardImageInArray;$cardNum;$cardName" }
                array2.filterTo(array3) { it != "$cardNum;$cardName" }
                saveCards(array3.joinToString(separator = "--divider--"))
                Toast.makeText(this, "Карта удалена", Toast.LENGTH_LONG).show()
                finish()
            }

            builder.setNegativeButton("Нет") {_, _ ->}

            builder.show()
        }

        deleteCardError.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Удаление карты")
            builder.setMessage("Вы уверены, что хотите удалить \"$cardName\"?")

            builder.setPositiveButton("Да") { _, _ ->
                val cardsString = getSavedCards()
                val cardsArray = cardsString?.split("--divider--")?.toTypedArray()
                val array2 = arrayListOf<String>()
                val array3 = arrayListOf<String>()
                cardsArray?.filterTo(array2) { it != "$cardImageInArray;$cardNum;$cardName" }
                array2.filterTo(array3) { it != "$cardNum;$cardName" }
                saveCards(array3.joinToString(separator = "--divider--"))
                Toast.makeText(this, "Карта удалена", Toast.LENGTH_LONG).show()
                finish()
            }

            builder.setNegativeButton("Нет") {_, _ ->}

            builder.show()
        }

        url += cardNum

        val getResponse = Get()

        getResponse.run(
            url,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        setFrameLayoutContent(0, 0, 1)
                        setError(0)
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.body != null) {
                        val stringResponse = response.body!!.string()
                        val gson = Gson()

                        val cardInfo: CardInfoModel = gson.fromJson(stringResponse, CardInfoModel::class.java)
                        runOnUiThread {

                            var balance = ""
                            if (!(cardInfo.rub === null) or !(cardInfo.cent === null)) {
                                balance = cardInfo.rub.toString()
                                if (cardInfo.cent !== null)
                                    if (cardInfo.cent.toString() != "00")
                                        balance += "," + cardInfo.cent.toString()
                            }

                            if (balance.isEmpty()) {
                                balTw.text = "0"
                                setFrameLayoutContent(0, 0, 1)
                                setError(1)
                            } else {
                                balTw.text = balance
                                setFrameLayoutContent(1, 0, 0)
                            }

                            expTw.text = if (cardInfo.exp === null) "" else if (cardInfo.exp.isEmpty()) "" else "до " + cardInfo.exp
                        }
                    } else {
                        runOnUiThread {
                            setFrameLayoutContent(0, 0, 1)
                            setError(0)
                        }
                    }
                }
            }
        )
    }

    private fun setError (
        errorCode: Int = 0
    ) {
        val stickerError = findViewById<ImageView>(R.id.errorSticker)
        val messageError = findViewById<TextView>(R.id.errorText)
        val deleteCard = findViewById<TextView>(R.id.deleteCardError)
        deleteCard.visibility = View.GONE
        var image = ""

        if (errorCode == 0) {
            //сервер не отвечает
            image = "cry"
            messageError.text = "Сервер не отвечает"
        }

        if (errorCode == 1) {
            //карта не найдена
            deleteCard.visibility = View.VISIBLE
            image = "think"
            messageError.text = "Карта не найдена"
        }

        val uri: Uri = Uri.parse("android.resource://nav.com.ru.egksev/drawable/$image")
        stickerError.setImageURI(null)
        stickerError.setImageURI(uri)


    }

    private fun setFrameLayoutContent (
        main: Int = 1,
        loading: Int = 0,
        error: Int = 0
    ) {
        val mainLayout = findViewById<ConstraintLayout>(R.id.mainPanel)
        val loadingLayout = findViewById<ConstraintLayout>(R.id.loadingPanel)
        val errorLayout = findViewById<ConstraintLayout>(R.id.errorPanel)

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

    private fun getSavedCards() = sharedPrefs.getString(KEY_TYPE, "")

    private fun saveCards (cards: String) = sharedPrefs.edit().putString(KEY_TYPE, cards).apply()
}