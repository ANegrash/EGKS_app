package nav.com.ru.egksev

import android.content.Context
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
import nav.com.ru.egksev.models.CardInfoModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class CardInfo : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_info)
        setFrameLayoutContent(0, 1, 0)

        var url = "https://nav-com.ru/egks/v2.php?query=getInfo&number="
        val intent = intent
        val cardNum = intent.getStringExtra("number")
        val cardName = intent.getStringExtra("name")
        var cardImage = intent.getStringExtra("image")?.split(".")?.get(0)
        val balTw = findViewById<TextView>(R.id.bal)
        val expTw = findViewById<TextView>(R.id.exp)
        val toolbarText = findViewById<TextView>(R.id.custom_title)
        val cardImg = findViewById<ImageView>(R.id.cardImage)
        val backBtn = findViewById<ImageView>(R.id.backButton)
        val delBtn = findViewById<ImageView>(R.id.deleteCard)
        toolbarText.text = cardName
        val cardImageInArray = intent.getStringExtra("image")

        if (cardImage == "card000") {
            val urlImage = "https://nav-com.ru/egks/v2.php?query=getCard&number=$cardNum"
            val getResponse = Get()
            val uri: Uri = Uri.parse("android.resource://nav.com.ru.egksev/drawable/card")
            cardImg.setImageURI(null)
            cardImg.setImageURI(uri)

            getResponse.run(
                urlImage,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            cardImage = "card000"
                            val uri: Uri = Uri.parse("android.resource://nav.com.ru.egksev/drawable/" + (cardImage?.split(".")
                                ?.get(0) ?: "card000"))
                            cardImg.setImageURI(null)
                            cardImg.setImageURI(uri)
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (response.body != null) {
                            val stringResponse = response.body!!.string()
                            runOnUiThread {
                                cardImage = stringResponse
                                val uri: Uri = Uri.parse("android.resource://nav.com.ru.egksev/drawable/" + (cardImage?.split(".")
                                    ?.get(0) ?: "card000"))
                                cardImg.setImageURI(null)
                                cardImg.setImageURI(uri)
                                val cardsString = getSavedCards()
                                val cardsArray = cardsString?.split("--divider--")?.toTypedArray()
                                val array2 = arrayListOf<String>()
                                val array3 = arrayListOf<String>()
                                cardsArray?.filterTo(array2, { it != "$cardImageInArray;$cardNum;$cardName" })
                                array2.filterTo(array3, { it != "$cardNum;$cardName" })
                                array3 += "$cardImage;$cardNum;$cardName"
                                saveCards(array3.joinToString(separator = "--divider--"))
                            }
                        } else {
                            runOnUiThread {
                                cardImage = "card000"
                                val uri: Uri = Uri.parse("android.resource://nav.com.ru.egksev/drawable/" + (cardImage?.split(".")
                                    ?.get(0) ?: "card000"))
                                cardImg.setImageURI(null)
                                cardImg.setImageURI(uri)
                            }
                        }
                    }
                }
            )
        } else {
            val uri: Uri = Uri.parse("android.resource://nav.com.ru.egksev/drawable/" + (cardImage?.split(".")
                ?.get(0) ?: "card000"))
            cardImg.setImageURI(null)
            cardImg.setImageURI(uri)
        }


        backBtn.setOnClickListener {
            finish()
        }

        delBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Удаление карты")
            builder.setMessage("Вы уверены, что хотите удалить карту \"$cardName\"?")

            builder.setPositiveButton("Да") { _, _ ->
                val cardsString = getSavedCards()
                val cardsArray = cardsString?.split("--divider--")?.toTypedArray()
                val array2 = arrayListOf<String>()
                val array3 = arrayListOf<String>()
                cardsArray?.filterTo(array2, { it != "$cardImageInArray;$cardNum;$cardName" })
                array2.filterTo(array3, { it != "$cardNum;$cardName" })
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

                            balTw.text = if ((cardInfo.rub === null) or (cardInfo.cent === null)) "нет данных" else cardInfo.rub.toString() + "р. " + cardInfo.cent.toString() + "к."
                            expTw.text = if (cardInfo.exp === null) "нет данных" else if (cardInfo.exp.isEmpty()) "нет данных" else cardInfo.exp

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

    private fun getSavedCards() = sharedPrefs.getString(KEY_TYPE, "")

    private fun saveCards (cards: String) = sharedPrefs.edit().putString(KEY_TYPE, cards).apply()
}