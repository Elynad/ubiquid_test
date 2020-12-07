package com.ubiquid.ubiquidtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = ArrayList<String>()

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
        (holder as? ResultItemViewHolder)?.setData(items[position])
    }

    inner class ResultItemViewHolder(val view : View) : RecyclerView.ViewHolder(view.rootView) {

        fun setData(result : String) {
            view.findViewById<TextView>(R.id.content).text = result
        }

    }
}