package com.byteshaft.hairrestorationcenter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byteshaft.hairrestorationcenter.R;

/**
 * Created by husnain on 8/6/16.
 */
public class AboutUsFragment extends Fragment {

    private View mBaseView;
    private TextView mAboutUsTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.delegate_aboutus_fragment, container, false);
        mAboutUsTextView = (TextView) mBaseView.findViewById(R.id.textview_about_us);
        mAboutUsTextView.setText("If you’re considering hair transplant surgery, this is no doubt one of the key questions on your mind. How much does a hair transplant cost? The answer is most likely a lot less than you think." +
                "\n\n" +
                "    Hair Restoration Centers (HRC) began in 1997 in Tulsa, Oklahoma, with a single, focused mission: to provide patients experiencing unwanted hair loss with the most affordable hair restoration procedures, performed by exceptionally qualified surgeons using the most advanced technologies, resulting in the highest quality outcomes." +
                "\n\n" +
                "    Today, we have extended that mission across the United States, working with surgeons whose individual practices embrace the same values that HRC has embodied since we began." +
                "\n\n" +
                "    HRC affiliated surgeons understand the impact that unwanted hair loss has made on the lives of countless men and women. They understand the difference that a full head of natural hair can make for someone’s self-confidence." +
                "\n\n" +
                "    Our surgeons offer the most advanced hair transplant surgery at the lowest fees possible to ensure that virtually anyone can afford the benefits of full, natural-looking hair: hair that grows and can be styled in any way their patients choose. We don’t want the cost of a hair transplant to be a barrier to the new you. Everyone deserves to be confident in their appearance." +
                "\n\n" +
                "    HRC affiliated surgeons are invited into our network not only because of their skill, but also because of their compassion for and understanding of the people who come to us for help." +
                "\n\n" +
                "    Our surgeons are committed to staying on the leading edge of advances in hair restoration technology, and they surround themselves with staff who are equally dedicated to the best patient outcomes." +
                "\n\n" +
                "    When you select your hair restoration specialist from among our highly trained and experienced surgeons, you are assured of a great price, and a great value. To find out how much your hair transplant will cost with Hair Restoration Centers, please complete our quotation request and we’ll contact you in confidence." +
                "\n\n" +
                "We look forward to helping you reach your hair replacement goals.");
        return mBaseView;
    }

}
