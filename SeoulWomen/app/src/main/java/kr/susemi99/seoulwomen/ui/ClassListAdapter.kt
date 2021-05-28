package kr.susemi99.seoulwomen.ui

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.susemi99.seoulwomen.application.App
import kr.susemi99.seoulwomen.databinding.ClassListItemBinding
import kr.susemi99.seoulwomen.model.RowItem

class ClassListAdapter : RecyclerView.Adapter<ClassListAdapter.AppViewHolder>() {
  private lateinit var binding: ClassListItemBinding

  private val items = arrayListOf<RowItem>()

  fun addAll(rowItems: List<RowItem>) {
    items.addAll(rowItems)
    notifyItemInserted(itemCount)
  }

  fun clear() {
    items.clear()
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
    binding = ClassListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return AppViewHolder(binding.root)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
    val item = items[position]
    holder.bind(item)
    holder.itemView.setOnClickListener { goToHomePage(item) }
  }

  private fun goToHomePage(item: RowItem) {
    val uri = Uri.parse(item.url)
    val intent = Intent(Intent.ACTION_VIEW, uri).apply { addFlags(FLAG_ACTIVITY_NEW_TASK) }
    App.instance.startActivity(intent)
  }

  inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: RowItem) {
      itemView.apply {
        binding.difficultyLabel.text = "[${item.difficultyName}]"
        binding.classNameLabel.text = item.className
        binding.receiveAtLabel.text = "${item.displayReceiveFrom()} ${item.receiveTimeFrom} ~\n${item.displayReceiveTo()} ${item.receiveTimeTo}"
        binding.educateOnLabel.text = "${item.displayEducateFrom()} ~\n${item.displayEducateTo()}"
        binding.educateAtLabel.text = "${item.displayDays()}\n${item.educateTimeFrom} ~ ${item.educateTimeTo}"
        binding.spareLabel.text = "${item.displaySpareNum()}/${item.displayCollectNum()}명"
        binding.feeLabel.text = "${item.displayEducateFee()}"
        binding.howToRegistLabel.text = "${item.displayHowToRegist()}"
      }
    }
  }
}