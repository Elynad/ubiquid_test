package com.ubiquid.ubiquidtest.results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ubiquid.ubiquidtest.R

class ResultsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = ArrayList<String>()

    fun addAll(results : ArrayList<String>) {
        items = results
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_result, parent, false)
        return ResultItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ResultItemViewHolder)?.setData(items[position], (position % 2 != 0))
    }

    inner class ResultItemViewHolder(private val view : View) : RecyclerView.ViewHolder(view.rootView) {

        fun setData(result : String, isOdd : Boolean) {
            view.findViewById<TextView>(R.id.content).text = result

            if (isOdd)
                view.findViewById<CardView>(R.id.background).setCardBackgroundColor(
                        ContextCompat.getColor(view.context, R.color.primary_background_dark)
                )
            else
                view.findViewById<CardView>(R.id.background).setCardBackgroundColor(
                        ContextCompat.getColor(view.context, R.color.secondary_background_light)
                )
        }

    }
}