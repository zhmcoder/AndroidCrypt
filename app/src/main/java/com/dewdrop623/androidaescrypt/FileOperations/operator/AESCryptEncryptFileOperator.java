package com.dewdrop623.androidaescrypt.FileOperations.operator;

import android.os.Bundle;

import com.dewdrop623.androidaescrypt.FileOperations.FileModifierService;
import com.dewdrop623.androidaescrypt.FileOperations.FileUtils;
import com.dewdrop623.androidaescrypt.R;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import es.vocali.util.AESCrypt;

/**
 * encrypts files using aescrypt
 */

public class AESCryptEncryptFileOperator extends FileOperator {

    public static final String AESCRYPT_FILE_OPERATOR_KEY_ARGUMENT = "com.dewdrop623.androidaescrypt.FileOperations.operator.AESCryptEncryptFileOperator.AESCRYPT_FILE_OPERATOR_KEY_ARGUMENT";
    public static final String AESCRYPT_FILE_OPERATOR_FILENAME_ARGUMENT = "com.dewdrop623.androidaescrypt.FileOperations.operator.AESCryptEncryptFileOperator.AESCRYPT_FILE_OPERATOR_FILENAME_ARGUMENT";

    private File outputFile;
    private long totalBytesSize = 0;
    private long lastUpdate = 0;
    private long fivepercentoffilesize = 0;
    private boolean conflict = false;
    private boolean validFilename = true;

    public AESCryptEncryptFileOperator(File file, Bundle args, FileModifierService fileModifierService) {
        super(file, args, fileModifierService);
    }

    public void updateProgress(int bytesRead) {
        if ((bytesRead - lastUpdate) > fivepercentoffilesize) {
            FileUtils.notificationUpdate(bytesRead, totalBytesSize, fileModifierService);
            lastUpdate = bytesRead;
        }
    }

    @Override
    public String getOperationName() {
        return fileModifierService.getString(R.string.encrypting)+" "+file.getName();
    }

    @Override
    protected void initMemVarFromArgs() {
        outputFile = new File(file.getParent()+"/"+args.getString(AESCRYPT_FILE_OPERATOR_FILENAME_ARGUMENT));
    }

    @Override
    protected void handleYesNoResponse(boolean yes) {
        if (yes) {
            finishTakingInput();
        } else {
            cancelOperation();
        }
    }

    @Override
    protected void handleYesNoRememberAnswerResponse(boolean yes, boolean remember) {

    }

    @Override
    protected void handleTextOrCancelResponse(String response) {
        if (response==null) {
            cancelOperation();
            return;
        }
        validFilename = FileUtils.validFilename(response, fileModifierService);
        if (validFilename) {
            outputFile = new File(file.getParent() + "/" + response);
            conflict = outputFile.exists();
        }
        getInfoFromUser();
    }

    @Override
    protected void doOperation() {
        try {
            totalBytesSize = file.length();
            fivepercentoffilesize = (long) (totalBytesSize * 0.05);
            AESCrypt aesCrypt = new AESCrypt(args.getString(AESCRYPT_FILE_OPERATOR_KEY_ARGUMENT));
            aesCrypt.fileOperator = this;
            aesCrypt.encrypt(2, file.getAbsolutePath(), outputFile.getAbsolutePath());
            fileModifierService.updateNotification(100);
        } catch (GeneralSecurityException gse) {
            fileModifierService.showToast(fileModifierService.getString(R.string.general_security_exception));
        } catch (UnsupportedEncodingException uee) {
            fileModifierService.showToast(fileModifierService.getString(R.string.unsupported_encoding_exception));
        } catch (IOException ioe) {
            fileModifierService.showToast(fileModifierService.getString(R.string.ioexception));
        }
    }

    @Override
    protected boolean prepareAndValidate() {
        if (!FileUtils.encryptionDecryptionValidationAndErrorToasts(file, outputFile, fileModifierService)) {
            return false;
        }
        validFilename = FileUtils.validFilename(args.getString(AESCRYPT_FILE_OPERATOR_FILENAME_ARGUMENT), fileModifierService);
        conflict = outputFile.exists();
        return true;
    }

    @Override
    protected void getInfoFromUser() {
        if(!validFilename) {
            askForTextOrCancel(fileModifierService.getString(R.string.invalid_filename)+". "+fileModifierService.getString(R.string.try_again)+"?");
        }
        if (conflict) {
            askYesNo("Overwrite "+outputFile.getName()+"?");
        } else {
            finishTakingInput();
        }
    }
}
