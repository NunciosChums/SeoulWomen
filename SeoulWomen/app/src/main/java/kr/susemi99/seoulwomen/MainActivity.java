package kr.susemi99.seoulwomen;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import kr.susemi99.seoulwomen.adapters.ClassListAdapter;
import kr.susemi99.seoulwomen.listeners.EndlessRecyclerViewScrollListener;
import kr.susemi99.seoulwomen.managers.PreferenceHelper;
import kr.susemi99.seoulwomen.models.RowItem;
import kr.susemi99.seoulwomen.models.WomenResourcesClassParentItem;
import kr.susemi99.seoulwomen.networks.WomenService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
{
  private static final int OFFSET = 20;

  private ClassListAdapter adapter;
  private TextView emptyTextView;
  private SwipeRefreshLayout refreshLayout;
  private DrawerLayout drawer;

  private String areaName, area;
  private int startIndex, endIndex;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

     drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_refresh);
    refreshLayout.setOnRefreshListener(() -> {
      resetIndex();
      load();
    });

    areaName = PreferenceHelper.instance().lastSelectedAreaName();
    area = PreferenceHelper.instance().lastSelectedAreaValue();
    if (TextUtils.isEmpty(areaName) || TextUtils.isEmpty(area))
    {
      areaName = getString(R.string.default_area_name);
      area = getString(R.string.default_area);
    }

    adapter = new ClassListAdapter();
    emptyTextView = (TextView) findViewById(android.R.id.empty);

    RecyclerView listView = (RecyclerView) findViewById(R.id.list);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    listView.setLayoutManager(linearLayoutManager);
    listView.setAdapter(adapter);
    listView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
      @Override
      public void onLoadMore(int page, int totalItemsCount) {
        startIndex = endIndex + 1;
        endIndex += OFFSET;
        load();
      }
    });

    resetIndex();
    load();
  }

  @Override
  public void onBackPressed()
  {
    if (drawer.isDrawerOpen(GravityCompat.START))
    {
      drawer.closeDrawer(GravityCompat.START);
    }
    else
    {
      super.onBackPressed();
    }
  }

  private void resetIndex()
  {
    startIndex = 1;
    endIndex = OFFSET;
    adapter.clear();
  }

  private void load()
  {
    PreferenceHelper.instance().lastSelectedAreaName(areaName);
    PreferenceHelper.instance().lastSelectedAreaValue(area);
    setTitle(areaName + " 여성인력 개발센터 교육강좌");

    emptyTextView.setVisibility(View.GONE);

    WomenService.api().list(area, startIndex, endIndex).enqueue(new Callback<ResponseBody>()
    {
      @Override
      public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
      {
        refreshLayout.setRefreshing(false);

        try
        {
          String responseString = response.body().string();
          responseString = responseString.replace(area, "WomenResourcesClass");

          Gson gson = new GsonBuilder().create();
          WomenResourcesClassParentItem item = gson.fromJson(responseString, WomenResourcesClassParentItem.class);

          if (item.classItem == null)
          {
            return;
          }

          if (item.classItem.rows.length == 0)
          {
            displayErrorString(getString(R.string.no_result));
            return;
          }

          for (RowItem row : item.classItem.rows)
          {
            adapter.add(row);
          }
          adapter.notifyDataSetChanged();
        } catch (IOException e)
        {
          e.printStackTrace();
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

  private NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener()
  {
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
      drawer.closeDrawer(GravityCompat.START);

      areaName = item.getTitle().toString();
      area = item.getTitleCondensed().toString();
      resetIndex();
      load();

      return true;
    }
  };
}
