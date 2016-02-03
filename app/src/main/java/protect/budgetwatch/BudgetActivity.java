package protect.budgetwatch;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class BudgetActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        final ListView budgetList = (ListView) findViewById(R.id.list);
        final TextView helpText = (TextView)findViewById(R.id.helpText);

        DBHelper db = new DBHelper(this);

        if(db.getBudgetCount() > 0)
        {
            budgetList.setVisibility(View.VISIBLE);
            helpText.setVisibility(View.GONE);
        }
        else
        {
            budgetList.setVisibility(View.GONE);
            helpText.setVisibility(View.VISIBLE);
            helpText.setText(R.string.noBudgets);
        }

        final Calendar date = Calendar.getInstance();

        // Set to the last ms at the end of the month
        final long dateMonthEndMs = getEndOfMonthMs(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH));

        // Set to beginning of the month
        final long dateMonthStartMs = getStartOfMonthMs(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH));

        final Bundle b = getIntent().getExtras();
        final long budgetStartMs = b != null ? b.getLong("budgetStart", dateMonthStartMs) : dateMonthStartMs;
        final long budgetEndMs = b != null ? b.getLong("budgetEnd", dateMonthEndMs) : dateMonthEndMs;

        date.setTimeInMillis(budgetStartMs);
        String budgetStartString = DateFormat.getDateInstance(DateFormat.SHORT).format(date.getTime());

        date.setTimeInMillis(budgetEndMs);
        String budgetEndString = DateFormat.getDateInstance(DateFormat.SHORT).format(date.getTime());

        String dateRangeFormat = getResources().getString(R.string.dateRangeFormat);
        String dateRangeString = String.format(dateRangeFormat, budgetStartString, budgetEndString);

        final TextView dateRangeField = (TextView) findViewById(R.id.dateRange);
        dateRangeField.setText(dateRangeString);

        final List<Budget> budgets = db.getBudgets(budgetStartMs, budgetEndMs);
        final BudgetAdapter budgetListAdapter = new BudgetAdapter(this, budgets);
        budgetList.setAdapter(budgetListAdapter);

        budgetList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Budget budget = (Budget)parent.getItemAtPosition(position);

                Intent i = new Intent(getApplicationContext(), BudgetViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", budget.name);
                bundle.putBoolean("view", true);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    private long getStartOfMonthMs(int year, int month)
    {
        final Calendar date = Calendar.getInstance();

        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, date.getActualMinimum(Calendar.DAY_OF_MONTH));
        date.set(Calendar.HOUR_OF_DAY, date.getActualMinimum(Calendar.HOUR_OF_DAY));
        date.set(Calendar.MINUTE, date.getActualMinimum(Calendar.MINUTE));
        date.set(Calendar.SECOND, date.getActualMinimum(Calendar.SECOND));
        date.set(Calendar.MILLISECOND, date.getActualMinimum(Calendar.MILLISECOND));
        return date.getTimeInMillis();
    }

    private long getEndOfMonthMs(int year, int month)
    {
        final Calendar date = Calendar.getInstance();

        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
        date.set(Calendar.HOUR_OF_DAY, date.getActualMaximum(Calendar.HOUR_OF_DAY));
        date.set(Calendar.MINUTE, date.getActualMaximum(Calendar.MINUTE));
        date.set(Calendar.SECOND, date.getActualMaximum(Calendar.SECOND));
        date.set(Calendar.MILLISECOND, date.getActualMaximum(Calendar.MILLISECOND));
        return date.getTimeInMillis();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.budget_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_add)
        {
            Intent i = new Intent(getApplicationContext(), BudgetViewActivity.class);
            startActivity(i);
            return true;
        }

        if(id == R.id.action_calendar)
        {

        }

        return super.onOptionsItemSelected(item);
    }
}