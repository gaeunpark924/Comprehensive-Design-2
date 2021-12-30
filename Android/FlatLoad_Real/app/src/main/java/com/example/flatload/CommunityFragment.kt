package com.example.flatload

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import kotlinx.android.synthetic.main.fragment_community.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CommunityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommunityFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var callback: OnBackPressedCallback
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
//        // Inflate the layout for this fragment
//        webview.apply {
//            webViewClient = WebViewClient() //페이지 컨트롤을 위한 기본적인 함수, 다양한 요청, 알림 수신 기능
//            webChromeClient = WebChromeClient() //크롬환경에 맞는 세팅을 해줌
//            settings.javaScriptEnabled = true //자바스크립트 허용
//            settings.javaScriptCanOpenWindowsAutomatically = false //팝업창을 띄우는 경우, 해당 속성을 추가해야 window.open()작동, 자바스크립트 새창 띄우기
//        }
//        webview.loadUrl("http://10.0.2.2:3000")
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
//        webview.apply {
//            webViewClient = WebViewClient() //페이지 컨트롤을 위한 기본적인 함수, 다양한 요청, 알림 수신 기능
//            webChromeClient = WebChromeClient() //크롬환경에 맞는 세팅을 해줌
//            settings.javaScriptEnabled = true //자바스크립트 허용
//            settings.javaScriptCanOpenWindowsAutomatically = false //팝업창을 띄우는 경우, 해당 속성을 추가해야 window.open()작동, 자바스크립트 새창 띄우기
//        }
//        webview.loadUrl("http://36b3-119-207-174-156.ngrok.io")
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //sample_text.text = "occur back pressed event!!"
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CommunityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CommunityFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}