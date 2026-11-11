package com.example.mindon

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mindon.models.CenterInfo

class CenterAdapter(
    private val context: Context,
    private val items: MutableList<CenterInfo>,
    private val onFavoriteChanged: (() -> Unit)? = null // 즐겨찾기 삭제 후 새로고침용 콜백
) : RecyclerView.Adapter<CenterAdapter.ViewHolder>() {

    private val dao = FavoriteDao(DBHelper(context).writableDatabase)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.tvInstNm)
        val addrText: TextView = view.findViewById(R.id.tvAddress)
        val phoneText: TextView = view.findViewById(R.id.tvTel)
        val favoriteBtn: ImageButton = view.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_center, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("CenterAdapter", "item 수: ${items.size}")
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameText.text = item.name
        holder.addrText.text = item.address
        holder.phoneText.text = item.phone

        // 아이콘 상태 설정
        holder.favoriteBtn.setImageResource(R.drawable.star_filled)

        holder.favoriteBtn.setOnClickListener {
            dao.deleteFavorite(item.centerId)
            Toast.makeText(context, "즐겨찾기에서 제거됨", Toast.LENGTH_SHORT).show()

            // 리스트에서 제거 + 갱신
            items.removeAt(position)
            notifyItemRemoved(position)

            onFavoriteChanged?.invoke() // 외부에 콜백 (예: Fragment에서 reload)
            Log.d("출력확인", "이름: ${item.name}, 주소: ${item.address}, 전화: ${item.phone}")
        }
    }
}