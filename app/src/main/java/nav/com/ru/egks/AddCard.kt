package nav.com.ru.egks

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast

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
                val cardsString = getSavedCards()
                val cardsArray = cardsString?.split("--divider--")?.toTypedArray()
                    ?.plus(number.text.toString() + ";" + name.text)
                if (cardsArray != null) {
                    saveCards(cardsArray.joinToString(separator = "--divider--"))
                }
                Toast.makeText(this, "Карта добавлена!", Toast.LENGTH_LONG).show()
                finish()
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
}