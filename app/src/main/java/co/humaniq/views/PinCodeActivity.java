package co.humaniq.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import co.humaniq.App;
import co.humaniq.Preferences;
import co.humaniq.R;
import co.humaniq.Router;
import co.humaniq.models.AuthToken;
import co.humaniq.models.Errors;
import co.humaniq.models.ResultData;
import co.humaniq.models.User;
import co.humaniq.services.UserService;


public class PinCodeActivity extends ToolbarActivity {
    private final static int TAKE_PHOTO_REQUEST = 1001;
    private final static int GET_USER_INFO_REQUEST = 2001;

    private PinButton[] pinButtons = new PinButton[11];
    private PinPlace[] pinPlaces = new PinPlace[4];
    private int pinCursor = 0;
    private UserService service;
    private Preferences preferences;

    private class PinButton implements View.OnClickListener {
        View includeView;
        ImageView icon;
        ImageView animal;
        int animalRes;
        String code;
        boolean selected = false;

        PinButton(String code, @IdRes int includeViewId, @DrawableRes int imageResource) {
            includeView = findViewById(includeViewId);
            icon = (ImageView) includeView.findViewById(R.id.iconImageView);
            animal = (ImageView) includeView.findViewById(R.id.animalImageView);
            animal.setImageResource(imageResource);
            animal.setOnClickListener(this);
            animalRes = imageResource;
            icon.setColorFilter(ContextCompat.getColor(PinCodeActivity.this, R.color.success));
            this.code = code;
        }

        @Override
        public void onClick(View v) {
            if (pinCursor >= 4 || selected)
                return;

            select();
            pinPlaces[pinCursor].select(this);
            pinCursor++;
        }

        void select() {
            icon.setVisibility(View.VISIBLE);
            animal.setAlpha(0.8f);
            selected = true;
        }

        void deSelect() {
            icon.setVisibility(View.INVISIBLE);
            animal.setAlpha(1.f);
            selected = false;
        }
    }

    private class PinPlace implements View.OnClickListener {
        View includeView;
        ImageView icon;
        ImageView animal;
        PinButton pinButton = null;
        int index = 0;

        PinPlace(int index, @IdRes int includeViewId) {
            includeView = findViewById(includeViewId);
            icon = (ImageView) includeView.findViewById(R.id.iconImageView);
            animal = (ImageView) includeView.findViewById(R.id.animalImageView);
            icon.setImageResource(R.drawable.ic_delete_pin_image);
            icon.setOnClickListener(this);
            this.index = index;
        }

        void select(PinButton button) {
            icon.setVisibility(View.VISIBLE);
            pinButton = button;
            animal.setImageResource(button.animalRes);
        }

        void deSelect() {
            icon.setVisibility(View.INVISIBLE);
            animal.setImageResource(0);

            if (pinButton == null)
                return;

            pinButton.deSelect();
            pinButton = null;
        }

        @Override
        public void onClick(View v) {
            for (int i = index; i < pinPlaces.length; ++i) {
                pinPlaces[i].deSelect();
            }

            if (index < pinCursor)
                pinCursor = index;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);
        initToolbar();
        initView();

        attachOnClickView(R.id.nextStepButton);
        preferences = App.getPreferences(this);
    }

    private String pinCodeToString() {
        String str = "";

        for (PinPlace place : pinPlaces) {
            if (place.pinButton == null)
                return "";

            str += place.pinButton.code;
        }

        return str;
    }

    private void initView() {
        pinButtons[0] = new PinButton("gi1", R.id.giraffe_include, R.drawable.ic_giraffe);
        pinButtons[1] = new PinButton("el2", R.id.elephant_include, R.drawable.ic_elephant);
        pinButtons[2] = new PinButton("ko3", R.id.koala_include, R.drawable.ic_koala);
        pinButtons[3] = new PinButton("li4", R.id.lion_include, R.drawable.ic_lion);
        pinButtons[4] = new PinButton("ka5", R.id.kangaroo_include, R.drawable.ic_kangaroo);
        pinButtons[5] = new PinButton("ti6", R.id.tiget_layout, R.drawable.ic_tiger);
        pinButtons[6] = new PinButton("ra7", R.id.raccoon_include, R.drawable.ic_raccoon);
        pinButtons[7] = new PinButton("he8", R.id.hedgehog_include, R.drawable.ic_hedgehog);
        pinButtons[8] = new PinButton("le9", R.id.lemur_include, R.drawable.ic_lemur);
        pinButtons[9] = new PinButton("ch10", R.id.chameleon_include, R.drawable.ic_chameleon);
        pinButtons[10] = new PinButton("ca11", R.id.cat_include, R.drawable.ic_cat);

        pinPlaces[0] = new PinPlace(0, R.id.pin_place_1);
        pinPlaces[1] = new PinPlace(1, R.id.pin_place_2);
        pinPlaces[2] = new PinPlace(2, R.id.pin_place_3);
        pinPlaces[3] = new PinPlace(3, R.id.pin_place_4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void nextStepOrLogin() {
        Log.d("PINCODE", preferences.getPinCode());
        Log.d("PINCODE", preferences.getLoginCount().toString());
        Log.d("PINCODE", preferences.getAccessToken());
        Log.d("PINCODE", preferences.getUserId().toString());

        final String pinCode = pinCodeToString();

        if (pinCode.trim().equals("") || pinCursor != 4)
            return;

        // Каждый третий заход, запрашиваем лицо
        // В данном случае - если 0, то просим лицо, если 3, сбрасываем на 0
        if (preferences.getLoginCount() >= 3) {
            preferences.setLoginCount(0);
        }

        if (!preferences.getPinCode().equals(pinCode) &&
                !preferences.getPinCode().trim().equals(""))
        {
            onErrorPinCode();
            return;
        }

        if (preferences.getLoginCount() == 0) {
            goTakePhotoActivity(pinCode);
        } else {
            AuthToken token = new AuthToken(preferences.getAccessToken());
            AuthToken.updateInstance(this, token, false);

            service = new UserService(this);
            service.getById(GET_USER_INFO_REQUEST, preferences.getUserId());
        }
    }

    private void goTakePhotoActivity(final String pinCode) {
        Bundle bundle = new Bundle();
        bundle.putString("pin_code", pinCode);
        Router.setBundle(bundle);
        Router.goActivity(this, Router.REGISTER, TAKE_PHOTO_REQUEST);
    }

    private void alert(final String title, final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.setTitle(title).setMessage(message).create();
        alertDialog.show();
    }

    // Пин код введен не верно
    public void onErrorPinCode() {
        alert("Error", "Invalid pin code");
    }

    // Если пользователя не удалось получить, вероятно AccessToken уже не действителен
    // Поэтому нужно получить новый на экране LoginRegisterActivity
    public void onErrorFetchUser() {
        final String pinCode = pinCodeToString();

        if (pinCode.trim().equals("") || pinCursor != 4)
            return;

        goTakePhotoActivity(pinCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextStepButton:
                nextStepOrLogin();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != AuthToken.RESULT_GOT_TOKEN) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        setResult(AuthToken.RESULT_GOT_TOKEN);
        finish();
    }

    @Override
    public void onApiSuccess(ResultData result, int requestCode) {
        User user = (User) result.data();
        AuthToken token = new AuthToken(preferences.getAccessToken(), user);
        AuthToken.updateInstance(this, token);

        setResult(AuthToken.RESULT_GOT_TOKEN);
        finish();
    }

    @Override
    public void onApiValidationError(Errors errors, int requestCode) {
        super.onApiValidationError(errors, requestCode);
        onErrorFetchUser();
    }

    @Override
    public void onApiPermissionError(Errors errors, int requestCode) {
        super.onApiPermissionError(errors, requestCode);
        onErrorFetchUser();
    }

    @Override
    public void onApiAuthorizationError(Errors errors, int requestCode) {
        super.onApiAuthorizationError(errors, requestCode);
        onErrorFetchUser();
    }

    @Override
    public void onApiCriticalError(Errors errors, int requestCode) {
        super.onApiCriticalError(errors, requestCode);
        onErrorFetchUser();
    }

    @Override
    public void onApiConnectionError(int requestCode) {
        super.onApiConnectionError(requestCode);
        onErrorFetchUser();
    }
}
