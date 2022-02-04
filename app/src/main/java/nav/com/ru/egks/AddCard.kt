package nav.com.ru.egks

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.gson.Gson
import nav.com.ru.egks.models.CardInfoModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class AddCard : AppCompatActivity() {
    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_add_card)

        val number = findViewById<EditText>(R.id.editTextNumber)
        val name = findViewById<EditText>(R.id.editTextCardName)
        val back = findViewById<ImageButton>(R.id.backBtn_add)
        val save = findViewById<ImageButton>(R.id.saveBtn_add)

        val inputType = InputType.TYPE_CLASS_NUMBER
        number.inputType = inputType

        back.setOnClickListener {
            finish()
        }

        save.setOnClickListener {
            if (number.text.isNotEmpty() && name.text.isNotEmpty()) {

                val url = "https://nav-com.ru/egks/v2.php?query=getCard&number=" + number.text
                Log.e("url", url)

                val getResponse = Get()
                var cardImage = ""

                getResponse.run(
                    url,
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            runOnUiThread {
                                cardImage = "card000.jpg"
                                addNewCard(cardImage, number.text.toString(), name.text.toString())
                                Log.e("err1", e.stackTraceToString())
                                finish()
                            }
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            if (response.body != null) {
                                val stringResponse = response.body!!.string()
                                runOnUiThread {
                                    cardImage = stringResponse
                                    addNewCard(cardImage, number.text.toString(), name.text.toString())
                                    Log.e("success", cardImage)
                                    finish()
                                }
                            } else {
                                runOnUiThread {
                                    cardImage = "card000.jpg"
                                    addNewCard(cardImage, number.text.toString(), name.text.toString())
                                    Log.e("err2", "WTF?!")
                                    finish()
                                }
                            }
                        }
                    }
                )
            } else {
                val toastText = if (number.text.isEmpty()){
                    "Поле номера не может быть пустым"
                } else {
                    "Поле имени не может быть пустым"
                }
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getSavedCards() = sharedPrefs.getString(KEY_TYPE, "")

    private fun saveCards (cards: String) = sharedPrefs.edit().putString(KEY_TYPE, cards).apply()

    private fun addNewCard (cardImg: String, cardNum: String, cardName: String) {
        val cardsString = getSavedCards()
        val cardsArray = cardsString?.split("--divider--")?.toTypedArray()
            ?.plus("$cardImg;$cardNum;$cardName")
        if (cardsArray != null) {
            saveCards(cardsArray.joinToString(separator = "--divider--"))
        }
        Toast.makeText(this, "Карта добавлена!", Toast.LENGTH_LONG).show()
    }
}