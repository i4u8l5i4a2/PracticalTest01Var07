package ro.pub.cs.systems.eim.practicaltest01var07;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.Random;

/**
 * CERINȚĂ D.1: Service Started care difuzează mesaje la fiecare 10 secunde
 *
 * SERVICE = Componentă Android care rulează în background (fără UI)
 * STARTED SERVICE = Pornit cu startService(), rulează independent de activitate
 *
 * Funcționalitate:
 * - Rulează un task periodic la fiecare 10 secunde
 * - Generează un număr întreg aleator
 * - Trimite numărul prin Broadcast către activitatea principală
 * - Activitatea va actualiza câmpurile text cu acest număr
 */
public class PracticalTest01Var07Service extends Service {

    // Tag pentru Log-uri
    private static final String TAG = "Var07Service";

    // CERINȚĂ D.1.a: Interval de difuzare - 10 secunde (în milisecunde)
    private static final long BROADCAST_INTERVAL = 10000; // 10 secunde = 10000 ms

    // ACTION pentru Broadcast - identificator unic pentru mesajele noastre
    // Activitatea principală va asculta mesaje cu acest action
    public static final String ACTION_RANDOM_NUMBER =
            "ro.pub.cs.systems.eim.practicaltest01var07.ACTION_RANDOM_NUMBER";

    // Cheia pentru valoarea din Broadcast
    public static final String EXTRA_RANDOM_NUMBER = "extra_random_number";

    // Handler = Obiect care permite programarea task-urilor pe Main Thread
    // Folosim Main Looper pentru a putea trimite Broadcast-uri
    private Handler handler;

    // Runnable = Task care va fi executat periodic
    private Runnable broadcastRunnable;

    // Random = Generator de numere aleatoare
    private Random random;

    // Flag pentru a controla oprirea serviciului
    private boolean isRunning = false;

    /**
     * Constructor implicit
     */
    public PracticalTest01Var07Service() {
        super();
    }

    /**
     * onBind() = Metodă obligatorie pentru Service
     *
     * Pentru Started Service returnăm null (nu avem nevoie de binding)
     * BOUND SERVICE ar returna un IBinder pentru comunicare directă
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * onCreate() = Apelat când serviciul este creat PRIMA DATĂ
     *
     * Aici inițializăm resursele necesare:
     * - Handler pentru task-uri periodice
     * - Random pentru generare numere
     * - Runnable cu logica de difuzare
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service CREAT - onCreate()");

        // Inițializare Handler pe Main Thread Looper
        // Main Looper = bucla principală a aplicației Android
        handler = new Handler(Looper.getMainLooper());

        // Inițializare generator de numere aleatoare
        random = new Random();

        // CERINȚĂ D.1.a: Definim task-ul care va rula la fiecare 10 secunde
        broadcastRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    // CERINȚĂ D.1.a: Generăm un număr întreg aleator
                    // nextInt(1000) = generează între 0 și 999
                    int randomNumber = random.nextInt(1000);

                    Log.d(TAG, "Generez și difuzez număr aleator: " + randomNumber);

                    // CERINȚĂ D.1.a: Creăm Intent pentru Broadcast
                    // Intent = mesaj care va fi trimis către BroadcastReceiver
                    Intent broadcastIntent = new Intent(ACTION_RANDOM_NUMBER);

                    // Adăugăm numărul aleator în Intent
                    broadcastIntent.putExtra(EXTRA_RANDOM_NUMBER, randomNumber);

                    // DIFUZĂM mesajul (trimitem Broadcast)
                    // Orice BroadcastReceiver înregistrat pentru ACTION_RANDOM_NUMBER
                    // va primi acest mesaj
                    sendBroadcast(broadcastIntent);

                    // CERINȚĂ D.1.a: Reprogramăm task-ul să ruleze din nou peste 10 secunde
                    // postDelayed() = programează Runnable să ruleze după un delay
                    handler.postDelayed(this, BROADCAST_INTERVAL);
                }
            }
        };
    }

    /**
     * onStartCommand() = Apelat de fiecare dată când se apelează startService()
     *
     * @param intent Intent-ul folosit pentru pornirea serviciului
     * @param flags Flag-uri suplimentare
     * @param startId ID unic pentru această cerere de pornire
     * @return Modul de comportament al serviciului la restart
     *
     * START_STICKY = Dacă Android omoară serviciul din lipsa de memorie,
     *                îl va reporni automat când are resurse disponibile
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service PORNIT - onStartCommand()");

        // Marcăm serviciul ca fiind activ
        isRunning = true;

        // CERINȚĂ D.1.a: Pornim task-ul periodic
        // postDelayed(runnable, delay) = execută Runnable după 'delay' milisecunde
        // Prima execuție va fi după 10 secunde de la pornire
        handler.postDelayed(broadcastRunnable, BROADCAST_INTERVAL);

        Log.d(TAG, "Task periodic programat - va difuza la fiecare " +
                (BROADCAST_INTERVAL / 1000) + " secunde");

        // START_STICKY = Android va reporni serviciul dacă e oprit forțat
        return START_STICKY;
    }

    /**
     * CERINȚĂ D.1.b: onDestroy() = Apelat când serviciul este oprit
     *
     * Aici curățăm resursele:
     * - Oprim task-ul periodic
     * - Marcăm serviciul ca inactiv
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service OPRIT - onDestroy()");

        // Oprim task-ul periodic
        isRunning = false;

        // Anulăm toate task-urile programate pe Handler
        // removeCallbacks() = șterge Runnable-ul din coadă
        if (handler != null && broadcastRunnable != null) {
            handler.removeCallbacks(broadcastRunnable);
        }

        Log.d(TAG, "Task periodic ANULAT - nu se vor mai difuza mesaje");
    }
}