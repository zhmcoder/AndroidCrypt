package com.dewdrop623.androidaescrypt.FileBrowsing.ui.dialog.filedialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.dewdrop623.androidaescrypt.FileBrowsing.ui.MainActivity;
import com.dewdrop623.androidaescrypt.FileOperations.FileModifierService;
import com.dewdrop623.androidaescrypt.FileOperations.FileOperationType;
import com.dewdrop623.androidaescrypt.FileOperations.operator.AESCryptEncryptFileOperator;
import com.dewdrop623.androidaescrypt.R;

/**
 * get password and filename from user to encrypt file
 */

public class DebugEncryptFileDialog extends FileDialog {

    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private CheckBox showPasswordCheckbox;
    private EditText fileNameEditText;
    private Button encryptButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = inflateLayout(R.layout.dialogfragment_debug_encrypt_file);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.confirmPasswordEditText);
        showPasswordCheckbox = (CheckBox) view.findViewById(R.id.showPasswordCheckbox);
        fileNameEditText = (EditText) view.findViewById(R.id.fileNameEditText);
        encryptButton = (Button) view.findViewById(R.id.encryptButton);
        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveButtonOnClick();
            }
        });

        fileNameEditText.setText(file.getName()+".aes");
        setShowPassword(false);
        showPasswordCheckbox.setOnCheckedChangeListener(showPasswordCheckBoxOnCheckedChangeListener);

        return createDialog(R.string.encrypt+" "+file.getName(), view, null);
    }

    private CheckBox.OnCheckedChangeListener showPasswordCheckBoxOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            setShowPassword(b);
        }
    };

    private void setShowPassword(boolean showPassword) {
        int inputType;
        if (showPassword) {
            inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        } else {
            inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        }
        passwordEditText.setInputType(inputType);
        confirmPasswordEditText.setInputType(inputType);
    }

    @Override
    protected void positiveButtonOnClick() {
        super.positiveButtonOnClick();
        String password1 = passwordEditText.getText().toString();
        String password2 = confirmPasswordEditText.getText().toString();
        if (!password1.equals(password2)) {
            ((MainActivity)getActivity()).showToast(getString(R.string.no_match));
            return;
        }
        if(password1.isEmpty()) {
            ((MainActivity)getActivity()).showToast(getString(R.string.password)+" "+getString(R.string.cannot_be_empty));
            return;
        }
        String fileName = fileNameEditText.getText().toString();
        if(fileName.length()==0){
            ((MainActivity)getActivity()).showToast(getString(R.string.file_name)+" "+getString(R.string.cannot_be_empty));
            return;
        }
        Bundle args = new Bundle();
        args.putString(AESCryptEncryptFileOperator.AESCRYPT_FILE_OPERATOR_KEY_ARGUMENT,password1);
        args.putString(AESCryptEncryptFileOperator.AESCRYPT_FILE_OPERATOR_FILENAME_ARGUMENT, fileName);
        args.putString(FileModifierService.FILEMODIFIERSERVICE_FILE, file.getAbsolutePath());
        args.putInt(FileModifierService.FILEMODIFIERSERVICE_OPERATIONTYPE, FileOperationType.ENCRYPT);
        fileViewer.sendFileCommandToFileBrowser(args);
        dismiss();
    }
}
