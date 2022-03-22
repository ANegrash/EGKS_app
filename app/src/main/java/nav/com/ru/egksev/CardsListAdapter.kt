package nav.com.ru.egksev

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import nav.com.ru.egksev.models.CardsModel

class CardsListAdapter (
    context: Context?,
    resource: Int,
    jsonObjects: List<CardsModel>
) : ArrayAdapter<CardsModel?>(context!!, resource, jsonObjects) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val layout: Int = resource
    private val jsonObject: List<CardsModel> = jsonObjects

    override fun getView (
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val view = inflater.inflate(layout, parent, false)
        val nameCard = view.findViewById<TextView>(R.id.name_card)
        val imageCard = view.findViewById<ImageView>(R.id.image_card)
        val obj: CardsModel = jsonObject[position]

        nameCard.text = obj.name
        val uri: Uri = Uri.parse("android.resource://nav.com.ru.egksev/drawable/" + obj.img.split(".")[0])
        imageCard.setImageURI(null)
        imageCard.setImageURI(uri)

        return view
    }
}