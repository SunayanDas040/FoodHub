package com.internshala.foodhub.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.internshala.foodhub.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var imgProfileIcon : ImageView
    lateinit var txtName: TextView
    lateinit var txtMobileNumber : TextView
    lateinit var txtEmail : TextView
    lateinit var txtDeliveryAddress : TextView

    lateinit var profilePreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        imgProfileIcon = view.findViewById(R.id.imgProfileIcon)
        txtName = view.findViewById(R.id.txtName)
        txtMobileNumber = view.findViewById(R.id.txtMobileNumber)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtDeliveryAddress = view.findViewById(R.id.txtDeliveryAddress)

        profilePreferences = this.activity!!.getSharedPreferences(getString(R.string.preferences_file_profile), Context.MODE_PRIVATE)

        txtName.text = profilePreferences.getString("name", "Name")

        txtMobileNumber.text = profilePreferences.getString("mobile_number", "Mobile Number")

        txtEmail.text = profilePreferences.getString("email", "Email")

        txtDeliveryAddress.text = profilePreferences.getString("address", "Address")

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}