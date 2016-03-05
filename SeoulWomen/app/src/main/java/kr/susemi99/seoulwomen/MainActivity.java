package kr.susemi99.seoulwomen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import kr.susemi99.seoulwomen.adapters.ClassListAdapter;
import kr.susemi99.seoulwomen.managers.PreferenceHelper;
import kr.susemi99.seoulwomen.models.RowItem;
import kr.susemi99.seoulwomen.models.WomenResourcesClassParentItem;
import kr.susemi99.seoulwomen.networks.WomenService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
  implements NavigationView.OnNavigationItemSelectedListener
{
  private ClassListAdapter adapter;
  private TextView emptyTextView;
  private SwipeRefreshLayout refreshLayout;

  private String areaName, area;
  private int startIndex, endIndex;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_refresh);
    refreshLayout.setOnRefreshListener(() -> {
      startIndex = 1;
      endIndex = 20;
      load();
    });

    adapter = new ClassListAdapter();
    emptyTextView = (TextView) findViewById(android.R.id.empty);

    ListView list = (ListView) findViewById(android.R.id.list);
    list.setOnItemClickListener(itemClickListener);
    list.setAdapter(adapter);

    areaName = PreferenceHelper.instance().lastSelectedAreaName();
    area = PreferenceHelper.instance().lastSelectedAreaValue();
    if (TextUtils.isEmpty(areaName) || TextUtils.isEmpty(area))
    {
      areaName = getString(R.string.default_area_name);
      area = getString(R.string.default_area);
    }

    navigationView.setCheckedItem(PreferenceHelper.instance().lastSelectedAreaIndex());
    load();
  }

  @Override
  public void onBackPressed()
  {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START))
    {
      drawer.closeDrawer(GravityCompat.START);
    }
    else
    {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem item)
  {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);

    PreferenceHelper.instance().lastSelectedAreaIndex(item.getOrder());

    areaName = item.getTitle().toString();
    area = item.getTitleCondensed().toString();
    load();

    return true;
  }

  private void load()
  {
    PreferenceHelper.instance().lastSelectedAreaName(areaName);
    PreferenceHelper.instance().lastSelectedAreaValue(area);
    setTitle(areaName + " 여성인력 개발센터 교육강좌");

    adapter.clear();
    emptyTextView.setVisibility(View.GONE);

    WomenService.api().list(area).enqueue(new Callback<ResponseBody>()
    {
      @Override
      public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
      {
        refreshLayout.setRefreshing(false);
        try
        {
          String result = response.body().string();
          result = result.replace(area, "WomenResourcesClass");

          Gson gson = new GsonBuilder().create();
          WomenResourcesClassParentItem item = gson.fromJson(result, WomenResourcesClassParentItem.class);

          for (RowItem row : item.classItem.rows)
          {
            adapter.add(row);
          }
          adapter.notifyDataSetChanged();
        } catch (IOException e)
        {
          e.printStackTrace();
          displayErrorString(e.getLocalizedMessage());
        }
      }

      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t)
      {
        refreshLayout.setRefreshing(false);
        displayErrorString(t.getLocalizedMessage());
      }
    });
  }

  private void displayErrorString(String string)
  {
    emptyTextView.setText(string);
    emptyTextView.setVisibility(View.VISIBLE);
  }

  private AdapterView.OnItemClickListener itemClickListener = (parent, view, position, id) -> {
    RowItem item = (RowItem) parent.getItemAtPosition(position);
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.url));
    startActivity(intent);
  };
}
