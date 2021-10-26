package com.example.flatload

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.w3c.dom.Text

class ListViewAdapter(val item: MutableList<ItemList>):BaseAdapter() {
    override fun getCount(): Int = item.size
    override fun getItem(position: Int): ItemList = item[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        var convertView = view
        if (convertView == null) {
            convertView =
                LayoutInflater.from(parent?.context).inflate(R.layout.listview_item, parent, false)
        }
        convertView?.let {
            val name = convertView.findViewById<TextView>(R.id.textview_list_name)
            //val address = convertView.findViewById<TextView>(R.id.textview_list_address)
            val roadaddress = convertView.findViewById<TextView>(R.id.textview_list_roadaddress)
            val type = convertView.findViewById<TextView>(R.id.textview_list_type)

            val item: ItemList = item[position]
            name.text = item.title
            //address.text = item.address
            roadaddress.text = if (item.roadAddress.length == 0) item.address else item.roadAddress
            type.text = item.category

            //return convertView
        }
//        convertView!!.image_title.setImageDrawable(item.icon)
//        convertView.text_title.text = item.title
//        convertView.text_sub_title.text = item.subTitle

        return convertView
    }
}