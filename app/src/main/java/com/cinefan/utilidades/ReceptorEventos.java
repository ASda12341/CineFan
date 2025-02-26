package com.cinefan.utilidades;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.cinefan.R;
import com.cinefan.actividades.ActividadPrincipal;

/**
 * Receptor de eventos del sistema (llamadas, mensajes)
 */
public class ReceptorEventos extends BroadcastReceiver {

    private static final String TAG = "ReceptorEventos";
    private static final String CANAL_ID = "canal_cinefan";
    private static final int NOTIFICACION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String accion = intent.getAction();
        
        if (accion != null) {
            if (accion.equals("android.intent.action.PHONE_STATE")) {
                // Evento de llamada telefónica
                manejarLlamadaEntrante(context, intent);
            } else if (accion.equals("android.provider.Telephony.SMS_RECEIVED")) {
                // Evento de SMS recibido
                manejarSmsRecibido(context, intent);
            }
        }
    }
    
    /**
     * Maneja el evento de llamada entrante
     */
    private void manejarLlamadaEntrante(Context context, Intent intent) {
        String estadoTelefono = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(estadoTelefono)) {
            // El teléfono está sonando
            String numeroTelefono = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            
            // Mostrar notificación de llamada entrante
            mostrarNotificacion(
                    context,
                    context.getString(R.string.llamada_entrante),
                    context.getString(R.string.numero_llamando, numeroTelefono)
            );
            
            Log.d(TAG, "Llamada entrante de: " + numeroTelefono);
        }
    }
    
    /**
     * Maneja el evento de SMS recibido
     */
    private void manejarSmsRecibido(Context context, Intent intent) {
        // Obtener los datos del SMS
        Bundle bundle = intent.getExtras();
        
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            String formato = bundle.getString("format");
            
            if (pdus != null) {
                // Recorrer todos los mensajes recibidos
                for (Object pdu : pdus) {
                    SmsMessage sms;
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        sms = SmsMessage.createFromPdu((byte[]) pdu, formato);
                    } else {
                        sms = SmsMessage.createFromPdu((byte[]) pdu);
                    }
                    
                    String numeroOrigen = sms.getOriginatingAddress();
                    String mensaje = sms.getMessageBody();
                    
                    // Mostrar notificación de SMS recibido
                    mostrarNotificacion(
                            context,
                            context.getString(R.string.sms_recibido),
                            context.getString(R.string.mensaje_de, numeroOrigen)
                    );
                    
                    Log.d(TAG, "SMS recibido de: " + numeroOrigen + ", mensaje: " + mensaje);
                }
            }
        }
    }
    
    /**
     * Muestra una notificación
     */
    private void mostrarNotificacion(Context context, String titulo, String mensaje) {
        // Crear canal de notificación (para Android 8.0 y superior)
        crearCanalNotificacion(context);
        
        // Intent para abrir la actividad principal cuando se pulse la notificación
        Intent intent = new Intent(context, ActividadPrincipal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        
        // Mostrar la notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICACION_ID, builder.build());
        }
    }
    
    /**
     * Crea un canal de notificación (requerido para Android 8.0 y superior)
     */
    private void crearCanalNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String nombre = context.getString(R.string.canal_notificaciones);
            String descripcion = context.getString(R.string.canal_notificaciones_desc);
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            
            NotificationChannel canal = new NotificationChannel(CANAL_ID, nombre, importancia);
            canal.setDescription(descripcion);
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(canal);
            }
        }
    }
}
