package com.chrjo.tradeking;

/**
 * Christian Johnson
 * TradeKing API Sample App
 * 05-2011
 */

import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int DIALOG_LOADING = 0;
	private final int MENU_ITEM_0 = 0;  
	private final int MENU_ITEM_1 = 1;  
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
				new getQuotes().execute();
			}
		});
        new getQuotes().execute();
    }
    
   	/** 
	 * Add menu items
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {  
	    menu.add(0, MENU_ITEM_0, 0, "Refresh");  
	    menu.add(0, MENU_ITEM_1, 0, "Quit");  
	    return true;  
	}  
	 
	/** 
	 * Define menu action
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	        case MENU_ITEM_0:  
	        	new getQuotes().execute(); 
	        	break;
	        case MENU_ITEM_1: 
	        	finish(); 
	        	break;
	    }  
	    return false;  
	}
    
    
    public String queryTK(){
    	String response = tk.run();
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
	        return "Total Gain/Loss is $" + amount + "\n" + chart;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error in getting quotes, please try again.";
		}
    }
    
    /**
     * Creates a Toast() Message to display to the user
     * @param str The message to display
     */
    private void message(String str){
    	Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Creates the loading... dialog box
     * @param the id of the dialog box
     */
    protected Dialog onCreateDialog(int id){
    	switch(id){
    		case DIALOG_LOADING:
    			ProgressDialog pd = ProgressDialog.show(this,"","Loading...", true);
    			return pd;
    		default:
    			return null;
    	}
    }
    
	class getQuotes extends AsyncTask<Void,Void,String> {
		protected void onPreExecute(){
			showDialog(DIALOG_LOADING);	
		}
		
		protected String doInBackground(Void... params) {
			return queryTK();
		}
		
		protected void onPostExecute(String message){
	        TextView t = (TextView) findViewById(R.id.textView1);
	        t.setText(message);
			removeDialog(DIALOG_LOADING);
		}
	 }
	
}