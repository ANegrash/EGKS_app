package nav.com.ru.egksev

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#192135")
        setContentView(R.layout.activity_info)
        val back = findViewById<ImageButton>(R.id.backInfoBtn)
        val phone = findViewById<ConstraintLayout>(R.id.phoneEGKS)
        val email = findViewById<ConstraintLayout>(R.id.mailEGKS)
        val site = findViewById<ConstraintLayout>(R.id.siteEGKS)
        val devSite = findViewById<ConstraintLayout>(R.id.navSite)
        val rate = findViewById<Button>(R.id.rateApp)

        back.setOnClickListener {
            finish()
        }

        phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("tel:+7(978)730-08-00")
            startActivity(intent)
        }

        email.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("mailto:info@egksev.ru")
            startActivity(intent)
        }

        site.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://egks.ru")
            startActivity(intent)
        }

        devSite.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://nav-com.ru")
            startActivity(intent)
        }

        rate.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=nav.com.ru.egksev")
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=nav.com.ru.egksev")
                    )
                )
            }
        }
    }
}