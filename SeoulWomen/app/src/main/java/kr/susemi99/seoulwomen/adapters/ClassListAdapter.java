package kr.susemi99.seoulwomen.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import kr.susemi99.seoulwomen.databinding.ListItemClassBinding;
import kr.susemi99.seoulwomen.models.RowItem;

public class ClassListAdapter extends BaseAdapter
{
  private ArrayList<RowItem> items = new ArrayList<>();

  public void add(RowItem item)
  {
    items.add(item);
  }

  public void clear()
  {
    items.clear();
  }


  @Override
  public int getCount()
  {
    return items.size();
  }

  @Override
  public Object getItem(int position)
  {
    return items.get(position);
  }

  @Override
  public long getItemId(int position)
  {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    ListItemClassBinding binding = ListItemClassBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    binding.setRow(items.get(position));
    return binding.getRoot();
  }
}
