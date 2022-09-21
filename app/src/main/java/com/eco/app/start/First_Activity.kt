package com.eco.app.start
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.eco.app.HomeWindow
import com.eco.app.R
import com.eco.app.databinding.ActivityFirstBinding
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class First_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstBinding
    private lateinit var btnLog : Button
    private lateinit var btnSign : Button
    private lateinit var btnDeb : Button
    private lateinit var txtWho : TextView
    private lateinit var consentInformation: ConsentInformation
    private var consentForm: ConsentForm? = null
    public val CONSENT_FORM_PREF_PATH="consent_form_prefs"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        binding= ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoToHomepage.setOnClickListener {
            val intent=Intent(this, HomeWindow::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        // Set tag for underage of consent. Here false means users are not underage.
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        //Controllo se il form di preferenza non è già stato mostrato
        //if(!getSharedPreferences(CONSENT_FORM_PREF_PATH,Context.MODE_PRIVATE).getBoolean("consentInformationShown",false)) {

            consentInformation = UserMessagingPlatform.getConsentInformation(this)
            consentInformation.requestConsentInfoUpdate(
                this,
                params,
                {
                    // The consent information state was updated.
                    // You are now ready to check if a form is available.
                    if(consentInformation.isConsentFormAvailable){
                        UserMessagingPlatform.loadConsentForm(this,
                            { consentForm->
                                this.consentForm=consentForm
                                if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                                    consentForm.show(this) { formError ->
                                        // Handle dismissal by reloading form.
                                        //loadForm()
                                    }
                                }
                            },
                            { formError->

                            }
                        )
                    }
                },
                { formError ->
                    // Handle the error.
                }
            )

            //getSharedPreferences(CONSENT_FORM_PREF_PATH,Context.MODE_PRIVATE).edit().putBoolean("consentInformationShown",true)
        //}
    }



}