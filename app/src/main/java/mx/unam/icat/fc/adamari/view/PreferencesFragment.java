package mx.unam.icat.fc.adamari.view;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import mx.unam.icat.fc.adamari.R;

public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Cargamos las preferencias desde el recurso XML
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}