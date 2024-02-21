# CloudBridge

## ✔ 프로젝트 소개
> 프렌차이즈 베이커리 중 소수 매장의 점주님들은 섭취에 문제가 없지만 특정한 이유로 판매가 불가능한 빵을 
사회 소외 계층들에게 기부해주시고 있습니다.
>
> 하지만 현재 이 시스템은 단순히 수기로 작성하고 영수증을
남기는 방식으로 이루어지고 있습니다. 이에 어플리케이션을 제작해 점주님들의 기부를 더욱 활성화 하고 많은 소외 계층들이 도움을
받을 수 있도록 하여 사회적 소외 계층들을 도와주는 다리가 되고자 기획하게 되었습니다.  

### 1️⃣ Language & Libraries
* Kotlin  
* Android Studio

### 2️⃣ 사업자 등록 번호 API
> 목적 : 허위 매장 등록을 방지하기 위해 사업자 등록 번호가 존재하는 매장만 등록이 가능하게 합니다.  
API 출처 : <https://www.data.go.kr/data/15081808/openapi.do>

#### 🎉 사업자 등록 상태조회 API Response
```
사업자등록 상태조회 API Response{
  status_code	string :조회 매칭 수
  request_cnt	integer: 조회 요청 수
  
  data	[
      사업자등록 상태조회 결과{
      b_no : 사업자등록번호
      b_stt	[...]
      b_stt_cd	[...]
      tax_type	01:부가가치세 일반과세자,
                02:부가가치세 간이과세자,
                03:부가가치세 과세특례자,
                04:부가가치세 면세사업자,
                05:수익사업을 영위하지 않는 비영리법인이거나 고유번호가 부여된 단체,국가기관 등,
                06:고유번호가 부여된 단체,
                07:부가가치세 간이과세자(세금계산서 발급사업자),
                * 등록되지 않았거나 삭제된 경우: "국세청에 등록되지 않은 사업자등록번호입니다"
      tax_type_cd	[...]
      end_dt	:폐업일 (YYYYMMDD 포맷)
      utcc_yn	[...]
      tax_type_change_dt	[...]
      invoice_apply_dt	[...]
      rbf_tax_type	[...]
      rbf_tax_type_cd	[...]
      }]
  }]
```

#### 🎉 응답 형태를 바탕으로 데이터를 받아올 data class를 생성합니다.
```
data class CompanyRegistrationNumberState(
    val b_no: String, 
    val b_stt: String, 
    val b_stt_cd: String, 
    val tax_type: String,  
    val tax_type_cd: String,
    val end_dt: String,
    val utcc_yn: String,
    val tax_type_change_dt: String,
    val invoice_apply_dt: String,
    val rbf_tax_type : String,
    val rbf_tax_type_cd: String
)

```
```
data class CprRequestModel (
  val request_cnt: Int,
  val match_cnt: Int,
  val status_code: String,
  val data: List<CompanyRegistrationNumberState>
)
```
#### 🎉 Retrofit interface

```
interface CRNApi {
    @POST("status")
    suspend fun getCRNState(
        @Body requestBody: CrnRequestModel, // 조회할 사업자 등록번호
        @Query("serviceKey") serviceKey:String // API 사용자 인증키
    ): CprRequestModel
}
```
#### 🎉 API 호출 함수
```

class RegistCRNVewModel: ViewModel() {

    private val _state = MutableLiveData<CprRequestModel>()
    val state: LiveData<CprRequestModel>
        get() = _state

    private val CRNApi = CRNRetrofitInstance.getInstance()

    fun getCPRState(bno: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val result = CRNApi.getCRNState(CrnRequestModel(arrayOf(bno)), SECRETE_KEY)
            _state.postValue(result)
        }catch (e : Exception){
            Log.d("CPRFragment", "Cause getCPRState: $e")
        }
    }
}
```

### 3️⃣ BackEnd Server  
https://github.com/chanho0908/android_Docker_server

### 4️⃣ REST API 
#### 🔥 REST API 서버와 통신하는 과정을 상세히 알아보겠습니다.
REST API에 관한 내용에 대해 포스팅한 글 입니다 😁   
[https://chanho-study.tistory.com/62]    
[https://chanho-study.tistory.com/55]
### ✔ Retrofit Instance 
가장 먼저 네트워크 요청을 보낼 정보를 담은 **Retrofit Instance**를 생성합니다.
```
object MySQLIStoreInstance {
    val BASE_URL = "http://172.30.1.7/"

    private val gson : Gson = GsonBuilder()
        .setLenient()
        .create()

    private val client: Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .client(MyOkHttpClient.client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getInstance(): StoreInfoApi = client.create(StoreInfoApi::class.java)
}
```

### ✔ [GET] 
#### 모든 매장 정보를 가져오는 메소드 입니다. 단순히 요청해야하는 URL로 GET 요청을 보내고 Response Type을 지정해줍니다.

```
@GET("/db/storeinfo")
suspend fun getAllStoreInfo(): List<AllStoreInfoResponseModel>

// Response Type
data class AllStoreInfoResponseModel(
    val CRN: String,
    val address: String,
    val ceoName: String,
    val contact: String,
    val image: String,
    val kind: String,
    val latitude: String,
    val longitude: String,
    val storename: String
)

```

#### 🔨 특정 사업자 등록번호에 해당 하는 매장을 가져오는 메소드입니다.
 + 여기서 중요한건  **@Path()** 어노테이션입니다. 이 어노테이션은 URL의 경로를 동적으로 지정해야할 때 사용합니다.
 + @Path 어노테이션의해 전달된 동적 데이터는 중괄호 { }로 감싸야합니다.
 + 최종 요청 URL 예시 : http://172.30.1.7/db/storeinfo/1208147521
```
@GET("/db/storeinfo/{crn}")
suspend fun getMyStoreInfo(@Path("crn") crn: String): MyStoreInfoResponseModel
```

#### 🔨 서버측에서 가져온 데이터를 ViewModel을 통해 어떻게 UI에 그려주는지 알아보겠습니다.
```
class MyPageViewModel: ViewModel() {
    private val storeInfoApi = MySQLIStoreInstance.getInstance()
    private val _myStore = MutableLiveData<MyStoreInfoResponseModel>()
    val myStore: LiveData<MyStoreInfoResponseModel>
        get() = _myStore

    fun setMyStoreInfo() = viewModelScope.launch(Dispatchers.IO) {
        try {
            MainDataStore.getCrn().collect{ crn->
                val response = storeInfoApi.getMyStoreInfo(crn)
                _myStore.postValue(response)
            }
        }catch (e: Exception){
            Log.d("MyPageViewModel","MyPageViewModel: $e")
        }
    }
  }
```
#### 📕 첫번째로 중요한 부분은 Live 데이터 부분입니다. 
 * _myStore는 MutableLiveData Type으로 변경이 가능하지만 외부에서 관측이 불가능합니다.
 * myStore는 LiveData Type으로 변경이 불가능 하지만 observer를 사용해 관측이 가능하고 getter()를 통해 _myStore의 값을 읽어오고 있습니다.
🔥 이러한 패턴은 **MVVM 아키텍쳐**에서 자주 사용하는 패턴입니다. 🔥 이런 방식을 사용면 외부에서 값을 변경할 수 없게 만들어 무결성을 유지할 수 있습니다.

#### 📕 두번째로 중요한 부분은 Coroutine 사용입니다 .
 * viewModelScope는 ViewModel LifeCycle을 따르는 코루틴입니다.
 * ViewModel은 onCreate시에 생성되며 액티비티나 프래그먼트가 onDestroy되면 onCleared() 됩니다
 * DisPatchers.IO를 사용해 백그라운드에서 안정적으로 비동기적으로 작업합니다.  

```
class MyStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyStoreBinding
    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.setMyStoreInfo()
        viewModel.myStore.observe(this){ data ->
            val bitmap =  StringToBitmaps(data.image)
            binding.mainImage.setImageBitmap(bitmap)

            binding.storeNameTextView.text = data. result.storename
            binding.storeCprTextView.text = data.result.CRN
            binding.storePhoneTextView.text = data.result.contact
            binding.storeRepreNameTextView.text = data.result.ceoName
            binding.storeKindTextView.text = data.result.kind
            binding.storeAddrTextView.text = data.result.address
        }
    }
}
```
#### 📕 observe 메소드는 라이브 데이터를 관찰하며 Live Data의 값이 변경되면 UI를 갱신합니다.

#### ✔ [POST]
> Retrofit Multipart 요청에 관한 포스팅 입니다. [https://chanho-study.tistory.com/42] 😁   
> 안드로이드와 서버의 데이터 전송시 이미지 처리에 관한 포스팅 입니다. [https://chanho-study.tistory.com/67] 😁

매장 정보를 서버측에 전송하는 메소드 입니다. 
 + Multipart : 이미지, 오디오, 비디오 등과 같은 여러 종류의 데이터를 서버에 업로드하거나 전송할 때 사용
 + 업로드할 데이터를 @Part 어노테이션을 사용해 파라미터로 지정
 + @Part : Multipart 요청의 한 부분을 나타냅니다.
 + @Part로 나눠진 파라미터를 하나의 객체로 만들어 전송
```
@Multipart
@POST("/db/upload") // 서버 엔드포인트 URL, HTTP POST 요청
suspend fun registStore(
  @Part storeimage: MultipartBody.Part, // 이미지 데이터를 나타내는 파라미터
  @Part("storename") storename: RequestBody,
  @Part("ceoName") ceoName: RequestBody,
  @Part("CRN") CRN: RequestBody,
  @Part("contact") contact: RequestBody,
  @Part("address") address: RequestBody,
  @Part("latitude") latitude: RequestBody,
  @Part("longitude") longitude: RequestBody,
  @Part("kind") kind: RequestBody
): ResponseBody
```
### 📕 latitude과 latitude을 구하는 과정을 알아보겠습니다. 
    + Daum 우편번호 서비스 API에 대한 포스팅 입니다 [https://chanho-study.tistory.com/68] 
    + 위도와 경도 값으로 추후 KaKao Map API를 사용해 매장 위치를 제공하는데 사용됩니다.
```
submitButton.setOnClickListener {

  val addr = addrEdit.text.toString()

  lifecycleScope.launch {
      val location = withContext(Dispatchers.IO) {
          TranslateGeo(addr)
      }
      val lat = location.latitude
      val lng = location.longitude
}

//주소로 위도,경도 구하는 GeoCoding
fun TranslateGeo(address: String): Location = try {
    val locations = Geocoder(requireContext(), Locale.KOREA).getFromLocationName(address, 1)
    if (!locations.isNullOrEmpty()) {
        Location("").apply {
            latitude = locations[0].latitude
            longitude = locations[0].longitude
        }
    } else {
        throw Exception("주소를 변환할 수 없습니다.")
    }
} catch (e: Exception) {
    e.printStackTrace()
    // 예외 발생 시 빈 Location 객체를 반환
    Location("").apply {
        latitude = 0.0
        longitude = 0.0
    }
}
```   

#### 📕 Activity에서 Background 작업을 위해 Coroutine을 사용했습니다.
 + Activity에서 버튼을 클릭하면 사용자가 입력한 주소값을 기준으로 위도와 경도를 계산합니다.
 + lifecycleScope : 현재 Activity의 생명주기를 따르는 Coroutine을 생성합니다.
 + withContext(Dispatchers.IO) : Background 작업을 위한 Dispatchers를 사용 했습니다.


#### ✔ [PUT]
매장 정보를 수정하기 위한 요청입니다.
 > 사용자가 이미지를 변경하지 않으면 기존 이미지를 그대로 사용해야하는데 이 부분에 대한 수정이 필요합니다.
```
@Multipart
@PUT("/db/modify-storeinfo")
suspend fun updateStoreInfo(
    @Part storeimage: MultipartBody.Part, // 이미지 데이터를 나타내는 파라미터
    @Part("storename") storename: RequestBody,
    @Part("ceoName") ceoName: RequestBody,
    @Part("CRN") CRN: RequestBody,
    @Part("contact") contact: RequestBody,
    @Part("address") address: RequestBody,
    @Part("latitude") latitude: RequestBody,
    @Part("longitude") longitude: RequestBody,
    @Part("kind") kind: RequestBody
): ResponseBody
```

#### ✔ [DELETE]
매장 정보를 삭제하기 위한 요청입니다. 
```
@DELETE("/db/delete-storeinfo/{crn}")
suspend fun deleteMyStoreInfo(@Path("crn") crn: String): Response<ResponseBody>
```


