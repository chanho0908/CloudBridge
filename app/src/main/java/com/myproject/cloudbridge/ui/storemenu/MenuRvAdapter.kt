package com.myproject.cloudbridge.ui.storemenu

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.myproject.cloudbridge.databinding.StoreItemMenuBinding
import com.myproject.cloudbridge.model.store.StoreMenuRvModel
import com.myproject.cloudbridge.utility.setHelperBoxBlack
import com.myproject.cloudbridge.utility.setHelperTextRed

class MenuRvAdapter(
    private val context: Context,
    private val menuList: ArrayList<StoreMenuRvModel>,
    private val imgClickListener: (Int) -> Unit,
    private val delButtonClickListener: (Int) -> Unit
) :
    RecyclerView.Adapter<MenuRvViewHolder>() {

    var isDuplicatedName = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuRvViewHolder {
        return MenuRvViewHolder(
            StoreItemMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: MenuRvViewHolder, position: Int) {
        val binding = holder.binding

        binding.apply {
            menuImgView.apply {
                setOnClickListener {
                    imgClickListener.invoke(position)
                }
                if (menuList[position].imgBitmap != null) {
                    setImageBitmap(menuList[position].imgBitmap)
                }
            }

            delButton.setOnClickListener {
                delButtonClickListener.invoke(position)
            }

            productNameEdit.addTextChangedListener {
                val input = it.toString()

                if (menuList.count { menu -> menu.productName == input } > 0){
                    productNameLayout.helperText = "이미 존재 하는 상품이에요"
                    productNameLayout.boxStrokeColor = context.setHelperTextRed()
                    isDuplicatedName = true
                } else if (input.isEmpty()) {
                    productNameLayout.helperText = "상품명을 입력해 주세요"
                    productNameLayout.boxStrokeColor = context.setHelperTextRed()
                } else {
                    productNameLayout.helperText = ""
                    productNameLayout.boxStrokeColor = context.setHelperBoxBlack()
                    //menuList[position].productName = input
                    isDuplicatedName = false
                }
            }

            productQuantityEdit.addTextChangedListener {
                val input = it.toString()

                if (input.isEmpty()) {
                    productQuantityLayout.helperText = "수량을 입력해 주세요"
                    productQuantityLayout.boxStrokeColor = context.setHelperTextRed()
                } else {
                    productQuantityLayout.helperText = ""
                    productQuantityLayout.boxStrokeColor = context.setHelperBoxBlack()
                    //menuList[position].productQuantity = it.toString().toInt()
                }
            }

            productIntroEdit.addTextChangedListener {
                menuList[position].productIntro = it.toString()
            }
        }
    }

    fun getRvListData() = menuList
}

class MenuRvViewHolder(val binding: StoreItemMenuBinding) : RecyclerView.ViewHolder(binding.root)
