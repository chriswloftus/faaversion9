/**
 * An adapter to store a list of cats
 * @author Chris Loftus
 * @version 1
 */
package uk.ac.aber.dcs.cs31620.faa.ui.cats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import uk.ac.aber.dcs.cs31620.faa.databinding.CatItemBinding
import uk.ac.aber.dcs.cs31620.faa.model.Cat

class CatsRecyclerWithListAdapter(
    private val context: Context?,
    private var dataSet: MutableList<Cat>
) :
    RecyclerView.Adapter<CatsRecyclerWithListAdapter.ViewHolder>() {

    var clickListener: View.OnClickListener? = null

    inner class ViewHolder(
        itemView: View,
        val imageView: ImageView,
        val nameView: TextView
    ) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener(clickListener)
        }

        fun bindDataSet(cat: Cat) {
            nameView.text = cat.name
            imageView.setImageDrawable(AppCompatResources.getDrawable(context!!, cat.resourceId))
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CatsRecyclerWithListAdapter.ViewHolder {
        val catItemBinding = CatItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(
            catItemBinding.catCard,
            catItemBinding.catImageView,
            catItemBinding.catNameTextView
        )
    }

    override fun onBindViewHolder(holder: CatsRecyclerWithListAdapter.ViewHolder, position: Int) {
        holder.bindDataSet(dataSet[position])
    }

    fun changeDataSet(dataSet: MutableList<Cat>){
        this.dataSet = dataSet
        this.notifyDataSetChanged()
    }

}