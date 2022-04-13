package nav.com.ru.egksev

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import androidx.constraintlayout.widget.ConstraintLayout
import nav.com.ru.egksev.models.CardsModel

const val PREFS_NAME = "nav-com.egks"
const val KEY_TYPE = "prefs.cards"
const val KEY_VERSION = "prefs.version"
const val KEY_ITEM_TYPE = "prefs.item_type"
const val CURRENT_VERSION = 2

class MainActivity : AppCompatActivity() {

    private val sharedPrefs by lazy {  getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_activity)
        supportActionBar?.hide()
        val addCard = findViewById<Button>(R.id.plusBtn)
        val infoBtn = findViewById<ImageButton>(R.id.infoBtn)
        val checkCardBtn = findViewById<ImageButton>(R.id.checkBtn)
        val rowBtn = findViewById<ImageButton>(R.id.rowITBtn)
        val blockBtn = findViewById<ImageButton>(R.id.blockITBtn)

        val itemType = getItemType() // 0 = row, 1 = block
        if (itemType == 0)
            setItemTypeBtnContent(1, 0)
        else
            setItemTypeBtnContent(0, 1)

        if (getVersion() < CURRENT_VERSION) {
            saveVersion(CURRENT_VERSION)
        }

        rowBtn.setOnClickListener {
            saveItemType(0)
            setItemTypeBtnContent(1, 0)
            loadList(0)
        }

        blockBtn.setOnClickListener {
            saveItemType(1)
            setItemTypeBtnContent(0, 1)
            loadList(1)
        }

        addCard.setOnClickListener {
            val intent = Intent(this@MainActivity, AddCard::class.java)
            startActivity(intent)
        }

        infoBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, InfoActivity::class.java)
            startActivity(intent)
        }

        checkCardBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, CheckCard::class.java)
            startActivity(intent)
        }

        loadList(itemType)
    }

    override fun onResume() {
        super.onResume()

        loadList(getItemType())
    }

    private fun loadList(
        itemType: Int
    ) {

        val listView = findViewById<ListView>(R.id.cardList)
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
            val stateAdapter = CardsListAdapter(this@MainActivity, R.layout.recycler_view_item, listOfCards)
            val stateAdapter2 = CardsListAdapter(this@MainActivity, R.layout.recycler_view_item_2, listOfCards)

            if (itemType == 0)
                listView.adapter = stateAdapter
            else
                listView.adapter = stateAdapter2

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
        val mainLayout = findViewById<ConstraintLayout>(R.id.mainScreen)
        val firstLayout = findViewById<ConstraintLayout>(R.id.errorScreen)
        val selectorLayout = findViewById<ConstraintLayout>(R.id.typeITLayout)

        when (main) {
            0 -> {
                mainLayout.visibility = View.GONE
                selectorLayout.visibility = View.GONE
            }
            1 -> {
                mainLayout.visibility = View.VISIBLE
                selectorLayout.visibility = View.VISIBLE
            }
        }

        when (first) {
            0 -> firstLayout.visibility = View.GONE
            1 -> firstLayout.visibility = View.VISIBLE
        }
    }

    private fun setItemTypeBtnContent (
        row: Int = 1,
        block: Int = 0
    ) {
        val rowBtn = findViewById<ImageButton>(R.id.rowITBtn)
        val blockBtn = findViewById<ImageButton>(R.id.blockITBtn)

        when (row) {
            0 -> {
                rowBtn.setImageResource(R.drawable.list_icon)
                rowBtn.setBackgroundResource(R.color.transp)
            }
            1 -> {
                rowBtn.setImageResource(R.drawable.list_icon_blue)
                rowBtn.setBackgroundResource(R.drawable.item_selector_shape)
            }
        }

        when (block) {
            0 -> {
                blockBtn.setImageResource(R.drawable.block_icon)
                blockBtn.setBackgroundResource(R.color.transp)
            }
            1 -> {
                blockBtn.setImageResource(R.drawable.block_icon_blue)
                blockBtn.setBackgroundResource(R.drawable.item_selector_shape)
            }
        }
    }

    private fun getSavedCards() = sharedPrefs.getString(KEY_TYPE, "")

    private fun getVersion() = sharedPrefs.getInt(KEY_VERSION, 0)

    private fun getItemType() = sharedPrefs.getInt(KEY_ITEM_TYPE, 0)

    private fun saveVersion (ver: Int) = sharedPrefs.edit().putInt(KEY_VERSION, ver).apply()

    private fun saveItemType (it: Int) = sharedPrefs.edit().putInt(KEY_ITEM_TYPE, it).apply()

}