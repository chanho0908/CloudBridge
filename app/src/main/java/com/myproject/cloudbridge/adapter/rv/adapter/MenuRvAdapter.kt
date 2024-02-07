package com.myproject.cloudbridge.adapter.rv.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.StoreMenuItemBinding
import com.myproject.cloudbridge.adapter.rv.model.StoreMenuModel
import com.myproject.cloudbridge.util.Constants.Companion.setHelperBoxBlack
import com.myproject.cloudbridge.util.Constants.Companion.setHelperTextRed
import com.myproject.cloudbridge.util.Constants.Companion.setHelperTextRedList

class MenuRvAdapter(private val context: Context,
                    private val menuList: ArrayList<StoreMenuModel>,
                    private val imgClickListener: (Int) -> Unit,
                    private val delButtonClickListener: (Int) -> Unit):
    RecyclerView.Adapter<MenuRvAdapter.MenuRvViewHolder>() {

    // 포커스 상태를 추적하기 위한 변수
    private var isProductNameFocused = false

    inner class MenuRvViewHolder(val binding: StoreMenuItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuRvViewHolder {
        return MenuRvViewHolder(StoreMenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int{
        return menuList.size
    }

    override fun onBindViewHolder(holder: MenuRvViewHolder, position: Int) {
        val binding = holder.binding

        binding.apply {
            menuImgView.apply {
                setOnClickListener{
                    imgClickListener.invoke(position)
                }
                if (menuList[position].imgBitmap != null){
                    setImageBitmap(menuList[position].imgBitmap)
                }
            }

            delButton.setOnClickListener{
                delButtonClickListener.invoke(position)
            }

            productNameEdit.addTextChangedListener {
                val input = it.toString()

                if (input.isEmpty()){
                    Log.d("dasds", "이름이 없음")
                    productNameLayout.helperText = "상품명을 입력해 주세요"
                    productNameLayout.boxStrokeColor = setHelperTextRed(context)

                }else {
                    Log.d("dasds", "이름이 있음")
                    productNameLayout.helperText = ""
                    productNameLayout.boxStrokeColor = setHelperBoxBlack(context)
                    menuList[position].productName = input
                }

            }

            productNameEdit.setOnFocusChangeListener { view, hasFocus ->
                val input = productNameEdit.text.toString()

                if (!hasFocus && input.isEmpty()) {
                    Log.d("dasds", "포커스를 잃음")
                    productNameLayout.helperText = "상품명을 입력해 주세요"
                    productNameLayout.boxStrokeColor = setHelperTextRed(context)
                }
            }

            productQuantityEdit.addTextChangedListener {
                val input = it.toString()

                if (input.isEmpty()){
                    productQuantityLayout.helperText = "수량을 입력해 주세요"
                    productQuantityLayout.boxStrokeColor = setHelperTextRed(context)
                }else {
                    productQuantityLayout.helperText = ""
                    productQuantityLayout.boxStrokeColor = setHelperBoxBlack(context)
                    menuList[position].productQuantity = it.toString().toInt()
                }

            }

            productIntroEdit.addTextChangedListener{
                menuList[position].productIntro = it.toString()
            }
        }

    }
    fun getRvListData(): ArrayList<StoreMenuModel> = menuList

}

//    fun setWarningBox(binding: StoreMenuItemBinding, msg: String){
//        binding.apply {
//            productQuantityLayout.helperText = msg
//            productQuantityLayout.boxStrokeColor = setHelperTextRed(context)
//        }
//    }
//
//    fun realeaseWarningBox(binding: StoreMenuItemBinding, position: Int, productQuantity: Int){
//        binding.apply {
//            productQuantityLayout.helperText = ""
//            productQuantityLayout.boxStrokeColor = setHelperBoxBlack(context)
//            menuList[position].productQuantity = productQuantity
//        }
//    }

