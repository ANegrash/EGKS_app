package nav.com.ru.egks

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import nav.com.ru.egks.models.CardsModel

const val PREFS_NAME = "nav-com.egks"
const val KEY_TYPE = "prefs.cards"

class MainActivity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val addCard = findViewById<FloatingActionButton>(R.id.addCard)

        addCard.setOnClickListener {
            val intent = Intent(this@MainActivity, AddCard::class.java)
            startActivity(intent)
        }

        loadList()
    }

    override fun onResume() {
        super.onResume()

        loadList()
    }

    private fun loadList() {
        val listView = findViewById<ListView>(R.id.listView)
        val listOfCards = mutableListOf<CardsModel>()
        listOfCards.clear()

        val cardsString = getSavedCards()
        val cardsArray = cardsString?.split("--divider--")?.toTypedArray()
        if (cardsArray != null) {
            for (cardData in cardsArray) {
                if (cardData.indexOf(";") > -1)
                    listOfCards.add(CardsModel(cardData.split(";")[0], cardData.split(";")[1]))
            }
        }

        val stateAdapter = CardsListAdapter(this@MainActivity, R.layout.item_card, listOfCards)
        listView.adapter = stateAdapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this@MainActivity, CardInfo::class.java)
            intent.putExtra("name", listOfCards[position].name)
            intent.putExtra("number", listOfCards[position].number)
            startActivity(intent)
        }
    }

    private fun getSavedCards() = sharedPrefs.getString(KEY_TYPE, "")

}