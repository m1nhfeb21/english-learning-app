package com.example.englishlearningsoftware;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/** Authentication screen using Java, XML, findViewById, and OnClickListener. */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String STATE_SIGN_UP = "sign_up";
    private ConstraintLayout rootLayout;
    private LinearLayout layoutSignIn;
    private LinearLayout layoutSignUp;
    private TextView btnToggleSignIn;
    private TextView btnToggleSignUp;
    private Button btnSubmit;
    private Button btnPreview;
    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private EditText etSignUpFirstName;
    private EditText etSignUpLastName;
    private EditText etSignUpEmail;
    private EditText etSignUpPhone;
    private EditText etSignUpPassword;
    private EditText etSignUpConfirmPassword;
    private AccountApi accountApi;
    private Call pendingCall;
    private boolean signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        accountApi = new AccountApi(getString(R.string.api_base_url));
        btnToggleSignIn.setOnClickListener(this);
        btnToggleSignUp.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnPreview.setOnClickListener(this);
        showForm(savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_SIGN_UP), false);

        // Apply insets for the status bar, navigation bar, and keyboard.
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout,
                new androidx.core.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View view,
                            WindowInsetsCompat windowInsets) {
                        Insets bars = windowInsets.getInsets(
                                WindowInsetsCompat.Type.systemBars());
                        Insets keyboard = windowInsets.getInsets(
                                WindowInsetsCompat.Type.ime());
                        view.setPadding(bars.left, bars.top, bars.right,
                                Math.max(bars.bottom, keyboard.bottom));
                        return windowInsets;
                    }
                });
        ViewCompat.requestApplyInsets(rootLayout);
    }

    private void bindViews() {
        rootLayout = findViewById(R.id.rootLayout);
        layoutSignIn = findViewById(R.id.layoutSignIn);
        layoutSignUp = findViewById(R.id.layoutSignUp);
        btnToggleSignIn = findViewById(R.id.btnToggleSignIn);
        btnToggleSignUp = findViewById(R.id.btnToggleSignUp);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnPreview = findViewById(R.id.btnPreview);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        etSignUpFirstName = findViewById(R.id.etSignUpFirstName);
        etSignUpLastName = findViewById(R.id.etSignUpLastName);
        etSignUpEmail = findViewById(R.id.etSignUpEmail);
        etSignUpPhone = findViewById(R.id.etSignUpPhone);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        etSignUpConfirmPassword = findViewById(R.id.etSignUpConfirmPassword);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnToggleSignIn) {
            showForm(false, true);
        } else if (id == R.id.btnToggleSignUp) {
            showForm(true, true);
        } else if (id == R.id.btnPreview) {
            openHome("", "", 0, true);
        } else if (id == R.id.btnSubmit) {
            if (signUp) {
                register();
            } else {
                login();
            }
        }
    }

    private void showForm(boolean showSignUp, boolean animate) {
        signUp = showSignUp;
        if (animate) {
            TransitionManager.beginDelayedTransition(rootLayout, new AutoTransition());
        }
        layoutSignIn.setVisibility(signUp ? View.GONE : View.VISIBLE);
        layoutSignUp.setVisibility(signUp ? View.VISIBLE : View.GONE);
        TextView active = signUp ? btnToggleSignUp : btnToggleSignIn;
        TextView inactive = signUp ? btnToggleSignIn : btnToggleSignUp;
        active.setBackgroundResource(R.drawable.bg_btn_primary);
        active.setBackgroundTintList(getColorStateList(android.R.color.black));
        active.setTextColor(Color.WHITE);
        inactive.setBackgroundResource(android.R.color.transparent);
        inactive.setBackgroundTintList(null);
        inactive.setTextColor(getColor(R.color.text_gray));
        setLoading(false);
    }

    private void login() {
        String loginInput = etLoginEmail.getText().toString().trim();
        // Passwords can contain spaces, so do not trim them.
        String password = etLoginPassword.getText().toString();
        if (loginInput.isEmpty() || password.isEmpty()) {
            showMessage(R.string.enter_all_fields);
            return;
        }
        setLoading(true);
        pendingCall = accountApi.login(loginInput, password, new AccountApi.ResultCallback() {
            @Override
            public void onResult(final int code, final String body) {
                updateUi(new Runnable() {
                    @Override
                    public void run() {
                        pendingCall = null;
                        setLoading(false);
                        if (code >= 200 && code < 300) {
                            try {
                                JSONObject account = new JSONObject(body);
                                openHome(account.optString("firstName", ""),
                                        account.optString("lastName", ""),
                                        account.optInt("accountId", 0), false);
                            } catch (JSONException error) {
                                showMessage(R.string.invalid_backend_data);
                            }
                        } else {
                            showLoginError(code);
                        }
                    }
                });
            }

            @Override
            public void onFailure() {
                handleConnectionFailure();
            }
        });
    }

    private void register() {
        String firstName = etSignUpFirstName.getText().toString().trim();
        String lastName = etSignUpLastName.getText().toString().trim();
        String email = etSignUpEmail.getText().toString().trim();
        String phone = etSignUpPhone.getText().toString().trim();
        String password = etSignUpPassword.getText().toString();
        String confirmPassword = etSignUpConfirmPassword.getText().toString();
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage(R.string.enter_all_fields);
            return;
        }
        if (!password.equals(confirmPassword)) {
            showMessage(R.string.password_mismatch);
            return;
        }
        try {
            JSONObject account = new JSONObject();
            account.put("firstName", firstName);
            account.put("lastName", lastName);
            account.put("email", email);
            account.put("phoneNumber", phone);
            account.put("password", password);
            setLoading(true);
            pendingCall = accountApi.register(account.toString(), new AccountApi.ResultCallback() {
                @Override
                public void onResult(final int code, String body) {
                    updateUi(new Runnable() {
                        @Override
                        public void run() {
                            pendingCall = null;
                            setLoading(false);
                            if (code >= 200 && code < 300) {
                                showMessage(R.string.register_success);
                                etLoginEmail.setText(etSignUpEmail.getText());
                                etSignUpPassword.setText("");
                                etSignUpConfirmPassword.setText("");
                                showForm(false, true);
                            } else {
                                Toast.makeText(MainActivity.this,
                                        getString(R.string.register_http_error, code),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onFailure() {
                    handleConnectionFailure();
                }
            });
        } catch (JSONException error) {
            setLoading(false);
            showMessage(R.string.invalid_registration_data);
        }
    }

    private void handleConnectionFailure() {
        updateUi(new Runnable() {
            @Override
            public void run() {
                pendingCall = null;
                setLoading(false);
                showMessage(R.string.connection_error);
            }
        });
    }

    private void updateUi(final Runnable action) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && !isDestroyed()) {
                    action.run();
                }
            }
        });
    }

    private void setLoading(boolean loading) {
        btnSubmit.setEnabled(!loading);
        btnPreview.setEnabled(!loading);
        btnToggleSignIn.setEnabled(!loading);
        btnToggleSignUp.setEnabled(!loading);
        if (loading) {
            btnSubmit.setText(signUp ? R.string.register_loading : R.string.login_loading);
        } else {
            btnSubmit.setText(signUp ? R.string.create_account : R.string.login);
        }
    }

    private void showLoginError(int code) {
        int message;
        switch (code) {
            case 400: message = R.string.login_invalid; break;
            case 401: message = R.string.login_unauthorized; break;
            case 404: message = R.string.login_api_not_found; break;
            case 500: message = R.string.backend_error; break;
            default:
                Toast.makeText(this, getString(R.string.login_http_error, code),
                        Toast.LENGTH_SHORT).show();
                return;
        }
        showMessage(message);
    }

    private void showMessage(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void openHome(String firstName, String lastName, int accountId, boolean preview) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra(HomeActivity.EXTRA_FIRST_NAME, firstName);
        intent.putExtra(HomeActivity.EXTRA_LAST_NAME, lastName);
        intent.putExtra(HomeActivity.EXTRA_ACCOUNT_ID, accountId);
        intent.putExtra(HomeActivity.EXTRA_PREVIEW, preview);
        startActivity(intent);
        if (!preview) {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_SIGN_UP, signUp);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (pendingCall != null) {
            pendingCall.cancel();
        }
        super.onDestroy();
    }
}
