package nav.com.ru.egksev

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class AddCard : AppCompatActivity() {
    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#ffb32d")
        setContentView(R.layout.new_add_card)

        val number = findViewById<EditText>(R.id.cardNumberInput)
        val name = findViewById<EditText>(R.id.cardNameInput)
        val back = findViewById<ImageButton>(R.id.cancelAdding)
        val save = findViewById<ImageButton>(R.id.saveCard)

        val inputType = InputType.TYPE_CLASS_NUMBER
        number.inputType = inputType

        val nextCardNumber = getSavedCards()?.split("--divider--")?.size
        name.setText("Карта $nextCardNumber")

        back.setOnClickListener {
            finish()
        }

        save.setOnClickListener {
            if (number.text.isNotEmpty() && name.text.isNotEmpty()) {

                if (number.text.length == 9) {
                    val url = "https://nav-com.ru/egks/v2.php?query=getCard&number=" + number.text

                    val getResponse = Get()
                    var cardImage: String

                    getResponse.run(
                        url,
                        object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                runOnUiThread {
                                    cardImage = "card000.jpg"
                                    addNewCard(
                                        cardImage,
                                        number.text.toString(),
                                        name.text.toString()
                                    )
                                    finish()
                                }
                            }

                            @Throws(IOException::class)
                            override fun onResponse(call: Call, response: Response) {
                                if (response.body != null) {
                                    val stringResponse = response.body!!.string()
                                    runOnUiThread {
                                        cardImage = stringResponse
                                        addNewCard(
                                            cardImage,
                                            number.text.toString(),
                                            name.text.toString()
                                        )
                                        finish()
                                    }
                                } else {
                                    runOnUiThread {
                                        cardImage = "card000.jpg"
                                        addNewCard(
                                            cardImage,
                                            number.text.toString(),
                                            name.text.toString()
                                        )
                                        finish()
                                    }
                                }
                            }
                        }
                    )
                } else {
                    val cardImage = "card000.jpg"
                    addNewCard(
                        cardImage,
                        number.text.toString(),
                        name.text.toString()
                    )
                }
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