package ro.pub.cs.systems.eim.simulare_practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SimularePracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortText = null;
    private EditText clientPortText = null;
    private EditText clientAddressText = null;
    private EditText clientCityText = null;
    private Button serverConnectButton = null;
    private Button clientGetForecastButton = null;
    private Spinner clientSpinner = null;
    private TextView weatherForecastTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private GetWeatherForecastButtonClickListener getWeatherForecastButtonClickListener = new GetWeatherForecastButtonClickListener();
    private class GetWeatherForecastButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressText.getText().toString();
            String clientPort = clientPortText.getText().toString();
            String city = clientCityText.getText().toString();
            String informationType = clientSpinner.getSelectedItem().toString();

            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (city == null || city.isEmpty()
                    || informationType == null || informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            weatherForecastTextView.setText(Constants.EMPTY_STRING);
            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), city, informationType, weatherForecastTextView
            );
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("SimulareTestPractic02", "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_simulare_practical_test02_main);

        serverConnectButton = (Button)findViewById(R.id.server_connect);
        serverConnectButton.setOnClickListener(connectButtonClickListener);

        serverPortText = (EditText)findViewById(R.id.server_port);
        clientAddressText = (EditText)findViewById(R.id.client_address);
        clientPortText = (EditText)findViewById(R.id.client_port);
        clientCityText = (EditText)findViewById(R.id.client_city);

        clientGetForecastButton = (Button)findViewById(R.id.client_get_forecast);
        clientGetForecastButton.setOnClickListener(getWeatherForecastButtonClickListener);

        clientSpinner = (Spinner)findViewById(R.id.spinner);

        weatherForecastTextView = (TextView)findViewById(R.id.weather_forecast_text_view);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
