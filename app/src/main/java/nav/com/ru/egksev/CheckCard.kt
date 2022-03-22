package nav.com.ru.egksev

import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import nav.com.ru.egksev.models.CardInfoModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class CheckCard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#ed023d")
        setContentView(R.layout.activity_check_card)

        setError(2)
        setFrameLayoutContent(1, 0, 0)
        val number = findViewById<EditText>(R.id.editTextNumberSigned)
        val inputType = InputType.TYPE_CLASS_NUMBER
        number.inputType = inputType
        val check = findViewById<ImageButton>(R.id.checkTick)
        val back = findViewById<ImageButton>(R.id.backToMenuCheck)
        val balanceTW = findViewById<TextView>(R.id.balanceTextCheck)
        val expiresTW = findViewById<TextView>(R.id.expiresDateCheck)
        var url = "https://nav-com.ru/egks/v2.php?query=getInfo&number="

        back.setOnClickListener {
            finish()
        }

        check.setOnClickListener {
            setFrameLayoutContent(0, 1, 0)
            url += number.text

            val getResponse = Get()

            getResponse.run(
                url,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            setFrameLayoutContent(1, 0, 0)
                            setError(0)
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (response.body != null) {
                            val stringResponse = response.body!!.string()
                            val gson = Gson()

                            val cardInfo: CardInfoModel =
                                gson.fromJson(stringResponse, CardInfoModel::class.java)
                            runOnUiThread {

                                var balance = ""
                                if (!(cardInfo.rub === null) or !(cardInfo.cent === null)) {
                                    balance = cardInfo.rub.toString()
                                    if (cardInfo.cent !== null)
                                        if (cardInfo.cent.toString() != "00")
                                            balance += "," + cardInfo.cent.toString()
                                }

                                if (balance.isEmpty()) {
                                    balanceTW.text = "0"
                                    setFrameLayoutContent(1, 0, 0)
                                    setError(1)
                                } else {
                                    balanceTW.text = balance
                                    setFrameLayoutContent(0, 0, 1)
                                }

                                expiresTW.text =
                                    if (cardInfo.exp === null) "" else if (cardInfo.exp.isEmpty()) "" else "до " + cardInfo.exp
                            }
                        } else {
                            runOnUiThread {
                                setFrameLayoutContent(1, 0, 0)
                                setError(0)
                            }
                        }
                    }
                }
            )
        }

    }

    private fun setError (
        errorCode: Int = 0
    ) {
        val stickerError = findViewById<ImageView>(R.id.imageView4)
        val messageError = findViewById<TextView>(R.id.textView7)
        var image = ""

        if (errorCode == 0) {
            //сервер не отвечает
            image = "cry"
            messageError.text = "Сервер не отвечает"
        }

        if (errorCode == 1) {
            //карта не найдена
            image = "think"
            messageError.text = "Карта не найдена"
        }

        if (errorCode == 2) {
            //дефолт
            image = "search"
            messageError.text = "Проверка баланса карты"
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
        val mainLayout = findViewById<ConstraintLayout>(R.id.infoCheck)
        val loadingLayout = findViewById<ConstraintLayout>(R.id.loadingCheck)
        val resultLayout = findViewById<ConstraintLayout>(R.id.balanceCheck)

        when (main) {
            0 -> mainLayout.visibility = View.GONE
            1 -> mainLayout.visibility = View.VISIBLE
        }

        when (loading) {
            0 -> loadingLayout.visibility = View.GONE
            1 -> loadingLayout.visibility = View.VISIBLE
        }

        when (error) {
            0 -> resultLayout.visibility = View.GONE
            1 -> resultLayout.visibility = View.VISIBLE
        }
    }
}