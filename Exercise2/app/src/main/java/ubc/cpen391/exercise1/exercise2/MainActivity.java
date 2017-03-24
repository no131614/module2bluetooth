package ubc.cpen391.exercise1.exercise2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        final Button tipPercentagePlus = (Button) findViewById(R.id.tipPercentagePlusButton);
        final Button tipPercentageMinus = (Button) findViewById(R.id.tipPercentageMinusButton);

        final Button splitBillPlus = (Button) findViewById(R.id.splitBillPlusButton);
        final Button splitBillMinus = (Button) findViewById(R.id.splitBillMinusButton);

        final Button num0Button = (Button) findViewById(R.id.button0);
        final Button num1Button = (Button) findViewById(R.id.button1);
        final Button num2Button = (Button) findViewById(R.id.button2);
        final Button num3Button = (Button) findViewById(R.id.button3);
        final Button num4Button = (Button) findViewById(R.id.button4);
        final Button num5Button = (Button) findViewById(R.id.button5);
        final Button num6Button = (Button) findViewById(R.id.button6);
        final Button num7Button = (Button) findViewById(R.id.button7);
        final Button num8Button = (Button) findViewById(R.id.button8);
        final Button num9Button = (Button) findViewById(R.id.button9);
        final Button resetButton = (Button) findViewById(R.id.buttonReset);
        final Button delButton = (Button) findViewById(R.id.buttonDel);

        final TextView tipPercentageView = (TextView)findViewById(R.id.tipPercentageValueTextView);
        final TextView splitBillView = (TextView)findViewById(R.id.splitBillValueTextView);
        final TextView totalBillView = (TextView) findViewById(R.id.totalBillValueTextView);

        //MyNewView my_new_view;
        //my_new_view = new MyNewView(this);
        //   setContentView(my_new_view);

        tipPercentagePlus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                int tipPercentageValue = Integer.valueOf(tipPercentageView.getText().toString());
                tipPercentageValue++;
                tipPercentageView.setText(String.valueOf(tipPercentageValue));
                calculateOutputs();
            }
        });


        tipPercentageMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                int tipPercentageValue = Integer.valueOf(tipPercentageView.getText().toString());
                if (tipPercentageValue > 0) {
                    tipPercentageValue--;
                    tipPercentageView.setText(String.valueOf(tipPercentageValue));
                }
                calculateOutputs();
            }
        });

        splitBillPlus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                int splitBillValue = Integer.valueOf(splitBillView.getText().toString());
                splitBillValue++;
                splitBillView.setText(String.valueOf(splitBillValue));
                calculateOutputs();
            }
        });

        splitBillMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                int splitBillValue = Integer.valueOf(splitBillView.getText().toString());
                if (splitBillValue > 1) {
                    splitBillValue--;
                    splitBillView.setText(String.valueOf(splitBillValue));
                }
                calculateOutputs();
            }
        });

        num1Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String tipPercentageValue = totalBillView.getText().toString();
                String newDisplayValue = keyboardAppendTotalBill(1, tipPercentageValue);
                totalBillView.setText(String.valueOf(newDisplayValue));
                calculateOutputs();
            }
        });

        num2Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String tipPercentageValue = totalBillView.getText().toString();
                String newDisplayValue = keyboardAppendTotalBill(2, tipPercentageValue);
                totalBillView.setText(String.valueOf(newDisplayValue));
                calculateOutputs();
            }
        });

        num3Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String tipPercentageValue = totalBillView.getText().toString();
                String newDisplayValue = keyboardAppendTotalBill(3, tipPercentageValue);
                totalBillView.setText(String.valueOf(newDisplayValue));
                calculateOutputs();
            }
        });

        num4Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String tipPercentageValue = totalBillView.getText().toString();
                String newDisplayValue = keyboardAppendTotalBill(4, tipPercentageValue);
                totalBillView.setText(String.valueOf(newDisplayValue));
                calculateOutputs();
            }
        });

        num5Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String tipPercentageValue = totalBillView.getText().toString();
                String newDisplayValue = keyboardAppendTotalBill(5, tipPercentageValue);
                totalBillView.setText(String.valueOf(newDisplayValue));
                calculateOutputs();
            }
        });

        num6Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String tipPercentageValue = totalBillView.getText().toString();
                String newDisplayValue = keyboardAppendTotalBill(6, tipPercentageValue);
                totalBillView.setText(String.valueOf(newDisplayValue));
                calculateOutputs();
            }
        });

        num7Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String tipPercentageValue = totalBillView.getText().toString();
                String newDisplayValue = keyboardAppendTotalBill(7, tipPercentageValue);
                totalBillView.setText(String.valueOf(newDisplayValue));
                calculateOutputs();
            }
        });

        num8Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String tipPercentageValue = totalBillView.getText().toString();
                String newDisplayValue = keyboardAppendTotalBill(8, tipPercentageValue);
                totalBillView.setText(String.valueOf(newDisplayValue));
                calculateOutputs();
            }
        });

        num9Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String tipPercentageValue = totalBillView.getText().toString();
                String newDisplayValue = keyboardAppendTotalBill(9, tipPercentageValue);
                totalBillView.setText(String.valueOf(newDisplayValue));
                calculateOutputs();
            }
        });

        num0Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String tipPercentageValue = totalBillView.getText().toString();
                String newDisplayValue = keyboardAppendTotalBill(0, tipPercentageValue);
                totalBillView.setText(String.valueOf(newDisplayValue));
                calculateOutputs();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                totalBillView.setText("$0.00");
                calculateOutputs();
            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                String totalBillValue = totalBillView.getText().toString();
                String parsedValue = totalBillValue.substring(totalBillValue.indexOf("$") + 1, totalBillValue.indexOf("."));
                int parsedIntValue = Integer.valueOf(parsedValue);
                int newIntValue = (int) Math.floor(parsedIntValue/10);
                totalBillView.setText("$" + newIntValue + ".00");
                calculateOutputs();
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, MainActivity .class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, Exercise2Activity .class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    String keyboardAppendTotalBill(int input, String curValue){
        if(curValue.length() > 7) {
            Toast.makeText(getApplicationContext(), "Value too large!", Toast.LENGTH_LONG).show();
            return curValue;
        }

        String parsedValue = curValue.substring(curValue.indexOf("$") + 1, curValue.indexOf("."));
        int parsedIntValue = Integer.valueOf(parsedValue);
        int newIntValue = parsedIntValue*10 + input;
        return "$" + newIntValue + ".00";
    }

    void calculateOutputs(){

        TextView tipPercentageView = (TextView)findViewById(R.id.tipPercentageValueTextView);
        TextView splitBillView = (TextView)findViewById(R.id.splitBillValueTextView);
        TextView totalBillView = (TextView) findViewById(R.id.totalBillValueTextView);

        TextView totoToPayText = (TextView)findViewById(R.id.totalToPayValue);
        TextView totalTipText = (TextView)findViewById(R.id.totalTipValue);
        TextView totalPerPersonText = (TextView) findViewById(R.id.totalPerPersonValue);

        String billValue = totalBillView.getText().toString();
        String parsedValue = billValue.substring(billValue.indexOf("$") + 1, billValue.indexOf("."));
        double tipPercentage = Double.valueOf(tipPercentageView.getText().toString());

        double tipPercentageValue = tipPercentage/100;
        int totalBillValue = Integer.valueOf(parsedValue);
        int splitBillValue = Integer.valueOf(splitBillView.getText().toString());

        double totalTip = totalBillValue * tipPercentageValue;
        totalTip = Math.round(totalTip*100.0)/100.0;

        double totalToPay = totalBillValue + totalTip;

        double totalPerPerson = totalToPay / splitBillValue;
        totalPerPerson = Math.round(totalPerPerson*100.0)/100.0;

        totoToPayText.setText("$" + totalToPay);
        totalTipText.setText("$" + totalTip);
        totalPerPersonText.setText("$" + totalPerPerson);
    }

}


