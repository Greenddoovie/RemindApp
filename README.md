# RemindApp
1차 개발기간: 2022년 1월 24일 ~ 1월 27일

2차 리팩토링: 2022년 2월 1일 ~ 2월 3일

참여자: 1명

### 프로젝트 시작 이유
1. Broadcast Receiver와 Service를 공부하기 위해 진행한 Toy Project

---

### 얻은 점
1. Broadcast Receiver와 Alarm Manager를 통한 정확한 시간에 알람 울리는 방법
2. Pending Intent
    - Andorid 12에서 Pending Intent에 대한 mutability를 명시하도록 유도
    - 그로 인해 Android Studio에서 Lint로 발견
3. Service
    - Service 정책
    - Service 사용 방법
    - Service 내에서 foreground Service로 실행할 때 notification 등록 필수란 점
4. Wake Lock
    - Wake Lock은 broadcast receiver, service 분리하여 관리(공식문서)
5. Databinding의 사용 방법에 대한 이해도 살짝 향상
6. 의도를 더욱 드러나는 Naming을 사용할 것

---


### 메인화면
<img src="https://user-images.githubusercontent.com/75981415/152740578-d96369f3-0f0d-4c56-8781-ebf706e6e1aa.jpg"  width="200" height="400"/>

1. Remind 목록을 보여주는 역할
2. 각 item의 체크박스를 통해 알람 설정/해제 가능
3. 추가 버튼을 통해 새로운 Remind 생성을 위한 화면 이동
4. 목록의 remind 클릭 시 Remind 수정화면 이동
5. 변경 및 추가된 Remind에 대한 알람 등록


---

### 설정 화면
|  화면 1 |  화면 2 |
| --- | --- |
| <img src="https://user-images.githubusercontent.com/75981415/152741011-0ea95e27-ac50-46a1-949a-cd98c66baca0.jpg"  width="200" height="400"/>    |  <img src="https://user-images.githubusercontent.com/75981415/152741021-6073f5d2-2faf-41d0-960d-da9e3b5d790c.jpg"  width="200" height="400"/>   |

1. 새로운 Remind 생성 및 생성된 Remind 수정을 위한 뷰를 보여주는 역할
2. Remind Item 추가
    1. 이름/시간/벨소리 미선택
    2. 이름/벨소리 선택되어 있지 않다면, 저장 불가
    3. 저장 버튼 클릭 시 Local DB에 저장
3. Remind Item 수정
    1. 설정된 이름/시간/벨소리으로 보임
    2. 수정 후 저장 버튼 클릭 시 Local DB에 Update
4. Save Button 클릭 후
    1. Local DB 추가 및 수정 후 해당 아이템을 다시 불러옴
    2. 불러왔을 때, 해당 item의 id를 fragment result로 set
    3. 이후 popBackStack을 통해 메인화면으로 이동

---

### 리마인드 알림 화면
<img src="https://user-images.githubusercontent.com/75981415/152740647-cf7bf1ec-c63e-4a0e-8dda-8c8383b45570.jpg"  width="200" height="400"/>

1. Remind 알림 시간이 되면 Remind 내용과 알림을 보여주는 역할
2. Remind 알림 화면 호출 과정
    1. 정해진 시간이 되면 Alarm Manager를 통해 Broadcast Receiver 실행
    2. Broadcast Receiver 내부에서 service 실행
    3. Service 내부에서 음악 재생 및 Main Activity 실행
    4. Main Activity 내부에서 리마인드 알림 화면으로 navigate
    5. navigate하면서 얻은 id와 Room을 이용해 Local DB에서 정보 로드
    6. 해당 정보를 이용하여 Remind 내용 설정
3. Dismiss Button 클릭
    1. 현재 Remind의 활성 상태를 inactive로 변경
    2. 메인 화면으로 이동

---

### Room Entity
**Remind 테이블**
| id | title | hour | minute | url | active |
| --- | --- | --- | --- | --- | --- |
|     |     |     |     |     |     |
1. 고려사항: id를 외부에서 주입하면, 설정화면에서 save button 클릭 후 다시 fetch하는 과정을 줄이기 가능

---

### Alarm Receiver
1. 재 부팅시 Active 상태의 Remind를 Alarm Manager를 통한 등록 역할
    1. ACTION_BOOT_COMPLTED 액션을 받기 위한 분기처리
    2. Local DB에서 Remind 전체 로드
    3. Remind 별로 active 상태인 것을 Android OS의 예약 시스템에 등록
    4. goAsync()를 호출하여 비동기 작업 시작
    5. 비동기 작업 종료 후 pendingResult.finish() 호출
    6. 개선점: Global Scope 사용을 다른 걸로 대체
2. Alarm Manager를 통해 예약된 Pending Intent를 받는 역할
    1. Service 실행
    2. 궁금증: Service로 바로 실행하는 건 어떤가?

---

### Alarm Service
1. Remind Id를 이용해 service에서 remind item 로드
2. 로드된 Remind를 통해 uri을 받아오고 mediaPlayer 설정
3. MediaPlayer 실행 및 반복 설정
4. Activity 실행하여 리마인드 알림 설정 화면 Navigate
5. onBind()를 제공하여 리마인드 알림 설정화면에서 boundService를 이용
6. binder를 통해 service를 멈추는 stopService 함수 제공
7. 개선점: boundService이므로 비동기작업을 fragment에서 진행하고 uri를 건네주는 방식으로 media 실행하게 하도록 변경
    1. 이유: bound fragment에서도 비동기작업 진행 중


---

### Remind Item Data Class
#### 메인화면에서 사용되는 RecyclerView Item Class
```kotlin
data class RemindItem(
    val id: Int,
    val title: String,
    val hour: Int,
    val minute: Int,
    val uri: String,
    val active: Boolean,
    val onClick: (RemindItem) -> Unit,
    val onCheckBoxClick: (RemindItem) -> Unit
) {

    fun convertTime(): String {
        val hourStr = if (hour < 10) "0$hour" else "$hour"
        val minuteStr = if (minute < 10) "0$minute" else "$minute"
        val apm = if (hour >= 12) "PM" else "AM"
        return "$hourStr:$minuteStr $apm"
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<RemindItem>() {
            override fun areItemsTheSame(oldItem: RemindItem, newItem: RemindItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RemindItem, newItem: RemindItem): Boolean {
                return oldItem == newItem
            }
        }

        fun from(
            remind: Remind,
            onClick: (RemindItem) -> Unit,
            onCheckBoxClick: (RemindItem) -> Unit
        ): RemindItem {
            return RemindItem(
                id = remind.id,
                title = remind.title,
                hour = remind.hour,
                minute = remind.minute,
                uri = remind.uri,
                active = remind.active,
                onClick = onClick,
                onCheckBoxClick = onCheckBoxClick
            )
        }

        fun to(remindItem: RemindItem): Remind {
            return Remind(
                title = remindItem.title,
                hour = remindItem.hour,
                minute = remindItem.minute,
                uri = remindItem.uri,
                active = remindItem.active
            ).apply {
                id = remindItem.id
            }
        }
    }
}
```
1. 변수: onClick, onCheckBoxClick 추가
2. Databinding을 통해 xml에서 onClick을 설정가능하므로 사용하기 위한 방법으로 Data Class에 onClick 이벤트 추가
    1. View 관련 로직이 들어있으므로 HomeFragment에서 remind를 remindItem으로 변경
    2. onClick: 클릭 시 수정 화면으로 이동하는 이벤트 발생
    3. onCheckBoxClick: 클릭 시 Remind의 Active 상태 변경 및 Alarm Manager 등록 및 해제
    ![image](https://user-images.githubusercontent.com/75981415/152741219-898821b5-7b40-4ad6-8e63-7917d6bd54f1.png)

3. Companion Object로 Remind Item관련 함수, 변수 노출
    1. DiffUtil: Data class에서 작성하는 것이 의도를 잘 드러낸다고 생각 
        - ListAdapter를 사용하므로 DiffUtil.ItemCallback 사용
        - 현재 고유값으로 id 사용중이므로 areItemsTheSame에서 id값을 이용해 비교
        - areContentsThemSame에서는 equals 활용 
    2. from, To 함수 추가 
        - convert RemindItem from Remind
        - convert RemindItem to Remind
