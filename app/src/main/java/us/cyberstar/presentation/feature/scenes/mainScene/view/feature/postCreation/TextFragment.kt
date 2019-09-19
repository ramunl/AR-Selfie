package us.cyberstar.presentation.feature.scenes.mainScene.view.feature.postCreation

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import us.cyberstar.arcyber.R


class TextFragment : Fragment() {
    companion object {
        fun newInstance(): TextFragment {
            return TextFragment()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View? = inflater.inflate(R.layout.fragment_home, container, false);
        /*val message = arguments!!.getString(EXTRA_MESSAGE)
        var textView: TextView = view!!.findViewById(R.id.text)
        textView!!.text = message*/

        return view
    }


}