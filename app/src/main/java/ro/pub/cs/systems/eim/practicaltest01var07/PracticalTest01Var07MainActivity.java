package ro.pub.cs.systems.eim.practicaltest01var07;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * ACTIVITATE PRINCIPALĂ - PracticalTest01Var07
 *
 * Autor: i4u8l5i4a2
 * Data: 2025-11-09
 *
 * ============================================================================
 * CERINȚE IMPLEMENTATE:
 * ============================================================================
 *
 * C.1. [15%] Validare și pornire activitate secundară
 *      - Butonul Set verifică dacă toate câmpurile conțin numere
 *      - Dacă DA → pornește activitatea secundară prin Intent
 *      - Dacă NU → ignoră click-ul
 *
 * C.2.a [10%] Variabile neasociate cu interfața grafică
 *      - Salvează Suma și Produsul în variabile private (nu în UI)
 *      - Le afișează în Toast când sunt primite din activitatea secundară
 *
 * C.2.b [10%] Mecanism salvare/restaurare stare
 *      - onSaveInstanceState() = salvează starea înainte de distrugere
 *      - onRestoreInstanceState() = restaurează starea după recreare
 *      - Protejează împotriva pierderii datelor la rotire ecran, etc.
 *
 * C.2.c [10%] Testare funcționalitate
 *      - Soluția simplă: android:configChanges în AndroidManifest.xml
 *      - Previne distrugerea activității la configuration changes
 *
 * D.1.a [10%] Service Started - Pornire și difuzare periodică
 *      - Pornește PracticalTest01Var07Service la nivelul sistemului Android
 *      - Service-ul difuzează un număr aleator la fiecare 10 secunde
 *
 * D.1.b [10%] Service - Oprire la distrugere activitate
 *      - Service-ul este oprit în onDestroy()
 *      - Eliberează resurse când activitatea se închide
 *
 * D.2. [10%] BroadcastReceiver - Primire și procesare mesaje
 *      - BroadcastReceiver ascultă mesaje de la Service
 *      - Actualizează cele 4 câmpuri text cu numărul aleator primit
 *      - Se înregistrează în onResume(), se dezînregistrează în onPause()
 *
 * ============================================================================
 */
public class PracticalTest01Var07MainActivity extends AppCompatActivity {

    // ========================================================================
    // CONSTANTE
    // ========================================================================

    // Tag pentru Log-uri (debugging în Logcat)
    private static final String TAG = "MainActivity";

    // Chei pentru salvarea stării în Bundle (CERINȚĂ C.2.b)
    private static final String KEY_SUMA = "saved_suma";
    private static final String KEY_PRODUS = "saved_produs";

    // ========================================================================
    // VARIABILE UI (Interfață grafică)
    // ========================================================================

    // Cele 4 câmpuri text editabile (tabel 2x2)
    private EditText editTextTopLeft;       // Câmpul din stânga-sus
    private EditText editTextTopRight;      // Câmpul din dreapta-sus
    private EditText editTextBottomLeft;    // Câmpul din stânga-jos
    private EditText editTextBottomRight;   // Câmpul din dreapta-jos

    // Butonul Set pentru validare și pornire activitate secundară
    private Button buttonSet;

    // ========================================================================
    // VARIABILE PENTRU ACTIVITATE SECUNDARĂ
    // ========================================================================

    // ActivityResultLauncher = Mecanism modern pentru pornirea unei activități
    // și primirea rezultatului înapoi (înlocuiește startActivityForResult deprecated)
    private ActivityResultLauncher<Intent> secondaryActivityLauncher;

    // ========================================================================
    // CERINȚĂ C.2.a: VARIABILE NEASOCIATE CU INTERFAȚA GRAFICĂ
    // ========================================================================

    // Acestea stochează Suma și Produsul primite din activitatea secundară
    // NU sunt legate direct de elemente UI (EditText, TextView, etc.)
    // Sunt afișate utilizatorului prin Toast, nu prin UI
    private String suma = null;      // Ultima sumă calculată (ex: "Sum: 14")
    private String produs = null;    // Ultimul produs calculat (ex: "Product: 120")

    // ========================================================================
    // CERINȚĂ D.2: BROADCASTRECEIVER PENTRU MESAJE DE LA SERVICE
    // ========================================================================

    /**
     * BroadcastReceiver = Ascultător care primește mesaje broadcast
     *
     * Acest receiver ascultă mesaje trimise de PracticalTest01Var07Service
     * la fiecare 10 secunde. Când primește un mesaj, actualizează cele 4
     * câmpuri text cu numărul aleator primit.
     *
     * FLOW:
     * 1. Service generează număr aleator la fiecare 10 secunde
     * 2. Service trimite Broadcast cu numărul
     * 3. Acest receiver primește Broadcast-ul
     * 4. Actualizează cele 4 EditText-uri cu numărul
     */
    private BroadcastReceiver randomNumberReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Verificăm că mesajul are action-ul corect (securitate)
            if (PracticalTest01Var07Service.ACTION_RANDOM_NUMBER.equals(intent.getAction())) {

                // CERINȚĂ D.2: Extragem numărul aleator din Intent
                // getIntExtra(cheie, valoare_default) = ia valoarea sau 0 dacă nu există
                int randomNumber = intent.getIntExtra(
                        PracticalTest01Var07Service.EXTRA_RANDOM_NUMBER, 0);

                Log.d(TAG, "📩 PRIMIT Broadcast cu număr aleator: " + randomNumber);

                // CERINȚĂ D.2: Suprascrierea câmpurilor text cu valoarea primită
                // Convertim numărul în String pentru a-l afișa în EditText
                String numberStr = String.valueOf(randomNumber);

                // Actualizăm toate cele 4 câmpuri simultan cu același număr
                editTextTopLeft.setText(numberStr);
                editTextTopRight.setText(numberStr);
                editTextBottomLeft.setText(numberStr);
                editTextBottomRight.setText(numberStr);

                // Afișăm Toast pentru feedback vizual utilizatorului
                Toast.makeText(PracticalTest01Var07MainActivity.this,
                        "📱 Câmpuri actualizate cu: " + randomNumber,
                        Toast.LENGTH_SHORT).show();

                Log.d(TAG, "✅ Câmpuri text suprascrise cu valoarea: " + numberStr);
            }
        }
    };

    // ========================================================================
    // LIFECYCLE METHODS (Metode de ciclu de viață Android)
    // ========================================================================

    /**
     * onCreate() = Prima metodă apelată când activitatea este creată
     *
     * Aici se face:
     * - Setarea layout-ului (interfața grafică din XML)
     * - Inițializarea variabilelor UI (findViewById)
     * - Înregistrarea ActivityResultLauncher
     * - Restaurarea stării salvate (dacă există)
     * - Setarea listener-ilor pentru butoane
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "🔵 onCreate() - Activitate creată");

        // Activează modul EdgeToEdge pentru interfață modernă (fără margini)
        EdgeToEdge.enable(this);

        // Setează layout-ul activității din fișierul XML
        setContentView(R.layout.activity_practical_test01_var07_main);

        // Gestionează padding-ul pentru bare de sistem (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ====================================================================
        // INIȚIALIZARE ELEMENTE UI
        // ====================================================================

        // findViewById() = Caută elementul cu ID-ul specificat în layout XML
        // și returnează o referință către el
        editTextTopLeft = findViewById(R.id.editTextTopLeft);
        editTextTopRight = findViewById(R.id.editTextTopRight);
        editTextBottomLeft = findViewById(R.id.editTextBottomLeft);
        editTextBottomRight = findViewById(R.id.editTextBottomRight);
        buttonSet = findViewById(R.id.buttonSet);

        Log.d(TAG, "✅ Elemente UI inițializate");

        // ====================================================================
        // CERINȚĂ C.2.b: RESTAURARE STARE SALVATĂ
        // ====================================================================

        // onCreate() primește un Bundle cu date salvate anterior
        // (sau null dacă e prima rulare a activității)
        if (savedInstanceState != null) {
            // Restaurăm variabilele suma și produs din Bundle
            suma = savedInstanceState.getString(KEY_SUMA);
            produs = savedInstanceState.getString(KEY_PRODUS);

            Log.d(TAG, "🔄 Stare RESTAURATĂ din Bundle: Suma=" + suma + ", Produs=" + produs);

            // Afișăm Toast pentru confirmare (testare C.2.c)
            if (suma != null || produs != null) {
                String message = "🔄 Stare restaurată!\n";
                if (suma != null) message += "Suma: " + suma + "\n";
                if (produs != null) message += "Produs: " + produs;

                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "🆕 Prima rulare - nu există stare salvată");
        }

        // ====================================================================
        // ÎNREGISTRARE ACTIVITYRESULTLAUNCHER
        // ====================================================================

        // ActivityResultLauncher = Mecanism pentru a lansa o activitate
        // și a primi rezultatul înapoi când se închide
        //
        // FLOW:
        // 1. User apasă butonul Set
        // 2. Lansăm activitatea secundară cu secondaryActivityLauncher.launch()
        // 3. User apasă Sum sau Product în activitatea secundară
        // 4. Activitatea secundară se închide și returnează rezultatul
        // 5. Metoda onActivityResult() de mai jos este apelată automat
        secondaryActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Această metodă se execută când activitatea secundară
                        // se închide și returnează un rezultat

                        Log.d(TAG, "🔙 Revenit din activitatea secundară");

                        // Verificăm dacă rezultatul este OK (succes)
                        // RESULT_OK = cod standard pentru operație reușită
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                // Extragem rezultatul (suma sau produsul)
                                String resultValue = data.getStringExtra(
                                        PracticalTest01Var07SecondaryActivity.EXTRA_RESULT);

                                Log.d(TAG, "📊 Rezultat primit: " + resultValue);

                                // CERINȚĂ C.2.a: Salvăm în variabilele neasociate cu UI
                                // Determinăm dacă e sumă sau produs pe baza conținutului
                                if (resultValue != null) {
                                    if (resultValue.startsWith("Sum:")) {
                                        suma = resultValue;
                                        Log.d(TAG, "💾 Suma salvată în variabilă: " + suma);
                                    } else if (resultValue.startsWith("Product:")) {
                                        produs = resultValue;
                                        Log.d(TAG, "💾 Produs salvat în variabilă: " + produs);
                                    }
                                }

                                // CERINȚĂ C.2.a: Afișăm în Toast (NU în UI)
                                Toast.makeText(PracticalTest01Var07MainActivity.this,
                                        "📊 Rezultat: " + resultValue,
                                        Toast.LENGTH_LONG).show();

                                // Log alternativ (cerință - Toast SAU Log)
                                Log.i(TAG, "📊 Rezultat afișat în Toast: " + resultValue);
                            }
                        } else {
                            Log.w(TAG, "⚠️ Activitatea secundară anulată sau închisă fără rezultat");
                        }
                    }
                });

        Log.d(TAG, "✅ ActivityResultLauncher înregistrat");

        // ====================================================================
        // CERINȚĂ C.1: LISTENER PENTRU BUTONUL SET CU VALIDARE NUMERICĂ
        // ====================================================================

        // setOnClickListener() = Înregistrează un ascultător pentru click-uri
        // Codul din onClick() se execută când utilizatorul apasă butonul
        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "🖱️ Buton Set apăsat - încep validarea");

                // Extragem valorile text din cele 4 câmpuri
                // getText().toString().trim() = ia textul și elimină spațiile
                String topLeftStr = editTextTopLeft.getText().toString().trim();
                String topRightStr = editTextTopRight.getText().toString().trim();
                String bottomLeftStr = editTextBottomLeft.getText().toString().trim();
                String bottomRightStr = editTextBottomRight.getText().toString().trim();

                // ============================================================
                // VALIDARE 1: Verificăm dacă toate câmpurile sunt completate
                // ============================================================
                if (topLeftStr.isEmpty() || topRightStr.isEmpty() ||
                        bottomLeftStr.isEmpty() || bottomRightStr.isEmpty()) {

                    Toast.makeText(PracticalTest01Var07MainActivity.this,
                            "❌ Eroare: Toate câmpurile trebuie completate!",
                            Toast.LENGTH_SHORT).show();

                    Log.w(TAG, "❌ Click IGNORAT: Câmpuri goale detectate");

                    // CERINȚĂ C.1: IGNORĂM click-ul (return = oprim execuția)
                    return;
                }

                // ============================================================
                // CERINȚĂ C.1: VALIDARE 2 - Verificare numerică
                // ============================================================
                // Verificăm dacă fiecare câmp conține un număr valid
                // Dacă găsim un câmp invalid → IGNORĂM click-ul

                if (!isNumeric(topLeftStr)) {
                    Toast.makeText(PracticalTest01Var07MainActivity.this,
                            "❌ Eroare: Top-Left nu conține un număr valid!",
                            Toast.LENGTH_SHORT).show();
                    editTextTopLeft.setError("Introdu un număr valid");
                    Log.w(TAG, "❌ Click IGNORAT: Top-Left nu e numeric: '" + topLeftStr + "'");
                    return; // IGNORĂM click-ul
                }

                if (!isNumeric(topRightStr)) {
                    Toast.makeText(PracticalTest01Var07MainActivity.this,
                            "❌ Eroare: Top-Right nu conține un număr valid!",
                            Toast.LENGTH_SHORT).show();
                    editTextTopRight.setError("Introdu un număr valid");
                    Log.w(TAG, "❌ Click IGNORAT: Top-Right nu e numeric: '" + topRightStr + "'");
                    return; // IGNORĂM click-ul
                }

                if (!isNumeric(bottomLeftStr)) {
                    Toast.makeText(PracticalTest01Var07MainActivity.this,
                            "❌ Eroare: Bottom-Left nu conține un număr valid!",
                            Toast.LENGTH_SHORT).show();
                    editTextBottomLeft.setError("Introdu un număr valid");
                    Log.w(TAG, "❌ Click IGNORAT: Bottom-Left nu e numeric: '" + bottomLeftStr + "'");
                    return; // IGNORĂM click-ul
                }

                if (!isNumeric(bottomRightStr)) {
                    Toast.makeText(PracticalTest01Var07MainActivity.this,
                            "❌ Eroare: Bottom-Right nu conține un număr valid!",
                            Toast.LENGTH_SHORT).show();
                    editTextBottomRight.setError("Introdu un număr valid");
                    Log.w(TAG, "❌ Click IGNORAT: Bottom-Right nu e numeric: '" + bottomRightStr + "'");
                    return; // IGNORĂM click-ul
                }

                // ============================================================
                // VALIDARE REUȘITĂ! Toate câmpurile conțin numere valide
                // ============================================================
                Log.d(TAG, "✅ Validare reușită! Toate câmpurile conțin numere.");

                // CERINȚĂ C.1: Dacă toate câmpurile conțin numere → emitem Intent

                // Creăm Intent pentru a porni activitatea secundară
                // Intent = obiect care descrie o acțiune de efectuat
                Intent intent = new Intent(
                        PracticalTest01Var07MainActivity.this,  // Context (activitatea curentă)
                        PracticalTest01Var07SecondaryActivity.class  // Clasa țintă
                );

                // Transmitem cele 4 valori prin Intent folosind putExtra()
                // putExtra(cheie, valoare) = adaugă o pereche cheie-valoare
                intent.putExtra(PracticalTest01Var07SecondaryActivity.EXTRA_TOP_LEFT, topLeftStr);
                intent.putExtra(PracticalTest01Var07SecondaryActivity.EXTRA_TOP_RIGHT, topRightStr);
                intent.putExtra(PracticalTest01Var07SecondaryActivity.EXTRA_BOTTOM_LEFT, bottomLeftStr);
                intent.putExtra(PracticalTest01Var07SecondaryActivity.EXTRA_BOTTOM_RIGHT, bottomRightStr);

                // Lansăm activitatea secundară și așteptăm rezultat
                secondaryActivityLauncher.launch(intent);

                Log.d(TAG, "🚀 Intent emis! Activitate secundară lansată cu valorile: [" +
                        topLeftStr + ", " + topRightStr + ", " + bottomLeftStr + ", " + bottomRightStr + "]");
            }
        });

        Log.d(TAG, "✅ Listener buton Set configurat");
    }

    /**
     * CERINȚĂ D.1.a și D.2: onResume() = Activitatea devine vizibilă
     *
     * onResume() este apelat când:
     * - Activitatea este afișată pentru prima dată
     * - Utilizatorul revine din altă aplicație
     * - Utilizatorul revine din activitatea secundară
     *
     * Aici pornim Service-ul și înregistrăm BroadcastReceiver-ul
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "🟢 onResume() - Activitate vizibilă");

        // ====================================================================
        // CERINȚĂ D.1.a: PORNIRE SERVICE
        // ====================================================================

        // startService() = pornește serviciul la nivelul sistemului Android
        // Dacă serviciul rulează deja, va apela doar onStartCommand()
        Intent serviceIntent = new Intent(this, PracticalTest01Var07Service.class);
        startService(serviceIntent);

        Log.d(TAG, "🚀 Service PORNIT - va difuza mesaje la fiecare 10 secunde");

        // ====================================================================
        // CERINȚĂ D.2: ÎNREGISTRARE BROADCASTRECEIVER
        // ====================================================================

        // IntentFilter = specifică ce tip de Broadcast-uri vrem să primim
        // Vrem să primim doar mesaje cu ACTION_RANDOM_NUMBER
        IntentFilter filter = new IntentFilter(PracticalTest01Var07Service.ACTION_RANDOM_NUMBER);

        // registerReceiver() = înregistrează receiver-ul pentru a primi mesaje
        // RECEIVER_NOT_EXPORTED = receiver-ul primește DOAR mesaje din propria aplicație
        // (securitate - previne aplicații rău-intenționate să ne trimită mesaje)
        registerReceiver(randomNumberReceiver, filter, Context.RECEIVER_NOT_EXPORTED);

        Log.d(TAG, "📡 BroadcastReceiver ÎNREGISTRAT - voi primi mesaje de la Service");
    }

    /**
     * CERINȚĂ D.2: onPause() = Activitatea nu mai este vizibilă
     *
     * onPause() este apelat când:
     * - Utilizatorul deschide altă aplicație
     * - Utilizatorul deschide activitatea secundară
     * - Activitatea este parțial acoperită
     *
     * Aici dezînregistrăm BroadcastReceiver-ul pentru a economisi resurse
     *
     * NOTĂ: NU oprim Service-ul aici, doar în onDestroy()!
     * Service-ul continuă să ruleze în background.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "🟡 onPause() - Activitate nu mai este vizibilă");

        // ====================================================================
        // CERINȚĂ D.2: DEZÎNREGISTRARE BROADCASTRECEIVER
        // ====================================================================

        // unregisterReceiver() = oprim primirea de mesaje broadcast
        // Important pentru a evita memory leak-uri (scurgeri de memorie)
        try {
            unregisterReceiver(randomNumberReceiver);
            Log.d(TAG, "📴 BroadcastReceiver DEZÎNREGISTRAT");
        } catch (IllegalArgumentException e) {
            // Receiver-ul nu era înregistrat - nu e problemă
            Log.w(TAG, "⚠️ Receiver nu era înregistrat (probabil prima rulare)");
        }
    }

    /**
     * CERINȚĂ C.2.b: onSaveInstanceState() = Salvare stare înainte de distrugere
     *
     * Această metodă este apelată AUTOMAT de Android înainte de a distruge
     * activitatea în următoarele situații:
     * - Rotirea ecranului (landscape ↔ portrait)
     * - Lipsa de memorie RAM (Android omoară activități în background)
     * - Schimbarea configurației dispozitivului (limbă, font size, etc.)
     *
     * Aici salvăm variabilele importante în Bundle pentru a le restaura mai târziu
     *
     * @param outState Bundle (dicționar cheie-valoare) în care salvăm datele
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Salvăm variabilele suma și produs în Bundle
        // putString(cheie, valoare) = adaugă o pereche în dicționar
        outState.putString(KEY_SUMA, suma);
        outState.putString(KEY_PRODUS, produs);

        Log.d(TAG, "💾 Stare SALVATĂ în Bundle: Suma=" + suma + ", Produs=" + produs);
    }

    /**
     * CERINȚĂ C.2.b: onRestoreInstanceState() = Restaurare stare după recreare
     *
     * Această metodă este apelată AUTOMAT de Android DUPĂ onCreate()
     * dacă există date salvate în Bundle (dacă onSaveInstanceState() a fost apelat)
     *
     * ALTERNATIVĂ: Poți restaura direct în onCreate(savedInstanceState)
     *
     * @param savedInstanceState Bundle cu datele salvate anterior
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restaurăm variabilele din Bundle
        // getString(cheie) = ia valoarea asociată cheii (sau null dacă nu există)
        suma = savedInstanceState.getString(KEY_SUMA);
        produs = savedInstanceState.getString(KEY_PRODUS);

        Log.d(TAG, "🔄 Stare RESTAURATĂ în onRestoreInstanceState: Suma=" + suma + ", Produs=" + produs);

        // Afișăm Toast pentru confirmare (testare CERINȚĂ C.2.c)
        if (suma != null || produs != null) {
            String message = "🔄 Stare restaurată din Bundle!\n";
            if (suma != null) message += "Suma: " + suma + "\n";
            if (produs != null) message += "Produs: " + produs;

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * CERINȚĂ D.1.b: onDestroy() = Activitatea este distrusă definitiv
     *
     * onDestroy() este apelat când:
     * - Utilizatorul apasă Back și închide aplicația
     * - Sistemul Android omoară activitatea din lipsa de memorie
     * - finish() este apelat programatic
     *
     * Aici oprim Service-ul pentru a elibera resurse
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "🔴 onDestroy() - Activitate DISTRUSĂ");

        // ====================================================================
        // CERINȚĂ D.1.b: OPRIRE SERVICE
        // ====================================================================

        // stopService() = oprește serviciul definitiv
        // Service-ul va apela onDestroy() și va opri task-ul periodic
        Intent serviceIntent = new Intent(this, PracticalTest01Var07Service.class);
        stopService(serviceIntent);

        Log.d(TAG, "🛑 Service OPRIT - nu se vor mai difuza mesaje");
    }

    // ========================================================================
    // METODE AUXILIARE (Helper Methods)
    // ========================================================================

    /**
     * CERINȚĂ C.1: Verifică dacă un String poate fi convertit în număr valid
     *
     * Această metodă încearcă să convertească String-ul în număr double.
     * Dacă reușește → returnează true (e număr valid)
     * Dacă eșuează (aruncă excepție) → returnează false (NU e număr)
     *
     * @param str String-ul de verificat
     * @return true dacă e număr valid, false altfel
     *
     * EXEMPLE:
     * - isNumeric("5") → true
     * - isNumeric("5.5") → true
     * - isNumeric("-3.14") → true
     * - isNumeric("abc") → false
     * - isNumeric("12.34.56") → false
     * - isNumeric("") → false
     * - isNumeric(null) → false
     */
    private boolean isNumeric(String str) {
        // Verificăm dacă String-ul e null sau gol
        if (str == null || str.isEmpty()) {
            return false;
        }

        try {
            // Încercăm să convertim String-ul în număr double
            // Double acceptă: întregi (5), zecimale (5.5), negative (-5), etc.
            Double.parseDouble(str);

            // Dacă am ajuns aici, conversia a reușit → e număr valid
            return true;

        } catch (NumberFormatException e) {
            // Dacă apare excepție → String-ul NU e număr valid
            Log.d(TAG, "❌ String-ul '" + str + "' NU este un număr valid");
            return false;
        }
    }
}