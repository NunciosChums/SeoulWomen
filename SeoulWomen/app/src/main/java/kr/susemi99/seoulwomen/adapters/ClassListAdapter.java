package kr.susemi99.seoulwomen.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import kr.susemi99.seoulwomen.R;
import kr.susemi99.seoulwomen.models.RowItem;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.RowViewHolder> {
  private ArrayList<RowItem> items = new ArrayList<>();
  private View.OnClickListener clickListener;

  public ClassListAdapter(View.OnClickListener clickListener) {
    this.clickListener = clickListener;
  }

  public void add(RowItem item) {
    items.add(item);
  }

  public void clear() {
    items.clear();
    notifyDataSetChanged();
  }

  @Override
  public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int resId = R.layout.list_item_class;
    View view = LayoutInflater.from(parent.getContext()).inflate(resId, null);
    view
      .setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    return new RowViewHolder(view);
  }

  @Override
  public void onBindViewHolder(RowViewHolder holder, int position) {
    RowItem item = items.get(position);
    holder.textDifficulty.setText("[" + item.difficultyName + "]");
    holder.textClassName.setText(item.className);
    holder.textReceiveAt.setText(item.displayReceiveFrom() + " " + item.receiveTimeFrom + " ~ " + item
      .displayReceiveTo() + " " + item.receiveTimeTo);
    holder.textEducateOn.setText(item.displayEducateFrom() + " ~ " + item.displayEducateTo());
    holder.textEducateAt.setText(item.displayDays() + " " + item.educateTimeFrom + " ~ " + item.educateTimeTo);
    holder.textSpare.setText(item.displaySpareNum() + "/" + item.displayCollectNum() + "명");
    holder.textFee.setText(item.displayEducateFee());
    holder.textRegist.setText(item.displayHowToRegist());

    holder.itemView.setTag(item.url);
    holder.itemView.setOnClickListener(clickListener);
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  public class RowViewHolder extends RecyclerView.ViewHolder {
    public TextView textDifficulty;
    public TextView textClassName;
    public TextView textReceiveAt;
    public TextView textEducateOn;
    public TextView textEducateAt;
    public TextView textSpare;
    public TextView textFee;
    public TextView textRegist;

    public RowViewHolder(View itemView) {
      super(itemView);
      textDifficulty = (TextView) itemView.findViewById(R.id.text_difficulty);
      textClassName = (TextView) itemView.findViewById(R.id.text_class_name);
      textReceiveAt = (TextView) itemView.findViewById(R.id.text_receive_at);
      textEducateOn = (TextView) itemView.findViewById(R.id.text_educate_on);
      textEducateAt = (TextView) itemView.findViewById(R.id.text_educate_at);
      textSpare = (TextView) itemView.findViewById(R.id.text_spare);
      textFee = (TextView) itemView.findViewById(R.id.text_fee);
      textRegist = (TextView) itemView.findViewById(R.id.text_how_to_regist);
    }
  }
}
