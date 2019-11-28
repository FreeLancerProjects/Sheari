package com.creative.share.apps.sheari.interfaces;


import com.creative.share.apps.sheari.models.ContactUsModel;

public interface Listeners {


    interface LoginListener {
        void checkDataLogin();
    }
    interface SkipListener
    {
        void skip();
    }
    interface BackListener
    {
        void back();
    }
    interface ShowCountryDialogListener
    {
        void showDialog();
    }

    interface SignUpListener {
        void checkDataSignUp();
    }

    interface ContactListener
    {
        void sendContact(ContactUsModel contactUsModel);
    }

    interface ProviderSteps
    {
        void step1();
        void step2();
        void step3();
        void step4();

    }
}
