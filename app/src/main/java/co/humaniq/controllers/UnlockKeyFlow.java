package co.humaniq.controllers;

import android.os.AsyncTask;
import co.humaniq.DebugTool;
import co.humaniq.Preferences;
import co.humaniq.models.ResultData;
import co.humaniq.models.Wallet;
import co.humaniq.models.WalletInfo;
import co.humaniq.models.WalletMeta;
import co.humaniq.services.AccountService;
import co.humaniq.views.GraphicKeyActivity;

import static android.app.Activity.RESULT_OK;


public class UnlockKeyFlow {
    final static int REQUEST_GET_META = 2001;
    final static int REQUEST_GET_SALT = 2002;
    final static int REQUEST_GENERATE_SALT = 2003;
    final static int REQUEST_FINISH_REGISTRATION = 2004;

    private GraphicKeyActivity activity;
    private WalletInfo walletInfo;
    private Wallet generatedWallet;
    private String pinCode;
    private Preferences preferences;

    static class WalletAsyncTaskParam {
        int action;
        WalletInfo walletInfo;
        Wallet wallet;

        WalletAsyncTaskParam(int action, WalletInfo walletInfo, Wallet wallet) {
            this.action = action;
            this.walletInfo = walletInfo;
            this.wallet = wallet;
        }

        WalletAsyncTaskParam(int action, WalletInfo walletInfo) {
            this.action = action;
            this.walletInfo = walletInfo;
        }

        WalletAsyncTaskParam(int action) {
            this.action = action;
        }
    }

    private class WalletAsyncTask extends AsyncTask<WalletAsyncTaskParam, Void, Wallet> {
        final static int SET_AS_WORK_WALLET = 1;
        final static int SAVE_WALLET = 2;
        final static int SIGN_WALLET = 3;

        private WalletAsyncTaskParam param;

        private Wallet saveWalletTask() {
            final String finalPassword = pinCode + param.walletInfo.getSalt();

            try {
                return Wallet.generateWallet(activity, finalPassword);
            } catch (Wallet.WalletNotGeneratedException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void saveWalletPostExecute(Wallet wallet) {
            wallet.setWalletInfo(walletInfo);
            generatedWallet = wallet;
            AccountService service = new AccountService(activity);
            service.finishRegistration(wallet.getWalletInfo().getId(),
                    wallet.getAddress(),
                    wallet.getWalletPath(),
                    REQUEST_FINISH_REGISTRATION);
        }

        private Wallet getSignedWalletTask() {
            final String accountFile = preferences.getAccountKeyFile();
            try {
                if (param.walletInfo == null) {
                    return Wallet.getSignedWallet(preferences.getAccountKeyFile(), pinCode,
                            preferences.getAccountSalt());
                } else {
                    return Wallet.getSignedWallet(accountFile, pinCode, param.walletInfo);
                }
            } catch (Wallet.CantSignedException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void getSignedWalletPostExecute(Wallet signedWallet) {
            new WalletAsyncTask().execute(new WalletAsyncTaskParam(WalletAsyncTask.SET_AS_WORK_WALLET, param.walletInfo, signedWallet));
        }

        private Wallet setAsWorkWalletTask() {
            if (param.wallet.setAsWorkWallet()) {
                return param.wallet;
            } else {
                return null;
            }
        }

        private void setAsWorkWalletPostExecute(Wallet signedWallet) {
            if (param.walletInfo != null)
                preferences.setAccountSalt(param.walletInfo.getSalt());

            preferences.setLoginCount(preferences.getLoginCount() + 1);

            activity.setResult(RESULT_OK);
            activity.finish();
        }

        @Override
        protected Wallet doInBackground(WalletAsyncTaskParam... params) {
            param = params[0];

            switch (param.action) {
                case SAVE_WALLET:
                    return saveWalletTask();

                case SIGN_WALLET:
                    return getSignedWalletTask();

                case SET_AS_WORK_WALLET:
                    return setAsWorkWalletTask();

                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override
        protected void onPostExecute(Wallet result) {
            if (result == null) {
                DebugTool.showDialog(activity, "Error", "Wallet not generated");
                return;
            }

            switch (param.action) {
                case SAVE_WALLET:
                    saveWalletPostExecute(result);
                    break;

                case SIGN_WALLET:
                    getSignedWalletPostExecute(result);
                    break;

                case SET_AS_WORK_WALLET:
                    setAsWorkWalletPostExecute(result);
                    break;

                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    public void onApiSuccess(ResultData result, int requestCode) {
        if (requestCode != REQUEST_GET_META)
            walletInfo = (WalletInfo) result.data();

        switch (requestCode) {
            case REQUEST_GET_META:
                WalletMeta walletMeta = (WalletMeta) result.data();
                walletMeta.save(activity);
                activity.nextStepOrLogin();
                break;

            case REQUEST_GET_SALT:
                new WalletAsyncTask().execute(new WalletAsyncTaskParam(WalletAsyncTask.SIGN_WALLET, walletInfo));
                break;

            case REQUEST_GENERATE_SALT:
                new WalletAsyncTask().execute(new WalletAsyncTaskParam(WalletAsyncTask.SAVE_WALLET, walletInfo));
                break;

            case REQUEST_FINISH_REGISTRATION:
                try {
                    generatedWallet.sign(pinCode, walletInfo);
                    generatedWallet.save(activity);
                    generatedWallet.setAsWorkWallet();

                    preferences.setAccountSalt(walletInfo.getSalt());
                    preferences.setLoginCount(preferences.getLoginCount() + 1);

                    activity.setResult(RESULT_OK);
                    activity.finish();
                } catch (Wallet.CantSignedException e) {
                    e.printStackTrace();
                    DebugTool.showDialog(activity, "Error", "wallet can't signed");
                }

                break;

            default:
                break;
        }
    }
}
