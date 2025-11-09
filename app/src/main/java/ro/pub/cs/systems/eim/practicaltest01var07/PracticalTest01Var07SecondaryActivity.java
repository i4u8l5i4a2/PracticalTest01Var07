package ro.pub.cs.systems.eim.practicaltest01var07;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * ACTIVITATE SECUNDARĂ
 *
 * CERINȚĂ C2: Salvează și restaurează valorile (suma/produs) în variabile
 * neasociate cu interfața grafică, pentru situația în care sistemul Android
 * distruge activitatea (ex: rotire ecran).
 */
public class PracticalTest01Var07SecondaryActivity extends AppCompatActivity {

    private static final String TAG = "SecondaryActivity";

    // CONSTANTE pentru cheile din Intent (primirea datelor)
    public static final String EXTRA_TOP_LEFT = "extra_top_left";
    public static final String EXTRA_TOP_RIGHT = "extra_top_right";
    public static final String EXTRA_BOTTOM_LEFT = "extra_bottom_left";
    public static final String EXTRA_BOTTOM_RIGHT = "extra_bottom_right";
    public static final String EXTRA_RESULT = "extra_result";

    // CONSTANTE pentru cheile din Bundle (salvare/restaurare stare)
    private static final String KEY_VALUE_TOP_LEFT = "key_value_top_left";
    private static final String KEY_VALUE_TOP_RIGHT = "key_value_top_right";
    private static final String KEY_VALUE_BOTTOM_LEFT = "key_value_bottom_left";
    private static final String KEY_VALUE_BOTTOM_RIGHT = "key_value_bottom_right";
    private static final String KEY_LAST_RESULT = "key_last_result";

    // Elemente UI
    private TextView textViewTopLeft;
    private TextView textViewTopRight;
    private TextView textViewBottomLeft;
    private TextView textViewBottomRight;
    private Button buttonSum;
    private Button buttonProduct;

    // CERINȚĂ C2.a: Variabile neasociate cu interfața grafică
    // Acestea SUNT membre ale clasei, dar NU sunt legate direct de TextView/EditText
    private double valueTopLeft;
    private double valueTopRight;
    private double valueBottomLeft;
    private double valueBottomRight;

    // Variabilă pentru a stoca ultimul rezultat calculat (suma sau produs)
    private String lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test01_var07_secondary);

        Log.d(TAG, "onCreate() apelat");

        // Inițializare elemente UI
        textViewTopLeft = findViewById(R.id.textViewTopLeft);
        textViewTopRight = findViewById(R.id.textViewTopRight);
        textViewBottomLeft = findViewById(R.id.textViewBottomLeft);
        textViewBottomRight = findViewById(R.id.textViewBottomRight);
        buttonSum = findViewById(R.id.buttonSum);
        buttonProduct = findViewById(R.id.buttonProduct);

        // CERINȚĂ C2.b: RESTAURARE date din savedInstanceState
        // savedInstanceState != null → activitatea a fost distrusă și recreată
        // savedInstanceState == null → prima dată când se creează activitatea
        if (savedInstanceState != null) {
            // Activitatea a fost RECREATĂ (ex: după rotire ecran)
            Log.d(TAG, "Restaurare stare din savedInstanceState");

            // Restaurăm valorile din Bundle
            valueTopLeft = savedInstanceState.getDouble(KEY_VALUE_TOP_LEFT, 0.0);
            valueTopRight = savedInstanceState.getDouble(KEY_VALUE_TOP_RIGHT, 0.0);
            valueBottomLeft = savedInstanceState.getDouble(KEY_VALUE_BOTTOM_LEFT, 0.0);
            valueBottomRight = savedInstanceState.getDouble(KEY_VALUE_BOTTOM_RIGHT, 0.0);
            lastResult = savedInstanceState.getString(KEY_LAST_RESULT, null);

            // Afișăm valorile restaurate în TextView-uri
            textViewTopLeft.setText(String.valueOf(valueTopLeft));
            textViewTopRight.setText(String.valueOf(valueTopRight));
            textViewBottomLeft.setText(String.valueOf(valueBottomLeft));
            textViewBottomRight.setText(String.valueOf(valueBottomRight));

            // Afișăm ultimul rezultat salvat (dacă există)
            if (lastResult != null) {
                Toast.makeText(this, "Rezultat restaurat: " + lastResult,
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Rezultat restaurat: " + lastResult);
            }

            Log.d(TAG, "Valori restaurate: " + valueTopLeft + ", " + valueTopRight +
                    ", " + valueBottomLeft + ", " + valueBottomRight);

        } else {
            // Prima dată când se creează activitatea - primim date din Intent
            Log.d(TAG, "Prima creare - primire date din Intent");

            Intent intent = getIntent();
            if (intent != null) {
                // Extragem valorile din Intent
                String topLeft = intent.getStringExtra(EXTRA_TOP_LEFT);
                String topRight = intent.getStringExtra(EXTRA_TOP_RIGHT);
                String bottomLeft = intent.getStringExtra(EXTRA_BOTTOM_LEFT);
                String bottomRight = intent.getStringExtra(EXTRA_BOTTOM_RIGHT);

                // Afișăm în TextView-uri
                textViewTopLeft.setText(topLeft != null ? topLeft : "0");
                textViewTopRight.setText(topRight != null ? topRight : "0");
                textViewBottomLeft.setText(bottomLeft != null ? bottomLeft : "0");
                textViewBottomRight.setText(bottomRight != null ? bottomRight : "0");

                // Convertim în double și salvăm în variabilele neasociate
                try {
                    valueTopLeft = Double.parseDouble(topLeft != null ? topLeft : "0");
                    valueTopRight = Double.parseDouble(topRight != null ? topRight : "0");
                    valueBottomLeft = Double.parseDouble(bottomLeft != null ? bottomLeft : "0");
                    valueBottomRight = Double.parseDouble(bottomRight != null ? bottomRight : "0");

                    Log.d(TAG, "Valori primite: " + valueTopLeft + ", " + valueTopRight +
                            ", " + valueBottomLeft + ", " + valueBottomRight);

                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Eroare: Valori numerice invalide!",
                            Toast.LENGTH_SHORT).show();
                    valueTopLeft = valueTopRight = valueBottomLeft = valueBottomRight = 0;
                    Log.e(TAG, "Eroare conversie: " + e.getMessage());
                }
            }
        }

        // BUTONUL SUM: Calculează suma
        buttonSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calculăm suma folosind variabilele neasociate
                double sum = valueTopLeft + valueTopRight + valueBottomLeft + valueBottomRight;

                // CERINȚĂ C2.a: Salvăm rezultatul în variabilă neasociată
                lastResult = "Sum: " + sum;

                // Afișăm local
                Toast.makeText(PracticalTest01Var07SecondaryActivity.this,
                        lastResult, Toast.LENGTH_SHORT).show();

                Log.d(TAG, "Calculat: " + lastResult);

                // Returnăm către prima activitate
                returnResult(lastResult);
            }
        });

        // BUTONUL PRODUCT: Calculează produsul
        buttonProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calculăm produsul folosind variabilele neasociate
                double product = valueTopLeft * valueTopRight * valueBottomLeft * valueBottomRight;

                // CERINȚĂ C2.a: Salvăm rezultatul în variabilă neasociată
                lastResult = "Product: " + product;

                // Afișăm local
                Toast.makeText(PracticalTest01Var07SecondaryActivity.this,
                        lastResult, Toast.LENGTH_SHORT).show();

                Log.d(TAG, "Calculat: " + lastResult);

                // Returnăm către prima activitate
                returnResult(lastResult);
            }
        });
    }

    /**
     * CERINȚĂ C2.b: SALVARE stare înainte ca activitatea să fie distrusă
     *
     * Această metodă este apelată AUTOMAT de sistem când:
     * - Utilizatorul rotește ecranul
     * - Sistemul Android trebuie să distrugă activitatea pentru a elibera memorie
     * - Se schimbă configurația dispozitivului
     *
     * @param outState Bundle în care salvăm datele importante
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState() apelat - salvare stare");

        // Salvăm valorile în Bundle folosind cheile definite
        // putDouble(cheie, valoare) = adaugă un double în Bundle
        outState.putDouble(KEY_VALUE_TOP_LEFT, valueTopLeft);
        outState.putDouble(KEY_VALUE_TOP_RIGHT, valueTopRight);
        outState.putDouble(KEY_VALUE_BOTTOM_LEFT, valueBottomLeft);
        outState.putDouble(KEY_VALUE_BOTTOM_RIGHT, valueBottomRight);

        // Salvăm și ultimul rezultat calculat (dacă există)
        if (lastResult != null) {
            outState.putString(KEY_LAST_RESULT, lastResult);
            Log.d(TAG, "Rezultat salvat: " + lastResult);
        }

        Log.d(TAG, "Stare salvată: " + valueTopLeft + ", " + valueTopRight +
                ", " + valueBottomLeft + ", " + valueBottomRight);
    }

    /**
     * CERINȚĂ C2.b: RESTAURARE stare (metodă alternativă la onCreate)
     *
     * Această metodă este apelată DUPĂ onCreate(), dacă savedInstanceState != null
     * Folosită pentru restaurarea stării UI mai complexe
     *
     * @param savedInstanceState Bundle cu datele salvate
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, "onRestoreInstanceState() apelat");

        // Restaurare suplimentară (dacă e necesar)
        // În cazul nostru, restaurarea se face deja în onCreate()
    }

    /**
     * Returnează rezultatul către prima activitate
     */
    private void returnResult(String result) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_RESULT, result);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}