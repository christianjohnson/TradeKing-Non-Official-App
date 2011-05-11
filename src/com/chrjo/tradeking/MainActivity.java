package com.chrjo.tradeking;

import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TradeKingAPI tk = new TradeKingAPI();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				queryTK();
			}
		});
        queryTK();
    }
    
    public void queryTK(){
    	String response = tk.run();
        TextView t = (TextView) findViewById(R.id.textView1);
        DecimalFormat twoDForm = new DecimalFormat("#.##");
		try {
			JSONObject object = (JSONObject) new JSONObject(response);
	        JSONObject locations = object.getJSONObject("response");
	        JSONObject accountholdings = locations.getJSONObject("accountholdings");
	        JSONArray holdings = accountholdings.getJSONArray("holding");
	        String chart = "";
	        double amount = 0.0;
	        for (int i=0;i < holdings.length();i++){
	        	JSONObject obj = holdings.getJSONObject(i);
	        	Double gain = Double.parseDouble(obj.getString("gainloss"));
	        	Double pos = Math.abs(Double.parseDouble(obj.getString("qty")));
	        	Double cost = Double.parseDouble(obj.getString("costbasis"));
	        	Double value = Double.parseDouble(obj.getString("marketvalue"));
	        	value = Double.valueOf(twoDForm.format(value));
	        	amount += gain;
	        	chart += (obj.getJSONObject("instrument").getString("desc") + "\n\t Cost: $" + cost + ", Value: $" + value + "\n");
	        }
	        
	        //Round
	        amount = Double.valueOf(twoDForm.format(amount));
			
	        t.setText("Total Gain/Loss is $" + amount + "\n" + chart);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}