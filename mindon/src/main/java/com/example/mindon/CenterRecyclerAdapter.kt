package com.example.mindon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mindon.models.CenterInfo

class CenterRecyclerAdapter(private val items: List<GgItem>, private val onItemClick: (GgItem) -> Unit) :
    RecyclerView.Adapter<CenterRecyclerAdapter.ViewHolder>() {

    private lateinit var dao: FavoriteDao


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText = view.findViewById<TextView>(R.id.tvInstNm)
        val addrText = view.findViewById<TextView>(R.id.tvAddress)
        val phoneText = view.findViewById<TextView>(R.id.tvTel)
        val favBtn = view.findViewById<ImageButton>(R.id.btnFavorite)
        init {
            view.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClick(items[pos])
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (!::dao.isInitialized) {
            val db = DBHelper(parent.context).writableDatabase
            dao = FavoriteDao(db)
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_center, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val centerId = item.centerId

        holder.nameText.text = item.centerName
        holder.addrText.text = item.roadAddress ?: item.lotAddress ?: "주소 없음"
        holder.phoneText.text = item.phone

        val isFavorite = dao.isFavorite(centerId)
        holder.favBtn.setImageResource(
            if (isFavorite) R.drawable.star_filled else R.drawable.star_nofilled
        )

        holder.favBtn.setOnClickListener {
            if (dao.isFavorite(centerId)) {
                dao.deleteFavorite(centerId)
                holder.favBtn.setImageResource(R.drawable.star_nofilled)
            } else {
                val center = CenterInfo(
                    centerId = centerId,
                    name = item.centerName ?: "이름 없음",
                    address = item.roadAddress ?: item.lotAddress ?: "주소 없음",
                    phone = item.phone ?: "전화 없음"
                )
                dao.insertFavorite(center)
                holder.favBtn.setImageResource(R.drawable.star_filled)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}