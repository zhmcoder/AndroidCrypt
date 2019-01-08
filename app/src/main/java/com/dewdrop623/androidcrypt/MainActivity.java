package com.dewdrop623.androidcrypt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dewdrop623.androidcrypt.FilePicker.FilePickerFragment;
import com.dewdrop623.androidcrypt.FilePicker.IconFilePickerFragment;
import com.dewdrop623.androidcrypt.FilePicker.ListFilePickerFragment;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private FloatingActionButton fab;
    private boolean mainActivityFragmentOnTop = true;

    private static final String MAINACITIVITYFRAGMENT_ON_TOP_KEY = "com.dewdrop623.androidcrypt.MainActivity.MAINACTIVITYFRAGMENT_ON_TOP_KEY";
    private static final String MAINACTIVITYFRAGMENT_TAG = "com.dewdrop623.androidcrypt.MainActivity.MAINACTIVITYFRAGMENT_TAG";
    private static final String FILEPICKERFRAGMENT_TAG = "com.dewdrop623.androidcrypt.MainActivity.FILEPICKERFRAGMENT_TAG";
    private static final String TITLE_KEY = "com.dewdrop623.androidcrypt.MainActivity.TITLE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (SettingsHelper.getUseDarkTeme(this)) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final MainActivityFragment mainActivityFragment;
        if (savedInstanceState == null) {
            mainActivityFragment = new MainActivityFragment();
            attachFragment(mainActivityFragment, false, MAINACTIVITYFRAGMENT_TAG);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            mainActivityFragment = getMainActivityFragment();
            mainActivityFragmentOnTop = savedInstanceState.getBoolean(MAINACITIVITYFRAGMENT_ON_TOP_KEY, true);
            CharSequence title = savedInstanceState.getCharSequence(TITLE_KEY, null);
            if (title != null) {
                getSupportActionBar().setTitle(title);
            }
        }


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityFragment.actionButtonPressed();
            }
        });
        setFabVisible(mainActivityFragmentOnTop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(!mainActivityFragmentOnTop);
    }

    private MainActivityFragment getMainActivityFragment() {
        return (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(MAINACTIVITYFRAGMENT_TAG);
    }

    private FilePickerFragment getFilePickerFragment() {
        return (FilePickerFragment) getSupportFragmentManager().findFragmentByTag(FILEPICKERFRAGMENT_TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == StorageAccessFrameworkHelper.SAF_REMOVABLE_STORAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri removableStorageRoot = data.getData();
                DocumentFile pickedDir = null;
                if (removableStorageRoot != null) {
                    pickedDir = DocumentFile.fromTreeUri(this, removableStorageRoot);
                }
                grantUriPermission(getPackageName(), removableStorageRoot, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(removableStorageRoot, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Uri removableStorageUri = null;
                if (pickedDir != null) {
                    removableStorageUri = pickedDir.getUri();
                }
                String name = null;
                if (removableStorageUri != null) {
                    name = DocumentFile.fromTreeUri(this, removableStorageUri).toString();
                }
                if (name != null) {
                    String removableStoragePath = StorageAccessFrameworkHelper.findLikelyRemovableStoragePathFromName(this, name);
                    HashMap<String, String> externalMountpoints = SettingsHelper.getExternalMountpointUris(this);
                    externalMountpoints.put(removableStoragePath, removableStorageUri.toString());
                    SettingsHelper.setExternalMountpointUris(this, externalMountpoints);
                    FilePickerFragment filePickerFragment = getFilePickerFragment();
                    if (filePickerFragment != null) {
                        filePickerFragment.changePathToSDCard();
                    }
                }
                else {
                    Toast.makeText(this, R.string.did_not_get_sdcard_access, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.did_not_get_sdcard_access, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(MAINACITIVITYFRAGMENT_ON_TOP_KEY, mainActivityFragmentOnTop);
        outState.putCharSequence(TITLE_KEY, getSupportActionBar().getTitle());
        super.onSaveInstanceState(outState);
    }

    //choose whether the Floating Action Button should be visible or not
    public void setFabVisible(boolean visible) {
        fab.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    //Called by MainActivityFragment to change the icon when switching between encryption and decryption.
    public void setFABIcon(int drawableId) {
        fab.setImageDrawable(AppCompatResources.getDrawable(this, drawableId));
    }

    /**
     * Called by MainActivityFragment when the user is picking an input or output file.
     * initialFolder - the file picker opens with this directory
     * defaultOutputFilename - if isOutput this will be filled in the output name field, otherwise it can be null
     */
    public void pickFile(boolean isOutput, DocumentFile initialFolder, String defaultOutputFilename) {
        FilePickerFragment filePickerFragment = null;
        int filePickerType = SettingsHelper.getFilePickerType(this);
        if (filePickerType == SettingsHelper.FILE_ICON_VIEWER) {
            filePickerFragment = new IconFilePickerFragment();
        } else if (filePickerType == SettingsHelper.FILE_LIST_VIEWER) {
            filePickerFragment = new ListFilePickerFragment();
        }
        String title = isOutput?getString(R.string.choose_output_file):getString(R.string.choose_input_file);
        Bundle args = new Bundle();
        args.putBoolean(FilePickerFragment.IS_OUTPUT_KEY, isOutput);
        GlobalDocumentFileStateHolder.setInitialFilePickerDirectory(initialFolder);
        args.putString(FilePickerFragment.DEFAULT_OUTPUT_FILENAME_KEY, defaultOutputFilename);
        filePickerFragment.setArguments(args);
        displaySecondaryFragmentScreen(filePickerFragment, title, FILEPICKERFRAGMENT_TAG);
    }

    public void filePicked(DocumentFile fileParentDirectory, String filename, boolean isOutput) {
        getMainActivityFragment().setFile(fileParentDirectory, filename, isOutput);
    }

    /**
     * Called to display things like SettingsFragment and AboutFragment, or by pickFile to display
     * the file picker.
     */
    public void displaySecondaryFragmentScreen(Fragment fragment, String title, String tag) {
        setFabVisible(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        attachFragment(fragment, true, tag);
        if (title != null) {
            getSupportActionBar().setTitle(title);
        }
        mainActivityFragmentOnTop = false;
    }

    /*
    * Called by MainActivityFragment's onResume.
    * Replace the fragment with itself so that the UI will update
    * Bring the FAB back, remove the back arrow from the action bar, change the title
    * */
    public void returnedToMainFragment() {
        mainActivityFragmentOnTop = true;
        setFabVisible(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    /*
    * display the given fragment
     */
    private void attachFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, android.R.anim.fade_out, android.R.anim.slide_in_left, android.R.anim.fade_out);
        } else {
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.slide_in_left, android.R.anim.fade_out);
        }
        fragmentTransaction.replace(R.id.main_fragment_container, fragment, tag);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //back button was pressed in a secondary fragment like aboutfragment or settingsfragment
        if (item.getItemId() == android.R.id.home) {
            superOnBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FilePickerFragment filePickerFragment = getFilePickerFragment();
        if (filePickerFragment != null && filePickerFragment.isVisible()) {
            filePickerFragment.onBackPressed();
        } else {
            superOnBackPressed();
        }
    }

    /**
     * Used to implement back button behavior, even when FilePickerFragment is visible.
     * e.g. FilePickerFragment usually goes up a directory in response to back button, unless it is at the
     * file system root.
     */
    public void superOnBackPressed() {
        super.onBackPressed();
    }

    /*Get the attributes for the current theme
    * attribute argument from R.attr or android.R.attr*/
    public int getDarkThemeColor(int attribute) {
        return getTheme().obtainStyledAttributes(R.style.AppThemeDark, new int[] {attribute}).getColor(0,0);
    }
}
