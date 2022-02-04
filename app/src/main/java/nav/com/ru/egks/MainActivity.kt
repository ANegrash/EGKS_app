package nav.com.ru.egks

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ListView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import nav.com.ru.egks.models.CardsModel

const val PREFS_NAME = "nav-com.egks"
const val KEY_TYPE = "prefs.cards"
const val KEY_VERSION = "prefs.version"
const val CURRENT_VERSION = 2

class MainActivity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val addCard = findViewById<FloatingActionButton>(R.id.addCard)
        val addCardFirst = findViewById<ImageButton>(R.id.addCardFirst)

        if (getVersion() < CURRENT_VERSION) {
            saveVersion(CURRENT_VERSION)
        }

        addCard.setOnClickListener {
            val intent = Intent(this@MainActivity, AddCard::class.java)
            startActivity(intent)
        }

        addCardFirst.setOnClickListener {
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
            cardsArray.reverse()
            for (cardData in cardsArray) {
                if (cardData.indexOf(";") > -1) {
                    if (cardData.split(";").toTypedArray().size == 2) {
                        listOfCards.add(
                            CardsModel(
                                "card000.jpg",
                                cardData.split(";")[0],
                                cardData.split(";")[1]
                            )
                        )
                    } else {
                        listOfCards.add(
                            CardsModel(
                                cardData.split(";")[0],
                                cardData.split(";")[1],
                                cardData.split(";")[2]
                            )
                        )
                    }
                }
            }
        }

        if (listOfCards.isNotEmpty()) {
            setFrameLayoutContent(1, 0)
            val stateAdapter = CardsListAdapter(this@MainActivity, R.layout.item_card, listOfCards)
            listView.adapter = stateAdapter

            listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val intent = Intent(this@MainActivity, CardInfo::class.java)
                intent.putExtra("name", listOfCards[position].name)
                intent.putExtra("number", listOfCards[position].number)
                intent.putExtra("image", listOfCards[position].img)
                startActivity(intent)
            }
        } else {
            setFrameLayoutContent(0, 1)
        }
    }

    private fun setFrameLayoutContent (
        main: Int = 1,
        first: Int = 0
    ) {
        val mainLayout = findViewById<ConstraintLayout>(R.id.mainPanel)
        val firstLayout = findViewById<ConstraintLayout>(R.id.firstPanel)

        when (main) {
            0 -> mainLayout.visibility = View.GONE
            1 -> mainLayout.visibility = View.VISIBLE
        }

        when (first) {
            0 -> firstLayout.visibility = View.GONE
            1 -> firstLayout.visibility = View.VISIBLE
        }
    }

    private fun getSavedCards() = sharedPrefs.getString(KEY_TYPE, "")

    private fun getVersion() = sharedPrefs.getInt(KEY_VERSION, 0)

    private fun saveVersion (ver: Int) = sharedPrefs.edit().putInt(KEY_VERSION, ver).apply()

}