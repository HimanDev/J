package com.example.himan.videotest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.example.himan.videotest.dialog.ProgressDialogFragment;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.example.himan.videotest.form.PaymentForm;
import com.example.himan.videotest.dialog.ErrorDialogFragment;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends FragmentActivity {
    /*
    * Change this to your publishable key.
    *
    * You can get your key here: https://manage.stripe.com/account/apikeys
    */
    public static final String PUBLISHABLE_KEY = "pk_test_NSvkfCQm7pkHAAuEnRF7Eqiz";
    private ProgressDialogFragment progressFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_main);
        progressFragment = ProgressDialogFragment.newInstance(R.string.progressMessage);
    }
    public void saveCreditCard(PaymentForm form) {
        Card card = new Card(
                form.getCardNumber(),
                form.getExpMonth(),
                form.getExpYear(),
                form.getCvc());
        //card.setCurrency(form.getCurrency());
        final Integer amount = new Integer(form.getAmount());
        boolean validation = card.validateCard();
        if (validation) {
            startProgress();
            new Stripe().createToken(
                    card,
                    PUBLISHABLE_KEY,
                    new TokenCallback() {
                        public void onSuccess(final Token token) {

                            runOnUiThread( new Runnable(){
                                public void run() {
                                    try {
                                        com.stripe.Stripe.apiKey = "sk_test_541IEZ7twGIM7tobCbeUvfex";
                                        Map<String, Object> chargeParams = new HashMap<String, Object>();
                                        chargeParams.put("amount", amount); // Amount in cents
                                        chargeParams.put("currency", "usd");
                                        chargeParams.put("source", token);
                                        chargeParams.put("description", "Donation Amount charge");
                                        String customerEmail = FolderStructure.getInstance().getGoogleAccountCredential().getSelectedAccountName();
                                        chargeParams.put("receiptEmail", customerEmail);
                                        final Charge charge = Charge.create(chargeParams);
                                        charge.getMetadata();
                                        charge.getStatus();
                                        charge.refund();

                                        new AlertDialog.Builder(getApplicationContext())
                                                .setTitle("Payment Successful")
                                                .setMessage("Thanks for your donation of " + amount)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        PaymentActivity.this.finish();
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();


                                    } catch (AuthenticationException e) {
                                        handleError("Invalid Authentication error occured");
                                    } catch (InvalidRequestException e) {
                                        handleError("Invalid Request Technical Error occured");
                                    } catch (APIConnectionException e) {
                                        handleError("Invalid API Connection Technical Error occured");
                                    } catch (CardException e) {
                                        handleError("This Card has been declined");
                                    } catch (APIException e) {
                                        handleError("Invalid API Details Error");
                                    } catch (Exception e) {
                                        handleError("Invalid Details Error");
                                    }
                                }
                            });
                            finishProgress();
                        }
                        public void onError(Exception error) {
                            handleError(error.getLocalizedMessage());
                            finishProgress();
                        }
                    });
        } else if (!card.validateNumber()) {
            handleError("The card number that you entered is invalid");
        } else if (!card.validateExpiryDate()) {
            handleError("The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
            handleError("The CVC code that you entered is invalid");
        } else {
            handleError("The card details that you entered are invalid");
        }
    }
    private void startProgress() {
        progressFragment.show(getSupportFragmentManager(), "progress");
    }
    private void finishProgress() {
        progressFragment.dismiss();
    }
    private void handleError(String error) {
        DialogFragment fragment = ErrorDialogFragment.newInstance(R.string.validationErrors, error);
        fragment.show(getSupportFragmentManager(), "error");
    }

}